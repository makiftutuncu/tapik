package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.plugin.core.GenerationTarget
import dev.akif.tapik.plugin.core.GenerationTargetRegistry

/** Makes the Spring WebMVC target discoverable by generation hosts. */
class WebMvcTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(WebMvcTarget)
}
