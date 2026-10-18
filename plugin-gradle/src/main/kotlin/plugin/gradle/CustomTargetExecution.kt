package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import javax.inject.Inject

/** Configuration for a service-loaded custom tapik generation target. */
abstract class CustomTargetExecution @Inject constructor(name: String, targetId: String) :
    TapikExecution(name, targetId) {
    /** Target-owned configuration values keyed in declaration order. */
    @get:Input
    abstract val configuration: MapProperty<String, GradleConfigurationValue>

    init {
        configuration.convention(emptyMap())
    }

    /** Adds a textual scalar [value] under [name]. */
    fun value(name: String, value: String) = put(name, ScalarGradleConfigurationValue(value))

    /** Adds a Boolean scalar [value] under [name]. */
    fun value(name: String, value: Boolean) = put(name, ScalarGradleConfigurationValue(value.toString()))

    /** Adds a numeric scalar [value] under [name]. */
    fun value(name: String, value: Number) = put(name, ScalarGradleConfigurationValue(value.toString()))

    /** Adds an ordered textual [values] list under [name]. */
    fun list(name: String, vararg values: String) =
        put(name, ListGradleConfigurationValue(values.map(::ScalarGradleConfigurationValue)))

    /** Adds a nested object configured by [action] under [name]. */
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
