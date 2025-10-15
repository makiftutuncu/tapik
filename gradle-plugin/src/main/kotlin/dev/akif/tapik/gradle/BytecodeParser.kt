package dev.akif.tapik.gradle

object BytecodeParser {
    private const val HTTP_ENDPOINT_FQCN = "dev.akif.tapik.http.HttpEndpoint"
    private const val PARAMETER_FQCN = "dev.akif.tapik.http.Parameter"
    private const val HEADER_FQCN = "dev.akif.tapik.http.Header"
    private const val OUTPUT_BODY_FQCN = "dev.akif.tapik.http.OutputBody"
    private const val TUPLE_PACKAGE = "dev.akif.tapik.tuples"
    private const val HTTP_PACKAGE = "dev.akif.tapik.http"
    private val JAVA_TO_KOTLIN_TYPES = mapOf(
        "java.lang.Boolean" to "Boolean",
        "java.lang.Byte" to "Byte",
        "java.lang.Short" to "Short",
        "java.lang.Integer" to "Int",
        "java.lang.Long" to "Long",
        "java.lang.Float" to "Float",
        "java.lang.Double" to "Double",
        "java.lang.Character" to "Char",
        "java.lang.String" to "String",
        "java.lang.Object" to "Any",
        "java.lang.Void" to "Unit"
    )

    internal fun parseHttpEndpoint(
        signature: String,
        ownerInternalName: String,
        methodName: String,
        metadata: EndpointMetadata?
    ): HttpEndpointDescription? {
        val returnSignature = extractReturnSignature(signature) ?: return null
        val rootType = SignatureParser(returnSignature).parseType()
        val endpointClass = rootType.asClassType() ?: return null

        if (!endpointClass.matches(HTTP_ENDPOINT_FQCN)) {
            return null
        }

        if (endpointClass.arguments.size != 5) {
            return null
        }

        val parameters = convertParameters(endpointClass.arguments[0], metadata?.parameters) ?: return null
        val inputHeaders = convertHeaders(endpointClass.arguments[1], metadata?.inputHeaders) ?: return null
        val inputBody = convertGeneral(endpointClass.arguments[2]) ?: return null
        val outputHeaders = convertHeaders(endpointClass.arguments[3], metadata?.outputHeaders) ?: return null
        val outputBodies = convertOutputBodies(endpointClass.arguments[4]) ?: return null

        val imports = linkedSetOf<String>()
        imports += HTTP_ENDPOINT_FQCN
        imports += parameters.imports
        imports += inputHeaders.imports
        imports += inputBody.imports
        imports += outputHeaders.imports
        imports += outputBodies.imports

        val ownerClassName = ownerInternalName.replace('/', '.')
        val packageName = ownerClassName.substringBeforeLast('.', "")
        val fileName = ownerClassName.substringAfterLast('.').substringAfterLast('$')
        val endpointName = deriveEndpointName(methodName)

        val rawType = TypeDescription(
            name = null,
            type = endpointClass.simpleName,
            arguments = listOf(
                parameters.description,
                inputHeaders.description,
                inputBody.description,
                outputHeaders.description,
                outputBodies.description
            )
        )

        return HttpEndpointDescription(
            name = endpointName,
            packageName = packageName,
            file = fileName,
            parameters = parameters.description,
            inputHeaders = inputHeaders.description,
            inputBody = inputBody.description,
            outputHeaders = outputHeaders.description,
            outputBodies = outputBodies.description,
            imports = imports.sorted(),
            rawType = rawType.toString()
        )
    }

    private fun extractReturnSignature(signature: String): String? {
        if (signature.isBlank()) {
            return null
        }

        val endOfParams = signature.indexOf(')')
        if (endOfParams == -1 || endOfParams >= signature.lastIndex) {
            return null
        }

        val afterParams = signature.substring(endOfParams + 1)
        val throwsIndex = afterParams.indexOf('^')
        return if (throwsIndex >= 0) {
            afterParams.take(throwsIndex)
        } else {
            afterParams
        }
    }

    private fun deriveEndpointName(methodName: String): String {
        if (methodName.startsWith("get") && methodName.length > 3) {
            val raw = methodName.substring(3)
            if (raw.isNotEmpty()) {
                return raw.replaceFirstChar { it.lowercaseChar() }
            }
        }

        if (methodName.startsWith("is") && methodName.length > 2) {
            val raw = methodName.substring(2)
            if (raw.isNotEmpty()) {
                return raw.replaceFirstChar { it.lowercaseChar() }
            }
        }

        return methodName
    }

