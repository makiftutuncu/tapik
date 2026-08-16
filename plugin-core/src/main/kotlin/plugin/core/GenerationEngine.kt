package dev.akif.tapik.plugin.core

import dev.akif.tapik.Api

/**
 * Dispatches generation requests to a fixed set of host-neutral targets.
 *
 * @param targets targets available to this engine.
 * @throws IllegalArgumentException when a target ID is blank or duplicated.
 */
class GenerationEngine(
    targets: Iterable<GenerationTarget>
) {
    private val targetsById: Map<String, GenerationTarget>

    init {
        val available = targets.toList()
        require(available.none { it.id.isBlank() }) { "Generation target IDs must not be blank" }
        require(available.map(GenerationTarget::id).distinct().size == available.size) {
            "Generation target IDs must be unique"
        }
        targetsById = available.associateBy(GenerationTarget::id)
    }

    /**
     * Generates with the target identified by [targetId].
     *
     * @param targetId ID of the target to invoke.
     * @param apis all APIs selected for this execution.
     * @param configuration host-neutral target configuration.
     * @return artifacts returned by the selected target.
     * @throws IllegalArgumentException when the target is unknown, no APIs are supplied, or API IDs are duplicated.
     */
    fun generate(
        targetId: String,
        apis: List<Api>,
        configuration: TargetConfiguration = TargetConfiguration()
    ): GenerationResult {
        val target = requireNotNull(targetsById[targetId]) { "Unknown generation target '$targetId'" }
        require(apis.isNotEmpty()) { "Generation requires at least one API" }
        requireUniqueApiIds(apis)
        return target.generate(GenerationRequest(apis, configuration))
    }
}
