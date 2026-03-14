package dev.akif.tapik.plugin

import arrow.core.None
import dev.akif.tapik.API
import dev.akif.tapik.Body
import dev.akif.tapik.EmptyBody
import dev.akif.tapik.Header
import dev.akif.tapik.HeaderValues
import dev.akif.tapik.HttpEndpoint
import dev.akif.tapik.Output
import dev.akif.tapik.Parameter
import dev.akif.tapik.ParameterPosition
import dev.akif.tapik.QueryParameter
import dev.akif.tapik.StatusMatcher
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.plugin.metadata.BodyMetadata
import dev.akif.tapik.plugin.metadata.HeaderCardinality
import dev.akif.tapik.plugin.metadata.HeaderMetadata
import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import dev.akif.tapik.plugin.metadata.InputMetadata
import dev.akif.tapik.plugin.metadata.OutputMatchMetadata
import dev.akif.tapik.plugin.metadata.OutputMetadata
import dev.akif.tapik.plugin.metadata.ParameterMetadata
import dev.akif.tapik.plugin.metadata.PathVariableMetadata
import dev.akif.tapik.plugin.metadata.QueryParameterMetadata
import dev.akif.tapik.plugin.metadata.TypeMetadata
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.URLClassLoader
import java.util.ServiceLoader

/**
 * Shared configuration for the Tapik code-generation engine.
 *
 * @property outputDirectory directory where non-source outputs such as reports are written.
 * @property generatedSourcesDirectory directory where generated Kotlin sources are written.
 * @property generatedPackageName package segment appended to source packages for generated Kotlin sources.
 * @property endpointsSuffix suffix appended to the source-level enclosing endpoints interface.
 * @property basePackage base package scanned for Tapik endpoint declarations.
 * @property compiledClassesDirectory compiled classes directory of the project under analysis.
 * @property additionalClasspathDirectories additional classpath roots used during endpoint resolution.
 * @property generatorConfigurations generator-specific configuration keyed by generator id.
 */
data class TapikCodeGenerationConfiguration(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val generatedPackageName: String = "generated",
    val endpointsSuffix: String = "Endpoints",
    val basePackage: String,
    val compiledClassesDirectory: File,
    val additionalClasspathDirectories: List<File>,
    val generatorConfigurations: Map<String, GeneratorConfiguration>
)

/**
 * Shared code-generation engine used by plugin frontends such as Gradle and Maven integrations.
 *
 * It scans compiled classes for Tapik endpoints, resolves metadata, emits the endpoint report, and
 * invokes all configured generators available on the classpath.
 *
 * @param config engine configuration.
 * @param logger logging callbacks used while scanning and generating.
 */
