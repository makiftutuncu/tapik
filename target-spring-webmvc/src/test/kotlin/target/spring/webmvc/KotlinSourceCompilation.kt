package dev.akif.tapik.target.spring.webmvc

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

internal data class KotlinSourceCompilation(
    val exitCode: ExitCode,
    val messages: String,
    val outputDirectory: Path
)

internal fun compileKotlin(vararg sourceTexts: String): KotlinSourceCompilation {
    val workspace = Files.createTempDirectory("tapik-webmvc-source-").apply { toFile().deleteOnExit() }
    val sources = sourceTexts.mapIndexed { index, sourceText ->
        workspace.resolve("Generated$index.kt").apply { Files.writeString(this, sourceText) }
    }
    val output = Files.createDirectories(workspace.resolve("classes"))
    val compilerOutput = ByteArrayOutputStream()
    val exitCode =
        K2JVMCompiler().exec(
            PrintStream(compilerOutput),
            *sources.map(Path::toString).toTypedArray(),
            "-d",
            output.toString(),
            "-classpath",
            System.getProperty("java.class.path"),
            "-module-name",
            "webmvc-generated-fixture"
        )
    return KotlinSourceCompilation(exitCode, compilerOutput.toString(), output)
}
