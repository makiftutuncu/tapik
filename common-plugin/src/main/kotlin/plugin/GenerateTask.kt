package dev.akif.tapik.plugin

import arrow.core.None
import dev.akif.tapik.*
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.plugin.metadata.*
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
 * A task that generates client code from tapik endpoint definitions.
 *
 * @param config The configuration for this task.
 * @param log A function to log messages.
 * @param logDebug A function to log debug messages.
 * @param logWarn A function to log warning messages.
 */
class GenerateTask(
    private val config: GenerateTaskConfiguration,
    private val log: (String, Throwable?) -> Unit,
    private val logDebug: (String, Throwable?) -> Unit,
    private val logWarn: (String, Throwable?) -> Unit
) {
    private val apiImplementors: MutableSet<String> = linkedSetOf()

    /**
     * Generates client code from tapik endpoint definitions.
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
            logWarn(
                "Missing compiled classes directory '${compiledDir.absolutePath}', please first compile the project.",
                null
            )
            return
        }

        val urls =
            (
                listOf(
                    compiledDir
                ) + config.additionalClasspathDirectories
            ).map { it.toURI().toURL() }.toTypedArray()

        URLClassLoader(urls, javaClass.classLoader).use { classLoader ->
            val endpoints =
                scanForHttpEndpoints(compiledDir, basePackage, classLoader).mapNotNull { signature ->
                    resolveEndpoint(signature, classLoader)?.let { endpoint ->
                        runCatching { signature.toMetadata(endpoint) }
                            .onFailure { error ->
                                logWarn(
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
                    log = { message -> log("[tapik] $message", null) },
                    logDebug = { message -> logDebug("[tapik (debug)] $message", null) },
                    logWarn = { message, throwable -> logWarn("[tapik (warning)] $message", throwable) },
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
        log("Endpoint scan report is written to: ${file.absolutePath}", null)
    }

    private fun invokeGenerators(
        endpoints: List<HttpEndpointMetadata>,
        classLoader: ClassLoader,
        context: TapikGeneratorContext
    ) {
        val generatorConfigurations = config.generatorConfigurations
        if (generatorConfigurations.isEmpty()) {
            log("No Tapik generators configured; skipping code generation.", null)
            return
        }

        val availableGenerators = ServiceLoader.load(TapikGenerator::class.java, classLoader).toList()
        val generatorsById = availableGenerators.associateBy { it.id }

        generatorConfigurations.keys
            .filterNot(generatorsById::containsKey)
            .forEach { id ->
                logWarn("Generator '$id' is configured but not available on the classpath.", null)
            }

        val kotlinGenerators = linkedMapOf<TapikKotlinEndpointGenerator, GeneratorConfiguration>()
        generatorConfigurations.forEach { (generatorId, generatorConfiguration) ->
            val generator = generatorsById[generatorId] ?: return@forEach
            if (generator is TapikKotlinEndpointGenerator) {
                kotlinGenerators[generator] = generatorConfiguration
                return@forEach
            }
            log("Running generator '${generator.id}'", null)
            val generatorContext = context.copy(generatorConfiguration = generatorConfiguration)
            runCatching {
                generator.generate(endpoints, generatorContext)
            }.onSuccess { generationResult ->
                log("Generator '${generator.id}' completed successfully.", null)
                val touchedFiles =
                    generationResult.generatedSourceFiles.filter { it.exists() && it.isFile && it.extension == "kt" }
                KotlinGeneratedSourceImportOptimizer.optimizeFiles(
                    files = touchedFiles,
                    logDebug = context.logDebug,
                    logWarn = context.logWarn
                )
            }.onFailure { error ->
                logWarn("Generator '${generator.id}' failed.", error)
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
        data class FileState(
            val packageName: String,
            val sourcePackageName: String,
            val sourceFile: String,
            val imports: MutableSet<String> = linkedSetOf(),
            val aggregateInterfaces: MutableList<AggregateInterfaceContribution> = mutableListOf(),
            val endpointMembers: MutableMap<String, MutableList<String>> = linkedMapOf()
        )

        val fileStates = linkedMapOf<Triple<String, String, String>, FileState>()
        val endpointsSuffix = context.endpointsSuffix

        generators.forEach { (generator, generatorConfiguration) ->
            log("Running generator '${generator.id}'", null)
            val generatorContext = context.copy(generatorConfiguration = generatorConfiguration)
            runCatching {
                generator.contribute(endpoints, generatorContext)
            }.onSuccess { contribution ->
                log("Generator '${generator.id}' completed successfully.", null)
                contribution.sourceFiles.forEach { sourceFile ->
                    val key = Triple(sourceFile.packageName, sourceFile.sourcePackageName, sourceFile.sourceFile)
                    val state =
                        fileStates.getOrPut(key) {
                            FileState(
                                packageName = sourceFile.packageName,
                                sourcePackageName = sourceFile.sourcePackageName,
                                sourceFile = sourceFile.sourceFile
                            )
                        }
                    state.imports += sourceFile.imports
                    state.aggregateInterfaces +=
                        AggregateInterfaceContribution(
                            name = sourceFile.aggregateInterfaceName,
                            nestedInterfaceName = sourceFile.nestedInterfaceName
                        )
                    sourceFile.endpointMembers.forEach { endpointMember ->
                        state.endpointMembers
                            .getOrPut(endpointMember.endpointPropertyName) { mutableListOf() }
                            .add(endpointMember.content)
                    }
                }
            }.onFailure { error ->
                logWarn("Generator '${generator.id}' failed.", error)
            }
        }

        if (fileStates.isEmpty()) {
            return emptySet()
        }

        val generatedFiles = linkedSetOf<File>()
        val filesToOptimize = linkedSetOf<File>()
        val writtenTargets = mutableMapOf<File, Triple<String, String, String>>()
        fileStates.forEach { (_, state) ->
            val packageName = state.packageName
            val sourceFile = state.sourceFile
            val packageDirectory = File(context.generatedSourcesDirectory, packageName.replace('.', '/')).also { it.mkdirs() }
            val targetFile = File(packageDirectory, "${sourceFile.toEndpointContainerName(endpointsSuffix)}.kt")
            writtenTargets[targetFile]?.let { existing ->
                context.logWarn(
                    "Multiple endpoint sources map to '${targetFile.absolutePath}' when using target package '$packageName': " +
                        "${existing.second}.${existing.third} and ${state.sourcePackageName}.${state.sourceFile}. " +
                        "Later output will overwrite earlier output.",
                    null
                )
            }
            writtenTargets[targetFile] = Triple(packageName, state.sourcePackageName, sourceFile)
            val fileEndpoints =
                endpoints.filter { it.packageName == state.sourcePackageName && it.sourceFile == sourceFile }
            targetFile.writeText(
                renderMergedKotlinEndpointFile(
                    packageName = packageName,
                    sourceFile = sourceFile,
                    endpointsSuffix = endpointsSuffix,
                    endpoints = fileEndpoints,
                    aggregateInterfaces = state.aggregateInterfaces.distinctBy { it.name to it.nestedInterfaceName },
                    endpointMembers = state.endpointMembers,
                    imports = state.imports
                )
            )
            generatedFiles += targetFile
            filesToOptimize += targetFile
        }

        KotlinGeneratedSourceImportOptimizer.optimizeFiles(
            files = filesToOptimize,
            logDebug = context.logDebug,
            logWarn = context.logWarn
        )

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
                        logWarn("Failed to analyse class: ${classFile.absolutePath}", e)
                    }
                } catch (e: Exception) {
                    logWarn("Failed to analyse class: ${classFile.absolutePath}", e)
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
        classLoader: URLClassLoader,
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
                logDebug("Failed to parse signature for ${classNode.name}#${method.name}", error)
            }.getOrNull()
                ?.let { signature ->
                    log("Discovered endpoint ${signature.packageName}.${signature.file}#${signature.name}", null)
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
                    logWarn("Failed to load class $className for reflection analysis", error)
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
                    logDebug("Failed to parse signature for $className#${method.name} via reflection", error)
                }.getOrNull()
                    ?.let { signatureResult ->
                        log(
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
            logWarn("Failed to resolve endpoint ${signature.packageName}.${signature.file}#${signature.name}", error)
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
            sourceFile = file,
            rawType = rawType
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
                        type = resolveQueryParameterType(parameter, originalTypeMetadata),
                        required = parameter.required,
                        default = (parameter as? QueryParameter<*>)?.let { q ->
                            q.default.map { default -> default?.let { d -> (q.codec as StringCodec<Any>).encode(d) } }
                        } ?: None
                    )
                }
            }
        }

    private fun resolveQueryParameterType(
        parameter: Parameter<*>,
        typeMetadata: TypeMetadata
    ): TypeMetadata {
        if (parameter !is QueryParameter<*>) {
            return typeMetadata
        }
        if (parameter.required) {
            return typeMetadata
        }
        return typeMetadata
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

    private fun runtimeTypeMetadata(type: Class<*>): TypeMetadata {
        val rawTypeName = type.name.replace('$', '.')
        val mappedTypeName = JAVA_TO_KOTLIN_RUNTIME_TYPES[rawTypeName] ?: rawTypeName
        return TypeMetadata(
            name = mappedTypeName,
            arguments = emptyList()
        )
    }

    private companion object {
        private val JAVA_TO_KOTLIN_RUNTIME_TYPES =
            mapOf(
                "java.lang.Boolean" to "kotlin.Boolean",
                "java.lang.Byte" to "kotlin.Byte",
                "java.lang.Short" to "kotlin.Short",
                "java.lang.Integer" to "kotlin.Int",
                "java.lang.Long" to "kotlin.Long",
                "java.lang.Float" to "kotlin.Float",
                "java.lang.Double" to "kotlin.Double",
                "java.lang.Character" to "kotlin.Char",
                "java.lang.String" to "kotlin.String",
                "java.lang.Object" to "kotlin.Any",
                "java.lang.Void" to "kotlin.Unit",
                "java.util.List" to "kotlin.collections.List",
                "java.util.Set" to "kotlin.collections.Set",
                "java.util.Map" to "kotlin.collections.Map"
            )
    }

    private fun Method.shouldProcessMethod(): Boolean {
        if (name == "<init>" || name == "<clinit>" || name.contains("$")) {
            return false
        }
        if (isSynthetic || isBridge) {
            return false
        }
        if (parameterCount != 0) {
            return false
        }
        return true
    }

    private fun MethodNode.shouldProcessMethod(): Boolean {
        if (signature == null) {
            return false
        }
        if ((access and Opcodes.ACC_SYNTHETIC) != 0 || (access and Opcodes.ACC_BRIDGE) != 0) {
            return false
        }
        if (name == "<init>" || name == "<clinit>" || name.contains("$")) {
            return false
        }
        return true
    }
}
