package dev.akif.tapik.plugin.gradle

import org.gradle.api.Named
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import javax.inject.Inject

/** Common, typed configuration for one tapik generation execution. */
abstract class TapikExecution @Inject constructor(
    private val executionName: String,
    @get:Input val targetId: String
) : Named {
    /** Stable name of this execution within the Gradle project. */
    @Input
    final override fun getName(): String = executionName

    /** Exact API IDs included by this execution, or empty to include every API. */
    @get:Input
    abstract val includeApis: SetProperty<String>

    /** Exact API IDs excluded by this execution. */
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
        val includes = includeApis.get()
        val excludes = excludeApis.get()
        require(includes.none(String::isBlank)) { "tapik API include filters must not contain blank IDs" }
        require(excludes.none(String::isBlank)) { "tapik API exclude filters must not contain blank IDs" }
        val overlap = includes intersect excludes
        require(overlap.isEmpty()) {
            "tapik APIs cannot be both included and excluded: ${overlap.joinToString()}"
        }
    }
}
