package dev.akif.tapik.plugin.spring.restclient

import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry

/** Contributes the Spring RestClient target to generation hosts. */
class RestClientTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(RestClientTarget)
}
