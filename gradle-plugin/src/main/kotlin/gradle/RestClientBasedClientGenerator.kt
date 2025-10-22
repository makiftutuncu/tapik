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

internal object RestClientBasedClientGenerator {
    private const val TAPIK_PACKAGE = "dev.akif.tapik"
    private const val BASE_PACKAGE = "$TAPIK_PACKAGE.spring.restclient"
    private const val BASE_INTERFACE_NAME = "RestClientBasedClient"
    private const val HTTP_PACKAGE_PREFIX = "$TAPIK_PACKAGE."
    private val KOTLIN_COLLECTION_OVERRIDES = mapOf(
        "java.util.Map" to "kotlin.collections.Map",
        "java.util.List" to "kotlin.collections.List",
        "java.util.Set" to "kotlin.collections.Set"
    )

    fun generate(endpoints: List<HttpEndpointMetadata>, rootDir: File) {
        if (endpoints.isEmpty()) return

        endpoints
            .groupBy { it.packageName }
            .filterKeys { it.isNotBlank() }
            .toSortedMap()
            .forEach { (packageName, packageEndpoints) ->
                val packageDirectory = File(rootDir, packageName.replace('.', '/')).also { it.mkdirs() }
                packageEndpoints
                    .groupBy { it.sourceFile }
                    .toSortedMap()
                    .forEach { (sourceFile, groupedEndpoints) ->
                        val sortedEndpoints = groupedEndpoints.sortedBy { it.id }
                        val signatures = sortedEndpoints.map(::EndpointSignature)
                        val imports = buildImportsFor(packageName, sortedEndpoints)
                        val interfaceName = "${sourceFile}Client"
                        val targetFile = File(packageDirectory, "$interfaceName.kt")
                        targetFile.writeText(
                            buildInterfaceContent(
                                packageName = packageName,
                                interfaceName = interfaceName,
                                sourceFile = sourceFile,
                                imports = imports,
                                signatures = signatures
                            )
                        )
                    }
            }
    }

    private fun buildInterfaceContent(
        packageName: String,
        interfaceName: String,
        sourceFile: String,
        imports: List<String>,
        signatures: List<EndpointSignature>
    ): String =
        buildString {
            appendLine("package $packageName")
            appendLine()
            imports.forEach { appendLine("import $it") }
            if (imports.isNotEmpty()) {
                appendLine()
            }
            appendLine("// Generated from: $packageName.$sourceFile")
            appendLine("interface $interfaceName : $BASE_INTERFACE_NAME {")
            signatures.forEachIndexed { index, signature ->
                appendEndpoint(signature)
                if (index != signatures.lastIndex) {
                    appendLine()
                }
            }
            appendLine("}")
        }

    private fun buildImportsFor(
        packageName: String,
        endpoints: List<HttpEndpointMetadata>
    ): List<String> {
        val imports = mutableSetOf(
            "arrow.core.getOrElse",
            "arrow.core.leftNel",
            "dev.akif.tapik.*",
            "$BASE_PACKAGE.*"
        )

        val typeImports = endpoints
            .flatMap(HttpEndpointMetadata::imports)
            .map { KOTLIN_COLLECTION_OVERRIDES[it] ?: it }
            .filterNot { import ->
                import.startsWith(HTTP_PACKAGE_PREFIX) ||
                    import.startsWith("$BASE_PACKAGE.") ||
                    import == BASE_PACKAGE ||
                    import == TAPIK_PACKAGE ||
                    import.substringBeforeLast('.', "") == packageName
            }

        imports += typeImports.filterNot {
            it.startsWith("kotlin.collections.")
        }

        return imports.toSortedSet().toList()
    }

    private fun StringBuilder.appendEndpoint(signature: EndpointSignature) {
        val hasSummary = signature.summaryLines.isNotEmpty()
        val hasDetails = signature.detailLines.isNotEmpty()
        if (hasSummary || hasDetails) {
            appendLine("    /**")
            signature.summaryLines.forEach { appendLine("     * ${it.escapeForKdoc()}") }
            if (hasSummary && hasDetails) {
                appendLine("     *")
            }
            signature.detailLines.forEach { appendLine("     * ${it.escapeForKdoc()}") }
            appendLine("     */")
        }
        append("    fun ${signature.methodName}(")
        if (signature.inputs.isNotEmpty()) {
            appendLine()
            appendLine(signature.inputs.joinToString(separator = ",\n") { "        $it" })
            append("    )")
        } else {
            append(")")
        }
        appendLine(": ${signature.returnType} {")
        val endpointExpr = signature.endpointExpression
        appendLine("        val responseEntity = interpreter.send(")
        appendLine("            method = $endpointExpr.method,")
        appendLine("            uri = $endpointExpr.uriWithParameters.toURI(${signature.uriArguments}),")
        appendLine("            inputHeaders = ${signature.inputHeadersEncoding},")
        appendLine("            inputBodyContentType = $endpointExpr.inputBody.mediaType,")
        appendLine("            inputBody = ${signature.encodeBodyCall},")
        appendLine("            outputBodies = $endpointExpr.outputBodies.toList()")
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
        private val endpointObject: String = endpoint.sourceFile
        private val endpointProperty: String = renderIdentifier(endpoint.propertyName)
        val endpointExpression: String =
            if (endpointObject.endsWith("Kt")) {
                endpointProperty
            } else {
                "$endpointObject.$endpointProperty"
            }
        val methodName: String = renderIdentifier(endpoint.id)

        val summaryLines: List<String> = linesForDocumentation(endpoint.description)
        val detailLines: List<String> = linesForDocumentation(endpoint.details)

        private val parameters = ParameterGroup(endpoint.parameters, endpointExpression)
        private val inputHeaders = InputHeadersGroup(endpoint.inputHeaders, endpointExpression)
        private val inputBody = InputBodyGroup(endpoint.inputBody, endpointExpression)
        private val outputHeaders = OutputHeadersGroup(endpoint.outputHeaders, endpointExpression)
        private val outputBodies = OutputBodiesGroup(endpoint.outputBodies, endpointExpression)

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
                        val parameterAccessor = "$endpointRef.uriWithParameters.parameters.item${index + 1}"
                        val declaration = if (hasDefault) {
                            "$name: $type = $parameterAccessor.asQueryParameter<$type>().getDefaultOrFail()"
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
        private val endpointExpression: String
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
        private val argumentList: String
        val encodingCall: String
            get() = if (count == 0) {
                "emptyMap()"
            } else {
                val suffix = if (argumentList.isEmpty()) "()" else "($argumentList)"
                "$endpointExpression.encodeInputHeaders$suffix"
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
                        "$name: $type = $endpointExpression.inputHeaders.item${index + 1}.asHeaderValues<$type>().getFirstValueOrFail()"
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

    private class InputBodyGroup(
        body: BodyMetadata?,
        private val endpointExpression: String
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
                    encodeCall = "$endpointExpression.inputBody.codec.encode(inputBody)"
                }
                "JsonBody" -> {
                    val valueType = body?.type?.arguments?.firstOrNull()?.render() ?: "Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "$endpointExpression.inputBody.codec.encode(inputBody)"
                }
                else -> {
                    val valueType = body?.type?.arguments?.firstOrNull()?.render() ?: "Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "$endpointExpression.inputBody.codec.encode(inputBody)"
                }
            }
        }
    }

