package dev.akif.tapik.plugin.spring.webmvc

import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry

/** Makes the Spring WebMVC target discoverable by generation hosts. */
class WebMvcTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(WebMvcTarget)
}
