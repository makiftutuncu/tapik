package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.*
import java.io.File

/**
 * A generator that creates Spring RestClient-based clients from tapik endpoint definitions.
 */
object RestClientBasedClientGenerator {
    private const val TAPIK_PACKAGE = "dev.akif.tapik"
    private const val BASE_PACKAGE = "$TAPIK_PACKAGE.spring.restclient"
    private const val BASE_INTERFACE_NAME = "RestClientBasedClient"
    private const val HTTP_PACKAGE_PREFIX = "$TAPIK_PACKAGE."
    private val KOTLIN_COLLECTION_OVERRIDES = mapOf(
        "java.util.Map" to "kotlin.collections.Map",
        "java.util.List" to "kotlin.collections.List",
        "java.util.Set" to "kotlin.collections.Set"
    )

    /**
     * Generates client code for the given endpoints and writes them to the given root directory.
     *
     * @param endpoints The endpoints to generate clients for.
     * @param rootDir The root directory to write the generated code to.
     */
    fun generate(endpoints: List<HttpEndpointMetadata>, rootDir: File) {
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
        appendLine("            uri = $endpointExpr.toURI(${signature.uriArguments}),")
        appendLine("            inputHeaders = ${signature.inputHeadersEncoding},")
        appendLine("            inputBodyContentType = $endpointExpr.input.body.mediaType,")
        appendLine("            inputBody = ${signature.encodeBodyCall},")
        appendLine("            outputs = $endpointExpr.outputs.toList()")
        appendLine("        )")
        appendLine()
        appendLine("        val status = responseEntity.statusCode.toStatus()")

        if (signature.needsHeadersMap) {
            appendLine()
            appendLine("        val headers = responseEntity.headers")
            appendLine("            .mapValues { entry -> entry.value.map { it.orEmpty() } }")
        }

        if (signature.needsBodyBytes) {
            appendLine()
            appendLine("        val bodyBytes = responseEntity.body ?: ByteArray(0)")
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
        private val inputHeaders = InputHeadersGroup(endpoint.input.headers, endpointExpression)
        private val inputBody = InputBodyGroup(endpoint.input.body, endpointExpression)
        private val outputs = OutputsGroup(endpoint.outputs, endpointExpression)

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
        val needsHeadersMap: Boolean = outputs.requiresHeaders
        val needsBodyBytes: Boolean = outputs.requiresBodyBytes
        val returnType: String = outputs.returnType
        val responseConstruction: String = outputs.responseConstruction
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
                        val parameterAccessor = "$endpointRef.parameters.item${index + 1}"
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
                "$endpointExpression.input.encodeInputHeaders$suffix"
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
                        "$name: $type = $endpointExpression.input.headers.item${index + 1}.asHeaderValues<$type>().getFirstValueOrFail()"
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
                    encodeCall = "$endpointExpression.input.body.codec.encode(inputBody)"
                }
                "JsonBody" -> {
                    val valueType = body?.type?.arguments?.firstOrNull()?.render() ?: "Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "$endpointExpression.input.body.codec.encode(inputBody)"
                }
                else -> {
                    val valueType = body?.type?.arguments?.firstOrNull()?.render() ?: "Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "$endpointExpression.input.body.codec.encode(inputBody)"
                }
            }
        }
    }

    private class OutputsGroup(
        outputs: List<OutputMetadata>,
        private val endpointExpression: String
    ) {
        private val options: List<OutputOption> = outputs.mapIndexed { index, metadata ->
            OutputOption(metadata = metadata, index = index + 1, endpointExpression = endpointExpression)
        }

        val requiresHeaders: Boolean = options.any(OutputOption::needsHeaders)
        val requiresBodyBytes: Boolean = options.any(OutputOption::needsBodyBytes)

        val returnType: String = when {
            options.isEmpty() -> "ResponseWithoutBody0"
            options.size == 1 -> options.first().responseType
            else -> {
                val optionTypes = options.joinToString(", ") { it.responseType }
                "OneOf${options.size}<$optionTypes>"
            }
        }

        val responseConstruction: String = when {
            options.isEmpty() -> "        return ResponseWithoutBody0(status)"
            options.size == 1 -> buildSingle(options.first())
            else -> buildMultiple()
        }

        private fun buildSingle(option: OutputOption): String {
            return buildString {
                if (option.needsHeaders) {
                    appendLine(option.buildHeaderDecodingBlock("        "))
                    if (option.needsBodyBytes) {
                        appendLine()
                    }
                }
                if (option.needsBodyBytes) {
                    appendLine(option.buildBodyDecodingBlock("        "))
                    appendLine()
                }
                append("        return ${option.responseExpression(false)}")
            }
        }

        private fun buildMultiple(): String {
            return buildString {
                appendLine("        val response = when {")
                options.forEach { option ->
                    appendLine("            ${option.statusCondition} -> {")
                    val innerBlocks = buildList {
                        if (option.needsHeaders) add(option.buildHeaderDecodingBlock("                "))
                        if (option.needsBodyBytes) add(option.buildBodyDecodingBlock("                "))
                        add("                ${option.responseExpression(true)}")
                    }.filter { it.isNotBlank() }
                    appendLine(innerBlocks.joinToString(separator = "\n\n"))
                    appendLine("            }")
                }
                appendLine("            else -> error(\"Response output did not match\")")
                appendLine("        }")
                appendLine()
                append("        return response")
            }
        }

        private inner class OutputOption(
            private val metadata: OutputMetadata,
            val index: Int,
            private val endpointExpression: String
        ) {
            val needsHeaders: Boolean = metadata.headers.isNotEmpty()
            val needsBodyBytes: Boolean = metadata.body.type.simpleName() != "EmptyBody"

            private val headerTypes: List<String> = metadata.headers.map { it.type.render() }
            private val headerVarNames: List<String>
            private val headerAccessors: List<String> =
                List(metadata.headers.size) { idx -> "$endpointExpression.outputs.item$index.headers.item${idx + 1}" }
            private val headerParametersName: String = "decodedOutput${index}Headers"
            val statusCondition: String = "$endpointExpression.outputs.item$index.statusMatcher(status)"

            private val bodyType: String = metadata.body.type.determineBodyValueType()
            private val hasBody: Boolean = needsBodyBytes
            private val bodyDecoderExpr: String = "$endpointExpression.outputs.item$index.body.codec.decode(bodyBytes)"

            val responseType: String

            init {
                val usedNames = mutableSetOf<String>()
                headerVarNames = metadata.headers.mapIndexed { idx, header ->
                    uniqueName(sanitizeIdentifier(header.name, "output${index}Header${idx + 1}"), usedNames)
                }
                responseType = computeResponseType()
            }

            private fun computeResponseType(): String =
                if (hasBody) {
                    if (headerTypes.isEmpty()) {
                        "Response0<$bodyType>"
                    } else {
                        "Response${headerTypes.size}<$bodyType, ${headerTypes.joinToString(", ")}>"
                    }
                } else {
                    if (headerTypes.isEmpty()) {
                        "ResponseWithoutBody0"
                    } else {
                        "ResponseWithoutBody${headerTypes.size}<${headerTypes.joinToString(", ")}>"
                    }
                }

            private fun responseInstance(): String {
                val headerArgs = if (headerVarNames.isEmpty()) "" else headerVarNames.joinToString(prefix = ", ")
                return if (hasBody) {
                    if (headerVarNames.isEmpty()) {
                        "Response0(status, decodedBody)"
                    } else {
                        "Response${headerVarNames.size}(status, decodedBody$headerArgs)"
                    }
                } else {
                    if (headerVarNames.isEmpty()) {
                        "ResponseWithoutBody0(status)"
                    } else {
                        "ResponseWithoutBody${headerVarNames.size}(status$headerArgs)"
                    }
                }
            }

            fun buildHeaderDecodingBlock(indent: String): String {
                if (!needsHeaders) {
                    return ""
                }
                return buildString {
                    appendLine("${indent}val $headerParametersName = decodeHeaders${headerVarNames.size}(")
                    appendLine("${indent}    headers${if (headerAccessors.isEmpty()) "" else ","}")
                    headerAccessors.forEachIndexed { idx, accessor ->
                        val suffix = if (idx == headerAccessors.lastIndex) "" else ","
                        appendLine("${indent}    $accessor$suffix")
                    }
                    appendLine("${indent}).getOrElse {")
                    appendLine("${indent}    error(\"Cannot decode headers: \" + it.joinToString(\": \") )")
                    appendLine("${indent}}")
                    headerVarNames.forEachIndexed { idx, name ->
                        appendLine("${indent}val $name = $headerParametersName.item${idx + 1}.values")
                    }
                }.trimEnd()
            }

            fun buildBodyDecodingBlock(indent: String): String {
                if (!hasBody) {
                    return ""
                }
                return buildString {
                    appendLine("${indent}val decodedBody = $bodyDecoderExpr")
                    appendLine("${indent}    .getOrElse { error(it.joinToString(\": \") ) }")
                }.trimEnd()
            }

            fun responseExpression(wrapInOneOf: Boolean): String {
                val instance = responseInstance()
                return if (wrapInOneOf) {
                    "OneOf${options.size}.Option$index($instance)"
                } else {
                    instance
                }
            }
        }
    }

}
