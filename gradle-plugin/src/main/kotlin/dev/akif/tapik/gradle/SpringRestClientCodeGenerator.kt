package dev.akif.tapik.gradle

import java.io.File

internal object SpringRestClientCodeGenerator {
    private const val OUTPUT_PACKAGE = "dev.akif.tapik.spring.restclient"

    fun generate(endpoints: List<HttpEndpointDescription>, rootDir: File, useContextReceivers: Boolean = true) {
        if (endpoints.isEmpty()) return

        val directory = File(rootDir, OUTPUT_PACKAGE.replace('.', '/')).also { it.mkdirs() }
        val targetFile = File(directory, "GeneratedSpringRestClientExtensions.kt")

        val imports = buildImportSet(endpoints, OUTPUT_PACKAGE)
        targetFile.writeText(buildString {
            appendLine("package $OUTPUT_PACKAGE")
            appendLine()
            imports.forEach { appendLine("import $it") }
            appendLine()

            endpoints.sortedWith(compareBy<HttpEndpointDescription> { it.packageName }.thenBy { it.file }.thenBy { it.name })
                .forEach { endpoint ->
                    appendEndpoint(endpoint, useContextReceivers)
                    appendLine()
                }
        })
    }

    private fun buildImportSet(endpoints: List<HttpEndpointDescription>, currentPackage: String): Set<String> {
        val baseImports = setOf(
            "arrow.core.getOrElse",
            "arrow.core.leftNel",
            "dev.akif.tapik.selections.*",
            "dev.akif.tapik.http.*",
            "java.net.URI"
        )

        val endpointImports = endpoints
            .flatMap(HttpEndpointDescription::imports)
            .filterNot { it.startsWith(currentPackage) }

        return (baseImports + endpointImports).toSortedSet()
    }

    private fun StringBuilder.appendEndpoint(endpoint: HttpEndpointDescription, useContextReceivers: Boolean) {
        val signature = EndpointSignature(endpoint, useContextReceivers)

        appendLine("// Generated from: ${endpoint.packageName}.${endpoint.file}#${endpoint.name}")
        if (useContextReceivers) {
            appendLine("context(interpreter: RestClientInterpreter)")
        }
        append("fun HttpEndpoint<${signature.parametersType}, ${signature.inputHeadersType}, ${signature.inputBodyWrapper}, ${signature.outputHeadersType}, ${signature.outputBodiesWrapper}>.sendWithRestClient(")
        if (signature.inputs.isNotEmpty()) {
            appendLine()
            appendLine(signature.inputs.joinToString(separator = ",\n") { "    $it" })
        }
        appendLine("):")
        appendLine("    ${signature.returnType} {")
        appendLine("    val responseEntity = interpreter.send(")
        appendLine("        method = method,")
        appendLine("        uri = buildURI(${signature.uriArguments}),")
        appendLine("        inputHeaders = ${signature.inputHeadersEncoding},")
        appendLine("        inputBodyContentType = this.inputBody.mediaType,")
        appendLine("        inputBody = ${signature.encodeBodyCall},")
        appendLine("        outputBodies = outputBodies.toList()")
        appendLine("    )")
        appendLine()
        appendLine("    val status = responseEntity.statusCode.toStatus()")

        if (signature.outputHeaderCount > 0) {
            appendLine()
            appendLine("    val headers = responseEntity.headers")
            appendLine("        .mapValues { entry -> entry.value.map { it.orEmpty() } }")
            appendLine()
            appendLine("    val ${signature.outputHeaderTupleName} = decodeHeaders${signature.outputHeaderCount}(")
            appendLine("        headers,")
            signature.outputHeaderAccessors.forEachIndexed { index, accessor ->
                appendLine("        $accessor${if (index == signature.outputHeaderAccessors.lastIndex) "" else ","}")
            }
            appendLine("    ).getOrElse {")
            appendLine("        error(\"Cannot decode headers: \" + it.joinToString(\": \") )")
            appendLine("    }")
            signature.outputHeaderAssignments.forEach { appendLine("    $it") }
        }

        appendLine()
        appendLine(signature.responseConstruction)
        appendLine("}")
    }

    private class EndpointSignature(endpoint: HttpEndpointDescription, useContextReceivers: Boolean) {
        private val parameters = AggregatedType(endpoint.parameters)
        private val inputHeaders = HeadersType(endpoint.inputHeaders, isInput = true)
        private val inputBody = BodyType(endpoint.inputBody)
        private val outputHeaders = HeadersType(endpoint.outputHeaders, isInput = false)
        private val outputBodies = OutputBodiesType(endpoint.outputBodies)

        val parametersType: String = parameters.wrapperType
        val inputHeadersType: String = inputHeaders.wrapperType
        val inputBodyWrapper: String = inputBody.wrapperType
        val outputHeadersType: String = outputHeaders.wrapperType
        val outputBodiesWrapper: String = outputBodies.wrapperType

