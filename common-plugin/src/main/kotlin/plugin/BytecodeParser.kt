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
    private const val URI_WITH_PARAMETERS_PREFIX = "$TAPIK_PACKAGE.URIWithParameters"
    private const val HEADERS_PREFIX = "$TAPIK_PACKAGE.Headers"
    private const val OUTPUTS_PREFIX = "$TAPIK_PACKAGE.Outputs"
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

        if (endpointClass.arguments.size != 4) {
            return null
        }

        val uriWithParameters = convertParameters(endpointClass.arguments[0]) ?: return null
        val inputHeaders = convertHeaders(endpointClass.arguments[1]) ?: return null
        val inputBody = convertGeneral(endpointClass.arguments[2]) ?: return null
        val outputs = convertOutputs(endpointClass.arguments[3]) ?: return null

        val imports = linkedSetOf<String>()
        imports += HTTP_ENDPOINT_FQCN
        imports += uriWithParameters.imports
        imports += inputHeaders.imports
        imports += inputBody.imports
        imports += outputs.imports

        val ownerClassName = ownerInternalName.replace('/', '.')
        val packageName = ownerClassName.substringBeforeLast('.', "")
        val fileName = ownerClassName.substringAfterLast('.').substringAfterLast('$')
        val endpointName = deriveEndpointName(methodName)

        val rawType = TypeMetadata(
            name = endpointClass.simpleName,
            arguments = listOf(
                uriWithParameters.type,
                inputHeaders.type,
                inputBody.type,
                outputs.type
            )
        )

        return HttpEndpointSignature(
            name = endpointName,
            packageName = packageName,
            file = fileName,
            uriWithParameters = uriWithParameters.type,
            inputHeaders = inputHeaders.type,
            inputBody = inputBody.type,
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
        if (!classType.name.startsWith(URI_WITH_PARAMETERS_PREFIX)) {
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

    private fun convertOutputs(type: SignatureType): ConversionResult? {
        val classType = type.asClassType() ?: return convertGeneral(type)
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
}
