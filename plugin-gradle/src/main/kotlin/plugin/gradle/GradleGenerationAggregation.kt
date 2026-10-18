package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationResult
import java.nio.charset.StandardCharsets.UTF_8

internal data class GradleExecutionGeneration(
    val executionName: String,
    val result: GenerationResult
)

internal fun aggregateGradleGenerationResults(results: List<GradleExecutionGeneration>): GenerationResult {
    val claims = linkedMapOf<String, GradleArtifactClaim>()
    results.forEach { generation ->
        generation.result.artifacts.forEach { artifact ->
            val routed = artifact.copy(relativePath = "${artifact.kind.directory}/${artifact.relativePath}")
            val existing = claims[routed.relativePath]
            if (existing == null) {
                claims[routed.relativePath] = GradleArtifactClaim(generation.executionName, routed)
            } else {
                check(existing.artifact.compatiblyShares(routed)) {
                    "tapik generated path '${routed.relativePath}' conflicts between executions " +
                        "'${existing.executionName}' and '${generation.executionName}'"
                }
            }
        }
    }
    return GenerationResult(claims.values.map(GradleArtifactClaim::artifact))
}

private data class GradleArtifactClaim(
    val executionName: String,
    val artifact: GeneratedArtifact
)

private val ArtifactKind.directory: String
    get() =
        when (this) {
            ArtifactKind.SOURCE -> "sources"
            ArtifactKind.RESOURCE -> "resources"
            ArtifactKind.DOCUMENTATION -> "documentation"
        }

private fun GeneratedArtifact.compatiblyShares(other: GeneratedArtifact): Boolean =
    sharingKey != null &&
        sharingKey == other.sharingKey &&
        content.toByteArray(UTF_8).contentEquals(other.content.toByteArray(UTF_8))
