package dev.akif.tapik.plugin.gradle

import org.gradle.api.Named
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import javax.inject.Inject

/**
 * Common, typed configuration for one declaration-ordered tapik generation execution.
 *
 * The execution `name` is a unique diagnostic identity; [targetId] independently selects the service-loaded target.
 *
 * @param executionName unique name for this execution in the Gradle project.
 * @property targetId exact identifier of the generation target.
 */
abstract class TapikExecution @Inject constructor(
    private val executionName: String,
    /** Exact identifier of the generation target. */
    @get:Input val targetId: String
) : Named {
    /** Stable name of this execution within the Gradle project. */
    @Input
    final override fun getName(): String = executionName

    /** Exact, case-sensitive API IDs included by this execution, or empty to include every discovered API. */
    @get:Input
    abstract val includeApis: SetProperty<String>

    /** Exact, case-sensitive API IDs excluded after inclusion has been evaluated. */
    @get:Input
    abstract val excludeApis: SetProperty<String>

    init {
        includeApis.convention(emptySet())
        excludeApis.convention(emptySet())
    }

    /**
     * Adds exact, case-sensitive API IDs to the include set. With no included IDs, this execution selects every
     * discovered API.
     *
     * @param apiIds API IDs to include.
     */
    fun includeApis(vararg apiIds: String) {
        includeApis.addAll(apiIds.toList())
    }

    /**
     * Adds exact, case-sensitive API IDs to the exclude set. Excluded IDs are removed after inclusion is evaluated.
     *
     * @param apiIds API IDs to exclude.
     */
    fun excludeApis(vararg apiIds: String) {
        excludeApis.addAll(apiIds.toList())
    }

    internal open fun validateConfiguration() {
        require(name.isNotBlank()) { "tapik execution names must not be blank" }
        require(targetId.isNotBlank()) { "tapik target IDs must not be blank" }
    }
}
