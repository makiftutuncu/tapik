package dev.akif.tapik.plugin.maven

import org.apache.maven.plugin.MojoExecution
import java.nio.file.Files
import java.nio.file.Path

internal enum class MavenGenerationMode(val compileGeneratedSources: Boolean) {
    COMPILED_CONTRACT(false),
    SAME_MODULE(true),
    DIRECT(true);

    fun requireCompiledOutput(directory: Path) {
        if (this != DIRECT) return
        require(Files.isDirectory(directory)) {
            "Direct tapik generation does not run user compilation or preceding lifecycle phases. " +
                "Main output directory '$directory' is missing; compile same-module APIs first, " +
                "or bind generation to generate-sources for a compiled contract dependency."
        }
    }

    companion object {
        fun from(execution: MojoExecution): MavenGenerationMode {
            if (execution.source == MojoExecution.Source.CLI) return DIRECT
            return when (execution.lifecyclePhase) {
                "generate-sources" -> COMPILED_CONTRACT
                "process-classes" -> SAME_MODULE
                else -> throw IllegalArgumentException(
                    "Unsupported tapik generation lifecycle phase '${execution.lifecyclePhase ?: "<missing>"}'. " +
                        "Bind generation to generate-sources for compiled contracts or process-classes for same-module APIs."
                )
            }
        }
    }
}
