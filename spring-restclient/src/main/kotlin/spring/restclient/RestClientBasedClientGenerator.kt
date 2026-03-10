package dev.akif.tapik.spring.restclient

import dev.akif.tapik.plugin.*
import dev.akif.tapik.plugin.metadata.*
import java.io.File

/**
 * A generator that creates Spring RestClient-based clients from tapik endpoint definitions.
 */
class RestClientBasedClientGenerator : TapikKotlinEndpointGenerator {
    /**
     * Identifier used by the Gradle plugin to decide whether this generator should execute.
     */
    override val id: String = ID

    /**
     * Builds Spring RestClient nested client contributions for the discovered endpoints.
     *
     * @param endpoints endpoint metadata to translate into client members.
     * @param context generation context carrying output directories and configuration.
     * @return Kotlin contributions grouped by source file.
     */
    override fun contribute(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): TapikKotlinContribution {
        if (endpoints.isEmpty()) {
            context.log("No endpoints discovered; skipping Spring RestClient client contributions.")
            return TapikKotlinContribution()
        }

        context.log("Generating Spring RestClient client contributions.")
        return TapikKotlinContribution(
            sourceFiles =
                buildSourceFileContributions(
                    endpoints = endpoints,
                    clientSuffix = context.generatorConfiguration.clientSuffix,
                    endpointsSuffix = context.endpointsSuffix,
                    generatedPackageName = context.generatedPackageName
                )
        )
    }

