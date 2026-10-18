package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import java.io.Serializable
import java.util.Collections
import java.util.LinkedHashMap

/** A serializable value supplied to a custom tapik generation target. */
sealed interface GradleConfigurationValue : Serializable

/** A scalar custom-target configuration [value]. */
data class ScalarGradleConfigurationValue(val value: String) : GradleConfigurationValue

/** An ordered custom-target configuration list. */
data class ListGradleConfigurationValue(val values: List<GradleConfigurationValue>) : GradleConfigurationValue

/** An ordered custom-target configuration object. */
data class ObjectGradleConfigurationValue(val values: Map<String, GradleConfigurationValue>) : GradleConfigurationValue

/** Builds an ordered object value for custom-target configuration. */
class GradleObjectConfigurationBuilder {
    private val values = linkedMapOf<String, GradleConfigurationValue>()

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
        val child = GradleObjectConfigurationBuilder()
        action.execute(child)
        put(name, child.build())
    }

    internal fun build(): ObjectGradleConfigurationValue =
        ObjectGradleConfigurationValue(Collections.unmodifiableMap(LinkedHashMap(values)))

    private fun put(name: String, value: GradleConfigurationValue) {
        require(name.isNotBlank()) { "tapik target configuration names must not be blank" }
        require(name !in values) { "tapik target configuration name '$name' is duplicated" }
        values[name] = value
    }
}
