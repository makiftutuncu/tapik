package dev.akif.tapik.plugin.maven

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

internal object GeneratedKotlinCompiler {
    fun compile(
        sources: List<Path>,
        classpath: List<Path>,
        outputDirectory: Path,
        moduleName: String
    ) {
        if (sources.isEmpty()) return
        require(moduleName.isNotBlank()) { "Generated Kotlin module name must not be blank" }
        Files.createDirectories(outputDirectory)
        val compilerOutput = ByteArrayOutputStream()
        val arguments =
            buildList {
                addAll(sources.map { source -> source.toAbsolutePath().normalize().toString() })
                addAll(listOf("-d", outputDirectory.toAbsolutePath().normalize().toString()))
                addAll(
                    listOf(
                        "-classpath",
                        (listOf(outputDirectory) + classpath)
                            .map { path -> path.toAbsolutePath().normalize() }
                            .distinct()
                            .joinToString(File.pathSeparator)
                    )
                )
                addAll(listOf("-jvm-target", "25"))
                addAll(listOf("-module-name", moduleName))
                add("-no-stdlib")
                add("-no-reflect")
            }
        val exitCode =
            PrintStream(compilerOutput).use { output ->
                K2JVMCompiler().exec(output, *arguments.toTypedArray())
            }
        check(exitCode == ExitCode.OK) {
            "Generated Kotlin compilation failed:\n${compilerOutput.toString().trim()}"
        }
    }
}
