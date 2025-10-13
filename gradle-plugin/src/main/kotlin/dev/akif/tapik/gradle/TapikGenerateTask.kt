package dev.akif.tapik.gradle

import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logger
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
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

        val endpoints = scanForHttpEndpoints(compiledDir, pkgs)

        writeJsonOutput(endpoints, outDir, logger)
        writeGeneratedSources(endpoints, generatedSourcesDir, pkgs)
    }

    private fun scanForHttpEndpoints(compiledDir: File, packages: List<String>): List<HttpEndpointDescription> {
        val packageFilters = packages.map { it.replace('.', '/') }
        val results = LinkedHashMap<String, HttpEndpointDescription>()

        val classRepository = ClassNodeRepository(
            compiledDir,
            additionalClassDirectories.getOrElse(emptyList()).map(::File),
            logger
        )
        val metadataAnalyzer = EndpointMetadataAnalyzer(logger, classRepository::load)

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
                    val metadataByMethod = metadataAnalyzer.analyze(classNode)
                    classNode.methods.forEach { method ->
                        val signature = method.signature ?: return@forEach
                        val methodName = method.name
                        if (!shouldProcessMethod(method.access, methodName, signature)) {
                            return@forEach
                        }
                        val metadata = resolveMetadata(classNode.name, methodName, method.desc, metadataByMethod)
                        runCatching {
                            BytecodeParser.parseHttpEndpoint(signature, classNode.name, methodName, metadata)
                        }.onFailure { error ->
                            logger.debug("[tapik] Failed to parse signature for ${classNode.name}#$methodName", error)
                        }.getOrNull()?.let { description ->
                            results[description.uniqueKey()] = description
                        }
                    }
                } catch (e: Exception) {
                    logger.warn("[tapik] Failed to analyse class: ${classFile.absolutePath}", e)
                }
            }

        return results.values
            .sortedWith(compareBy<HttpEndpointDescription> { it.packageName }
                .thenBy { it.file }
                .thenBy { it.name })
    }

    private fun HttpEndpointDescription.uniqueKey(): String = buildString {
        append(packageName)
        append('/')
        append(file)
        append('#')
        append(name)
    }

    private fun writeJsonOutput(
        endpoints: List<HttpEndpointDescription>,
        outputDir: File,
        logger: Logger
    ) {
        val json = Json { prettyPrint = true }
        val jsonOutput = json.encodeToString(endpoints)

        outputDir.mkdirs()
        val outputFile = outputDir.resolve("tapik-endpoints.json")
        outputFile.writeText(jsonOutput)

        logger.lifecycle("[tapik] JSON report written to: ${outputFile.absolutePath}")
    }

    private fun writeGeneratedSources(
        endpoints: List<HttpEndpointDescription>,
        outputDir: File,
        endpointPackages: List<String>
    ) {
        if (endpoints.isEmpty()) {
            return
        }

        if (endpointPackages.isNotEmpty()) {
            SpringRestClientCodeGenerator.generate(
                endpoints = endpoints,
                rootDir = outputDir
            )
        }
    }

    private fun resolveMetadata(
        ownerInternalName: String,
        methodName: String,
        descriptor: String,
        metadataByMethod: Map<MethodKey, EndpointMetadata>
    ): EndpointMetadata? {
        val directKey = MethodKey(ownerInternalName, methodName, descriptor)
        metadataByMethod[directKey]?.let { return it }

        val propertyName = methodName.toPropertyName() ?: return null
        val delegatePrefix = "${propertyName}_delegate\$lambda"
        return metadataByMethod.entries
            .firstOrNull { (key, _) ->
                key.ownerInternalName == ownerInternalName && key.name.startsWith(delegatePrefix)
            }
            ?.value
    }

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

    private fun String.toPropertyName(): String? = when {
        startsWith("get") && length > 3 -> substring(3).replaceFirstChar { it.lowercaseChar() }
        startsWith("is") && length > 2 -> substring(2).replaceFirstChar { it.lowercaseChar() }
        else -> null
    }
}

private class ClassNodeRepository(
    private val compiledDir: File,
    additionalDirs: List<File>,
    private val logger: Logger
) {
    private val cache = mutableMapOf<String, ClassNode?>()
    private val searchDirectories: List<File> = buildList {
        add(compiledDir)
        additionalDirs.filterTo(this) { it != compiledDir }
    }

    fun load(internalName: String): ClassNode? = cache.getOrPut(internalName) {
        loadFromFile(internalName) ?: loadFromClasspath(internalName)
    }

    private fun loadFromFile(internalName: String): ClassNode? {
        val relativePath = "$internalName.class"
        searchDirectories.forEach { dir ->
            val file = File(dir, relativePath)
            if (file.exists()) {
                return runCatching {
                    ClassReader(file.readBytes()).toClassNode()
                }.onFailure {
                    logger.debug("[tapik] Failed to read class from file: $file", it)
                }.getOrNull()
            }
        }
        return null
    }

    private fun loadFromClasspath(internalName: String): ClassNode? {
        val resourceName = "$internalName.class"
        val loaders = sequenceOf(
            this::class.java.classLoader,
            Thread.currentThread().contextClassLoader
        ).filterNotNull()

        for (loader in loaders) {
            loader.getResourceAsStream(resourceName)?.use { stream ->
                return runCatching {
                    ClassReader(stream.readBytes()).toClassNode()
                }.onFailure {
                    logger.debug("[tapik] Failed to read class from classpath: $resourceName", it)
                }.getOrNull()
            }
        }
        return null
    }

    private fun ClassReader.toClassNode(): ClassNode = ClassNode().also {
        accept(it, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
    }
}
