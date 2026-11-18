package dev.akif.tapik.test.fixtures.generation

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.GenerationResult
import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry

// tag::custom-target[]
/** Registers a target that lists the selected API IDs. */
class ApiListTargetRegistry : GenerationTargetRegistry {
    /** Target contributed to tapik's host. */
    override val targets: List<GenerationTarget> = listOf(ApiListTarget)
}

/** Writes selected tapik API IDs to a text documentation artifact. */
object ApiListTarget : GenerationTarget {
    /** Stable target ID used by build configuration. */
    override val id: String = "api-list"

    /** Returns one documentation artifact listing the selected API IDs. */
    override fun generate(request: GenerationRequest): GenerationResult =
        GenerationResult(
            listOf(
                GeneratedArtifact(
                    relativePath = "apis.txt",
                    mediaType = "text/plain",
                    kind = ArtifactKind.DOCUMENTATION,
                    content = request.apis.joinToString("\n") { api -> api.apiId }
                )
            )
        )
}
// end::custom-target[]
