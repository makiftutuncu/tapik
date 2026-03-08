package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.plugin.*
import dev.akif.tapik.plugin.metadata.*
import java.io.File

/**
 * Generates Spring Web MVC controller interfaces from Tapik endpoint metadata.
 */
class SpringWebMvcControllerGenerator : TapikGenerator {
    /**
     * Identifier used by the Gradle plugin to decide whether this generator should execute.
     */
    override val id: String = ID

    /**
     * Generates Spring MVC controller interfaces for the supplied endpoints, writing them under the configured output directory.
     *
     * @param endpoints endpoint metadata discovered during analysis.
     * @param context generator execution context containing output directories and logging callbacks.
     */
    override fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ) {
        if (endpoints.isEmpty()) {
            context.log("No endpoints discovered; skipping Spring WebMVC generation.")
            return
        }

        context.log("Generating Spring WebMVC controllers.")
        generateControllers(endpoints, context.generatedSourcesDirectory)
    }

    /**
     * Generates controller interfaces for Tapik endpoints.
     *
     * @param endpoints metadata describing the endpoints.
     * @param rootDir directory that will contain the generated sources.
     */
    private fun generateControllers(
        endpoints: List<HttpEndpointMetadata>,
        rootDir: File
    ) {
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
                        val signatures = sortedEndpoints.map { EndpointSignature(it) }
                        val interfaceName = "${sourceFile}Controller"
                        val targetFile = File(packageDirectory, "$interfaceName.kt")
                        targetFile.writeText(
                            buildInterfaceContent(
                                packageName = packageName,
                                interfaceName = interfaceName,
                                sourceFile = sourceFile,
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
        signatures: List<EndpointSignature>
    ): String =
        buildString {
            appendLine("package $packageName")
            appendLine()
            appendLine("// Generated from: $packageName.$sourceFile")
            appendLine("interface $interfaceName : dev.akif.tapik.Helpers {")
            signatures.forEachIndexed { index, signature ->
                appendSignature(signature)
                if (index != signatures.lastIndex) {
                    appendLine()
                }
            }
            appendLine("}")
        }

    private fun StringBuilder.appendSignature(signature: EndpointSignature) {
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
        signature.mappingAnnotations.forEach { appendLine("    $it") }
        append("    fun ${signature.methodName}(")
        if (signature.parameters.isNotEmpty()) {
            appendLine()
            appendLine(signature.parameters.joinToString(separator = ",\n") { "        $it" })
            append("    )")
        } else {
            append(")")
        }
        appendLine(": ${signature.returnType}")
    }

    private inner class EndpointSignature(
        endpoint: HttpEndpointMetadata
    ) {
        val methodName: String = renderIdentifier(endpoint.id)
        val summaryLines: List<String> = linesForDocumentation(endpoint.description)
        val detailLines: List<String> = linesForDocumentation(endpoint.details)
        val mappingAnnotations: List<String>
        val parameters: List<String>
        val returnType: String

        init {
            mappingAnnotations = buildMappingAnnotation(endpoint)

            val endpointExpression = endpoint.renderEndpointExpression()
            val nameAllocator = NameAllocator()
            val parameterSpecs = buildParameterSpecs(endpoint, endpointExpression, nameAllocator)
            parameters = parameterSpecs.map { it.declaration }

            val returnInfo = buildReturnType(endpoint.outputs)
            returnType = returnInfo.type
        }
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

    private fun TypeMetadata.withNullable(nullable: Boolean): TypeMetadata = copy(nullable = nullable)

    private data class ReturnTypeInfo(
        val type: String
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
                    val hasNonNullDefault = parameter.default.fold({ false }) { it != null }
                    val nonNullableType = parameter.type.withNullable(false)
                    val parameterType =
                        if (parameter.required || hasNonNullDefault) {
                            nonNullableType
                        } else {
                            parameter.type.withNullable(true)
                        }
                    val parameterAccessor = "$endpointExpression.parameters.item${index + 1}"
                    val defaultExpression =
                        if (parameter.required || hasNonNullDefault) {
                            "$parameterAccessor.asQueryParameter<${nonNullableType.render()}>().getDefaultOrFail()"
                        } else {
                            "$parameterAccessor.asQueryParameter<${nonNullableType.render()}>().default.getOrNull()"
                        }
                    ParameterSpec(
                        declaration = "$annotation $argumentName: ${parameterType.render()} = $defaultExpression"
                    )
                }

                else -> {
                    null
                }
            }
        }

    private fun List<HeaderMetadata>.buildHeaderSpecs(allocator: NameAllocator): List<ParameterSpec> =
        mapIndexed { index, header ->
            val argumentName = allocator.allocate(header.name, "header${index + 1}")
            val attributes = mutableListOf<String>()
            attributes += """name = "${header.name}""""
            if (!header.required) {
                attributes += "required = false"
            }
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
        val info = determineBodyParameterType()
        return ParameterSpec(
            declaration = "$annotation $argumentName: ${info.typeName}"
        )
    }

    private data class BodyParameterType(
        val typeName: String
    )

    private fun BodyMetadata.determineBodyParameterType(): BodyParameterType =
        when (type.simpleName()) {
            "JsonBody" -> {
                val argument = type.arguments.firstOrNull()
                BodyParameterType(
                    typeName = argument?.render() ?: "kotlin.Any"
                )
            }

            "StringBody" -> {
                BodyParameterType(
                    typeName = "kotlin.String"
                )
            }

            "RawBody" -> {
                BodyParameterType(
                    typeName = "kotlin.ByteArray"
                )
            }

            else -> {
                val argument = type.arguments.firstOrNull()
                BodyParameterType(
                    typeName = argument?.render() ?: "kotlin.Any"
                )
            }
        }

    private fun buildReturnType(outputs: List<OutputMetadata>): ReturnTypeInfo {
        if (outputs.isEmpty()) {
            return ReturnTypeInfo(
                type = "$TAPIK_PACKAGE.ResponseWithoutBody0"
            )
        }

        val options = outputs.map { it.toResponseTypeInfo() }

        return if (options.size == 1) {
            ReturnTypeInfo(
                type = options.first().type
            )
        } else {
            val optionTypes = options.joinToString(", ") { it.type }
            ReturnTypeInfo(
                type = "$TAPIK_PACKAGE.OneOf${options.size}<$optionTypes>"
            )
        }
    }

    private data class OutputTypeInfo(
        val type: String
    )

    private fun OutputMetadata.toResponseTypeInfo(): OutputTypeInfo {
        val headerTypes = headers.map { it.type.render() }
        val hasBody = body.type.simpleName() != "EmptyBody"
        val bodyType = if (hasBody) body.type.determineBodyValueType() else null

        val responseType =
            if (hasBody) {
                if (headerTypes.isEmpty()) {
                    "$TAPIK_PACKAGE.Response0<$bodyType>"
                } else {
                    "$TAPIK_PACKAGE.Response${headerTypes.size}<$bodyType, ${headerTypes.joinToString(
                        ", "
                    )}>"
                }
            } else {
                if (headerTypes.isEmpty()) {
                    "$TAPIK_PACKAGE.ResponseWithoutBody0"
                } else {
                    "$TAPIK_PACKAGE.ResponseWithoutBody${headerTypes.size}<${headerTypes.joinToString(
                        ", "
                    )}>"
                }
            }

        return OutputTypeInfo(responseType)
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

    private fun HttpEndpointMetadata.renderEndpointExpression(): String {
        val endpointProperty = renderIdentifier(propertyName)
        return if (sourceFile.endsWith("Kt")) {
            endpointProperty
        } else {
            "$sourceFile.$endpointProperty"
        }
    }

    private companion object {
        private const val ID = "spring-webmvc"
        private const val TAPIK_PACKAGE = "dev.akif.tapik"
    }
}
