package dev.akif.tapik.gradle

import dev.akif.tapik.gradle.metadata.BodyMetadata
import dev.akif.tapik.gradle.metadata.HeaderMetadata
import dev.akif.tapik.gradle.metadata.HttpEndpointMetadata
import dev.akif.tapik.gradle.metadata.OutputBodyMetadata
import dev.akif.tapik.gradle.metadata.ParameterMetadata
import dev.akif.tapik.gradle.metadata.PathVariableMetadata
import dev.akif.tapik.gradle.metadata.QueryParameterMetadata
import dev.akif.tapik.gradle.metadata.TypeMetadata
import java.io.File

internal object SpringRestClientClientGenerator {
    private const val OUTPUT_PACKAGE = "dev.akif.tapik.spring.restclient"
    private const val OUTPUT_FILE_NAME = "GeneratedSpringRestClientClients.kt"
    private val KOTLIN_COLLECTION_OVERRIDES = mapOf(
        "java.util.Map" to "kotlin.collections.Map",
        "java.util.List" to "kotlin.collections.List",
        "java.util.Set" to "kotlin.collections.Set"
    )

    fun generate(endpoints: List<HttpEndpointMetadata>, rootDir: File) {
        if (endpoints.isEmpty()) return

        val directory = File(rootDir, OUTPUT_PACKAGE.replace('.', '/')).also { it.mkdirs() }
        val targetFile = File(directory, OUTPUT_FILE_NAME)

        val imports = buildImportSet(endpoints)
        val groupedEndpoints = endpoints
            .groupBy { it.packageName to it.sourceFile }
            .toSortedMap(compareBy<Pair<String, String>> { it.first }.thenBy { it.second })

        targetFile.writeText(buildString {
            appendLine("package $OUTPUT_PACKAGE")
            appendLine()
            imports.forEach { appendLine("import $it") }
            appendLine()

            groupedEndpoints.entries.forEachIndexed { index, (key, grouped) ->
                val (_, sourceFile) = key
                appendClientClass("${sourceFile}Client", grouped.sortedBy { it.id })
                if (index != groupedEndpoints.size - 1) {
                    appendLine()
                }
            }
        })
    }

    private fun buildImportSet(endpoints: List<HttpEndpointMetadata>): Set<String> {
        val baseImports = setOf(
            "arrow.core.getOrElse",
            "arrow.core.leftNel",
            "dev.akif.tapik.selections.*",
            "dev.akif.tapik.http.*",
            "java.net.URI",
            "org.springframework.web.client.RestClient"
        )

        val typeImports = endpoints
            .flatMap(HttpEndpointMetadata::imports)
            .map { KOTLIN_COLLECTION_OVERRIDES[it] ?: it }

        val endpointImports = endpoints
            .mapNotNull {
                when {
                    it.packageName.isBlank() -> null
                    else -> "${it.packageName}.${it.sourceFile}"
                }
            }

        return (baseImports + typeImports + endpointImports)
            .filterNot { it.startsWith(OUTPUT_PACKAGE) }
            .toSortedSet()
    }

    private fun StringBuilder.appendClientClass(
        className: String,
        endpoints: List<HttpEndpointMetadata>
    ) {
        appendLine("class $className(")
        appendLine("    private val restClient: RestClient")
        appendLine(") {")
        appendLine("    private val interpreter = RestClientInterpreter(restClient)")
        appendLine()

        val signatures = endpoints.map { EndpointSignature(it) }

        signatures.forEach {
            appendLine("    private val ${it.endpointPropertyName} = ${it.endpointRef}")
        }
        appendLine()

        signatures.forEachIndexed { index, signature ->
            appendEndpoint(signature)
            if (index != endpoints.lastIndex) {
                appendLine()
            }
        }

        appendLine("}")
    }

