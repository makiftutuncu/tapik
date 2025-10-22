package dev.akif.tapik.gradle

import dev.akif.tapik.*
import dev.akif.tapik.gradle.metadata.*
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.URLClassLoader
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * Gradle task that scans compiled classes for Tapik endpoints and generates supporting artefacts.
 */
@CacheableTask
abstract class TapikGenerateTask : DefaultTask() {
    /** Packages that will be scanned for compiled `HttpEndpoint` declarations. */
    @get:Input
    abstract val endpointPackages: ListProperty<String>

    /** Directory containing the textual endpoint summary output. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    /** Source directory containing the Kotlin declarations that define Tapik endpoints. */
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDirectory: DirectoryProperty

    /** Directory holding compiled classes associated with the project under analysis. */
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    abstract val compiledClassesDirectory: DirectoryProperty

    /** Directory where generated client source files are written. */
    @get:OutputDirectory
    abstract val generatedSourcesDirectory: DirectoryProperty

    /** Additional class directories (for example, from other projects) to include on the analysis classpath. */
    @get:Input
    abstract val additionalClassDirectories: ListProperty<String>

    /** Runtime classpath used when analysing endpoint bytecode via reflection fallback. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val runtimeClasspath: ConfigurableFileCollection

    /**
     * Executes endpoint discovery and generates Tapik metadata and client sources.
     */
    @TaskAction
    fun generate() {
        val outDir = outputDirectory.get().asFile.also { it.mkdirs() }
        val generatedSourcesDir = generatedSourcesDirectory.get().asFile.also {
            if (it.exists()) {
                it.deleteRecursively()
            }
            it.mkdirs()
        }
        val pkgs = endpointPackages.orNull?.filter { it.isNotBlank() }?.distinct()?.sorted().orEmpty()

        val compiledDir = compiledClassesDirectory.get().asFile
        if (!compiledDir.exists() || !compiledDir.isDirectory) {
            logger.warn("[tapik] Compiled classes directory is missing: ${compiledDir.absolutePath}")
            return
        }

        val classpathEntries = buildClasspathEntries(
            compiledDir = compiledDir,
            additionalDirectories = additionalClassDirectories.getOrElse(emptyList()),
            runtimeClasspath = runtimeClasspath.files
        )
        if (classpathEntries.isEmpty()) {
            logger.warn("[tapik] No classpath entries available; generated clients will be empty.")
            return
        }

        val urls = classpathEntries.map { it.toURI().toURL() }.toTypedArray()

        val signatures = URLClassLoader(urls, javaClass.classLoader).use { classLoader ->
            scanForHttpEndpoints(compiledDir, pkgs, classLoader)
        }

        val metadata = buildHttpEndpointMetadata(
            signatures = signatures,
            classpathEntries = classpathEntries,
            logger = logger
        )

        writeJsonOutput(metadata, outDir, logger)
        writeGeneratedSources(metadata, generatedSourcesDir, pkgs)
    }

    private fun scanForHttpEndpoints(
        compiledDir: File,
        packages: List<String>,
        classLoader: URLClassLoader
    ): List<HttpEndpointSignature> {
        val packageFilters = packages.map { it.replace('.', '/') }
        val results = LinkedHashMap<String, HttpEndpointSignature>()

        compiledDir.walkTopDown()
            .filter { it.isFile && it.extension == "class" }
            .forEach { classFile ->
                val relativePath = classFile.relativeTo(compiledDir).path.replace(File.separatorChar, '/')
                if (relativePath.startsWith("META-INF/")) {
                    return@forEach
                }
                if (packageFilters.isNotEmpty() && packageFilters.none { relativePath.startsWith(it) }) {
                    return@forEach
                }

                try {
                    val classReader = ClassReader(classFile.readBytes())
                    val classNode = ClassNode()
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                    processClassNode(classNode, results)
                } catch (e: IllegalArgumentException) {
                    if (e.message?.contains("Unsupported class file major version") == true) {
                        parseClassWithReflection(relativePath, classLoader, results)
                    } else {
                        logger.warn("[tapik] Failed to analyse class: ${classFile.absolutePath}", e)
                    }
                } catch (e: Exception) {
                    logger.warn("[tapik] Failed to analyse class: ${classFile.absolutePath}", e)
                }
            }

        return results.values
            .sortedWith(compareBy<HttpEndpointSignature> { it.packageName }
                .thenBy { it.file }
                .thenBy { it.name })
    }

