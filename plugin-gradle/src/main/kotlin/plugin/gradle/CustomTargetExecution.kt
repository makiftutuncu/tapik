package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import javax.inject.Inject

/**
 * Configuration for a service-loaded custom tapik generation target.
 *
 * Setting the same top-level name more than once replaces its previous value.
 *
 * @param name unique execution name in the Gradle project.
 * @param targetId stable ID of the service-loaded generation target.
 */
abstract class CustomTargetExecution @Inject constructor(name: String, targetId: String) :
    TapikExecution(name, targetId) {
    /** Target-owned configuration values keyed in declaration order. */
    @get:Input
    abstract val configuration: MapProperty<String, GradleConfigurationValue>

    init {
        configuration.convention(emptyMap())
    }

    /**
     * Adds a textual scalar [value] under [name], replacing any previous value with that name.
     *
     * @param name configuration name.
     * @param value text stored as the scalar value.
     * @throws IllegalArgumentException when [name] is blank.
     */
    fun value(name: String, value: String) = put(name, ScalarGradleConfigurationValue(value))

    /**
     * Adds a Boolean scalar [value] under [name], replacing any previous value with that name.
     *
     * @param name configuration name.
     * @param value Boolean stored as its text form.
     * @throws IllegalArgumentException when [name] is blank.
     */
    fun value(name: String, value: Boolean) = put(name, ScalarGradleConfigurationValue(value.toString()))

    /**
     * Adds a numeric scalar [value] under [name], replacing any previous value with that name.
     *
     * @param name configuration name.
     * @param value number stored as its text form.
     * @throws IllegalArgumentException when [name] is blank.
     */
    fun value(name: String, value: Number) = put(name, ScalarGradleConfigurationValue(value.toString()))

    /**
     * Adds an ordered textual [values] list under [name], replacing any previous value with that name.
     *
     * @param name configuration name.
     * @param values list items, stored as scalar text values.
     * @throws IllegalArgumentException when [name] is blank.
     */
    fun list(name: String, vararg values: String) =
        put(name, ListGradleConfigurationValue(values.map(::ScalarGradleConfigurationValue)))

    /**
     * Adds an ordered, structurally typed list configured by [action] under [name], replacing any previous value
     * with that name.
     *
     * @param name configuration name.
     * @param action configures the list items.
     * @throws IllegalArgumentException when [name] is blank.
     */
    fun list(name: String, action: Action<in GradleListConfigurationBuilder>) {
        val builder = GradleListConfigurationBuilder()
        action.execute(builder)
        put(name, builder.build())
    }

    /**
     * Adds a nested object configured by [action] under [name], replacing any previous value with that name.
     *
     * @param name configuration name.
     * @param action configures the child object.
     * @throws IllegalArgumentException when [name] is blank.
     */
    fun objectValue(name: String, action: Action<in GradleObjectConfigurationBuilder>) {
        val builder = GradleObjectConfigurationBuilder()
        action.execute(builder)
        put(name, builder.build())
    }

    private fun put(name: String, value: GradleConfigurationValue) {
        require(name.isNotBlank()) { "tapik target configuration names must not be blank" }
        configuration.put(name, value)
    }
}
