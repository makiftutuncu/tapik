package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.plugin.*
import dev.akif.tapik.plugin.metadata.*

/**
 * Generates Spring Web MVC controller interfaces from Tapik endpoint metadata.
 */
class SpringWebMvcControllerGenerator : TapikKotlinEndpointGenerator {
    /**
     * Identifier used by the Gradle plugin to decide whether this generator should execute.
     */
    override val id: String = ID

    /**
     * Builds Spring WebMVC nested server contributions for the discovered endpoints.
     *
     * @param endpoints endpoint metadata to translate into server members.
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
            emptyMessage = "No endpoints discovered; skipping Spring WebMVC server contributions.",
            generatingMessage = "Generating Spring WebMVC server contributions."
        ) {
            buildSourceFileContributions(
                endpoints = endpoints,
                serverSuffix = context.generatorConfiguration.serverSuffix,
                endpointsSuffix = context.endpointsSuffix,
                generatedPackageName = context.generatedPackageName
            )
        }

    private fun buildSourceFileContributions(
        endpoints: List<HttpEndpointMetadata>,
        serverSuffix: String,
        endpointsSuffix: String,
        generatedPackageName: String
    ): List<KotlinSourceFileContribution> =
        buildKotlinSourceFileContributions(
            endpoints = endpoints,
            endpointsSuffix = endpointsSuffix,
            generatedPackageName = generatedPackageName,
            aggregateInterfaceName = { sourceFile -> "$sourceFile$serverSuffix" },
            nestedInterfaceName = serverSuffix,
            imports = ::buildExtensionImports,
            endpointMemberContent = { endpoint, contractModel ->
                buildNestedServerContent(EndpointSignature(endpoint, contractModel), serverSuffix)
            },
            topLevelDeclarations = { _, aggregateInterfaceName, endpointContexts ->
                buildTopLevelDeclarations(aggregateInterfaceName, endpointContexts)
            }
        )

    private fun buildExtensionImports(endpoints: List<HttpEndpointMetadata>): Set<String> =
        buildSet {
            if (endpoints.isNotEmpty()) {
                add("$TAPIK_SPRING_PACKAGE.toResponseEntity")
            }
            if (endpoints.any { endpoint -> endpoint.parameters.any { it is QueryParameterMetadata } }) {
                add("$TAPIK_PACKAGE.asQueryParameter")
                add("$TAPIK_PACKAGE.getDefaultOrFail")
            }
        }

    private fun buildNestedServerContent(
        signature: EndpointSignature,
        nestedInterfaceName: String
    ): String =
        buildString {
            appendLine("interface $nestedInterfaceName {")
            appendSignature(signature, indentation = "    ")
            appendLine("}")
        }

    private fun buildTopLevelDeclarations(
        aggregateInterfaceName: String,
        endpointContexts: List<Pair<HttpEndpointMetadata, KotlinEndpointGenerationContext>>
    ): List<String> {
        val signatures = endpointContexts.map { (endpoint, context) -> EndpointSignature(endpoint, context) }
        if (signatures.isEmpty()) {
            return emptyList()
        }

        return buildList {
            signatures.forEach { signature ->
                add(buildResponseEntityExtension(signature))
            }
            add(buildResponseAdvice(aggregateInterfaceName, signatures))
        }
    }

    private fun StringBuilder.appendSignature(
        signature: EndpointSignature,
        indentation: String
    ) {
        appendGeneratedKdoc(signature.summaryLines, signature.detailLines, indentation)
        signature.mappingAnnotations.forEach { appendLine("$indentation$it") }
        append("${indentation}fun ${signature.methodName}(")
        if (signature.parameters.isNotEmpty()) {
            appendLine()
            appendLine(signature.parameters.joinToString(separator = ",\n") { "$indentation    $it" })
            append("$indentation)")
        } else {
            append(")")
        }
        appendLine(": ${signature.returnType}")
    }

    private inner class EndpointSignature(
        val endpoint: HttpEndpointMetadata,
        context: KotlinEndpointGenerationContext
    ) {
        val endpointExpression: String = context.endpointReference
        val methodName: String = context.methodName
        val summaryLines: List<String> = context.summaryLines
        val detailLines: List<String> = context.detailLines
        val mappingAnnotations: List<String> = buildMappingAnnotation(endpoint)
        val parameters: List<String>
        val returnType: String
        val responseTypeName: String = context.response.typeName
        val responseModel: KotlinEndpointResponseModel = context.response

        init {
            val nameAllocator = NameAllocator()
            val parameterSpecs = buildParameterSpecs(endpoint, endpointExpression, nameAllocator)
            parameters = parameterSpecs.map { it.declaration }
            returnType = "Response"
        }
    }

    private fun buildResponseEntityExtension(signature: EndpointSignature): String =
        buildString {
            appendLine(
                "fun ${signature.responseTypeName}.toResponseEntity(): org.springframework.http.ResponseEntity<kotlin.Any> ="
            )
            appendLine("    when (this) {")
            signature.endpoint.outputs.forEachIndexed { index, output ->
                appendLine(
                    "        is ${signature.responseTypeName}.${signature.responseModel.variants[index].typeName} ->"
                )
                appendLine("            toResponseEntity(")
                appendLine(
                    "                status = ${renderResponseStatusExpression(signature, index + 1)},"
                )
                appendLine(
                    "                headers = ${renderResponseHeadersExpression(signature, output, index + 1)},"
                )
                appendLine(
                    "                mediaType = ${signature.endpointExpression}.outputs.item${index + 1}.body.mediaType,"
                )
                appendLine("                body = ${renderResponseBodyExpression(signature, output, index + 1)}")
                appendLine("            )")
            }
            append("    }")
        }

    private fun renderResponseStatusExpression(
        signature: EndpointSignature,
        outputIndex: Int
    ): String {
        val variant = signature.responseModel.variants[outputIndex - 1]
        val exactStatus = (variant.match as? OutputMatchMetadata.Exact)?.status
        return exactStatus?.let { "dev.akif.tapik.Status.${it.name}" } ?: checkNotNull(variant.statusFieldName)
    }

    private fun renderResponseHeadersExpression(
        signature: EndpointSignature,
        output: OutputMetadata,
        outputIndex: Int
    ): String {
        if (output.headers.isEmpty()) {
            return "kotlin.collections.emptyMap()"
        }

        val fieldNames = resolveHeaderFieldNames(signature, outputIndex)
        return output.headers
            .mapIndexed { index, header ->
                val accessor = "${signature.endpointExpression}.outputs.item$outputIndex.headers.item${index + 1}"
                val encodedValues =
                    if (header.cardinality == HeaderCardinality.Multiple) {
                        "${fieldNames[index]}.map { $accessor.codec.encode(it) }"
                    } else {
                        "kotlin.collections.listOf($accessor.codec.encode(${fieldNames[index]}))"
                    }
                "$accessor.name to $encodedValues"
            }.joinToString(
                prefix = "kotlin.collections.linkedMapOf(",
                separator = ", ",
                postfix = ")"
            )
    }

    private fun renderResponseBodyExpression(
        signature: EndpointSignature,
        output: OutputMetadata,
        outputIndex: Int
    ): String {
        if (output.body.type.simpleName() == "EmptyBody") {
            return "null"
        }

        val bodyFieldName = checkNotNull(resolveBodyFieldName(signature, outputIndex))
        return "${signature.endpointExpression}.outputs.item$outputIndex.body.bytes($bodyFieldName)"
    }

    private fun resolveBodyFieldName(
        signature: EndpointSignature,
        outputIndex: Int
    ): String? {
        val variant = signature.responseModel.variants[outputIndex - 1]
        val hasBody =
            signature.endpoint.outputs[outputIndex - 1]
                .body.type
                .simpleName() != "EmptyBody"
        return if (hasBody) variant.fields.first().name else null
    }

    private fun resolveHeaderFieldNames(
        signature: EndpointSignature,
        outputIndex: Int
    ): List<String> {
        val variant = signature.responseModel.variants[outputIndex - 1]
        val output = signature.endpoint.outputs[outputIndex - 1]
        val bodyOffset = if (output.body.type.simpleName() == "EmptyBody") 0 else 1
        return variant.fields.drop(bodyOffset).map { it.name }
    }

    private fun buildResponseAdvice(
        aggregateInterfaceName: String,
        signatures: List<EndpointSignature>
    ): String =
        buildString {
            appendLine("@org.springframework.web.bind.annotation.ControllerAdvice")
            appendLine(
                "class ${aggregateInterfaceName}ResponseAdvice : dev.akif.tapik.spring.TapikResponseBodyAdvice {"
            )
            appendLine("    override fun supportsTapikResponseType(responseType: Class<*>): kotlin.Boolean =")
            appendLine(
                signatures.joinToString(separator = " ||\n") { signature ->
                    "        ${signature.responseTypeName}::class.java.isAssignableFrom(responseType)"
                }
            )
            appendLine()
            appendLine(
                "    override fun toResponseEntity(body: dev.akif.tapik.TapikResponse): org.springframework.http.ResponseEntity<kotlin.Any> ="
            )
            appendLine("        when (body) {")
            signatures.forEach { signature ->
                appendLine("            is ${signature.responseTypeName} -> body.toResponseEntity()")
            }
            appendLine(
                "            else -> kotlin.error(\"Unsupported TapikResponse type: \" + body::class.qualifiedName)"
            )
            appendLine("        }")
            append("}")
        }

    private class NameAllocator {
        private val used = mutableSetOf<String>()

        fun allocate(
            rawName: String?,
            fallback: String
        ): String = uniqueName(sanitizeIdentifier(rawName, fallback), used)
    }

    private data class ParameterSpec(
        val declaration: String
    )

    private fun buildParameterSpecs(
        endpoint: HttpEndpointMetadata,
        endpointExpression: String,
        allocator: NameAllocator
    ): List<ParameterSpec> =
        buildList {
            addAll(endpoint.parameters.buildPathVariableSpecs(allocator))
            addAll(endpoint.parameters.buildQueryParameterSpecs(allocator, endpointExpression))
            addAll(endpoint.input.headers.buildHeaderSpecs(allocator))
            endpoint.input.body
                ?.buildBodySpec(allocator)
                ?.let { add(it) }
        }

    private fun List<ParameterMetadata>.buildPathVariableSpecs(allocator: NameAllocator): List<ParameterSpec> =
        mapIndexedNotNull { index, parameter ->
            when (parameter) {
                is PathVariableMetadata -> {
                    val argumentName = allocator.allocate(parameter.name, "path${index + 1}")
                    val annotation =
                        """@org.springframework.web.bind.annotation.PathVariable(name = "${parameter.name}")"""
                    ParameterSpec(
                        declaration = "$annotation $argumentName: ${parameter.type.render()}"
                    )
                }

                else -> {
                    null
                }
            }
        }

    private fun List<ParameterMetadata>.buildQueryParameterSpecs(
        allocator: NameAllocator,
        endpointExpression: String
    ): List<ParameterSpec> =
        mapIndexedNotNull { index, parameter ->
            when (parameter) {
                is QueryParameterMetadata -> {
                    val argumentName = allocator.allocate(parameter.name, "query${index + 1}")
                    val attributes = mutableListOf<String>()
                    attributes += """name = "${parameter.name}""""
                    attributes += "required = ${parameter.required}"
                    val annotation = "@org.springframework.web.bind.annotation.RequestParam(${attributes.joinToString(
                        ", "
                    )})"
                    val typeInfo = parameter.toGeneratedTypeInfo()
                    val parameterAccessor = "$endpointExpression.parameters.item${index + 1}"
                    val defaultExpression =
                        if (!typeInfo.hasDefault || typeInfo.hasNonNullDefault) {
                            "$parameterAccessor.asQueryParameter<${typeInfo.nonNullableType}>().getDefaultOrFail()"
                        } else {
                            "$parameterAccessor.asQueryParameter<${typeInfo.nonNullableType}>().default.getOrNull()"
                        }
                    ParameterSpec(
                        declaration = "$annotation $argumentName: ${typeInfo.renderedType} = $defaultExpression"
                    )
                }

                else -> {
                    null
                }
            }
        }

    private fun List<HeaderMetadata>.buildHeaderSpecs(allocator: NameAllocator): List<ParameterSpec> =
        filter { it.required }.mapIndexed { index, header ->
            val argumentName = allocator.allocate(header.name, "header${index + 1}")
            val attributes = mutableListOf<String>()
            attributes += """name = "${header.name}""""
            val annotation = "@org.springframework.web.bind.annotation.RequestHeader(${attributes.joinToString(", ")})"
            ParameterSpec(
                declaration = "$annotation $argumentName: ${header.type.render()}"
            )
        }

    private fun BodyMetadata.buildBodySpec(allocator: NameAllocator): ParameterSpec? {
        if (type.simpleName() == "EmptyBody") {
            return null
        }
        val argumentName = allocator.allocate(name ?: "body", "body")
        val annotation = "@org.springframework.web.bind.annotation.RequestBody"
        return ParameterSpec(
            declaration = "$annotation $argumentName: ${renderValueType()}"
        )
    }

    private fun buildMappingAnnotation(endpoint: HttpEndpointMetadata): List<String> {
        val path =
            endpoint.path
                .joinToString(
                    separator = "/",
                    prefix = "/"
                ) { it.trim('/') }
                .takeIf { it.isNotBlank() } ?: "/"

        val consumes = endpoint.input.determineConsumes()
        val produces = endpoint.outputs.determineProduces()

        val attributes = mutableListOf<String>()
        attributes += """path = ["${path.escapeForAnnotation()}"]"""
        consumes?.let { attributes += """consumes = ${listOf(it).formatAsArray()}""" }
        produces.takeIf { it.isNotEmpty() }?.let { attributes += """produces = ${it.formatAsArray()}""" }

        val method = endpoint.method.uppercase()
        val annotation =
            when (method) {
                "GET" -> "@org.springframework.web.bind.annotation.GetMapping"
                "POST" -> "@org.springframework.web.bind.annotation.PostMapping"
                "PUT" -> "@org.springframework.web.bind.annotation.PutMapping"
                "DELETE" -> "@org.springframework.web.bind.annotation.DeleteMapping"
                "PATCH" -> "@org.springframework.web.bind.annotation.PatchMapping"
                else -> "@org.springframework.web.bind.annotation.RequestMapping"
            }

        val annotationLine =
            if (annotation == "@org.springframework.web.bind.annotation.RequestMapping") {
                val methodAttribute = """method = [org.springframework.web.bind.annotation.RequestMethod.$method]"""
                val combined = listOf(methodAttribute) + attributes
                "$annotation(${combined.joinToString(", ")})"
            } else {
                if (attributes.isEmpty()) {
                    annotation
                } else {
                    "$annotation(${attributes.joinToString(", ")})"
                }
            }

        return listOf(annotationLine)
    }

    private fun InputMetadata.determineConsumes(): String? = body?.mediaType?.takeIf { it.isNotBlank() }

    private fun List<OutputMetadata>.determineProduces(): List<String> =
        mapNotNull { it.body.mediaType?.takeIf(String::isNotBlank) }
            .distinct()

    private fun List<String>.formatAsArray(): String =
        joinToString(prefix = "[\"", separator = "\", \"", postfix = "\"]") { it.escapeForAnnotation() }

    private companion object {
        private const val ID = "spring-webmvc"
        private const val TAPIK_PACKAGE = "dev.akif.tapik"
        private const val TAPIK_SPRING_PACKAGE = "dev.akif.tapik.spring"
    }
}