    private fun processClassNode(
        classNode: ClassNode,
        results: MutableMap<String, HttpEndpointSignature>
    ) {
        classNode.methods.forEach { method ->
            val signature = method.signature ?: return@forEach
            if (!shouldProcessMethod(method.access, method.name, signature)) {
                return@forEach
            }
            runCatching {
                BytecodeParser.parseHttpEndpoint(signature, classNode.name, method.name)
            }.onFailure { error ->
                logger.debug("[tapik] Failed to parse signature for ${classNode.name}#${method.name}", error)
            }.getOrNull()?.let { signatureResult ->
                logger.lifecycle("[tapik] discovered endpoint ${signatureResult.packageName}.${signatureResult.file}#${signatureResult.name}")
                results[signatureResult.uniqueKey] = signatureResult
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

        val clazz = runCatching { classLoader.loadClass(className) }
            .onFailure { error ->
                logger.warn("[tapik] Failed to load class $className for reflection analysis", error)
            }
            .getOrNull()
            ?: return

        clazz.declaredMethods
            .filter { shouldProcessMethod(it) }
            .forEach { method ->
                runCatching {
                    BytecodeParser.parseHttpEndpoint(method.genericReturnType, ownerInternalName, method.name)
                }.onFailure { error ->
                    logger.debug("[tapik] Failed to parse signature for $className#${method.name} via reflection", error)
                }.getOrNull()?.let { signatureResult ->
                    logger.lifecycle("[tapik] discovered endpoint ${signatureResult.packageName}.${signatureResult.file}#${signatureResult.name}")
                    results[signatureResult.uniqueKey] = signatureResult
                }
            }
    }

    private fun writeJsonOutput(
        endpoints: List<HttpEndpointMetadata>,
        outputDir: File,
        logger: Logger
    ) {
        outputDir.mkdirs()
        val outputFile = outputDir.resolve("tapik-endpoints.txt")
        outputFile.writeText(
            endpoints.joinToString(separator = System.lineSeparator()) { it.toString() }
        )

        logger.lifecycle("[tapik] Endpoint summary written to: ${outputFile.absolutePath}")
    }

    private fun writeGeneratedSources(
        endpoints: List<HttpEndpointMetadata>,
        outputDir: File,
        endpointPackages: List<String>
    ) {
        if (endpoints.isEmpty()) {
            return
        }

        if (endpointPackages.isNotEmpty()) {
            RestClientBasedClientGenerator.generate(
                endpoints = endpoints,
                rootDir = outputDir
            )
        }
    }

    private fun buildHttpEndpointMetadata(
        signatures: List<HttpEndpointSignature>,
        classpathEntries: List<File>,
        logger: Logger
    ): List<HttpEndpointMetadata> {
        if (signatures.isEmpty()) {
            return emptyList()
        }

        val existingEntries = classpathEntries.filter { it.exists() }

        if (existingEntries.isEmpty()) {
            logger.warn("[tapik] No classpath entries available to reflect endpoints; generated clients will be empty.")
            return emptyList()
        }

        val urls = existingEntries.map { it.toURI().toURL() }.toTypedArray()

        URLClassLoader(urls, javaClass.classLoader).use { classLoader ->
            return signatures.mapNotNull { signature ->
                val endpoint = resolveEndpoint(signature, classLoader, logger) ?: return@mapNotNull null
                runCatching { signature.toMetadata(endpoint) }
                    .onFailure { error ->
                        logger.warn("[tapik] Failed to build metadata for ${signature.packageName}.${signature.file}#${signature.name}", error)
                    }
                    .getOrNull()
            }
        }
    }

    private fun resolveEndpoint(
        signature: HttpEndpointSignature,
        classLoader: ClassLoader,
        logger: Logger
    ): HttpEndpoint<*, *, *, *, *>? {
        val className = signature.ownerInternalName
            ?.replace('/', '.')
            ?: listOfNotNull(signature.packageName.takeIf { it.isNotBlank() }, signature.file)
                .joinToString(".")
        val getterName = signature.methodName ?: buildGetterName(signature.name)

        return runCatching {
            val clazz = classLoader.loadClass(className)
            val method = clazz.methods.firstOrNull { it.name == getterName }
                ?: clazz.declaredMethods.firstOrNull { it.name == getterName }
                ?: throw NoSuchMethodException("Method $getterName not found on $className")
            val instance = if (Modifier.isStatic(method.modifiers)) {
                null
            } else {
                resolveOwnerInstance(clazz)
            }
            method.isAccessible = true
            method.invoke(instance) as? HttpEndpoint<*, *, *, *, *>
                ?: throw IllegalStateException("Resolved member $className#$getterName does not return HttpEndpoint")
        }.onFailure { error ->
            logger.warn("[tapik] Failed to resolve endpoint ${signature.packageName}.${signature.file}#${signature.name}", error)
        }.getOrNull()
    }

    private fun resolveOwnerInstance(clazz: Class<*>): Any? =
        runCatching {
            clazz.getField("INSTANCE").get(null)
        }.recoverCatching {
            clazz.getDeclaredConstructor().apply { isAccessible = true }.newInstance()
        }.getOrNull()

    private fun buildGetterName(propertyName: String): String =
        if (propertyName.isEmpty()) {
            "get"
        } else {
            val capitalized = propertyName.replaceFirstChar { ch ->
                if (ch.isLowerCase()) ch.uppercaseChar().toString() else ch.toString()
            }
            "get$capitalized"
        }

    private fun buildClasspathEntries(
        compiledDir: File,
        additionalDirectories: List<String>,
        runtimeClasspath: Set<File>
    ): List<File> = buildList {
        add(compiledDir)
        additionalDirectories.forEach { add(File(it)) }
        runtimeClasspath.forEach { add(it) }
    }

    private fun shouldProcessMethod(method: Method): Boolean {
        if (method.name == "<init>" || method.name == "<clinit>") {
            return false
        }
        if (method.name.contains("$")) {
            return false
        }
        if (method.isSynthetic || method.isBridge) {
            return false
        }
        if (method.parameterCount != 0) {
            return false
        }
        return true
    }

    private fun HttpEndpointSignature.toMetadata(
        endpoint: HttpEndpoint<*, *, *, *, *>
    ): HttpEndpointMetadata {
        val parametersMetadata = buildParametersMetadata(
            parameterTypes = uriWithParameters.arguments,
            parameterObjects = endpoint.uriWithParameters.extractParameters()
        )
        val inputHeadersMetadata = buildHeaderMetadata(
            headerTypes = inputHeaders.arguments,
            headerObjects = endpoint.inputHeaders.toList()
        )
        val inputBodyMetadata = endpoint.inputBody
            .takeUnless { it === EmptyBody }
            ?.let { createBodyMetadata(inputBody, it) }
        val outputHeadersMetadata = buildHeaderMetadata(
            headerTypes = outputHeaders.arguments,
            headerObjects = endpoint.outputHeaders.toList()
        )
        val outputBodyMetadata = buildOutputBodiesMetadata(
            bodyTypes = outputBodies.arguments,
            outputBodies = endpoint.outputBodies.toList()
        )

        return HttpEndpointMetadata(
            id = endpoint.id,
            propertyName = name,
            description = endpoint.description,
            details = endpoint.details,
            method = endpoint.method.name,
            uri = endpoint.uriWithParameters.toUriSegments(),
            parameters = parametersMetadata,
            inputHeaders = inputHeadersMetadata,
            inputBody = inputBodyMetadata,
            outputHeaders = outputHeadersMetadata,
            outputBodies = outputBodyMetadata,
            packageName = packageName,
            sourceFile = file,
            imports = imports,
            rawType = rawType
        )
    }

    private fun buildParametersMetadata(
        parameterTypes: List<TypeMetadata>,
        parameterObjects: List<Parameter<*>>
    ): List<dev.akif.tapik.gradle.metadata.ParameterMetadata> {
        if (parameterObjects.isEmpty()) {
            return emptyList()
        }

        return parameterObjects.mapIndexed { index, parameter ->
            val typeMetadata = parameterTypes.getOrNull(index)
                ?: runtimeTypeMetadata(parameter::class.java)
            when (parameter.position) {
                ParameterPosition.Path -> PathVariableMetadata(
                    name = parameter.name,
                    type = typeMetadata
                )
                ParameterPosition.Query -> QueryParameterMetadata(
                    name = parameter.name,
                    type = typeMetadata,
                    required = parameter.required,
                    default = (parameter as? QueryParameter<*>)?.default?.toString()
                )
            }
        }
    }

    private fun buildHeaderMetadata(
        headerTypes: List<TypeMetadata>,
        headerObjects: List<Header<*>>
    ): List<HeaderMetadata> {
        if (headerObjects.isEmpty()) {
            return emptyList()
        }

        return headerObjects.mapIndexed { index, header ->
            val typeMetadata = headerTypes.getOrNull(index)
                ?: runtimeTypeMetadata(header::class.java)
            val values = if (header is HeaderValues<*>) {
                header.values.map { it.toString() }
            } else {
                emptyList()
            }
            HeaderMetadata(
                name = header.name,
                type = typeMetadata,
                required = header.required,
                values = values
            )
        }
    }

    private fun buildOutputBodiesMetadata(
        bodyTypes: List<TypeMetadata>,
        outputBodies: List<OutputBody<*>>
    ): List<OutputBodyMetadata> {
        if (outputBodies.isEmpty()) {
            return emptyList()
        }

        return outputBodies.mapIndexed { index, output ->
            val typeMetadata = bodyTypes.getOrNull(index)
            val bodyMetadata = createBodyMetadata(typeMetadata, output.body)
            OutputBodyMetadata(
                description = describeStatusMatcher(output.statusMatcher),
                body = bodyMetadata
            )
        }
    }

    private fun describeStatusMatcher(matcher: StatusMatcher): String = when (matcher) {
        is StatusMatcher.Is -> matcher.status.toString()
        is StatusMatcher.AnyOf -> matcher.statuses.joinToString(", ") { it.toString() }
        is StatusMatcher.Predicate -> matcher.description
        StatusMatcher.Unmatched -> "unmatched"
    }

    private fun createBodyMetadata(
        typeMetadata: TypeMetadata?,
        body: Body<*>
    ): BodyMetadata =
        BodyMetadata(
            type = typeMetadata ?: runtimeTypeMetadata(body::class.java),
            name = null,
            mediaType = body.mediaType?.toString()
        )

    private fun runtimeTypeMetadata(type: Class<*>): TypeMetadata =
        TypeMetadata(
            name = type.simpleName,
            arguments = emptyList()
        )

    private fun Any?.extractParameters(): List<Parameter<*>> =
        this
            .invokeNoArg("getParameters")
            .extractTupleItems()

    private fun Any?.toUriSegments(): List<String> =
        this
            .invokeNoArg("getUri")
            ?.let { segments ->
                if (segments is Collection<*>) {
                    segments.filterIsInstance<String>()
                } else {
                    emptyList()
                }
            }.orEmpty()

    private fun Any?.invokeNoArg(methodName: String): Any? =
        runCatching {
            this?.javaClass
                ?.methods
                ?.firstOrNull { it.name == methodName && it.parameterCount == 0 }
                ?.apply { isAccessible = true }
                ?.invoke(this)
        }.getOrNull()

    @Suppress("UNCHECKED_CAST")
    private fun Any?.extractTupleItems(): List<Parameter<*>> =
        (this as? Tuple)
            ?.toList<Any?>()
            ?.map { it as? Parameter<*> }
            ?.filterNotNull()
            .orEmpty()

    private fun shouldProcessMethod(access: Int, name: String, signature: String?): Boolean {
        if (signature == null) {
            return false
        }
        if (name == "<init>" || name == "<clinit>") {
            return false
        }
        if (name.contains("$")) {
            return false
        }
        if ((access and Opcodes.ACC_SYNTHETIC) != 0 || (access and Opcodes.ACC_BRIDGE) != 0) {
            return false
        }
        return true
    }

    private fun HttpEndpointMetadata.summaryLine(): String =
        buildString {
            append(id)
            append(" -> ")
            append(method)
            append(' ')
            append(uri.joinToString("/"))
            description?.let { append(" : ").append(it) }
        }

}