    private fun convertParameters(type: SignatureType, metadata: List<ParameterMetadata>?): ConversionResult? {
        val tuple = type.asClassType() ?: return convertGeneral(type)
        val alias = tuple.tupleAliasPrefix("Parameters") ?: return convertGeneral(type)
        val argumentResults = tuple.arguments.drop(1).mapNotNull { extractParameterType(it) }
        val namedArguments = argumentResults.mapIndexed { index, result ->
            val meta = metadata?.getOrNull(index)
            result.withMetadata(
                name = meta?.name,
                hasKnownValues = null,
                isRequired = meta?.isRequired,
                hasDefault = meta?.hasDefault
            )
        }

        val imports = linkedSetOf("$HTTP_PACKAGE.$alias")
        namedArguments.forEach { imports += it.imports }

        return ConversionResult(
            description = TypeDescription(name = null, type = alias, arguments = namedArguments.map { it.description }),
            imports = imports
        )
    }

    private fun convertHeaders(type: SignatureType, metadata: List<HeaderMetadata>?): ConversionResult? {
        val tuple = type.asClassType() ?: return convertGeneral(type)
        val alias = tuple.tupleAliasPrefix("Headers") ?: return convertGeneral(type)
        val argumentResults = tuple.arguments.drop(1).mapNotNull { extractHeaderType(it) }
        val namedArguments = argumentResults.mapIndexed { index, result ->
            val meta = metadata?.getOrNull(index)
            result.withMetadata(
                name = meta?.name,
                hasKnownValues = meta?.hasKnownValues,
                isRequired = null,
                hasDefault = null
            )
        }

        val imports = linkedSetOf("$HTTP_PACKAGE.$alias")
        namedArguments.forEach { imports += it.imports }

        return ConversionResult(
            description = TypeDescription(name = null, type = alias, arguments = namedArguments.map { it.description }),
            imports = imports
        )
    }

    private fun convertOutputBodies(type: SignatureType): ConversionResult? {
        val tuple = type.asClassType() ?: return convertGeneral(type)
        val alias = tuple.tupleAliasPrefix("OutputBodies") ?: return convertGeneral(type)
        val argumentResults = tuple.arguments.drop(1).mapNotNull { extractOutputBodyType(it) }

        val imports = linkedSetOf("$HTTP_PACKAGE.$alias")
        argumentResults.forEach { imports += it.imports }

        return ConversionResult(
            description = TypeDescription(name = null, type = alias, arguments = argumentResults.map { it.description }),
            imports = imports
        )
    }

    private fun extractParameterType(type: SignatureType): ConversionResult? {
        val classType = type.asClassType() ?: return convertGeneral(type)
        if (!classType.matches(PARAMETER_FQCN)) {
            return convertGeneral(type)
        }

        val argument = classType.arguments.firstOrNull() ?: return null
        return convertGeneral(argument)
    }

    private fun extractHeaderType(type: SignatureType): ConversionResult? {
        val classType = type.asClassType() ?: return convertGeneral(type)
        if (!classType.matches(HEADER_FQCN)) {
            return convertGeneral(type)
        }

        val argument = classType.arguments.firstOrNull() ?: return null
        return convertGeneral(argument)
    }

    private fun extractOutputBodyType(type: SignatureType): ConversionResult? {
        val classType = type.asClassType() ?: return convertGeneral(type)
        if (!classType.matches(OUTPUT_BODY_FQCN)) {
            return convertGeneral(type)
        }

        val argument = classType.arguments.firstOrNull() ?: return null
        return convertGeneral(argument)
    }

    private fun convertGeneral(type: SignatureType): ConversionResult? = when (type) {
        SignatureType.Star -> null
        is SignatureType.Array -> convertArray(type)
        is SignatureType.Class -> convertClass(type)
        is SignatureType.TypeVariable -> ConversionResult(TypeDescription(name = null, type = type.name, arguments = emptyList()), emptySet())
    }

    private fun convertArray(type: SignatureType.Array): ConversionResult? {
        val component = convertGeneral(type.component) ?: return null
        val imports = linkedSetOf<String>()
        imports += component.imports
        val arrayFqcn = "kotlin.Array"
        if (shouldImport(arrayFqcn)) {
            imports += arrayFqcn
        }

        return ConversionResult(
            description = TypeDescription(name = null, type = "Array", arguments = listOf(component.description)),
            imports = imports
        )
    }

    private fun convertClass(type: SignatureType.Class): ConversionResult {
        val argumentResults = type.arguments.mapNotNull { convertGeneral(it) }
        val imports = linkedSetOf<String>()
        argumentResults.forEach { imports += it.imports }

        val fqcn = type.importName()
        val replacement = JAVA_TO_KOTLIN_TYPES[fqcn]
        if (replacement == null && shouldImport(fqcn)) {
            imports += fqcn
        }

        return ConversionResult(
            description = TypeDescription(
                name = null,
                type = replacement ?: type.simpleName,
                arguments = argumentResults.map { it.description }
            ),
            imports = imports
        )
    }

    private fun shouldImport(fqcn: String): Boolean {
        if (fqcn.isBlank()) {
            return false
        }

        if (fqcn.startsWith("kotlin.")) {
            return false
        }

        if (fqcn.startsWith("java.lang.")) {
            return false
        }

        return true
    }

    private fun SignatureType.asClassType(): SignatureType.Class? = this as? SignatureType.Class

    private fun SignatureType.Class.matches(expected: String): Boolean = name == expected

