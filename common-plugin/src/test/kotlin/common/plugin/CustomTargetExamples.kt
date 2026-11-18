package dev.akif.tapik.test.fixtures.generation

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.GenerationResult
import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry

// tag::custom-target[]
class ApiListTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(ApiListTarget)
}

object ApiListTarget : GenerationTarget {
    override val id: String = "api-list"

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