    private class OutputHeadersGroup(
        headers: List<HeaderMetadata>,
        endpointExpression: String
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
            accessors = List(count) { index -> "$endpointExpression.outputHeaders.item${index + 1}" }
            assignments = valueNames.mapIndexed { index, name -> "val $name = $tupleName.item${index + 1}" }
        }
    }

    private class OutputBodiesGroup(
        outputBodies: List<OutputBodyMetadata>,
        private val endpointExpression: String
    ) {
        private val bodies: List<BodyEntry> = outputBodies.mapIndexed { index, metadata ->
            BodyEntry(
                metadata = metadata,
                index = index + 1,
                endpointExpression = endpointExpression
            )
        }

        fun returnType(headerTypes: List<String>): String = when {
            bodies.isEmpty() && headerTypes.isEmpty() -> "ResponseWithoutBody0"
            bodies.isEmpty() -> "ResponseWithoutBody${headerTypes.size}<${headerTypes.joinToString(", ")}>"
            bodies.size == 1 && headerTypes.isEmpty() -> "Response0<${bodies.first().valueType}>"
            bodies.size == 1 -> "Response${headerTypes.size}<${bodies.first().valueType}, ${headerTypes.joinToString(", ")}>"
            else -> {
                val options = bodies.joinToString(", ") { body ->
                    if (headerTypes.isEmpty()) {
                        "Response0<${body.valueType}>"
                    } else {
                        "Response${headerTypes.size}<${body.valueType}, ${headerTypes.joinToString(", ")}>"
                    }
                }
                "OneOf${bodies.size}<$options>"
            }
        }

        fun responseConstruction(headerValues: List<String>, headerTypes: List<String>): String = when {
            bodies.isEmpty() && headerValues.isEmpty() -> "        return ResponseWithoutBody0(status)"
            bodies.isEmpty() -> buildString {
                appendLine("        return ResponseWithoutBody${headerTypes.size}(")
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
                    appendLine("                Response0(status, decodedBody)")
                } else {
                    appendLine("                Response${headerTypes.size}(")
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
                        appendLine("                OneOf${bodies.size}.Option${body.index}(")
                        appendLine("                    Response0(status, decodedBody)")
                        appendLine("                )")
                    } else {
                        appendLine("                OneOf${bodies.size}.Option${body.index}(")
                        appendLine("                    Response${headerTypes.size}(")
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
            endpointExpression: String
        ) {
            val valueType: String = when (metadata.body.type.simpleName()) {
                "JsonBody" -> metadata.body.type.arguments.firstOrNull()?.render() ?: "Any"
                "StringBody" -> "String"
                "RawBody" -> "ByteArray"
                "EmptyBody" -> "Unit"
                else -> metadata.body.type.arguments.firstOrNull()?.render() ?: "Any"
            }

            val statusMatcher: String = "$endpointExpression.outputBodies.item$index.statusMatcher(status)"
            val decoder: String = "$endpointExpression.outputBodies.item$index.body.codec.decode(bodyBytes)"
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
    var cleaned = buildString {
        for (ch in source) {
            when {
                ch.isLetterOrDigit() -> append(ch)
                ch == '_' -> append('_')
                else -> append('_')
            }
        }
    }.replace(Regex("_+"), "_").trim('_')

    if (cleaned.isEmpty()) {
        cleaned = fallback
    }

    if (cleaned.firstOrNull()?.let { it.isLetter() || it == '_' } != true) {
        cleaned = "_$cleaned"
    }

    return cleaned
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

private fun linesForDocumentation(text: String?): List<String> =
    text
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.lines()
        ?.map { it.trim() }
        ?: emptyList()

private fun String.escapeForKdoc(): String = replace("*/", "* /")

private val KOTLIN_KEYWORDS = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
    "var", "when", "while"
)