        val inputs: List<String> = if (useContextReceivers) {
            parameters.inputs + inputHeaders.inputs + inputBody.inputs
        } else {
            listOf("interpreter: RestClientInterpreter") + parameters.inputs + inputHeaders.inputs + inputBody.inputs
        }
        val uriArguments: String = parameters.argumentList
        val inputHeadersEncoding: String = inputHeaders.encodingCall
        val encodeBodyCall: String = inputBody.encodeCall

        val outputHeaderCount: Int = outputHeaders.count
        val outputHeaderTupleName: String = outputHeaders.tupleName
        val outputHeaderAccessors: List<String> = outputHeaders.accessors
        val outputHeaderAssignments: List<String> = outputHeaders.assignments
        val outputHeaderValueNames: List<String> = outputHeaders.valueNames
        val outputHeaderTypeNames: List<String> = outputHeaders.typeNames

        val returnType: String = outputBodies.returnType(outputHeaderTypeNames)
        val responseConstruction: String = outputBodies.responseConstruction(outputHeaderValueNames, outputHeaderTypeNames)
    }

    private class AggregatedType(description: TypeDescription) {
        val wrapperType: String
        val inputs: List<String>
        val argumentList: String

        init {
            val typeArgs = description.arguments.map { it.displayName() }
            wrapperType = if (typeArgs.isEmpty()) description.type else "${description.type}<${typeArgs.joinToString(", ")}>"

            val usedNames = mutableSetOf<String>()
            val entries = description.arguments.mapIndexed { index, arg ->
                val fallback = "parameter${index + 1}"
                val baseName = sanitizeIdentifier(arg.name, fallback)
                val unique = uniqueName(baseName, usedNames)
                NamedValue(unique, arg.displayName())
            }

            inputs = entries.map { "${it.name}: ${it.type}" }
            argumentList = entries.joinToString(", ") { it.name }
        }
    }

    private class HeadersType(description: TypeDescription, private val isInput: Boolean) {
        private val entries: List<NamedValue>

        val count: Int
        val typeNames: List<String>
        val wrapperType: String = description.displayName()

        val inputs: List<String>
        val argumentList: String
        val valueNames: List<String>
        val accessors: List<String>
        val tupleName: String = "decodedOutputHeaders"
        val assignments: List<String>
        val encodingCall: String
            get() = if (!isInput || count == 0) {
                "emptyMap()"
            } else {
                val args = if (argumentList.isEmpty()) "" else "($argumentList)"
                "encodeInputHeaders$args"
            }

        init {
            val usedNames = mutableSetOf<String>()
            entries = description.arguments.mapIndexed { index, arg ->
                val fallback = if (isInput) "inputHeader${index + 1}" else "outputHeader${index + 1}"
                val baseName = sanitizeIdentifier(arg.name, fallback)
                val unique = uniqueName(baseName, usedNames)
                NamedValue(unique, arg.displayName())
            }

            count = entries.size
            typeNames = entries.map { it.type }
            inputs = if (isInput) entries.map { "${it.name}: ${it.type}" } else emptyList()
            argumentList = entries.joinToString(", ") { it.name }
            valueNames = entries.map { it.name }
            accessors = List(count) { index -> "outputHeaders.item${index + 1}" }
            assignments = if (count == 0) emptyList() else entries.mapIndexed { index, entry -> "val ${entry.name} = $tupleName.item${index + 1}" }
        }
    }

    private class BodyType(description: TypeDescription) {
        val wrapperType: String = description.displayName()
        private val valueType: String = when (description.type) {
            "EmptyBody" -> "Unit"
            "StringBody" -> "String"
            "RawBody" -> "ByteArray"
            "JsonBody" -> description.arguments.firstOrNull()?.displayName() ?: "Any"
            else -> description.arguments.firstOrNull()?.displayName() ?: "Any"
        }

        val inputs: List<String> = if (valueType == "Unit") emptyList() else listOf("inputBody: $valueType")
        val encodeCall: String = when (valueType) {
            "Unit" -> "ByteArray(0)"
            "ByteArray" -> "inputBody"
            else -> "this.inputBody.codec.encode(inputBody)"
        }
    }

    private class OutputBodiesType(type: TypeDescription) {
        private val bodies: List<OutputBodyEntry>
        val wrapperType: String = type.displayName()

        init {
            val count = type.countSuffix()
            bodies = if (count == 0) emptyList() else type.arguments.mapIndexed { index, arg -> OutputBodyEntry(arg, index + 1) }
        }

        fun returnType(headerTypes: List<String>): String = when {
            bodies.isEmpty() && headerTypes.isEmpty() -> "ResponseWithoutBodyWithHeaders0"
            bodies.isEmpty() -> "ResponseWithoutBodyWithHeaders${headerTypes.size}<${headerTypes.joinToString(", ")}>"
            bodies.size == 1 && headerTypes.isEmpty() -> "ResponseWithHeaders0<${bodies.first().valueType}>"
            bodies.size == 1 -> "ResponseWithHeaders${headerTypes.size}<${bodies.first().valueType}, ${headerTypes.joinToString(", ")}>"
            else -> {
                val options = bodies.joinToString(", ") { body ->
                    if (headerTypes.isEmpty()) "ResponseWithHeaders0<${body.valueType}>"
                    else "ResponseWithHeaders${headerTypes.size}<${body.valueType}, ${headerTypes.joinToString(", ")}>"
                }
                "Selection${bodies.size}<$options>"
            }
        }