    /**
     * Generates standalone Kotlin source files for tests or direct invocation.
     *
     * @param endpoints endpoint metadata to translate into generated files.
     * @param context generation context carrying output directories and configuration.
     * @return paths of generated Kotlin source files.
     */
    override fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): TapikGenerationResult {
        val contribution = contribute(endpoints, context)
        val generatedFiles = linkedSetOf<File>()
        contribution.sourceFiles.forEach { sourceFile ->
            val packageDirectory =
                File(context.generatedSourcesDirectory, sourceFile.packageName.replace('.', '/')).also { it.mkdirs() }
            val targetFile = File(packageDirectory, "${sourceFile.aggregateInterfaceName}.kt")
            val endpointMembers =
                sourceFile.endpointMembers.groupBy(
                    keySelector = { it.endpointPropertyName },
                    valueTransform = { it.content }
                )
            val fileEndpoints =
                endpoints.filter {
                    it.packageName == sourceFile.sourcePackageName && it.sourceFile == sourceFile.sourceFile
                }
            targetFile.writeText(
                renderMergedKotlinEndpointFile(
                    packageName = sourceFile.packageName,
                    sourceFile = sourceFile.sourceFile,
                    endpointsSuffix = context.endpointsSuffix,
                    endpoints = fileEndpoints,
                    aggregateInterfaces =
                        listOf(
                            AggregateInterfaceContribution(
                                name = sourceFile.aggregateInterfaceName,
                                nestedInterfaceName = sourceFile.nestedInterfaceName
                            )
                        ),
                    endpointMembers = endpointMembers,
                    imports = sourceFile.imports
                )
            )
            generatedFiles += targetFile
        }
        return TapikGenerationResult(generatedFiles)
    }

    private fun buildSourceFileContributions(
        endpoints: List<HttpEndpointMetadata>,
        clientSuffix: String,
        endpointsSuffix: String,
        generatedPackageName: String
    ): List<KotlinSourceFileContribution> {
        val contributions = mutableListOf<KotlinSourceFileContribution>()
        endpoints
            .groupBy { it.packageName }
            .filterKeys { it.isNotBlank() }
            .toSortedMap()
            .forEach { (packageName, packageEndpoints) ->
                packageEndpoints
                    .groupBy { it.sourceFile }
                    .toSortedMap()
                    .forEach { (sourceFile, groupedEndpoints) ->
                        val sortedEndpoints = groupedEndpoints.sortedBy { it.id }
                        val signatures = createEndpointSignatures(sortedEndpoints, sourceFile, endpointsSuffix)
                        contributions +=
                            KotlinSourceFileContribution(
                                packageName = packageName.toGeneratedPackageName(generatedPackageName),
                                sourcePackageName = packageName,
                                sourceFile = sourceFile,
                                aggregateInterfaceName = "$sourceFile$clientSuffix",
                                nestedInterfaceName = clientSuffix,
                                imports = buildExtensionImports(signatures).toSet(),
                                endpointMembers =
                                    signatures.map { signature ->
                                        KotlinEndpointMemberContribution(
                                            endpointPropertyName = signature.endpoint.propertyName,
                                            content = buildNestedClientContent(signature, clientSuffix)
                                        )
                                    }
                            )
                    }
            }
        return contributions
    }

    private fun createEndpointSignatures(
        endpoints: List<HttpEndpointMetadata>,
        sourceFile: String,
        endpointsSuffix: String = "Endpoints"
    ): List<EndpointSignature> {
        val modelsByPropertyName =
            buildEndpointContractModels(endpoints, sourceFile, endpointsSuffix).associateBy { it.endpoint.propertyName }
        return endpoints.map { endpoint ->
            EndpointSignature(
                endpoint = endpoint,
                contractModel = checkNotNull(modelsByPropertyName[endpoint.propertyName])
            )
        }
    }

    private fun buildExtensionImports(signatures: List<EndpointSignature>): List<String> =
        buildSet {
            add("$BASE_PACKAGE.toStatus")
            if (signatures.any(EndpointSignature::usesEncodeInputHeaders)) {
                add("$TAPIK_PACKAGE.encodeInputHeaders")
            }
        }.toList()

    private fun buildNestedClientContent(
        signature: EndpointSignature,
        nestedInterfaceName: String
    ): String =
        buildString {
            appendLine("interface $nestedInterfaceName : $BASE_INTERFACE_NAME {")
            appendEndpointMethod(signature, indentation = "    ")
            appendLine("}")
        }

    private fun StringBuilder.appendEndpointMethod(
        signature: EndpointSignature,
        indentation: String
    ) {
        val hasSummary = signature.summaryLines.isNotEmpty()
        val hasDetails = signature.detailLines.isNotEmpty()
        if (hasSummary || hasDetails) {
            appendLine("$indentation/**")
            signature.summaryLines.forEach { appendLine("$indentation * ${it.escapeForKdoc()}") }
            if (hasSummary && hasDetails) {
                appendLine("$indentation *")
            }
            signature.detailLines.forEach { appendLine("$indentation * ${it.escapeForKdoc()}") }
            appendLine("$indentation */")
        }
        append("${indentation}fun ${signature.methodName}(")
        if (signature.inputs.isNotEmpty()) {
            appendLine()
            appendLine(signature.inputs.joinToString(separator = ",\n") { "$indentation    $it" })
            append("$indentation)")
        } else {
            append(")")
        }
        appendLine(": ${signature.returnType} {")
        val endpointExpr = signature.endpointExpression
        appendLine("$indentation    val responseEntity = interpreter.send(")
        appendLine("$indentation        method = $endpointExpr.method,")
        appendInlineUriRendering(signature, indentation = "$indentation        ")
        appendLine("$indentation        inputHeaders = ${signature.inputHeadersEncoding},")
        appendLine("$indentation        inputBodyContentType = $endpointExpr.input.body.mediaType,")
        appendLine("$indentation        inputBody = ${signature.encodeBodyCall},")
        appendLine("$indentation        outputs = $endpointExpr.outputs.toList()")
        appendLine("$indentation    )")
        appendLine()
        appendLine("$indentation    val status = responseEntity.statusCode.toStatus()")

        if (signature.needsHeadersMap) {
            appendLine()
            appendLine("$indentation    val headers = responseEntity.headers")
            appendLine("$indentation        .mapValues { entry -> entry.value.map { it.orEmpty() } }")
        }

        if (signature.needsBodyBytes) {
            appendLine()
            appendLine("$indentation    val bodyBytes = responseEntity.body ?: kotlin.ByteArray(0)")
        }

        appendLine()
        appendLine(signature.responseConstruction)
        appendLine("$indentation}")
    }

    private fun StringBuilder.appendInlineUriRendering(
        signature: EndpointSignature,
        indentation: String
    ) {
        if (signature.uriRenderArguments.isEmpty()) {
            appendLine(
                "$indentation" + "uri = dev.akif.tapik.renderURI(${signature.endpointExpression}.path),"
            )
            return
        }

        appendLine("$indentation" + "uri = dev.akif.tapik.renderURI(")
        appendLine("$indentation    ${signature.endpointExpression}.path,")
        appendLine(
            signature.uriRenderArguments.joinToString(separator = ",\n") { "$indentation    $it" }
        )
        appendLine("$indentation),")
    }

    private class EndpointSignature(
        val endpoint: HttpEndpointMetadata,
        contractModel: EndpointContractModel
    ) {
        val endpointExpression: String = contractModel.endpointReference
        val methodName: String = contractModel.methodName

        val summaryLines: List<String> = contractModel.summaryLines
        val detailLines: List<String> = contractModel.detailLines

        private val parameters = ParameterGroup(endpoint.parameters, endpointExpression)
        private val inputHeaders = InputHeadersGroup(endpoint.input.headers, endpointExpression)
        private val inputBody = InputBodyGroup(endpoint.input.body, endpointExpression)
        private val outputs = OutputsGroup(endpoint.outputs, contractModel.response, endpointExpression)

        val inputs: List<String> =
            buildList {
                addAll(parameters.requiredDeclarations)
                addAll(inputHeaders.requiredDeclarations)
                addAll(inputBody.inputs)
                addAll(parameters.optionalDeclarations)
                addAll(inputHeaders.optionalDeclarations)
            }

        val uriRenderArguments: List<String> = parameters.uriRenderArguments
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
        val uriRenderArguments: List<String>

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
            uriRenderArguments = entriesInMethodOrder.map { it.uriRenderArgument }
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
        responseModel: EndpointContractModel.ResponseModel,
        private val endpointExpression: String
    ) {
        private val responseQualifier: String = "Response."
        private val options: List<OutputOption> =
            outputs.mapIndexed { index, metadata ->
                OutputOption(
                    metadata = metadata,
                    variant = responseModel.variants[index],
                    index = index + 1,
                    endpointExpression = endpointExpression,
                    responseQualifier = responseQualifier
                )
            }

        val requiresHeaders: Boolean = options.any(OutputOption::needsHeaders)
        val requiresBodyBytes: Boolean = options.any(OutputOption::needsBodyBytes)

        val returnType: String = "Response"

        val responseConstruction: String =
            when {
                options.isEmpty() -> "        kotlin.error(\"Endpoint does not declare any outputs\")"
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
                append("        return ${option.responseExpression()}")
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
                            add("                ${option.responseExpression()}")
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
            private val variant: EndpointContractModel.ResponseModel.Variant,
            val index: Int,
            private val endpointExpression: String,
            private val responseQualifier: String
        ) {
            val needsHeaders: Boolean = metadata.headers.isNotEmpty()
            val needsBodyBytes: Boolean = metadata.body.type.simpleName() != "EmptyBody"

            private val headerVarNames: List<String>
            private val headerAccessors: List<String> =
                List(metadata.headers.size) { idx -> "$endpointExpression.outputs.item$index.headers.item${idx + 1}" }
            private val headerParametersName: String = "decodedOutput${index}Headers"
            val statusCondition: String = "$endpointExpression.outputs.item$index.statusMatcher(status)"

            private val hasBody: Boolean = needsBodyBytes
            private val bodyDecoderExpr: String = "$endpointExpression.outputs.item$index.body.codec.decode(bodyBytes)"

            init {
                headerVarNames = if (hasBody) variant.fields.drop(1).map { it.name } else variant.fields.map { it.name }
            }

            private fun responseInstance(): String {
                val arguments = mutableListOf<String>()
                if (variant.match !is OutputMatchMetadata.Exact) {
                    arguments += "status"
                }
                if (hasBody) {
                    arguments += "decodedBody"
                }
                arguments += headerVarNames
                val typeReference = "$responseQualifier${variant.typeName}"
                return if (arguments.isEmpty()) {
                    typeReference
                } else {
                    "$typeReference(${arguments.joinToString(", ")})"
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
                    metadata.headers.forEachIndexed { idx, header ->
                        val name = headerVarNames[idx]
                        val valueExpression =
                            if (header.cardinality == HeaderCardinality.Multiple) {
                                "$headerParametersName.item${idx + 1}.values"
                            } else {
                                "$headerParametersName.item${idx + 1}.getFirstValueOrFail()"
                            }
                        appendLine("${indent}val $name = $valueExpression")
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

            fun responseExpression(): String = responseInstance()
        }
    }

    private companion object {
        private const val ID = "spring-restclient"
        private const val TAPIK_PACKAGE = "dev.akif.tapik"
        private const val BASE_PACKAGE = "$TAPIK_PACKAGE.spring.restclient"
        private const val BASE_INTERFACE_NAME = "$BASE_PACKAGE.RestClientBasedClient"
    }
}

private fun String.toGeneratedPackageName(generatedPackageName: String): String =
    listOfNotNull(
        takeIf(String::isNotBlank),
        generatedPackageName.trim().takeIf(String::isNotBlank)
    ).joinToString(".")