class TapikCodeGenerationEngine(
    private val config: TapikCodeGenerationConfiguration,
    private val logger: TapikLogger
) {
    private val apiImplementors: MutableSet<String> = linkedSetOf()

    /**
     * Executes endpoint discovery and generation using the configured engine inputs.
     */
    fun generate() {
        val generationOutputDirectory = config.outputDirectory.also { it.mkdirs() }
        val generatedSourcesDir =
            config.generatedSourcesDirectory.also {
                if (it.exists()) {
                    it.deleteRecursively()
                }
                it.mkdirs()
            }
        val basePackage = config.basePackage.trim()

        val compiledDir = config.compiledClassesDirectory
        if (!compiledDir.exists() || !compiledDir.isDirectory) {
            logger.warn(
                "Missing compiled classes directory '${compiledDir.absolutePath}', please first compile the project.",
                null
            )
            return
        }

        val urls = (listOf(compiledDir) + config.additionalClasspathDirectories).map { it.toURI().toURL() }.toTypedArray()

        URLClassLoader(urls, javaClass.classLoader).use { classLoader ->
            val endpoints =
                scanForHttpEndpoints(compiledDir, basePackage, classLoader).mapNotNull { signature ->
                    resolveEndpoint(signature, classLoader)?.let { endpoint ->
                        runCatching { signature.toMetadata(endpoint) }
                            .onFailure { error ->
                                logger.warn(
                                    "Failed to build metadata for ${signature.packageName}.${signature.file}#${signature.name}",
                                    error
                                )
                            }.getOrNull()
                    }
                }

            writeReport(endpoints, generationOutputDirectory)

            val generatorContext =
                TapikGeneratorContext(
                    outputDirectory = generationOutputDirectory,
                    generatedSourcesDirectory = generatedSourcesDir,
                    generatedPackageName = config.generatedPackageName.trim().ifBlank { "generated" },
                    endpointsSuffix = config.endpointsSuffix,
                    logger = logger,
                    generatorConfiguration = GeneratorConfiguration()
                )

            invokeGenerators(endpoints, classLoader, generatorContext)
        }
    }

    private fun writeReport(
        endpoints: List<HttpEndpointMetadata>,
        outputDir: File
    ) {
        val file = outputDir.resolve("tapik-endpoints.txt")
        file.writeText(
            endpoints.joinToString(separator = System.lineSeparator() + System.lineSeparator()) { it.toString() }
        )
        logger.info("Endpoint scan report is written to: ${file.absolutePath}", null)
    }

    private fun invokeGenerators(
        endpoints: List<HttpEndpointMetadata>,
        classLoader: ClassLoader,
        context: TapikGeneratorContext
    ) {
        val generatorConfigurations = config.generatorConfigurations
        if (generatorConfigurations.isEmpty()) {
            logger.info("No Tapik generators configured; skipping code generation.", null)
            return
        }

        val availableGenerators = ServiceLoader.load(TapikGenerator::class.java, classLoader).toList()
        val generatorsById = availableGenerators.associateBy { it.id }

        generatorConfigurations.keys
            .filterNot(generatorsById::containsKey)
            .forEach { id ->
                logger.warn("Generator '$id' is configured but not available on the classpath.", null)
            }

        val kotlinGenerators = linkedMapOf<TapikKotlinEndpointGenerator, GeneratorConfiguration>()
        generatorConfigurations.forEach { (generatorId, generatorConfiguration) ->
            val generator = generatorsById[generatorId] ?: return@forEach
            if (generator is TapikKotlinEndpointGenerator) {
                kotlinGenerators[generator] = generatorConfiguration
                return@forEach
            }
            if (generator !is TapikDirectGenerator) {
                logger.warn("Generator '${generator.id}' does not implement a supported generator contract.", null)
                return@forEach
            }
            logger.info("Running generator '${generator.id}'", null)
            val generatorContext = context.copy(generatorConfiguration = generatorConfiguration)
            runCatching {
                generator.generate(endpoints, generatorContext)
            }.onSuccess { generatedFiles ->
                logger.info("Generator '${generator.id}' completed successfully.", null)
                val touchedFiles =
                    generatedFiles.filter { it.exists() && it.isFile && it.extension == "kt" }
                KotlinGeneratedSourceImportOptimizer.optimizeFiles(touchedFiles, logger)
            }.onFailure { error ->
                logger.warn("Generator '${generator.id}' failed.", error)
            }
        }

        if (kotlinGenerators.isNotEmpty()) {
            generateMergedKotlinSources(endpoints, kotlinGenerators, context)
        }
    }

    private fun generateMergedKotlinSources(
        endpoints: List<HttpEndpointMetadata>,
        generators: Map<TapikKotlinEndpointGenerator, GeneratorConfiguration>,
        context: TapikGeneratorContext
    ): Set<File> {
        val sourceFiles = mutableListOf<KotlinSourceFileContribution>()

        generators.forEach { (generator, generatorConfiguration) ->
            logger.info("Running generator '${generator.id}'", null)
            val generatorContext = context.copy(generatorConfiguration = generatorConfiguration)
            runCatching {
                generator.contribute(endpoints, generatorContext)
            }.onSuccess { contribution ->
                logger.info("Generator '${generator.id}' completed successfully.", null)
                sourceFiles += contribution.sourceFiles
            }.onFailure { error ->
                logger.warn("Generator '${generator.id}' failed.", error)
            }
        }

        if (sourceFiles.isEmpty()) {
            return emptySet()
        }

        val generatedFiles =
            writeMergedKotlinSourceFiles(
                endpoints = endpoints,
                sourceFiles = sourceFiles,
                generatedSourcesDirectory = context.generatedSourcesDirectory,
                endpointsSuffix = context.endpointsSuffix,
                logWarn = { message, throwable -> logger.warn(message, throwable) }
            )

        KotlinGeneratedSourceImportOptimizer.optimizeFiles(generatedFiles, logger)

        return generatedFiles
    }

    private fun scanForHttpEndpoints(
        compiledDir: File,
        basePackage: String,
        classLoader: URLClassLoader
    ): List<HttpEndpointSignature> {
        val baseFilter = basePackage.replace('.', '/').takeIf { it.isNotBlank() }
        val results = LinkedHashMap<String, HttpEndpointSignature>()

        compiledDir
            .walkTopDown()
            .filter { it.isFile && it.extension == "class" }
            .forEach { classFile ->
                val relativePath = classFile.relativeTo(compiledDir).path.replace(File.separatorChar, '/')
                if (relativePath.startsWith("META-INF/")) return@forEach
                if (baseFilter != null && !relativePath.startsWith(baseFilter)) return@forEach

                try {
                    val classReader = ClassReader(classFile.readBytes())
                    val classNode = ClassNode()
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                    processClassNode(classNode, classLoader, results)
                } catch (e: IllegalArgumentException) {
                    if (e.message?.contains("Unsupported class file major version") == true) {
                        parseClassWithReflection(relativePath, classLoader, results)
                    } else {
                        logger.warn("Failed to analyse class: ${classFile.absolutePath}", e)
                    }
                } catch (e: Exception) {
                    logger.warn("Failed to analyse class: ${classFile.absolutePath}", e)
                }
            }

        return results.values
            .sortedWith(
                compareBy<HttpEndpointSignature> { it.packageName }
                    .thenBy { it.file }
                    .thenBy { it.name }
            )
    }

    private fun processClassNode(
        classNode: ClassNode,
        classLoader: ClassLoader,
        results: MutableMap<String, HttpEndpointSignature>
    ) {
        if (!classNode.isApiImplementor(classLoader)) return
        if (!classNode.isInterfaceFlag) {
            apiImplementors += classNode.name
        }

        classNode.methods.forEach { method ->
            if (!method.shouldProcessMethod()) return@forEach
            runCatching {
                BytecodeParser.parseHttpEndpoint(method.signature, classNode.name, method.name)
            }.onFailure { error ->
                logger.debug("Failed to parse signature for ${classNode.name}#${method.name}", error)
            }.getOrNull()
                ?.let { signature ->
                    logger.info("Discovered endpoint ${signature.packageName}.${signature.file}#${signature.name}", null)
                    results[signature.uniqueKey] = signature
                }
        }
    }

    private fun parseClassWithReflection(
        relativePath: String,
        classLoader: URLClassLoader,
        results: MutableMap<String, HttpEndpointSignature>
    ) {
        val ownerInternalName = relativePath.removeSuffix(".class")
        val className = ownerInternalName.replace('/', '.')

        val clazz =
            runCatching { classLoader.loadClass(className) }
                .onFailure { error ->
                    logger.warn("Failed to load class $className for reflection analysis", error)
                }.getOrNull()
                ?: return

        if (!API::class.java.isAssignableFrom(clazz)) {
            return
        }

        clazz.declaredMethods
            .filter { it.shouldProcessMethod() }
            .forEach { method ->
                runCatching {
                    BytecodeParser.parseHttpEndpoint(method.genericReturnType, ownerInternalName, method.name)
                }.onFailure { error ->
                    logger.debug("Failed to parse signature for $className#${method.name} via reflection", error)
                }.getOrNull()
                    ?.let { signatureResult ->
                        logger.info(
                            "Discovered endpoint via reflection ${signatureResult.packageName}.${signatureResult.file}#${signatureResult.name}",
                            null
                        )
                        results[signatureResult.uniqueKey] = signatureResult
                    }
            }
    }

    private fun resolveEndpoint(
        signature: HttpEndpointSignature,
        classLoader: ClassLoader
    ): HttpEndpoint<*, *, *>? {
        val className =
            signature.ownerInternalName
                ?.replace('/', '.')
                ?: listOfNotNull(signature.packageName.takeIf { it.isNotBlank() }, signature.file)
                    .joinToString(".")
        val getterName = signature.methodName ?: buildGetterName(signature.name)

        return runCatching {
            val clazz = classLoader.loadClass(className)
            val method =
                clazz.methods.firstOrNull { it.name == getterName }
                    ?: clazz.declaredMethods.firstOrNull { it.name == getterName }
                    ?: throw NoSuchMethodException("Method $getterName not found on $className")
            val instance =
                if (Modifier.isStatic(method.modifiers)) {
                    null
                } else {
                    resolveOwnerInstance(clazz)
                        ?: resolveFallbackInterfaceInstance(clazz, classLoader)
                }
            if (!Modifier.isStatic(method.modifiers) && instance == null) {
                throw IllegalStateException("Failed to create instance of $className for endpoint resolution")
            }
            method.isAccessible = true
            method.invoke(instance) as? HttpEndpoint<*, *, *>
                ?: throw IllegalStateException("Resolved member $className#$getterName does not return HttpEndpoint")
        }.onFailure { error ->
            logger.warn("Failed to resolve endpoint ${signature.packageName}.${signature.file}#${signature.name}", error)
        }.getOrNull()
    }

    private fun resolveOwnerInstance(clazz: Class<*>): Any? =
        runCatching {
            clazz.getField("INSTANCE").get(null)
        }.recoverCatching {
            clazz.getDeclaredConstructor().apply { isAccessible = true }.newInstance()
        }.getOrNull()

    private fun resolveFallbackInterfaceInstance(
        interfaceClass: Class<*>,
        classLoader: ClassLoader
    ): Any? {
        if (!interfaceClass.isInterface) return null
        val implementor =
            apiImplementors.firstNotNullOfOrNull { name ->
                runCatching { classLoader.loadClass(name.replace('/', '.')) }
                    .getOrNull()
                    ?.takeIf { interfaceClass.isAssignableFrom(it) && !it.isInterface }
            } ?: return null
        return resolveOwnerInstance(implementor)
    }

    private val ClassNode.isInterfaceFlag: Boolean
        get() = (access and Opcodes.ACC_INTERFACE) != 0

    private fun ClassNode.isApiImplementor(classLoader: ClassLoader): Boolean {
        if (interfaces.any { it == API::class.java.name.replace('.', '/') }) return true
        return runCatching { classLoader.loadClass(name.replace('/', '.')) }
            .map { API::class.java.isAssignableFrom(it) }
            .getOrDefault(false)
    }

    private fun buildGetterName(propertyName: String): String =
        if (propertyName.isEmpty()) {
            "get"
        } else {
            val capitalized =
                propertyName.replaceFirstChar { ch ->
                    if (ch.isLowerCase()) ch.uppercaseChar().toString() else ch.toString()
                }
            "get$capitalized"
        }

    private fun HttpEndpointSignature.toMetadata(endpoint: HttpEndpoint<*, *, *>): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = endpoint.id,
            propertyName = name,
            description = endpoint.description,
            details = endpoint.details,
            method = endpoint.method.name,
            path = endpoint.path,
            parameters =
                endpoint.parameters
                    .toList()
                    .buildParametersMetadata(this.parameters.arguments),
            input =
                InputMetadata(
                    headers =
                        endpoint.input.headers
                            .toList()
                            .buildHeaderMetadata(input.headers.arguments),
                    body =
                        endpoint.input.body
                            .takeUnless { it === EmptyBody }
                            ?.let { createBodyMetadata(input.body, it) }
                ),
            outputs = endpoint.outputs.toList().buildOutputsMetadata(outputs.arguments),
            packageName = packageName,
            sourceFile = file
        )

    @Suppress("UNCHECKED_CAST")
    private fun List<Parameter<*>>.buildParametersMetadata(
        parameterTypes: List<TypeMetadata>
    ): List<ParameterMetadata> =
        mapIndexed { index, parameter ->
            val originalTypeMetadata =
                parameterTypes.getOrNull(index)
                    ?: runtimeTypeMetadata(parameter::class.java)
            when (parameter.position) {
                ParameterPosition.Path -> {
                    PathVariableMetadata(
                        name = parameter.name,
                        type = originalTypeMetadata
                    )
                }

                ParameterPosition.Query -> {
                    QueryParameterMetadata(
                        name = parameter.name,
                        type = originalTypeMetadata,
                        required = parameter.required,
                        default = (parameter as? QueryParameter<*>)?.let { q ->
                            q.default.map { default -> default?.let { d -> (q.codec as StringCodec<Any>).encode(d) } }
                        } ?: None
                    )
                }
            }
        }

    private fun List<Header<*>>.buildHeaderMetadata(headerTypes: List<TypeMetadata>): List<HeaderMetadata> =
        mapIndexed { index, header ->
            val typeMetadata =
                headerTypes.getOrNull(index)
                    ?: runtimeTypeMetadata(header::class.java)
            val values =
                if (header is HeaderValues<*>) {
                    header.values.map { it.toString() }
                } else {
                    emptyList()
                }
            HeaderMetadata(
                name = header.name,
                type = typeMetadata,
                required = header.required,
                values = values,
                cardinality =
                    when (header) {
                        is HeaderValues<*> ->
                            if (header.values.size <= 1) {
                                HeaderCardinality.Single
                            } else {
                                HeaderCardinality.Multiple
                            }

                        else -> HeaderCardinality.Single
                    }
            )
        }

    private fun List<Output<*, *>>.buildOutputsMetadata(outputTypes: List<TypeMetadata>): List<OutputMetadata> =
        mapIndexed { index, output ->
            val declaredType = outputTypes.getOrNull(index)
            val headerType = declaredType?.arguments?.getOrNull(0)
            val bodyType = declaredType?.arguments?.getOrNull(1)
            val headerValueTypes = headerType?.arguments ?: emptyList()
            val headers = output.headers.toList().buildHeaderMetadata(headerValueTypes)
            val bodyMetadata = createBodyMetadata(bodyType, output.body)
            OutputMetadata(
                match = output.statusMatcher.toMetadata(),
                description = describeStatusMatcher(output.statusMatcher),
                headers = headers,
                body = bodyMetadata
            )
        }

    private fun StatusMatcher.toMetadata(): OutputMatchMetadata =
        when (this) {
            is StatusMatcher.Is -> OutputMatchMetadata.Exact(status)
            is StatusMatcher.AnyOf -> OutputMatchMetadata.AnyOf(statuses.toList())
            is StatusMatcher.Predicate -> OutputMatchMetadata.Described(description)
            StatusMatcher.Unmatched -> OutputMatchMetadata.Unmatched
        }

    private fun describeStatusMatcher(matcher: StatusMatcher): String =
        when (matcher) {
            is StatusMatcher.Is -> {
                matcher.status.let { "${it.code} ${it.name}" }
            }

            is StatusMatcher.AnyOf -> {
                matcher.statuses.joinToString(prefix = "One of [", separator = ", ", postfix = "]") {
                    "${it.code} ${it.name}"
                }
            }

            is StatusMatcher.Predicate -> {
                matcher.description
            }

            StatusMatcher.Unmatched -> {
                "Unmatched"
            }
        }

    private fun createBodyMetadata(
        typeMetadata: TypeMetadata?,
        body: Body<*>
    ): BodyMetadata =
        BodyMetadata(
            type = typeMetadata ?: runtimeTypeMetadata(body::class.java),
            name = body.name,
            mediaType = body.mediaType?.toString()
        )

    private fun MethodNode.shouldProcessMethod(): Boolean =
        !name.startsWith("<") &&
            desc.startsWith("()") &&
            (access and (Opcodes.ACC_SYNTHETIC or Opcodes.ACC_BRIDGE)) == 0

    private fun Method.shouldProcessMethod(): Boolean =
        parameterCount == 0 &&
            !isSynthetic &&
            !isBridge &&
            !Modifier.isPrivate(modifiers) &&
            declaringClass != Any::class.java

    private fun runtimeTypeMetadata(type: Class<*>): TypeMetadata =
        TypeMetadata(
            name = type.name,
            nullable = false
        )
}
