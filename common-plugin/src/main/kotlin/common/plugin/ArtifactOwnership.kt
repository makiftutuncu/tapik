package dev.akif.tapik.common.plugin

import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.LinkOption.NOFOLLOW_LINKS
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.util.Base64

internal class ArtifactOwnership(
    private val root: Path
) {
    private val manifest: Path = root.resolve(MANIFEST_NAME)

    fun claimedPaths(owner: String, artifacts: List<GeneratedArtifact>): Set<String> {
        val paths = artifacts.map(GeneratedArtifact::relativePath)
        require(paths.none { path -> path == MANIFEST_NAME || path.startsWith("$MANIFEST_NAME/") }) {
            "Generated artifact path '$MANIFEST_NAME' is reserved by tapik"
        }
        val ownership = read()
        val requested = artifacts.associate { artifact -> artifact.relativePath to artifact.sharingKey }
        val conflicts =
            ownership
                .filterKeys { candidate -> candidate != owner }
                .flatMap { (candidate, claims) ->
                    claims.mapNotNull { claim ->
                        if (claim.path !in requested) return@mapNotNull null
                        val sharingKey = requested[claim.path]
                        (claim.path to candidate).takeUnless {
                            claim.sharingKey != null && claim.sharingKey == sharingKey
                        }
                    }
                }
        check(conflicts.isEmpty()) {
            conflicts.joinToString(
                prefix = "Generated artifact paths are already owned by other executions: ",
                transform = { (path, candidate) -> "'$path' by '$candidate'" }
            )
        }
        val claimed = ownership[owner].orEmpty().mapTo(linkedSetOf(), OwnershipClaim::path)
        val ownedByOthers = ownership.filterKeys { candidate -> candidate != owner }.values.flatten().mapTo(hashSetOf(), OwnershipClaim::path)
        val unowned = paths.filter { path -> path !in claimed && path !in ownedByOthers && Files.exists(root.resolve(path), NOFOLLOW_LINKS) }
        check(unowned.isEmpty()) {
            unowned.joinToString(
                prefix = "Generated artifact paths already exist without tapik ownership for execution '$owner': ",
                transform = { path -> "'$path'" }
            )
        }
        return claimed
    }

    fun pathsNotClaimedByOthers(owner: String, paths: Set<String>): Set<String> {
        val claimedByOthers =
            read()
                .filterKeys { candidate -> candidate != owner }
                .values
                .flatten()
                .mapTo(hashSetOf(), OwnershipClaim::path)
        return paths - claimedByOthers
    }

    fun update(owner: String, artifacts: List<GeneratedArtifact>) {
        val ownership = read().toMutableMap()
        val claims = artifacts.mapTo(linkedSetOf()) { artifact -> OwnershipClaim(artifact.relativePath, artifact.sharingKey) }
        if (claims.isEmpty()) ownership.remove(owner) else ownership[owner] = claims
        if (ownership.isEmpty()) {
            Files.deleteIfExists(manifest)
            return
        }

        val lines =
            ownership.toSortedMap().flatMap { (candidate, claims) ->
                claims.sortedBy(OwnershipClaim::path).map { claim ->
                    "${candidate.encoded()}\t${claim.path.encoded()}\t${claim.sharingKey?.encoded() ?: NO_SHARING_KEY}"
                }
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

    private fun read(): Map<String, Set<OwnershipClaim>> {
        if (!Files.exists(manifest)) return emptyMap()
        val ownership = linkedMapOf<String, MutableSet<OwnershipClaim>>()
        Files.readAllLines(manifest, UTF_8).forEach { line ->
            val fields = line.split('\t')
            check(fields.size == 2 || fields.size == 3) { "Invalid tapik artifact ownership manifest at '$manifest'" }
            val sharingKey = fields.getOrNull(2)?.takeUnless { field -> field == NO_SHARING_KEY }?.decoded()
            ownership.getOrPut(fields[0].decoded(), ::linkedSetOf).add(OwnershipClaim(fields[1].decoded(), sharingKey))
        }
        return ownership
    }
}

private data class OwnershipClaim(
    val path: String,
    val sharingKey: String?
)

private const val MANIFEST_NAME: String = ".tapik-ownership"
private const val NO_SHARING_KEY: String = "-"
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
