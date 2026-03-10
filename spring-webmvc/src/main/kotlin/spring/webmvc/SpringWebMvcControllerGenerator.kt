package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.plugin.*
import dev.akif.tapik.plugin.metadata.*
import java.io.File

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
    ): TapikKotlinContribution {
        if (endpoints.isEmpty()) {
            context.log("No endpoints discovered; skipping Spring WebMVC server contributions.")
            return TapikKotlinContribution()
        }

        context.log("Generating Spring WebMVC server contributions.")
        return TapikKotlinContribution(
            sourceFiles =
                buildSourceFileContributions(
                    endpoints = endpoints,
                    serverSuffix = context.generatorConfiguration.serverSuffix,
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
        serverSuffix: String,
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
                                aggregateInterfaceName = "$sourceFile$serverSuffix",
                                nestedInterfaceName = serverSuffix,
                                endpointMembers =
                                    signatures.map { signature ->
                                        KotlinEndpointMemberContribution(
                                            endpointPropertyName = signature.endpoint.propertyName,
                                            content = buildNestedServerContent(signature, serverSuffix)
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

    private fun buildNestedServerContent(
        signature: EndpointSignature,
        nestedInterfaceName: String
    ): String =
        buildString {
            appendLine("interface $nestedInterfaceName : $TAPIK_PACKAGE.Helpers {")
            appendSignature(signature, indentation = "    ")
            appendLine("}")
        }

    private fun StringBuilder.appendSignature(
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
        val contractModel: EndpointContractModel
    ) {
        val endpointExpression: String = contractModel.endpointReference
        val methodName: String = contractModel.methodName
        val summaryLines: List<String> = contractModel.summaryLines
        val detailLines: List<String> = contractModel.detailLines
        val mappingAnnotations: List<String> = buildMappingAnnotation(endpoint)
        val parameters: List<String>
        val returnType: String

        init {
            val nameAllocator = NameAllocator()
            val parameterSpecs = buildParameterSpecs(endpoint, endpointExpression, nameAllocator)
            parameters = parameterSpecs.map { it.declaration }
            returnType = "Response"
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
    }
}

private fun String.toGeneratedPackageName(generatedPackageName: String): String =
    listOfNotNull(
        takeIf(String::isNotBlank),
        generatedPackageName.trim().takeIf(String::isNotBlank)
    ).joinToString(".")
