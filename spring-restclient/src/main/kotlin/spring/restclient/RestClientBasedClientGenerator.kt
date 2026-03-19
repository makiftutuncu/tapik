package dev.akif.tapik.spring.restclient

import dev.akif.tapik.plugin.*
import dev.akif.tapik.plugin.metadata.*

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
    ): TapikKotlinContribution =
        contributeKotlinSourceFiles(
            endpoints = endpoints,
            context = context,
            emptyMessage = "No endpoints discovered; skipping Spring RestClient client contributions.",
            generatingMessage = "Generating Spring RestClient client contributions."
        ) {
            buildSourceFileContributions(
                endpoints = endpoints,
                clientSuffix = context.generatorConfiguration.clientSuffix,
                endpointsSuffix = context.endpointsSuffix,
                generatedPackageName = context.generatedPackageName
            )
        }

    private fun buildSourceFileContributions(
        endpoints: List<HttpEndpointMetadata>,
        clientSuffix: String,
        endpointsSuffix: String,
        generatedPackageName: String
    ): List<KotlinSourceFileContribution> =
        buildKotlinSourceFileContributions(
            endpoints = endpoints,
            endpointsSuffix = endpointsSuffix,
            generatedPackageName = generatedPackageName,
            aggregateInterfaceName = { sourceFile -> "$sourceFile$clientSuffix" },
            nestedInterfaceName = clientSuffix,
            imports = ::buildExtensionImports,
            endpointMemberContent = { endpoint, contractModel ->
                buildNestedClientContent(EndpointSignature(endpoint, contractModel), clientSuffix)
            }
        )

    private fun buildExtensionImports(endpoints: List<HttpEndpointMetadata>): Set<String> =
        buildSet {
            add("$SHARED_SPRING_PACKAGE.toStatus")
            if (endpoints.any { endpoint -> endpoint.outputs.any { output -> output.headers.isNotEmpty() } }) {
                add("$SHARED_SPRING_PACKAGE.toTapikHeaders")
            }
            if (endpoints.any { endpoint -> endpoint.parameters.any { it is QueryParameterMetadata } }) {
                add("$TAPIK_PACKAGE.asQueryParameter")
                add("$TAPIK_PACKAGE.getDefaultOrFail")
            }
            if (endpoints.any { it.input.headers.isNotEmpty() }) {
                add("$TAPIK_PACKAGE.encodeInputHeaders")
            }
            if (endpoints.any { endpoint -> endpoint.outputs.any { output -> output.headers.isNotEmpty() } }) {
                add("$TAPIK_PACKAGE.getFirstValueOrFail")
            }
        }

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
        appendGeneratedKdoc(signature.summaryLines, signature.detailLines, indentation)
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
        appendLine("$indentation        uri = ${signature.uriInvocation},")
        appendLine("$indentation        inputHeaders = ${signature.inputHeadersEncoding},")
        appendLine("$indentation        inputBodyContentType = $endpointExpr.input.body.mediaType,")
        appendLine("$indentation        inputBody = ${signature.encodeBodyCall},")
        appendLine("$indentation        outputs = $endpointExpr.outputs.toList()")
        appendLine("$indentation    )")
        appendLine()
        appendLine("$indentation    val status = responseEntity.statusCode.toStatus()")

        if (signature.needsHeadersMap) {
            appendLine()
            appendLine("$indentation    val headers = responseEntity.headers.toTapikHeaders()")
        }

        if (signature.needsBodyBytes) {
            appendLine()
            appendLine("$indentation    val bodyBytes = responseEntity.body ?: kotlin.ByteArray(0)")
        }

        appendLine()
        appendLine(signature.responseConstruction)
        appendLine("$indentation}")
    }

    private class EndpointSignature(
        val endpoint: HttpEndpointMetadata,
        context: KotlinEndpointGenerationContext
    ) {
        private val contractTypeName: String = context.contractTypeName
        val endpointExpression: String = context.endpointReference
        val methodName: String = context.methodName

        val summaryLines: List<String> = context.summaryLines
        val detailLines: List<String> = context.detailLines

        private val parameters = ParameterGroup(endpoint.parameters, endpointExpression)
        private val inputHeaders = InputHeadersGroup(endpoint.input.headers, endpointExpression)
        private val inputBody = InputBodyGroup(endpoint.input.body, endpointExpression)
        private val outputs = OutputsGroup(endpoint.outputs, context.response, endpointExpression)

        val inputs: List<String> =
            buildList {
                addAll(parameters.requiredDeclarations)
                addAll(inputHeaders.requiredDeclarations)
                addAll(inputBody.inputs)
                addAll(parameters.optionalDeclarations)
                addAll(inputHeaders.optionalDeclarations)
            }

        val uriInvocation: String = "$contractTypeName.uri(${parameters.callArguments})"
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
            val declaration: String,
            val hasDefault: Boolean
        )

        private val entries: List<Entry>
        private val entriesInMethodOrder: List<Entry>

        val requiredDeclarations: List<String>
        val optionalDeclarations: List<String>
        val callArguments: String

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
                    val declaration =
                        when (parameter) {
                            is PathVariableMetadata -> "$name: ${parameter.type.render()}"

                            is QueryParameterMetadata -> {
                                val typeInfo = parameter.toGeneratedTypeInfo()
                                if (typeInfo.hasDefault) {
                                    val parameterAccessor = "$endpointRef.parameters.item${index + 1}"
                                    if (typeInfo.hasNonNullDefault) {
                                        "$name: ${typeInfo.renderedType} = $parameterAccessor.asQueryParameter<${typeInfo.nonNullableType}>().getDefaultOrFail()"
                                    } else {
                                        "$name: ${typeInfo.renderedType} = $parameterAccessor.asQueryParameter<${typeInfo.nonNullableType}>().default.getOrNull()"
                                    }
                                } else {
                                    "$name: ${typeInfo.renderedType}"
                                }
                            }
                        }

                    Entry(
                        name = name,
                        declaration = declaration,
                        hasDefault = parameter is QueryParameterMetadata && !parameter.required
                    )
                }

            requiredDeclarations = entries.filterNot { it.hasDefault }.map { it.declaration }
            optionalDeclarations = entries.filter { it.hasDefault }.map { it.declaration }
            entriesInMethodOrder = entries.filterNot { it.hasDefault } + entries.filter { it.hasDefault }
            callArguments = entriesInMethodOrder.joinToString(", ") { it.name }
        }
    }

    private class InputHeadersGroup(
        headers: List<HeaderMetadata>,
        private val endpointExpression: String
    ) {
        private data class Entry(
            val name: String,
            val declaration: String,
            val hasDefault: Boolean
        )

        private val entries: List<Entry>
        private val hasHeaders: Boolean = headers.isNotEmpty()

        val count: Int
        val requiredDeclarations: List<String>
        val optionalDeclarations: List<String>
        private val argumentList: String
        val usesEncodeInputHeaders: Boolean
            get() = hasHeaders
        val encodingCall: String
            get() =
                if (!hasHeaders) {
                    "kotlin.collections.emptyMap()"
                } else {
                    val arguments = argumentList.takeIf { it.isNotEmpty() } ?: ""
                    "$endpointExpression.input.encodeInputHeaders($arguments)"
                }

        init {
            val usedNames = mutableSetOf<String>()
            entries =
                headers.mapIndexedNotNull { index, header ->
                    if (!header.required) {
                        return@mapIndexedNotNull null
                    }
                    val fallback = "inputHeader${index + 1}"
                    val baseName = sanitizeIdentifier(header.name, fallback)
                    val name = uniqueName(baseName, usedNames)
                    Entry(
                        name = name,
                        declaration = "$name: ${header.type.render()}",
                        hasDefault = false
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
                else -> {
                    val valueType = body?.renderValueType() ?: "kotlin.Any"
                    inputs = listOf("inputBody: $valueType")
                    encodeCall = "$endpointExpression.input.body.codec.encode(inputBody)"
                }
            }
        }
    }

    private class OutputsGroup(
        outputs: List<OutputMetadata>,
        responseModel: KotlinEndpointResponseModel,
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
            private val variant: KotlinEndpointResponseModel.Variant,
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
        private const val SHARED_SPRING_PACKAGE = "$TAPIK_PACKAGE.spring"
        private const val BASE_PACKAGE = "$TAPIK_PACKAGE.spring.restclient"
        private const val BASE_INTERFACE_NAME = "$BASE_PACKAGE.RestClientBasedClient"
    }
}
