package dev.akif.tapik.target.openapi

import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry

/** Contributes the OpenAPI generation target to host adapters. */
class OpenApiTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(OpenApiTarget)
}
