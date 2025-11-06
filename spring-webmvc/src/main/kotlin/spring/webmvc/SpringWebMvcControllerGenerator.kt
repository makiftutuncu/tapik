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
            context.log("[tapik] No endpoints discovered; skipping Spring WebMVC generation.")
            return
        }

        context.log("[tapik] Generating Spring WebMVC controllers.")
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
                        val imports = buildImportsFor(packageName, sortedEndpoints, signatures)
                        val interfaceName = "${sourceFile}Controller"
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
            appendLine("interface $interfaceName {")
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

    private fun buildImportsFor(
        packageName: String,
        endpoints: List<HttpEndpointMetadata>,
        signatures: List<EndpointSignature>
    ): List<String> {
        val imports =
            mutableSetOf(
                "dev.akif.tapik.*",
                "org.springframework.web.bind.annotation.*",
                "org.springframework.web.bind.annotation.PathVariable as SpringPathVariable"
            )

        val importCandidates = mutableMapOf<String, String>()
        endpoints.flatMap(HttpEndpointMetadata::imports).forEach { fqcn ->
            val simple = fqcn.substringAfterLast('.')
            importCandidates.putIfAbsent(simple, fqcn)
        }

        val referencedTypes = signatures.flatMap { it.referencedTypeNames }.toSet()

        val typeImports =
            referencedTypes
                .mapNotNull { simple ->
                    importCandidates[simple]
                }.map { candidate ->
                    KOTLIN_COLLECTION_OVERRIDES[candidate] ?: candidate
                }.filterNot { import ->
                    import.startsWith(HTTP_PACKAGE_PREFIX) ||
                        import.startsWith("$packageName.") ||
                        import == packageName ||
                        import.startsWith("kotlin.")
                }

        imports += typeImports

        return imports.toSortedSet().toList()
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
        val referencedTypeNames: Set<String>

        init {
            mappingAnnotations = buildMappingAnnotation(endpoint)

            val nameAllocator = NameAllocator()
            val parameterSpecs = buildParameterSpecs(endpoint, nameAllocator)
            parameters = parameterSpecs.map { it.declaration }

            val returnInfo = buildReturnType(endpoint.outputs)
            returnType = returnInfo.type

            val typeNames = mutableSetOf<String>()
            parameterSpecs.forEach { typeNames += it.typeSimpleNames }
            typeNames += returnInfo.typeSimpleNames
            referencedTypeNames = typeNames
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
        val declaration: String,
        val typeSimpleNames: Set<String>
    )

    private data class ReturnTypeInfo(
        val type: String,
        val typeSimpleNames: Set<String>
    )

    private fun buildParameterSpecs(
        endpoint: HttpEndpointMetadata,
        allocator: NameAllocator
    ): List<ParameterSpec> =
        buildList {
            addAll(endpoint.parameters.buildPathVariableSpecs(allocator))
            addAll(endpoint.parameters.buildQueryParameterSpecs(allocator))
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
                    val annotation = """@SpringPathVariable(name = "${parameter.name}")"""
                    ParameterSpec(
                        declaration = "$annotation $argumentName: ${parameter.type.render()}",
                        typeSimpleNames = parameter.type.collectSimpleNames()
                    )
                }
                else -> null
            }
        }

    private fun List<ParameterMetadata>.buildQueryParameterSpecs(allocator: NameAllocator): List<ParameterSpec> =
        mapIndexedNotNull { index, parameter ->
            when (parameter) {
                is QueryParameterMetadata -> {
                    val argumentName = allocator.allocate(parameter.name, "query${index + 1}")
                    val attributes = mutableListOf<String>()
                    attributes += """name = "${parameter.name}""""
                    attributes += "required = ${parameter.required}"
                    parameter.default?.takeIf { it.isNotBlank() }?.let { default ->
                        attributes += """defaultValue = "${default.escapeForAnnotation()}""""
                    }
                    val annotation = "@RequestParam(${attributes.joinToString(", ")})"
                    ParameterSpec(
                        declaration = "$annotation $argumentName: ${parameter.type.render()}",
                        typeSimpleNames = parameter.type.collectSimpleNames()
                    )
                }
                else -> null
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
            val annotation = "@RequestHeader(${attributes.joinToString(", ")})"
            ParameterSpec(
                declaration = "$annotation $argumentName: ${header.type.render()}",
                typeSimpleNames = header.type.collectSimpleNames()
            )
        }

    private fun BodyMetadata.buildBodySpec(allocator: NameAllocator): ParameterSpec? {
        if (type.simpleName() == "EmptyBody") {
            return null
        }
        val argumentName = allocator.allocate(name ?: "body", "body")
        val annotation = "@RequestBody"
        val info = determineBodyParameterType()
        return ParameterSpec(
            declaration = "$annotation $argumentName: ${info.typeName}",
            typeSimpleNames = info.simpleNames
        )
    }

    private data class BodyParameterType(
        val typeName: String,
        val simpleNames: Set<String>
    )

    private fun BodyMetadata.determineBodyParameterType(): BodyParameterType =
        when (type.simpleName()) {
            "JsonBody" -> {
                val argument = type.arguments.firstOrNull()
                BodyParameterType(
                    typeName = argument?.render() ?: "Any",
                    simpleNames = argument?.collectSimpleNames() ?: emptySet()
                )
            }
            "StringBody" ->
                BodyParameterType(
                    typeName = "String",
                    simpleNames = emptySet()
                )
            "RawBody" ->
                BodyParameterType(
                    typeName = "ByteArray",
                    simpleNames = emptySet()
                )
            else -> {
                val argument = type.arguments.firstOrNull()
                BodyParameterType(
                    typeName = argument?.render() ?: "Any",
                    simpleNames = argument?.collectSimpleNames() ?: emptySet()
                )
            }
        }

    private fun buildReturnType(outputs: List<OutputMetadata>): ReturnTypeInfo {
        if (outputs.isEmpty()) {
            return ReturnTypeInfo(
                type = "ResponseWithoutBody0",
                typeSimpleNames = emptySet()
            )
        }

        val options = outputs.map { it.toResponseTypeInfo() }

        return if (options.size == 1) {
            ReturnTypeInfo(
                type = options.first().type,
                typeSimpleNames = options.first().typeSimpleNames
            )
        } else {
            val optionTypes = options.joinToString(", ") { it.type }
            ReturnTypeInfo(
                type = "OneOf${options.size}<$optionTypes>",
                typeSimpleNames = options.flatMapTo(mutableSetOf()) { it.typeSimpleNames }
            )
        }
    }

    private data class OutputTypeInfo(
        val type: String,
        val typeSimpleNames: Set<String>
    )

    private fun OutputMetadata.toResponseTypeInfo(): OutputTypeInfo {
        val headerTypes = headers.map { it.type.render() }
        val headerSimpleNames = headers.flatMap { it.type.collectSimpleNames() }
        val hasBody = body.type.simpleName() != "EmptyBody"
        val bodyType = if (hasBody) body.type.determineBodyValueType() else null
        val bodySimpleNames = if (hasBody) body.type.collectSimpleNames() else emptySet()

        val responseType =
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

        val simpleNames =
            buildSet {
                addAll(headerSimpleNames)
                addAll(bodySimpleNames)
            }

        return OutputTypeInfo(responseType, simpleNames)
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
                "GET" -> "GetMapping"
                "POST" -> "PostMapping"
                "PUT" -> "PutMapping"
                "DELETE" -> "DeleteMapping"
                "PATCH" -> "PatchMapping"
                else -> "RequestMapping"
            }

        val annotationLine =
            if (annotation == "RequestMapping") {
                val methodAttribute = """method = [RequestMethod.$method]"""
                val combined = listOf(methodAttribute) + attributes
                "@RequestMapping(${combined.joinToString(", ")})"
            } else {
                if (attributes.isEmpty()) {
                    "@$annotation"
                } else {
                    "@$annotation(${attributes.joinToString(", ")})"
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
        private const val HTTP_PACKAGE_PREFIX = "$TAPIK_PACKAGE."
        private val KOTLIN_COLLECTION_OVERRIDES =
            mapOf(
                "java.util.Map" to "kotlin.collections.Map",
                "java.util.List" to "kotlin.collections.List",
                "java.util.Set" to "kotlin.collections.Set"
            )
    }
}
