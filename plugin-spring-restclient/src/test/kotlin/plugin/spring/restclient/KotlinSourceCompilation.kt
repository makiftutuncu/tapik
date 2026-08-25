package dev.akif.tapik.plugin.spring.restclient

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files

internal data class KotlinSourceCompilation(
    val exitCode: ExitCode,
    val messages: String
)

internal fun compileKotlin(sourceText: String): KotlinSourceCompilation {
    val workspace = Files.createTempDirectory("tapik-restclient-source-").apply { toFile().deleteOnExit() }
    val source = workspace.resolve("GeneratedClient.kt").apply { Files.writeString(this, sourceText) }
    val output = Files.createDirectories(workspace.resolve("classes"))
    val compilerOutput = ByteArrayOutputStream()
    val exitCode =
        K2JVMCompiler().exec(
            PrintStream(compilerOutput),
            source.toString(),
            "-d",
            output.toString(),
            "-classpath",
            System.getProperty("java.class.path"),
            "-module-name",
            "restclient-generated-fixture"
        )
    return KotlinSourceCompilation(exitCode, compilerOutput.toString())
}
