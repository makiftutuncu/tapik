package dev.akif.tapik.plugin.gradle

import org.gradle.api.Named
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import javax.inject.Inject

/**
 * Common, typed configuration for one declaration-ordered tapik generation execution.
 *
 * The execution `name` is a unique diagnostic identity; [targetId] independently selects the service-loaded target.
 */
abstract class TapikExecution @Inject constructor(
    private val executionName: String,
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

    /** Appends [apiIds] to the exact API-ID include set. */
    fun includeApis(vararg apiIds: String) {
        includeApis.addAll(apiIds.toList())
    }

    /** Appends [apiIds] to the exact API-ID exclude set. */
    fun excludeApis(vararg apiIds: String) {
        excludeApis.addAll(apiIds.toList())
    }

    internal open fun validateConfiguration() {
        require(name.isNotBlank()) { "tapik execution names must not be blank" }
        require(targetId.isNotBlank()) { "tapik target IDs must not be blank" }
    }
}