    private fun SignatureType.Class.tupleAliasPrefix(prefix: String): String? {
        if (!name.startsWith(TUPLE_PACKAGE)) {
            return null
        }

        val suffix = name.substringAfterLast('.')
        if (!suffix.startsWith("Tuple")) {
            return null
        }

        val count = suffix.removePrefix("Tuple")
        if (count.isEmpty()) {
            return null
        }

        return "$prefix$count"
    }

    private val SignatureType.Class.simpleName: String
        get() = name.substringAfterLast('.').replace('$', '.')

    private fun SignatureType.Class.importName(): String = name.replace('$', '.')

    private class SignatureParser(private val signature: String) {
        private var index = 0

        fun parseType(): SignatureType {
            val type = parseTypeInternal() ?: throw IllegalArgumentException("Unable to parse signature '$signature'")
            if (index != signature.length) {
                throw IllegalArgumentException("Unexpected signature suffix in '$signature'")
            }
            return type
        }

        private fun parseTypeInternal(): SignatureType? {
            if (index >= signature.length) {
                return null
            }

            return when (val current = signature[index]) {
                'L' -> parseClassType()
                'T' -> parseTypeVariable()
                '[' -> parseArrayType()
                '*' -> {
                    index += 1
                    SignatureType.Star
                }
                '+', '-' -> {
                    val variance = if (current == '+') Variance.OUT else Variance.IN
                    index += 1
                    val argument = parseTypeInternal() ?: throw IllegalArgumentException("Invalid wildcard in '$signature'")
                    argument.withVariance(variance)
                }
                else -> null
            }
        }

        private fun parseClassType(): SignatureType.Class {
            index += 1 // consume 'L'
            val start = index
            while (index < signature.length) {
                val ch = signature[index]
                if (ch == ';' || ch == '<') {
                    break
                }
                index += 1
            }

            if (index > signature.length) {
                throw IllegalArgumentException("Malformed class signature in '$signature'")
            }

            val rawName = signature.substring(start, index)
            val dottedName = rawName.replace('/', '.')

            val arguments = mutableListOf<SignatureType>()
            if (index < signature.length && signature[index] == '<') {
                index += 1 // consume '<'
                while (index < signature.length && signature[index] != '>') {
                    val argument = parseTypeInternal() ?: throw IllegalArgumentException("Invalid type argument in '$signature'")
                    arguments += argument
                }

                if (index >= signature.length || signature[index] != '>') {
                    throw IllegalArgumentException("Unterminated type arguments in '$signature'")
                }
                index += 1 // consume '>'
            }

            if (index >= signature.length || signature[index] != ';') {
                throw IllegalArgumentException("Missing ';' after class type in '$signature'")
            }
            index += 1 // consume ';'

            return SignatureType.Class(dottedName, arguments)
        }

        private fun parseTypeVariable(): SignatureType.TypeVariable {
            index += 1 // consume 'T'
            val start = index
            while (index < signature.length && signature[index] != ';') {
                index += 1
            }

            if (index >= signature.length) {
                throw IllegalArgumentException("Unterminated type variable in '$signature'")
            }

            val name = signature.substring(start, index)
            index += 1 // consume ';'

            return SignatureType.TypeVariable(name)
        }

        private fun parseArrayType(): SignatureType.Array {
            index += 1 // consume '['
            val component = parseTypeInternal() ?: throw IllegalArgumentException("Invalid array component in '$signature'")
            return SignatureType.Array(component)
        }
    }

    private enum class Variance {
        INVARIANT,
        IN,
        OUT
    }

    private sealed interface SignatureType {
        val variance: Variance

        fun withVariance(variance: Variance): SignatureType = when (this) {
            is Array -> copy(variance = variance)
            is Class -> copy(variance = variance)
            is TypeVariable -> copy(variance = variance)
            Star -> this
        }

        data class Class(
            val name: String,
            val arguments: List<SignatureType>,
            override val variance: Variance = Variance.INVARIANT
        ) : SignatureType

        data class Array(
            val component: SignatureType,
            override val variance: Variance = Variance.INVARIANT
        ) : SignatureType

        data class TypeVariable(
            val name: String,
            override val variance: Variance = Variance.INVARIANT
        ) : SignatureType

        data object Star : SignatureType {
            override val variance: Variance = Variance.INVARIANT
        }
    }

    private data class ConversionResult(
        val description: TypeDescription,
        val imports: Set<String>
    )

    private fun ConversionResult.withMetadata(
        name: String?,
        hasKnownValues: Boolean?,
        isRequired: Boolean?,
        hasDefault: Boolean?
    ): ConversionResult {
        val trimmed = name?.takeIf { it.isNotBlank() }
        val updated = description.copy(
            name = trimmed ?: description.name,
            hasKnownValues = hasKnownValues ?: description.hasKnownValues,
            required = isRequired ?: description.required,
            hasDefault = hasDefault ?: description.hasDefault
        )
        return copy(description = updated)
    }
}
