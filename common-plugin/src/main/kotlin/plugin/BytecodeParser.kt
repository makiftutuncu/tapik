package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.TypeMetadata
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType

/**
 * Inspects bytecode or reflective type information to extract Tapik HTTP endpoint signatures.
 */
object BytecodeParser {
    private const val TAPIK_PACKAGE = "dev.akif.tapik"
    private const val HTTP_ENDPOINT_FQCN = "$TAPIK_PACKAGE.HttpEndpoint"
    private const val PARAMETERS_PREFIX = "$TAPIK_PACKAGE.Parameters"
    private const val INPUT_FQCN = "$TAPIK_PACKAGE.Input"
    private const val HEADERS_PREFIX = "$TAPIK_PACKAGE.Headers"
    private const val OUTPUTS_PREFIX = "$TAPIK_PACKAGE.Outputs"
    private const val TUPLE_PREFIX = "$TAPIK_PACKAGE.Tuple"
    private const val HEADER_FQCN = "$TAPIK_PACKAGE.Header"
    private const val HEADER_VALUES_FQCN = "$TAPIK_PACKAGE.HeaderValues"
    private const val HEADER_VALUES_PREFIX = "$TAPIK_PACKAGE.HeaderValues"
    private const val PARAMETER_FQCN = "$TAPIK_PACKAGE.Parameter"
    private const val OUTPUT_FQCN = "$TAPIK_PACKAGE.Output"
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
    private val PATH_TYPE = TypeMetadata(
        name = "List",
        arguments = listOf(TypeMetadata("String"))
    )

    internal fun parseHttpEndpoint(
        signature: String,
        ownerInternalName: String,
        methodName: String
    ): HttpEndpointSignature? {
        val returnSignature = extractReturnSignature(signature) ?: return null
        val rootType = SignatureParser(returnSignature).parseType()
        val endpointClass = rootType.asClassType() ?: return null
        return buildEndpointSignature(endpointClass, ownerInternalName, methodName)
    }

    internal fun parseHttpEndpoint(
        returnType: Type,
        ownerInternalName: String,
        methodName: String
    ): HttpEndpointSignature? {
        val rootType = returnType.toSignatureType() ?: return null
        val endpointClass = rootType.asClassType() ?: return null
        return buildEndpointSignature(endpointClass, ownerInternalName, methodName)
    }

