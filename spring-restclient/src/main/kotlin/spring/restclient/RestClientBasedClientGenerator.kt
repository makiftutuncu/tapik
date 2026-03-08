package dev.akif.tapik.spring.restclient

import dev.akif.tapik.plugin.*
import dev.akif.tapik.plugin.metadata.*
import java.io.File

/**
 * A generator that creates Spring RestClient-based clients from tapik endpoint definitions.
 */
class RestClientBasedClientGenerator : TapikGenerator {
    /**
     * Identifier used by the Gradle plugin to decide whether this generator should execute.
     */
    override val id: String = ID

    /**
     * Generates RestClient-based clients for the supplied endpoints, writing them under the given output directory.
     *
     * @param endpoints endpoint metadata discovered during analysis.
     * @param context generator execution context containing output directories and logging callbacks.
     */
    override fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): TapikGenerationResult {
        if (endpoints.isEmpty()) {
            context.log("No endpoints discovered; skipping Spring RestClient generation.")
            return TapikGenerationResult()
        }

        context.log("Generating Spring RestClient clients.")
        return TapikGenerationResult(
            generateClients(
                endpoints = endpoints,
                rootDir = context.generatedSourcesDirectory,
                namePrefix = context.generatorConfiguration.namePrefix.orEmpty(),
                nameSuffix = context.generatorConfiguration.nameSuffix ?: DEFAULT_INTERFACE_SUFFIX
            )
        )
    }

    /**
     * Generates client code for the given endpoints and writes them to the given root directory.
     *
     * @param endpoints The endpoints to generate clients for.
     * @param rootDir The root directory to write the generated code to.
     */
    private fun generateClients(
        endpoints: List<HttpEndpointMetadata>,
        rootDir: File,
        namePrefix: String,
        nameSuffix: String
    ): Set<File> {
        val generatedFiles = linkedSetOf<File>()
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
                        val interfaceName = "$namePrefix$sourceFile$nameSuffix"
                        val targetFile = File(packageDirectory, "$interfaceName.kt")
                        targetFile.writeText(
                            buildInterfaceContent(
                                packageName = packageName,
                                interfaceName = interfaceName,
                                sourceFile = sourceFile,
                                signatures = signatures
                            )
                        )
                        generatedFiles += targetFile
                    }
            }
        return generatedFiles
    }

    private fun buildInterfaceContent(
        packageName: String,
        interfaceName: String,
        sourceFile: String,
        signatures: List<EndpointSignature>
    ): String =
        buildString {
            appendLine("package $packageName")
            appendLine()
            val extensionImports = buildExtensionImports(signatures)
            extensionImports.forEach { appendLine("import $it") }
            if (extensionImports.isNotEmpty()) {
                appendLine()
            }
            appendLine("// Generated from: $packageName.$sourceFile")
            appendLine("interface $interfaceName : $BASE_INTERFACE_NAME {")
            signatures.forEachIndexed { index, signature ->
                appendEndpoint(signature)
                appendLine()
                appendUriMethod(signature)
                if (index != signatures.lastIndex) {
                    appendLine()
                }
            }
            appendLine("}")
        }

    private fun buildExtensionImports(signatures: List<EndpointSignature>): List<String> =
        buildSet {
            add("$BASE_PACKAGE.toStatus")
            if (signatures.any(EndpointSignature::usesEncodeInputHeaders)) {
                add("$TAPIK_PACKAGE.encodeInputHeaders")
            }
        }.toList()

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
        val uriInvocation =
            if (signature.uriArguments.isEmpty()) {
                "${signature.uriMethodName}()"
            } else {
                "${signature.uriMethodName}(${signature.uriArguments})"
            }
        appendLine("        val responseEntity = interpreter.send(")
        appendLine("            method = $endpointExpr.method,")
        appendLine("            uri = $uriInvocation,")
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
            appendLine("        val bodyBytes = responseEntity.body ?: kotlin.ByteArray(0)")
        }

        appendLine()
        appendLine(signature.responseConstruction)
        appendLine("    }")
    }

    private fun StringBuilder.appendUriMethod(signature: EndpointSignature) {
        append("    fun ${signature.uriMethodName}(")
        if (signature.uriMethodParameters.isNotEmpty()) {
            appendLine()
            appendLine(signature.uriMethodParameters.joinToString(separator = ",\n") { "        $it" })
            appendLine("    ): java.net.URI =")
        } else {
            appendLine("): java.net.URI =")
        }

        if (signature.uriRenderArguments.isEmpty()) {
            appendLine("        dev.akif.tapik.renderURI(${signature.endpointExpression}.path)")
            return
        }

        appendLine("        dev.akif.tapik.renderURI(")
        appendLine("            ${signature.endpointExpression}.path,")
        appendLine(
            signature.uriRenderArguments.joinToString(separator = ",\n") { "            $it" }
        )
        appendLine("        )")
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

        val inputs: List<String> =
            buildList {
                addAll(parameters.requiredDeclarations)
                addAll(inputHeaders.requiredDeclarations)
                addAll(inputBody.inputs)
                addAll(parameters.optionalDeclarations)
                addAll(inputHeaders.optionalDeclarations)
            }

        val uriMethodName: String = "${methodName}ToURI"
        val uriMethodParameters: List<String> = parameters.uriMethodDeclarations
        val uriRenderArguments: List<String> = parameters.uriRenderArguments
        val uriArguments: String = parameters.argumentList
        val inputHeadersEncoding: String = inputHeaders.encodingCall
        val usesEncodeInputHeaders: Boolean = inputHeaders.usesEncodeInputHeaders
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
            val uriRenderArgument: String,
            val hasDefault: Boolean
        )

        private val entries: List<Entry>
        private val entriesInMethodOrder: List<Entry>

        val requiredDeclarations: List<String>
        val optionalDeclarations: List<String>
        val uriMethodDeclarations: List<String>
        val uriRenderArguments: List<String>
        val argumentList: String

        init {
            val usedNames = mutableSetOf<String>()
            entries =
                parameters.mapIndexed { index, parameter ->
                    val fallback = "parameter${index + 1}"
                    val baseName =
                        when (parameter) {
                            is PathVariableMetadata -> sanitizeIdentifier(parameter.name, fallback)
                            is QueryParameterMetadata -> sanitizeIdentifier(parameter.name, fallback)
                        }
                    val name = uniqueName(baseName, usedNames)
                    val type =
                        when (parameter) {
                            is PathVariableMetadata -> parameter.type.render()
                            is QueryParameterMetadata -> parameter.type.render()
                        }
                    when (parameter) {
                        is PathVariableMetadata -> {
                            val parameterAccessor = "$endpointRef.parameters.item${index + 1}"
                            Entry(
                                name = name,
                                type = type,
                                declaration = "$name: $type",
                                uriRenderArgument = "$parameterAccessor to $parameterAccessor.codec.encode($name)",
                                hasDefault = false
                            )
                        }

                        is QueryParameterMetadata -> {
                            val hasDefault = !parameter.required
                            val hasNonNullDefault = parameter.default.fold({ false }) { it != null }
                            val nonNullableType = parameter.type.copy(nullable = false).render()
                            val renderedType =
                                if (parameter.required || hasNonNullDefault) {
                                    nonNullableType
                                } else {
                                    parameter.type.copy(nullable = true).render()
                                }
                            val parameterAccessor = "$endpointRef.parameters.item${index + 1}"
                            val declaration =
                                if (hasDefault) {
                                    if (hasNonNullDefault) {
                                        "$name: $renderedType = $parameterAccessor.asQueryParameter<$nonNullableType>().getDefaultOrFail()"
                                    } else {
                                        "$name: $renderedType = $parameterAccessor.asQueryParameter<$nonNullableType>().default.getOrNull()"
                                    }
                                } else {
                                    "$name: $renderedType"
                                }
                            val encodedValueExpression =
                                if (parameter.required || hasNonNullDefault) {
                                    "$parameterAccessor.codec.encode($name)"
                                } else {
                                    "$name?.let { $parameterAccessor.codec.encode(it) }"
                                }
                            Entry(
                                name = name,
                                type = renderedType,
                                declaration = declaration,
                                uriRenderArgument = "$parameterAccessor to $encodedValueExpression",
                                hasDefault = hasDefault
                            )
                        }
                    }
                }

            requiredDeclarations = entries.filterNot { it.hasDefault }.map { it.declaration }
            optionalDeclarations = entries.filter { it.hasDefault }.map { it.declaration }
            entriesInMethodOrder = entries.filterNot { it.hasDefault } + entries.filter { it.hasDefault }
            uriMethodDeclarations = entriesInMethodOrder.map { it.declaration }
            uriRenderArguments = entriesInMethodOrder.map { it.uriRenderArgument }
            argumentList = entriesInMethodOrder.joinToString(", ") { it.name }
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
        val usesEncodeInputHeaders: Boolean
            get() = count > 0
        val encodingCall: String
            get() =
                if (count == 0) {
                    "kotlin.collections.emptyMap()"
                } else {
                    val arguments = argumentList.takeIf { it.isNotEmpty() } ?: ""
                    "$endpointExpression.input.encodeInputHeaders($arguments)"
                }

        init {
            val usedNames = mutableSetOf<String>()
            entries =
                headers.mapIndexed { index, header ->
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
                    encodeCall = "kotlin.ByteArray(0)"
                }
                "RawBody" -> {
                    inputs = listOf("inputBody: kotlin.ByteArray")
                    encodeCall = "inputBody"
                }
                "StringBody" -> {
                    inputs = listOf("inputBody: kotlin.String")
                    encodeCall = "$endpointExpression.input.body.codec.encode(inputBody)"
                }
                "JsonBody" -> {
                    val valueType =
                        body
                            ?.type
                            ?.arguments
                            ?.firstOrNull()
                            ?.render() ?: "kotlin.Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "$endpointExpression.input.body.codec.encode(inputBody)"
                }
                else -> {
                    val valueType =
                        body
                            ?.type
                            ?.arguments
                            ?.firstOrNull()
                            ?.render() ?: "kotlin.Any"
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
        private val options: List<OutputOption> =
            outputs.mapIndexed { index, metadata ->
                OutputOption(metadata = metadata, index = index + 1, endpointExpression = endpointExpression)
            }

        val requiresHeaders: Boolean = options.any(OutputOption::needsHeaders)
        val requiresBodyBytes: Boolean = options.any(OutputOption::needsBodyBytes)

        val returnType: String =
            when {
                options.isEmpty() -> "$TAPIK_PACKAGE.ResponseWithoutBody0"
                options.size == 1 -> options.first().responseType
                else -> {
                    val optionTypes = options.joinToString(", ") { it.responseType }
                    "$TAPIK_PACKAGE.OneOf${options.size}<$optionTypes>"
                }
            }

        val responseConstruction: String =
            when {
                options.isEmpty() -> "        return $TAPIK_PACKAGE.ResponseWithoutBody0(status)"
                options.size == 1 -> buildSingle(options.first())
                else -> buildMultiple()
            }

        private fun buildSingle(option: OutputOption): String =
            buildString {
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

        private fun buildMultiple(): String =
            buildString {
                appendLine("        val response = when {")
                options.forEach { option ->
                    appendLine("            ${option.statusCondition} -> {")
                    val innerBlocks =
                        buildList {
                            if (option.needsHeaders) add(option.buildHeaderDecodingBlock("                "))
                            if (option.needsBodyBytes) add(option.buildBodyDecodingBlock("                "))
                            add("                ${option.responseExpression(true)}")
                        }.filter { it.isNotBlank() }
                    appendLine(innerBlocks.joinToString(separator = "\n\n"))
                    appendLine("            }")
                }
                appendLine("            else -> kotlin.error(\"Response output did not match\")")
                appendLine("        }")
                appendLine()
                append("        return response")
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
                headerVarNames =
                    metadata.headers.mapIndexed { idx, header ->
                        uniqueName(sanitizeIdentifier(header.name, "output${index}Header${idx + 1}"), usedNames)
                    }
                responseType = computeResponseType()
            }

            private fun computeResponseType(): String {
                val renderedHeaderTypes = headerTypes.joinToString(", ")
                return if (hasBody) {
                    if (headerTypes.isEmpty()) {
                        "$TAPIK_PACKAGE.Response0<$bodyType>"
                    } else {
                        "$TAPIK_PACKAGE.Response${headerTypes.size}<$bodyType, $renderedHeaderTypes>"
                    }
                } else {
                    if (headerTypes.isEmpty()) {
                        "$TAPIK_PACKAGE.ResponseWithoutBody0"
                    } else {
                        "$TAPIK_PACKAGE.ResponseWithoutBody${headerTypes.size}<$renderedHeaderTypes>"
                    }
                }
            }

            private fun responseInstance(): String {
                val headerArgs = if (headerVarNames.isEmpty()) "" else headerVarNames.joinToString(prefix = ", ")
                return if (hasBody) {
                    if (headerVarNames.isEmpty()) {
                        "$TAPIK_PACKAGE.Response0(status, decodedBody)"
                    } else {
                        "$TAPIK_PACKAGE.Response${headerVarNames.size}(status, decodedBody$headerArgs)"
                    }
                } else {
                    if (headerVarNames.isEmpty()) {
                        "$TAPIK_PACKAGE.ResponseWithoutBody0(status)"
                    } else {
                        "$TAPIK_PACKAGE.ResponseWithoutBody${headerVarNames.size}(status$headerArgs)"
                    }
                }
            }

            fun buildHeaderDecodingBlock(indent: String): String {
                if (!needsHeaders) {
                    return ""
                }
                return buildString {
                    appendLine(
                        "${indent}val ${headerParametersName}Result = dev.akif.tapik.decodeHeaders${headerVarNames.size}("
                    )
                    appendLine("$indent    headers,")
                    headerAccessors.forEachIndexed { idx, accessor ->
                        val suffix = if (idx == headerAccessors.lastIndex) "" else ","
                        appendLine("$indent    $accessor$suffix")
                    }
                    appendLine("$indent)")
                    appendLine(
                        "${indent}val $headerParametersName = when (val either = ${headerParametersName}Result) {"
                    )
                    appendLine(
                        "$indent    is arrow.core.Either.Left -> kotlin.error(\"Cannot decode headers: \" + either.value.joinToString(\": \"))"
                    )
                    appendLine("$indent    is arrow.core.Either.Right -> either.value")
                    appendLine("$indent}")
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
                    appendLine("${indent}val decodedBodyResult = $bodyDecoderExpr")
                    appendLine("${indent}val decodedBody = when (val either = decodedBodyResult) {")
                    appendLine(
                        "$indent    is arrow.core.Either.Left -> kotlin.error(either.value.joinToString(\": \"))"
                    )
                    appendLine("$indent    is arrow.core.Either.Right -> either.value")
                    appendLine("$indent}")
                }.trimEnd()
            }

            fun responseExpression(wrapInOneOf: Boolean): String {
                val instance = responseInstance()
                return if (wrapInOneOf) {
                    "$TAPIK_PACKAGE.OneOf${options.size}.Option$index($instance)"
                } else {
                    instance
                }
            }
        }
    }

    private companion object {
        private const val ID = "spring-restclient"
        private const val TAPIK_PACKAGE = "dev.akif.tapik"
        private const val BASE_PACKAGE = "$TAPIK_PACKAGE.spring.restclient"
        private const val BASE_INTERFACE_NAME = "$BASE_PACKAGE.RestClientBasedClient"
        private const val DEFAULT_INTERFACE_SUFFIX = "Client"
    }
}
