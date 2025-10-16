package dev.akif.tapik.gradle

import dev.akif.tapik.gradle.metadata.BodyMetadata
import dev.akif.tapik.gradle.metadata.HeaderMetadata
import dev.akif.tapik.gradle.metadata.HttpEndpointMetadata
import dev.akif.tapik.gradle.metadata.OutputBodyMetadata
import dev.akif.tapik.gradle.metadata.PathVariableMetadata
import dev.akif.tapik.gradle.metadata.QueryParameterMetadata
import dev.akif.tapik.gradle.metadata.TypeMetadata
import dev.akif.tapik.http.Body
import dev.akif.tapik.http.EmptyBody
import dev.akif.tapik.http.Header
import dev.akif.tapik.http.HeaderValues
import dev.akif.tapik.http.HttpEndpoint
import dev.akif.tapik.http.OutputBody
import dev.akif.tapik.http.Parameter
import dev.akif.tapik.http.ParameterPosition
import dev.akif.tapik.http.QueryParameter
import dev.akif.tapik.http.StatusMatcher
import dev.akif.tapik.tuples.Tuple
import java.io.File
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

@CacheableTask
abstract class TapikGenerateTask : DefaultTask() {
    @get:Input
    abstract val endpointPackages: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDirectory: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    abstract val compiledClassesDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val generatedSourcesDirectory: DirectoryProperty

    @get:Input
    abstract val additionalClassDirectories: ListProperty<String>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val runtimeClasspath: ConfigurableFileCollection

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

        val signatures = scanForHttpEndpoints(compiledDir, pkgs)

        val metadata = buildHttpEndpointMetadata(
            signatures = signatures,
            compiledDir = compiledDir,
            additionalDirectories = additionalClassDirectories.getOrElse(emptyList()),
            runtimeClasspath = runtimeClasspath.files,
            logger = logger
        )

        writeJsonOutput(metadata, outDir, logger)
        writeGeneratedSources(metadata, generatedSourcesDir, pkgs)
    }

    private fun scanForHttpEndpoints(compiledDir: File, packages: List<String>): List<HttpEndpointSignature> {
        val packageFilters = packages.map { it.replace('.', '/') }
        val results = LinkedHashMap<String, HttpEndpointSignature>()

        compiledDir.walkTopDown()
            .filter { it.isFile && it.extension == "class" }
            .forEach { classFile ->
                val relativePath = classFile.relativeTo(compiledDir).path.replace(File.separatorChar, '/')
                if (packageFilters.isNotEmpty() && packageFilters.none { relativePath.startsWith(it) }) {
                    return@forEach
                }

                try {
                    val classReader = ClassReader(classFile.readBytes())
                    val classNode = ClassNode()
                    classReader.accept(classNode, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                    classNode.methods.forEach { method ->
                        val signature = method.signature ?: return@forEach
                        val methodName = method.name
                        if (!shouldProcessMethod(method.access, methodName, signature)) {
                            return@forEach
                        }
                        runCatching {
                            BytecodeParser.parseHttpEndpoint(signature, classNode.name, methodName)
                        }.onFailure { error ->
                            logger.debug("[tapik] Failed to parse signature for ${classNode.name}#$methodName", error)
                        }.getOrNull()?.let { signatureResult ->
                            logger.lifecycle("[tapik] discovered endpoint ${signatureResult.packageName}.${signatureResult.file}#${signatureResult.name}")
                            results[signatureResult.uniqueKey] = signatureResult
                        }
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

    private fun writeJsonOutput(
        endpoints: List<HttpEndpointMetadata>,
        outputDir: File,
        logger: Logger
    ) {
        outputDir.mkdirs()
        val outputFile = outputDir.resolve("tapik-endpoints.txt")
        outputFile.writeText(
            endpoints.joinToString(separator = System.lineSeparator()) { it.summaryLine() }
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
            SpringRestClientClientGenerator.generate(
                endpoints = endpoints,
                rootDir = outputDir
            )
        }
    }

    private fun buildHttpEndpointMetadata(
        signatures: List<HttpEndpointSignature>,
        compiledDir: File,
        additionalDirectories: List<String>,
        runtimeClasspath: Set<File>,
        logger: Logger
    ): List<HttpEndpointMetadata> {
        if (signatures.isEmpty()) {
            return emptyList()
        }

        val classpathEntries = buildList<File> {
            add(compiledDir)
            additionalDirectories.forEach { add(File(it)) }
            runtimeClasspath.forEach { add(it) }
        }.filter { it.exists() }

        if (classpathEntries.isEmpty()) {
            logger.warn("[tapik] Classpath is empty; generated clients may be incomplete.")
            return emptyList()
        }

        if (classpathEntries.isEmpty()) {
            logger.warn("[tapik] No classpath entries available to reflect endpoints; generated clients will be empty.")
            return emptyList()
        }

        val urls = classpathEntries.map { it.toURI().toURL() }.toTypedArray()

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

    private fun HttpEndpointSignature.toMetadata(
        endpoint: HttpEndpoint<*, *, *, *, *>
    ): HttpEndpointMetadata {
        val parametersMetadata = buildParametersMetadata(
            parameterTypes = parameters.arguments,
            parameterObjects = endpoint.parameters.extractTupleItems()
        )
        val inputHeadersMetadata = buildHeaderMetadata(
            headerTypes = inputHeaders.arguments,
            headerObjects = endpoint.inputHeaders.extractTupleItems()
        )
        val inputBodyMetadata = endpoint.inputBody
            .takeUnless { it === EmptyBody }
            ?.let { createBodyMetadata(inputBody, it) }
        val outputHeadersMetadata = buildHeaderMetadata(
            headerTypes = outputHeaders.arguments,
            headerObjects = endpoint.outputHeaders.extractTupleItems()
        )
        val outputBodyMetadata = buildOutputBodiesMetadata(
            bodyTypes = outputBodies.arguments,
            outputBodies = endpoint.outputBodies.extractTupleItems()
        )

        return HttpEndpointMetadata(
            id = endpoint.id,
            propertyName = name,
            description = endpoint.description,
            details = endpoint.details,
            method = endpoint.method.name,
            uri = endpoint.uri,
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

    @Suppress("UNCHECKED_CAST")
    private fun <T> Any?.extractTupleItems(): List<T> =
        (this as? Tuple<*>)?.toList()?.map { it as T }.orEmpty()

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