    private fun StringBuilder.appendEndpoint(signature: EndpointSignature) {
        val endpoint = signature.metadata

        appendLine("    // Generated from: ${endpoint.packageName}.${endpoint.sourceFile}#${endpoint.id}")
        append("    fun ${signature.methodName}(")
        if (signature.inputs.isNotEmpty()) {
            appendLine()
            appendLine(signature.inputs.joinToString(separator = ",\n") { "        $it" })
            append("    )")
        } else {
            append(")")
        }
        appendLine(": ${signature.returnType} {")
        appendLine("        val endpoint = ${signature.endpointPropertyName}")
        appendLine("        val responseEntity = interpreter.send(")
        appendLine("            method = endpoint.method,")
        appendLine("            uri = endpoint.buildURI(${signature.uriArguments}),")
        appendLine("            inputHeaders = ${signature.inputHeadersEncoding},")
        appendLine("            inputBodyContentType = endpoint.inputBody.mediaType,")
        appendLine("            inputBody = ${signature.encodeBodyCall},")
        appendLine("            outputBodies = endpoint.outputBodies.toList()")
        appendLine("        )")
        appendLine()
        appendLine("        val status = responseEntity.statusCode.toStatus()")

        if (signature.outputHeaderCount > 0) {
            appendLine()
            appendLine("        val headers = responseEntity.headers")
            appendLine("            .mapValues { entry -> entry.value.map { it.orEmpty() } }")
            appendLine()
            appendLine("        val ${signature.outputHeaderTupleName} = decodeHeaders${signature.outputHeaderCount}(")
            appendLine("            headers,")
            signature.outputHeaderAccessors.forEachIndexed { index, accessor ->
                val suffix = if (index == signature.outputHeaderAccessors.lastIndex) "" else ","
                appendLine("            $accessor$suffix")
            }
            appendLine("        ).getOrElse {")
            appendLine("            error(\"Cannot decode headers: \" + it.joinToString(\": \") )")
            appendLine("        }")
            signature.outputHeaderAssignments.forEach { appendLine("        $it") }
        }

        appendLine()
        appendLine(signature.responseConstruction)
        appendLine("    }")
    }

