package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api

/**
 * Input supplied to one [GenerationTarget].
 *
 * @property apis every API selected for this execution.
 * @property configuration target-owned, host-neutral configuration.
 */
data class GenerationRequest(
    val apis: List<Api>,
    val configuration: TargetConfiguration = TargetConfiguration()
)

/** A host-neutral generation target. */
interface GenerationTarget {
    /** Stable ID selected by build-tool and command-line adapters. */
    val id: String

    /**
     * Generates artifacts for [request] without writing them.
     *
     * @param request selected APIs and target configuration.
     * @return generated artifacts.
     */
    fun generate(request: GenerationRequest): GenerationResult
}