    private fun buildEndpointSignature(
        endpointClass: SignatureType.Class,
        ownerInternalName: String,
        methodName: String
    ): HttpEndpointSignature? {
        if (!endpointClass.matches(HTTP_ENDPOINT_FQCN)) {
            return null
        }

        if (endpointClass.arguments.size != 3) {
            return null
        }

        val parameters = convertParameters(endpointClass.arguments[0]) ?: return null
        val input = convertInput(endpointClass.arguments[1]) ?: return null
        val outputs = convertOutputs(endpointClass.arguments[2]) ?: return null

        val imports = linkedSetOf<String>()
        imports += HTTP_ENDPOINT_FQCN
        imports += parameters.imports
        imports += input.imports
        imports += outputs.imports

        val ownerClassName = ownerInternalName.replace('/', '.')
        val packageName = ownerClassName.substringBeforeLast('.', "")
        val fileName =
            ownerClassName
                .substringAfterLast('.')
                .split('$')
                .joinToString(separator = "") { it }
        val endpointName = deriveEndpointName(methodName)

        val rawType = TypeMetadata(
            name = endpointClass.simpleName,
            arguments = listOf(
                parameters.type,
                input.type,
                outputs.type
            )
        )

        val inputSignature =
            InputSignature(
                type = input.type,
                headers = input.headers.type,
                body = input.body.type
            )

        return HttpEndpointSignature(
            name = endpointName,
            packageName = packageName,
            file = fileName,
            path = PATH_TYPE,
            parameters = parameters.type,
            input = inputSignature,
            outputs = outputs.type,
            imports = imports.sorted(),
            rawType = rawType.toString(),
            ownerInternalName = ownerInternalName,
            methodName = methodName
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

    private fun convertParameters(type: SignatureType): ConversionResult? {
        val classType = type.asClassType() ?: return convertGeneral(type)
        val tupleAlias = classType.asTupleAlias(PARAMETER_FQCN)
        if (tupleAlias != null) {
            val imports = linkedSetOf<String>()
            val aliasImport = "${PARAMETERS_PREFIX}${tupleAlias.arity}"
            if (shouldImport(aliasImport)) {
                imports += aliasImport
            }
            val argumentResults =
                if (tupleAlias.arity == 0) {
                    emptyList()
                } else {
                    tupleAlias.elements.mapNotNull { convertParameterElement(it) }
                }
            argumentResults.forEach { imports += it.imports }

            return ConversionResult(
                type = TypeMetadata(
                    name = "Parameters${tupleAlias.arity}",
                    arguments = argumentResults.map { it.type }
                ),
                imports = imports
            )
        }
        if (!classType.name.startsWith(PARAMETERS_PREFIX)) {
            return convertGeneral(type)
        }

        val argumentResults = classType.arguments.mapNotNull { convertGeneral(it) }
        val imports = linkedSetOf<String>()
        val classImport = classType.importName()
        if (shouldImport(classImport)) {
            imports += classImport
        }
        argumentResults.forEach { imports += it.imports }

        return ConversionResult(
            type = TypeMetadata(
                name = classType.simpleName,
                arguments = argumentResults.map { it.type }
            ),
            imports = imports
        )
    }

    private fun convertHeaders(type: SignatureType): ConversionResult? {
        val classType = type.asClassType() ?: return convertGeneral(type)
        val tupleAlias = classType.asTupleAlias(HEADER_FQCN)
        if (tupleAlias != null) {
            val imports = linkedSetOf<String>()
            val aliasImport = "${HEADERS_PREFIX}${tupleAlias.arity}"
            if (shouldImport(aliasImport)) {
                imports += aliasImport
            }
            val argumentResults =
                if (tupleAlias.arity == 0) {
                    emptyList()
                } else {
                    tupleAlias.elements.mapNotNull { convertHeaderElement(it) }
                }
            argumentResults.forEach { imports += it.imports }

            return ConversionResult(
                type = TypeMetadata(
                    name = "Headers${tupleAlias.arity}",
                    arguments = argumentResults.map { it.type }
                ),
                imports = imports
            )
        }
        if (!classType.name.startsWith(HEADERS_PREFIX)) {
            return convertGeneral(type)
        }

        val argumentResults = classType.arguments.mapNotNull { convertGeneral(it) }
        val imports = linkedSetOf<String>()
        val classImport = classType.importName()
        if (shouldImport(classImport)) {
            imports += classImport
        }
        argumentResults.forEach { imports += it.imports }

        return ConversionResult(
            type = TypeMetadata(
                name = classType.simpleName,
                arguments = argumentResults.map { it.type }
            ),
            imports = imports
        )
    }

    private fun convertInput(type: SignatureType): InputConversionResult? {
        val classType = type.asClassType() ?: return null
        if (!classType.matches(INPUT_FQCN) || classType.arguments.size != 2) {
            return null
        }

        val headers = convertHeaders(classType.arguments[0]) ?: return null
        val body = convertGeneral(classType.arguments[1]) ?: return null
        val imports = linkedSetOf<String>()
        val classImport = classType.importName()
        if (shouldImport(classImport)) {
            imports += classImport
        }
        imports += headers.imports
        imports += body.imports

        return InputConversionResult(
            type = TypeMetadata(
                name = classType.simpleName,
                arguments = listOf(headers.type, body.type)
            ),
            headers = headers,
            body = body,
            imports = imports
        )
    }

    private fun convertOutputs(type: SignatureType): ConversionResult? {
        val classType = type.asClassType() ?: return convertGeneral(type)
        val tupleAlias = classType.asTupleAlias(OUTPUT_FQCN)
        if (tupleAlias != null) {
            val imports = linkedSetOf<String>()
            val aliasImport = "${OUTPUTS_PREFIX}${tupleAlias.arity}"
            if (shouldImport(aliasImport)) {
                imports += aliasImport
            }
            val argumentResults =
                if (tupleAlias.arity == 0) {
                    emptyList()
                } else {
                    tupleAlias.elements.mapNotNull { convertGeneral(it) }
                }
            argumentResults.forEach { imports += it.imports }

            return ConversionResult(
                type = TypeMetadata(
                    name = "Outputs${tupleAlias.arity}",
                    arguments = argumentResults.map { it.type }
                ),
                imports = imports
            )
        }
        if (!classType.name.startsWith(OUTPUTS_PREFIX)) {
            return convertGeneral(type)
        }

        val argumentResults = classType.arguments.mapNotNull { convertGeneral(it) }
        val imports = linkedSetOf<String>()
        val classImport = classType.importName()
        if (shouldImport(classImport)) {
            imports += classImport
        }
        argumentResults.forEach { imports += it.imports }

        return ConversionResult(
            type = TypeMetadata(
                name = classType.simpleName,
                arguments = argumentResults.map { it.type }
            ),
            imports = imports
        )
    }

    private fun convertGeneral(type: SignatureType): ConversionResult? = when (type) {
        SignatureType.Star -> null
        is SignatureType.Array -> convertArray(type)
        is SignatureType.Class -> convertClass(type)
        is SignatureType.TypeVariable -> ConversionResult(TypeMetadata(name = type.name, arguments = emptyList()), emptySet())
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
            type = TypeMetadata(name = "Array", arguments = listOf(component.type)),
            imports = imports
        )
    }

    private fun convertClass(type: SignatureType.Class): ConversionResult {
        type.asTupleAlias(HEADER_FQCN)?.let { alias ->
            val imports = linkedSetOf<String>()
            val aliasImport = "${HEADERS_PREFIX}${alias.arity}"
            if (shouldImport(aliasImport)) {
                imports += aliasImport
            }
            val argumentResults =
                if (alias.arity == 0) {
                    emptyList()
                } else {
                    alias.elements.mapNotNull { convertHeaderElement(it) }
                }
            argumentResults.forEach { imports += it.imports }

            return ConversionResult(
                type = TypeMetadata(
                    name = "Headers${alias.arity}",
                    arguments = argumentResults.map { it.type }
                ),
                imports = imports
            )
        }

        type.asTupleAlias(HEADER_VALUES_FQCN)?.let { alias ->
            val imports = linkedSetOf<String>()
            val aliasImport = "${HEADER_VALUES_PREFIX}${alias.arity}"
            if (shouldImport(aliasImport)) {
                imports += aliasImport
            }
            val argumentResults =
                if (alias.arity == 0) {
                    emptyList()
                } else {
                    alias.elements.mapNotNull { convertHeaderElement(it) }
                }
            argumentResults.forEach { imports += it.imports }

            return ConversionResult(
                type = TypeMetadata(
                    name = "HeaderValues${alias.arity}",
                    arguments = argumentResults.map { it.type }
                ),
                imports = imports
            )
        }

        type.asTupleAlias(PARAMETER_FQCN)?.let { alias ->
            val imports = linkedSetOf<String>()
            val aliasImport = "${PARAMETERS_PREFIX}${alias.arity}"
            if (shouldImport(aliasImport)) {
                imports += aliasImport
            }
            val argumentResults =
                if (alias.arity == 0) {
                    emptyList()
                } else {
                    alias.elements.mapNotNull { convertParameterElement(it) }
                }
            argumentResults.forEach { imports += it.imports }

            return ConversionResult(
                type = TypeMetadata(
                    name = "Parameters${alias.arity}",
                    arguments = argumentResults.map { it.type }
                ),
                imports = imports
            )
        }

        type.asTupleAlias(OUTPUT_FQCN)?.let { alias ->
            val imports = linkedSetOf<String>()
            val aliasImport = "${OUTPUTS_PREFIX}${alias.arity}"
            if (shouldImport(aliasImport)) {
                imports += aliasImport
            }
            val argumentResults =
                if (alias.arity == 0) {
                    emptyList()
                } else {
                    alias.elements.mapNotNull { convertGeneral(it) }
                }
            argumentResults.forEach { imports += it.imports }

            return ConversionResult(
                type = TypeMetadata(
                    name = "Outputs${alias.arity}",
                    arguments = argumentResults.map { it.type }
                ),
                imports = imports
            )
        }

        val argumentResults = type.arguments.mapNotNull { convertGeneral(it) }
        val imports = linkedSetOf<String>()
        argumentResults.forEach { imports += it.imports }

        val fqcn = type.importName()
        val replacement = JAVA_TO_KOTLIN_TYPES[fqcn]
        if (replacement == null && shouldImport(fqcn)) {
            imports += fqcn
        }

        return ConversionResult(
            type = TypeMetadata(
                name = replacement ?: type.simpleName,
                arguments = argumentResults.map { it.type }
            ),
            imports = imports
        )
    }

    private fun convertHeaderElement(type: SignatureType): ConversionResult? {
        val classType = type.asClassType()
        return when {
            classType == null -> convertGeneral(type)
            classType.name == HEADER_FQCN && classType.arguments.isNotEmpty() -> convertGeneral(classType.arguments.first())
            classType.name == HEADER_VALUES_FQCN && classType.arguments.isNotEmpty() -> convertGeneral(classType.arguments.first())
            else -> convertGeneral(type)
        }
    }

    private fun convertParameterElement(type: SignatureType): ConversionResult? {
        val classType = type.asClassType()
        val valueType = classType?.arguments?.firstOrNull()
        return if (valueType != null) convertGeneral(valueType) else convertGeneral(type)
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

    private val SignatureType.Class.simpleName: String
        get() = name.substringAfterLast('.').replace('$', '.')

    private fun SignatureType.Class.importName(): String = name.replace('$', '.')

    private data class TupleAlias(val arity: Int, val elements: List<SignatureType>)

    private fun SignatureType.Class.asTupleAlias(expectedSuperFqcn: String): TupleAlias? {
        if (!name.startsWith(TUPLE_PREFIX)) {
            return null
        }

        val arity = simpleName.removePrefix("Tuple").toIntOrNull() ?: return null
        if (arity == 0) {
            if (arguments.isNotEmpty()) {
                return null
            }
            return TupleAlias(arity, emptyList())
        }

        if (arguments.size != arity + 1) {
            return null
        }

        val superArgument = arguments.first()
        if (!superArgument.matchesRawType(expectedSuperFqcn)) {
            return null
        }

        return TupleAlias(arity, arguments.drop(1))
    }

    private fun SignatureType.matchesRawType(fqcn: String): Boolean =
        (this as? SignatureType.Class)?.name == fqcn

    private fun Type.toSignatureType(): SignatureType? = when (this) {
        is Class<*> -> {
            if (isArray) {
                componentType?.toSignatureType()?.let { SignatureType.Array(it) }
            } else {
                SignatureType.Class(name, emptyList())
            }
        }
        is ParameterizedType -> {
            val raw = rawType as? Class<*> ?: return null
            val arguments = actualTypeArguments.mapNotNull { it.toSignatureType() }
            SignatureType.Class(raw.name, arguments)
        }
        is GenericArrayType -> genericComponentType?.toSignatureType()?.let { SignatureType.Array(it) }
        is TypeVariable<*> -> SignatureType.TypeVariable(name)
        is WildcardType -> {
            val lower = lowerBounds.firstOrNull()
            val upper = upperBounds.firstOrNull()
            when {
                lower != null -> lower.toSignatureType()?.withVariance(Variance.IN)
                upper != null && upper != Any::class.java -> upper.toSignatureType()?.withVariance(Variance.OUT)
                else -> SignatureType.Star
            }
        }
        else -> null
    }

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
        val type: TypeMetadata,
        val imports: Set<String>
    )

    private data class InputConversionResult(
        val type: TypeMetadata,
        val headers: ConversionResult,
        val body: ConversionResult,
        val imports: Set<String>
    )
}
