package dev.akif.tapik.plugin.compiler

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.io.path.inputStream

internal data class Compilation(
    val exitCode: ExitCode,
    val outputDirectory: Path,
    val messages: String
)

internal fun compile(
    sourceText: String,
    workspace: Path = compilerWorkspace(),
    incremental: Boolean = false
): Compilation {
    val source = workspace.resolve("Fixture.kt").apply { Files.writeString(this, sourceText) }
    val output = Files.createDirectories(workspace.resolve("classes"))
    val pluginJar = pluginJar(workspace.resolve("tapik-plugin-compiler.jar"))
    val compilerOutput = ByteArrayOutputStream()
    val arguments =
        buildList {
            add(source.toString())
            addAll(listOf("-d", output.toString()))
            addAll(listOf("-classpath", System.getProperty("java.class.path")))
            add("-Xplugin=${pluginJar}")
            addAll(listOf("-module-name", "compiler-plugin-fixture"))
            if (incremental) add("-Xenable-incremental-compilation")
        }
    val exitCode = K2JVMCompiler().exec(PrintStream(compilerOutput), *arguments.toTypedArray())
    return Compilation(exitCode, output, compilerOutput.toString())
}

internal fun compilerWorkspace(): Path =
    Files.createTempDirectory("tapik-compiler-").apply { toFile().deleteOnExit() }

internal fun generatedRegistryClasses(outputDirectory: Path): List<Path> {
    val generatedPackage = outputDirectory.resolve("dev/akif/tapik/generated")
    if (!Files.isDirectory(generatedPackage)) return emptyList()
    return Files.list(generatedPackage).use { paths ->
        paths
            .filter { path ->
                path.fileName.toString().startsWith("TapikApiRegistry") &&
                    path.fileName.toString().endsWith(".class")
            }
            .toList()
    }
}

private fun pluginJar(destination: Path): Path {
    val classes = Path.of("target", "classes")
    JarOutputStream(Files.newOutputStream(destination)).use { jar ->
        Files.walk(classes).use { paths ->
            paths
                .filter(Files::isRegularFile)
                .forEach { path ->
                    val relative = classes.relativize(path).toString().replace(path.fileSystem.separator, "/")
                    jar.putNextEntry(JarEntry(relative))
                    path.inputStream().use { input -> input.copyTo(jar) }
                    jar.closeEntry()
                }
        }
    }
    return destination
}

internal const val API_REGISTRY_SERVICE_PATH: String = "META-INF/services/dev.akif.tapik.ApiRegistry"
