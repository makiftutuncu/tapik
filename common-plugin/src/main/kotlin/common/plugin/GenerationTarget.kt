package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api

/**
 * Input supplied to one [GenerationTarget].
 *
 * @property apis every API selected for this execution.
 * @property configuration target-owned, host-neutral configuration.
 */
class GenerationRequest(
    apis: List<Api>,
    val configuration: TargetConfiguration = TargetConfiguration()
) {
    val apis: List<Api> = apis.snapshotList()

    /** Returns [apis] for destructuring. */
    operator fun component1(): List<Api> = apis

    /** Returns [configuration] for destructuring. */
    operator fun component2(): TargetConfiguration = configuration

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        apis: List<Api> = this.apis,
        configuration: TargetConfiguration = this.configuration
    ): GenerationRequest = GenerationRequest(apis, configuration)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is GenerationRequest &&
                apis == other.apis &&
                configuration == other.configuration)

    override fun hashCode(): Int {
        var result = apis.hashCode()
        result = 31 * result + configuration.hashCode()
        return result
    }

    override fun toString(): String =
        "GenerationRequest(apis=$apis, configuration=$configuration)"
}

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
