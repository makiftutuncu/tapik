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
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

@CacheableTask
abstract class TapikGenerateTask : DefaultTask() {
    @get:Input
    abstract val endpointPackages: ListProperty<String>

    @get:Input
    abstract val useContextReceivers: Property<Boolean>

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

        writeJsonOutput(endpoints, outDir, pkgs, logger)
        writeGeneratedSources(endpoints, generatedSourcesDir, pkgs)
    }

    private fun scanForHttpEndpoints(compiledDir: File, packages: List<String>): List<HttpEndpointDescription> {
        val packageFilters = packages.map { it.replace('.', '/') }
        val results = LinkedHashMap<String, HttpEndpointDescription>()

        compiledDir.walkTopDown()
            .filter { it.isFile && it.extension == "class" }
            .forEach { classFile ->
                val relativePath = classFile.relativeTo(compiledDir).path.replace(File.separatorChar, '/')
                if (packageFilters.isNotEmpty() && packageFilters.none { relativePath.startsWith(it) }) {
                    return@forEach
                }

                try {
                    val classReader = ClassReader(classFile.readBytes())
                    classReader.accept(ClassScanner(logger) { signature, owner, memberName ->
                        runCatching {
                            BytecodeParser.parseHttpEndpoint(signature, owner, memberName)
                        }.onFailure { error ->
                            logger.debug("[tapik] Failed to parse signature for $owner#$memberName", error)
                        }.getOrNull()?.let { description ->
                            results[description.uniqueKey()] = description
                        }
                    }, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
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
        endpointPackages: List<String>,
        logger: Logger
    ) {
        val report = HttpEndpointsReport(
            endpointPackages = endpointPackages,
            endpoints = endpoints
        )

        val json = Json { prettyPrint = true }
        val jsonOutput = json.encodeToString(report)

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
                rootDir = outputDir,
                useContextReceivers = useContextReceivers.getOrElse(true)
            )
        }
    }

    private class ClassScanner(
        private val logger: Logger,
        private val onEndpointFound: (signature: String, owner: String, name: String) -> Unit
    ) : ClassVisitor(Opcodes.ASM9) {
        private var owner: String = ""

        override fun visit(
            version: Int,
            access: Int,
            name: String?,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            if (name != null) {
                owner = name
            }
            super.visit(version, access, name, signature, superName, interfaces)
        }

        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            if (signature != null &&
                name != "<init>" &&
                name != "<clinit>" &&
                (name.startsWith("get") || name.startsWith("is")) &&
                (access and Opcodes.ACC_SYNTHETIC) == 0 &&
                (access and Opcodes.ACC_BRIDGE) == 0
            ) {
                try {
                    onEndpointFound(signature, owner, name)
                } catch (e: Exception) {
                    logger.debug("[tapik] Error while handling $owner#$name", e)
                }
            }

            return super.visitMethod(access, name, descriptor, signature, exceptions)
                ?: object : MethodVisitor(Opcodes.ASM9) {}
        }
    }
}
