package dev.akif.tapik.spring.restclient

import dev.akif.tapik.plugin.core.GenerationTarget
import dev.akif.tapik.plugin.core.GenerationTargetRegistry

/** Contributes the Spring RestClient target to generation hosts. */
class RestClientTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(RestClientTarget)
}
