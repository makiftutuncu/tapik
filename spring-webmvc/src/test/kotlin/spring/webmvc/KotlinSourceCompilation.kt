package dev.akif.tapik.spring.webmvc

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
    val workspace = Files.createTempDirectory("tapik-webmvc-source-").apply { toFile().deleteOnExit() }
    val source = workspace.resolve("GeneratedServer.kt").apply { Files.writeString(this, sourceText) }
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
            "webmvc-generated-fixture"
        )
    return KotlinSourceCompilation(exitCode, compilerOutput.toString())
}