    private class EndpointSignature(
        endpoint: HttpEndpointMetadata
    ) {
        val metadata: HttpEndpointMetadata = endpoint
        private val endpointObject: String = endpoint.sourceFile
        private val endpointProperty: String = renderIdentifier(endpoint.propertyName)
        val endpointRef: String = "$endpointObject.$endpointProperty"
        val methodName: String = renderIdentifier(endpoint.id)
        val endpointPropertyName: String = renderIdentifier("${endpoint.id}Endpoint")
        private val endpointAccessor: String = "endpoint"

        private val parameters = ParameterGroup(endpoint.parameters, endpointAccessor, endpointPropertyName)
        private val inputHeaders = InputHeadersGroup(endpoint.inputHeaders, endpointAccessor, endpointPropertyName)
        private val inputBody = InputBodyGroup(endpoint.inputBody, endpointAccessor)
        private val outputHeaders = OutputHeadersGroup(endpoint.outputHeaders, endpointAccessor)
        private val outputBodies = OutputBodiesGroup(endpoint.outputBodies, endpointAccessor)

        val inputs: List<String> = buildList {
            addAll(parameters.requiredDeclarations)
            addAll(inputHeaders.requiredDeclarations)
            addAll(inputBody.inputs)
            addAll(parameters.optionalDeclarations)
            addAll(inputHeaders.optionalDeclarations)
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

    private class ParameterGroup(
        parameters: List<ParameterMetadata>,
        private val endpointAccessor: String,
        private val endpointRef: String
    ) {
        private data class Entry(
            val name: String,
            val type: String,
            val declaration: String,
            val hasDefault: Boolean
        )

        private val entries: List<Entry>

        val requiredDeclarations: List<String>
        val optionalDeclarations: List<String>
        val argumentList: String

        init {
            val usedNames = mutableSetOf<String>()
            entries = parameters.mapIndexed { index, parameter ->
                val fallback = "parameter${index + 1}"
                val baseName = when (parameter) {
                    is PathVariableMetadata -> sanitizeIdentifier(parameter.name, fallback)
                    is QueryParameterMetadata -> sanitizeIdentifier(parameter.name, fallback)
                }
                val name = uniqueName(baseName, usedNames)
                val type = when (parameter) {
                    is PathVariableMetadata -> parameter.type.render()
                    is QueryParameterMetadata -> parameter.type.render()
                }
                when (parameter) {
                    is PathVariableMetadata -> Entry(
                        name = name,
                        type = type,
                        declaration = "$name: $type",
                        hasDefault = false
                    )
                    is QueryParameterMetadata -> {
                        val hasDefault = parameter.default != null
                        val declaration = if (hasDefault) {
                            val errorName = parameter.name.ifBlank { fallback }
                            """$name: $type = requireNotNull(($endpointRef.parameters.item${index + 1} as QueryParameter<$type>).default) { "Default value missing for query parameter $errorName" }"""
                        } else {
                            "$name: $type"
                        }
                        Entry(
                            name = name,
                            type = type,
                            declaration = declaration,
                            hasDefault = hasDefault
                        )
                    }
                }
            }

            requiredDeclarations = entries.filterNot { it.hasDefault }.map { it.declaration }
            optionalDeclarations = entries.filter { it.hasDefault }.map { it.declaration }
            argumentList = entries.joinToString(", ") { it.name }
        }
    }

    private class InputHeadersGroup(
        headers: List<HeaderMetadata>,
        private val endpointAccessor: String,
        private val endpointRef: String
    ) {
        private data class Entry(
            val name: String,
            val type: String,
            val declaration: String,
            val hasDefault: Boolean
        )

        private val entries: List<Entry>

        val count: Int
        val requiredDeclarations: List<String>
        val optionalDeclarations: List<String>
        val argumentList: String
        val encodingCall: String
            get() = if (count == 0) {
                "emptyMap()"
            } else {
                val suffix = if (argumentList.isEmpty()) "()" else "($argumentList)"
                "${endpointAccessor}.encodeInputHeaders$suffix"
            }

        init {
            val usedNames = mutableSetOf<String>()
            entries = headers.mapIndexed { index, header ->
                val fallback = "inputHeader${index + 1}"
                val baseName = sanitizeIdentifier(header.name, fallback)
                val name = uniqueName(baseName, usedNames)
                val type = header.type.render()
                val hasDefault = !header.required
                val declaration =
                    if (hasDefault) {
                        "$name: $type = ($endpointRef.inputHeaders.item${index + 1} as HeaderValues<$type>).values.first()"
                    } else {
                        "$name: $type"
                    }
                Entry(
                    name = name,
                    type = type,
                    declaration = declaration,
                    hasDefault = hasDefault
                )
            }
            count = entries.size
            requiredDeclarations = entries.filterNot { it.hasDefault }.map { it.declaration }
            optionalDeclarations = entries.filter { it.hasDefault }.map { it.declaration }
            argumentList = entries.joinToString(", ") { it.name }
        }
    }

    private class OutputHeadersGroup(
        headers: List<HeaderMetadata>,
        endpointAccessor: String
    ) {
        data class HeaderEntry(
            val name: String,
            val type: String
        )

        private val entries: List<HeaderEntry>

        val count: Int
        val tupleName: String = "decodedOutputHeaders"
        val valueNames: List<String>
        val typeNames: List<String>
        val accessors: List<String>
        val assignments: List<String>

        init {
            val usedNames = mutableSetOf<String>()
            entries = headers.mapIndexed { index, header ->
                val fallback = "outputHeader${index + 1}"
                val baseName = sanitizeIdentifier(header.name, fallback)
                val name = uniqueName(baseName, usedNames)
                HeaderEntry(
                    name = name,
                    type = header.type.render()
                )
            }
            count = entries.size
            valueNames = entries.map { it.name }
            typeNames = entries.map { it.type }
            accessors = List(count) { index -> "${endpointAccessor}.outputHeaders.item${index + 1}" }
            assignments = valueNames.mapIndexed { index, name -> "val $name = $tupleName.item${index + 1}" }
        }
    }

    private class InputBodyGroup(
        body: BodyMetadata?,
        private val endpointAccessor: String
    ) {
        val inputs: List<String>
        val encodeCall: String

        init {
            val typeName = body?.type?.simpleName() ?: "EmptyBody"
            when (typeName) {
                "EmptyBody" -> {
                    inputs = emptyList()
                    encodeCall = "ByteArray(0)"
                }
                "RawBody" -> {
                    inputs = listOf("inputBody: ByteArray")
                    encodeCall = "inputBody"
                }
                "StringBody" -> {
                    inputs = listOf("inputBody: String")
                    encodeCall = "${endpointAccessor}.inputBody.codec.encode(inputBody)"
                }
                "JsonBody" -> {
                    val valueType = body?.type?.arguments?.firstOrNull()?.render() ?: "Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "${endpointAccessor}.inputBody.codec.encode(inputBody)"
                }
                else -> {
                    val valueType = body?.type?.arguments?.firstOrNull()?.render() ?: "Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "${endpointAccessor}.inputBody.codec.encode(inputBody)"
                }
            }
        }
    }

    private class OutputBodiesGroup(
        outputBodies: List<OutputBodyMetadata>,
        private val endpointAccessor: String
    ) {
        private val bodies: List<BodyEntry>

        init {
            bodies = outputBodies.mapIndexed { index, metadata ->
                BodyEntry(
                    metadata = metadata,
                    index = index + 1,
                    endpointAccessor = endpointAccessor
                )
            }
        }

        fun returnType(headerTypes: List<String>): String = when {
            bodies.isEmpty() && headerTypes.isEmpty() -> "ResponseWithoutBodyWithHeaders0"
            bodies.isEmpty() -> "ResponseWithoutBodyWithHeaders${headerTypes.size}<${headerTypes.joinToString(", ")}>"
            bodies.size == 1 && headerTypes.isEmpty() -> "ResponseWithHeaders0<${bodies.first().valueType}>"
            bodies.size == 1 -> "ResponseWithHeaders${headerTypes.size}<${bodies.first().valueType}, ${headerTypes.joinToString(", ")}>"
            else -> {
                val options = bodies.joinToString(", ") { body ->
                    if (headerTypes.isEmpty()) {
                        "ResponseWithHeaders0<${body.valueType}>"
                    } else {
                        "ResponseWithHeaders${headerTypes.size}<${body.valueType}, ${headerTypes.joinToString(", ")}>"
                    }
                }
                "Selection${bodies.size}<$options>"
            }
        }

        fun responseConstruction(headerValues: List<String>, headerTypes: List<String>): String = when {
            bodies.isEmpty() && headerValues.isEmpty() -> "        return ResponseWithoutBodyWithHeaders0(status)"
            bodies.isEmpty() -> buildString {
                appendLine("        return ResponseWithoutBodyWithHeaders${headerTypes.size}(")
                appendLine("            status,")
                headerValues.forEachIndexed { index, value ->
                    val suffix = if (index == headerValues.lastIndex) "" else ","
                    appendLine("            $value$suffix")
                }
                append("        )")
            }
            bodies.size == 1 -> buildString {
                appendLine("        val bodyBytes = responseEntity.body ?: ByteArray(0)")
                appendLine("        val decoded = ${bodies.first().decoder}")
                appendLine("            .map { decodedBody ->")
                if (headerValues.isEmpty()) {
                    appendLine("                ResponseWithHeaders0(status, decodedBody)")
                } else {
                    appendLine("                ResponseWithHeaders${headerTypes.size}(")
                    appendLine("                    status,")
                    appendLine("                    decodedBody,")
                    headerValues.forEachIndexed { index, value ->
                        val suffix = if (index == headerValues.lastIndex) "" else ","
                        appendLine("                    $value$suffix")
                    }
                    appendLine("                )")
                }
                appendLine("            }")
                appendLine("        return decoded.getOrElse { error(it.joinToString(\": \") ) }")
            }
            else -> buildString {
                appendLine("        val bodyBytes = responseEntity.body ?: ByteArray(0)")
                appendLine("        val response = when {")
                bodies.forEach { body ->
                    appendLine("            ${body.statusMatcher} -> ${body.decoder}.map { decodedBody ->")
                    if (headerValues.isEmpty()) {
                        appendLine("                Selection${bodies.size}.Option${body.index}(")
                        appendLine("                    ResponseWithHeaders0(status, decodedBody)")
                        appendLine("                )")
                    } else {
                        appendLine("                Selection${bodies.size}.Option${body.index}(")
                        appendLine("                    ResponseWithHeaders${headerTypes.size}(")
                        appendLine("                        status,")
                        appendLine("                        decodedBody,")
                        headerValues.forEachIndexed { index, value ->
                            val suffix = if (index == headerValues.lastIndex) "" else ","
                            appendLine("                        $value$suffix")
                        }
                        appendLine("                    )")
                        appendLine("                )")
                    }
                    appendLine("            }")
                }
                appendLine("            else -> \"Response output did not match\".leftNel()")
                appendLine("        }")
                appendLine()
                append("        return response.getOrElse { error(it.joinToString(\": \") ) }")
            }
        }

        private class BodyEntry(
            metadata: OutputBodyMetadata,
            val index: Int,
            endpointAccessor: String
        ) {
            val valueType: String = when (metadata.body.type.simpleName()) {
                "JsonBody" -> metadata.body.type.arguments.firstOrNull()?.render() ?: "Any"
                "StringBody" -> "String"
                "RawBody" -> "ByteArray"
                "EmptyBody" -> "Unit"
                else -> metadata.body.type.arguments.firstOrNull()?.render() ?: "Any"
            }

            val statusMatcher: String = "${endpointAccessor}.outputBodies.item$index.statusMatcher(status)"
            val decoder: String = "${endpointAccessor}.outputBodies.item$index.body.codec.decode(bodyBytes)"
        }
    }
}

private fun TypeMetadata.render(): String {
    val base = simpleName()
    val renderedArgs = arguments.joinToString(", ") { it.render() }
    val nullableSuffix = if (nullable == true) "?" else ""
    return if (renderedArgs.isEmpty()) {
        "$base$nullableSuffix"
    } else {
        "$base<$renderedArgs>$nullableSuffix"
    }
}

private fun TypeMetadata.simpleName(): String = name.substringAfterLast('.')

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
    val components = cleaned.split('_').mapNotNull { part ->
        part.takeIf { it.isNotBlank() }
    }
    val camel = if (components.isEmpty()) {
        fallback
    } else {
        components.mapIndexed { index, part ->
            val lower = part.lowercase()
            if (index == 0) lower else lower.replaceFirstChar { it.uppercaseChar() }
        }.joinToString("")
    }

    val base = camel.ifBlank { fallback }
    return base.replaceFirstChar { it.lowercaseChar() }
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
