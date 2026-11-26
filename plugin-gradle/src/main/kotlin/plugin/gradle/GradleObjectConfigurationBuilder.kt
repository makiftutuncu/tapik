package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import java.util.Collections
import java.util.LinkedHashMap

/**
 * Builds an ordered object value for custom-target configuration.
 *
 * Names must be non-blank and unique within this object.
 */
class GradleObjectConfigurationBuilder {
    private val values = linkedMapOf<String, GradleConfigurationValue>()

    /**
     * Adds a textual scalar [value] under [name].
     *
     * @param name unique child name in this object.
     * @param value text stored as the scalar value.
     * @throws IllegalArgumentException when [name] is blank or already used in this object.
     */
    fun value(name: String, value: String) = put(name, ScalarGradleConfigurationValue(value))

    /**
     * Adds a Boolean scalar [value] under [name].
     *
     * @param name unique child name in this object.
     * @param value Boolean stored as its text form.
     * @throws IllegalArgumentException when [name] is blank or already used in this object.
     */
    fun value(name: String, value: Boolean) = put(name, ScalarGradleConfigurationValue(value.toString()))

    /**
     * Adds a numeric scalar [value] under [name].
     *
     * @param name unique child name in this object.
     * @param value number stored as its text form.
     * @throws IllegalArgumentException when [name] is blank or already used in this object.
     */
    fun value(name: String, value: Number) = put(name, ScalarGradleConfigurationValue(value.toString()))

    /**
     * Adds an ordered textual [values] list under [name].
     *
     * @param name unique child name in this object.
     * @param values list items, stored as scalar text values.
     * @throws IllegalArgumentException when [name] is blank or already used in this object.
     */
    fun list(name: String, vararg values: String) =
        put(name, ListGradleConfigurationValue(values.map(::ScalarGradleConfigurationValue)))

    /**
     * Adds an ordered, structurally typed list configured by [action] under [name].
     *
     * @param name unique child name in this object.
     * @param action configures the list items.
     * @throws IllegalArgumentException when [name] is blank or already used in this object.
     */
    fun list(name: String, action: Action<in GradleListConfigurationBuilder>) {
        val child = GradleListConfigurationBuilder()
        action.execute(child)
        put(name, child.build())
    }

    /**
     * Adds a nested object configured by [action] under [name].
     *
     * @param name unique child name in this object.
     * @param action configures the child object.
     * @throws IllegalArgumentException when [name] is blank or already used in this object.
     */
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
