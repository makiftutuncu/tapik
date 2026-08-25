package dev.akif.tapik.common.plugin

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import kotlin.io.path.isDirectory

/** Writes host-neutral generation results to a build-tool-owned directory. */
object ArtifactWriter {
    /**
     * Writes every artifact in [result] beneath [outputDirectory].
     *
     * Existing files at generated paths are replaced. Paths previously written by [owner] but absent from [result] are
     * removed. Paths belonging to other owners and unowned files are left untouched.
     *
     * @param result generated artifacts to write.
     * @param outputDirectory root directory owned by the host adapter.
     * @param owner stable identity of the host execution writing these artifacts.
     * @return written paths in artifact order.
     * @throws java.io.IOException when a directory or file cannot be written.
     * @throws IllegalArgumentException when an artifact resolves outside [outputDirectory].
     * @throws IllegalStateException when another execution owns a generated path.
     */
    @Synchronized
    fun write(
        result: GenerationResult,
        outputDirectory: Path,
        owner: String
    ): List<Path> {
        require(owner.isNotBlank()) { "Generated artifact owner must not be blank" }
        val root = outputDirectory.toAbsolutePath().normalize()
        val paths = result.artifacts.map { artifact ->
            val path = root.resolve(artifact.relativePath).normalize()
            require(path.startsWith(root)) {
                "Generated artifact path must remain beneath the output directory: '${artifact.relativePath}'"
            }
            path
        }
        val ownership = ArtifactOwnership(root)
        val previousPaths = ownership.claimedPaths(owner, result.artifacts.map(GeneratedArtifact::relativePath))
        val currentPaths = result.artifacts.mapTo(linkedSetOf(), GeneratedArtifact::relativePath)
        val stalePaths = previousPaths - currentPaths

        Files.createDirectories(root)
        val staging = Files.createTempDirectory(root, ".tapik-stage-")
        try {
            result.artifacts.forEach { artifact ->
                val staged = staging.resolve(artifact.relativePath)
                Files.createDirectories(requireNotNull(staged.parent))
                Files.writeString(staged, artifact.content, UTF_8)
            }
            stalePaths.forEach { relativePath -> Files.deleteIfExists(root.resolve(relativePath)) }
            stalePaths.forEach { relativePath -> deleteEmptyParents(root.resolve(relativePath).parent, root) }
            result.artifacts.zip(paths).forEach { (artifact, path) ->
                Files.createDirectories(requireNotNull(path.parent))
                moveReplacing(staging.resolve(artifact.relativePath), path)
            }
            ownership.update(owner, currentPaths)
        } finally {
            deleteRecursively(staging)
        }
        return paths
    }
}

private fun moveReplacing(source: Path, target: Path) {
    try {
        Files.move(source, target, REPLACE_EXISTING, ATOMIC_MOVE)
    } catch (_: AtomicMoveNotSupportedException) {
        Files.move(source, target, REPLACE_EXISTING)
    }
}

private fun deleteEmptyParents(path: Path?, root: Path) {
    var current = path
    while (current != null && current != root && current.isDirectory() && isEmpty(current)) {
        Files.delete(current)
        current = current.parent
    }
}

private fun isEmpty(directory: Path): Boolean = Files.list(directory).use { entries -> entries.findAny().isEmpty }

private fun deleteRecursively(root: Path) {
    if (!Files.exists(root)) return
    Files.walk(root).use { paths ->
        paths.sorted(Comparator.reverseOrder()).forEach(Files::deleteIfExists)
    }
}
