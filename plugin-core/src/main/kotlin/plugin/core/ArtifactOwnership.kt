package dev.akif.tapik.plugin.core

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.Base64

internal class ArtifactOwnership(
    private val root: Path
) {
    private val manifest: Path = root.resolve(MANIFEST_NAME)

    fun claimedPaths(owner: String, paths: List<String>): Set<String> {
        require(paths.none { path -> path == MANIFEST_NAME || path.startsWith("$MANIFEST_NAME/") }) {
            "Generated artifact path '$MANIFEST_NAME' is reserved by Tapik"
        }
        val ownership = read()
        val conflicts =
            ownership
                .filterKeys { candidate -> candidate != owner }
                .flatMap { (candidate, ownedPaths) -> ownedPaths.intersect(paths.toSet()).map { it to candidate } }
        check(conflicts.isEmpty()) {
            conflicts.joinToString(
                prefix = "Generated artifact paths are already owned by other executions: ",
                transform = { (path, candidate) -> "'$path' by '$candidate'" }
            )
        }
        return ownership[owner].orEmpty()
    }

    fun update(owner: String, paths: Set<String>) {
        val ownership = read().toMutableMap()
        if (paths.isEmpty()) ownership.remove(owner) else ownership[owner] = paths
        if (ownership.isEmpty()) {
            Files.deleteIfExists(manifest)
            return
        }

        val lines =
            ownership.toSortedMap().flatMap { (candidate, ownedPaths) ->
                ownedPaths.sorted().map { path -> "${candidate.encoded()}\t${path.encoded()}" }
            }
        Files.createDirectories(root)
        val temporary = Files.createTempFile(root, ".tapik-ownership-", ".tmp")
        try {
            Files.write(temporary, lines, UTF_8)
            moveManifest(temporary, manifest)
        } finally {
            Files.deleteIfExists(temporary)
        }
    }

    private fun read(): Map<String, Set<String>> {
        if (!Files.exists(manifest)) return emptyMap()
        val ownership = linkedMapOf<String, MutableSet<String>>()
        Files.readAllLines(manifest, UTF_8).forEach { line ->
            val fields = line.split('\t')
            check(fields.size == 2) { "Invalid Tapik artifact ownership manifest at '$manifest'" }
            ownership.getOrPut(fields[0].decoded(), ::linkedSetOf).add(fields[1].decoded())
        }
        return ownership
    }
}

private const val MANIFEST_NAME: String = ".tapik-ownership"
private val ENCODER: Base64.Encoder = Base64.getUrlEncoder().withoutPadding()
private val DECODER: Base64.Decoder = Base64.getUrlDecoder()

private fun String.encoded(): String = ENCODER.encodeToString(toByteArray(UTF_8))

private fun String.decoded(): String = String(DECODER.decode(this), UTF_8)

private fun moveManifest(source: Path, target: Path) {
    try {
        Files.move(source, target, REPLACE_EXISTING, ATOMIC_MOVE)
    } catch (_: AtomicMoveNotSupportedException) {
        Files.move(source, target, REPLACE_EXISTING)
    }
}
