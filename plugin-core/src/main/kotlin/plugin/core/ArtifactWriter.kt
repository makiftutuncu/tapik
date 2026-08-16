package dev.akif.tapik.plugin.core

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path

/** Writes host-neutral generation results to a build-tool-owned directory. */
object ArtifactWriter {
    /**
     * Writes every artifact in [result] beneath [outputDirectory].
     *
     * Existing files at generated paths are replaced. Other files in the output directory are left untouched.
     *
     * @param result generated artifacts to write.
     * @param outputDirectory root directory owned by the host adapter.
     * @return written paths in artifact order.
     * @throws java.io.IOException when a directory or file cannot be written.
     * @throws IllegalArgumentException when an artifact resolves outside [outputDirectory].
     */
    fun write(
        result: GenerationResult,
        outputDirectory: Path
    ): List<Path> {
        val root = outputDirectory.toAbsolutePath().normalize()
        return result.artifacts.map { artifact ->
            val path = root.resolve(artifact.relativePath).normalize()
            require(path.startsWith(root)) {
                "Generated artifact path must remain beneath the output directory: '${artifact.relativePath}'"
            }
            Files.createDirectories(requireNotNull(path.parent))
            Files.writeString(path, artifact.content, UTF_8)
            path
        }
    }
}