        fun responseConstruction(headerValues: List<String>, headerTypes: List<String>): String = when {
            bodies.isEmpty() && headerValues.isEmpty() -> "    return ResponseWithoutBodyWithHeaders0(status)"
            bodies.isEmpty() -> buildString {
                appendLine("    return ResponseWithoutBodyWithHeaders${headerTypes.size}(")
                appendLine("        status,")
                headerValues.forEachIndexed { index, value ->
                    appendLine("        $value${if (index == headerValues.lastIndex) "" else ","}")
                }
                append("    )")
            }
            bodies.size == 1 -> buildString {
                appendLine("    val bodyBytes = responseEntity.body ?: ByteArray(0)")
                appendLine("    val decoded = ${bodies.first().decoder}")
                appendLine("        .map { decodedBody ->")
                if (headerValues.isEmpty()) {
                    appendLine("            ResponseWithHeaders0(status, decodedBody)")
                } else {
                    appendLine("            ResponseWithHeaders${headerTypes.size}(")
                    appendLine("                status,")
                    appendLine("                decodedBody,")
                    headerValues.forEachIndexed { index, value ->
                        appendLine("                $value${if (index == headerValues.lastIndex) "" else ","}")
                    }
                    appendLine("            )")
                }
                appendLine("        }")
                appendLine("    return decoded.getOrElse { error(it.joinToString(\": \") ) }")
            }
            else -> buildString {
                appendLine("    val bodyBytes = responseEntity.body ?: ByteArray(0)")
                appendLine("    val response = when {")
                bodies.forEach { body ->
                    appendLine("        ${body.statusMatcher} -> ${body.decoder}.map { decodedBody ->")
                    if (headerValues.isEmpty()) {
                        appendLine("            Selection${bodies.size}.Option${body.index}(")
                        appendLine("                ResponseWithHeaders0(status, decodedBody)")
                        appendLine("            )")
                    } else {
                        appendLine("            Selection${bodies.size}.Option${body.index}(")
                        appendLine("                ResponseWithHeaders${headerTypes.size}(")
                        appendLine("                    status,")
                        appendLine("                    decodedBody,")
                        headerValues.forEachIndexed { index, value ->
                            appendLine("                    $value${if (index == headerValues.lastIndex) "" else ","}")
                        }
                        appendLine("                )")
                        appendLine("            )")
                    }
                    appendLine("        }")
                }
                appendLine("        else -> \"Response output did not match\".leftNel()")
                appendLine("    }")
                appendLine()
                append("    return response.getOrElse { error(it.joinToString(\": \") ) }")
            }
        }

        private fun headerAlias(count: Int): String =
            when (count) {
                0 -> "Headers0"
                else -> "Headers$count"
            }

        private class OutputBodyEntry(description: TypeDescription, val index: Int) {
            val valueType: String = when (description.type) {
                "JsonBody" -> description.arguments.firstOrNull()?.type ?: "Any"
                "StringBody" -> "String"
                "RawBody" -> "ByteArray"
                "EmptyBody" -> "Unit"
                else -> description.arguments.firstOrNull()?.type ?: "Any"
            }

            val statusMatcher: String = "outputBodies.item$index.statusMatcher(status)"
            val decoder: String = "outputBodies.item$index.body.codec.decode(bodyBytes)"
        }
    }
}

private fun TypeDescription.displayName(): String =
    if (arguments.isEmpty()) type else "$type<${arguments.joinToString(", ") { it.displayName() }}>"

private fun TypeDescription.countSuffix(): Int =
    type.takeLastWhile { it.isDigit() }.toIntOrNull() ?: arguments.size

private data class NamedValue(val name: String, val type: String)

private fun sanitizeIdentifier(rawName: String?, fallback: String): String {
    val source = rawName?.trim().takeUnless { it.isNullOrEmpty() } ?: fallback
    val cleaned = buildString {
        for (ch in source) {
            when {
                ch.isLetterOrDigit() -> append(ch)
                ch == '_' -> append('_')
                else -> append('_')
            }
        }
    }.replace(Regex("_+"), "_").trim('_')

    val base = (cleaned.ifBlank { fallback }).replaceFirstChar { it.lowercaseChar() }
    return base
}

private fun uniqueName(base: String, used: MutableSet<String>): String {
    var candidate = base
    var index = 2
    while (renderIdentifier(candidate) in used) {
        candidate = "${base}_$index"
        index++
    }
    val rendered = renderIdentifier(candidate)
    used += rendered
    return rendered
}

private fun renderIdentifier(name: String): String {
    val keyword = name in KOTLIN_KEYWORDS
    val identifierPattern = Regex("[A-Za-z_][A-Za-z0-9_]*")
    return if (!keyword && identifierPattern.matches(name)) {
        name
    } else {
        "`$name`"
    }
}

private val KOTLIN_KEYWORDS = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
    "var", "when", "while"
)
