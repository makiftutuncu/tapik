package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import java.util.Collections

/** Builds an ordered list value for custom-target configuration. */
class GradleListConfigurationBuilder {
    private val values = mutableListOf<GradleConfigurationValue>()

    /**
     * Appends a textual scalar [value].
     *
     * @param value text stored as the scalar value.
     */
    fun value(value: String) {
        values.add(ScalarGradleConfigurationValue(value))
    }

    /**
     * Appends a Boolean scalar [value].
     *
     * @param value Boolean stored as its text form.
     */
    fun value(value: Boolean) {
        values.add(ScalarGradleConfigurationValue(value.toString()))
    }

    /**
     * Appends a numeric scalar [value].
     *
     * @param value number stored as its text form.
     */
    fun value(value: Number) {
        values.add(ScalarGradleConfigurationValue(value.toString()))
    }

    /**
     * Appends an ordered textual [values] list.
     *
     * @param values list items, stored as scalar text values.
     */
    fun list(vararg values: String) {
        this.values.add(ListGradleConfigurationValue(values.map(::ScalarGradleConfigurationValue)))
    }

    /**
     * Appends an ordered, structurally typed list configured by [action].
     *
     * @param action configures the list items.
     */
    fun list(action: Action<in GradleListConfigurationBuilder>) {
        val child = GradleListConfigurationBuilder()
        action.execute(child)
        values.add(child.build())
    }

    /**
     * Appends an object configured by [action].
     *
     * @param action configures the child object.
     */
    fun objectValue(action: Action<in GradleObjectConfigurationBuilder>) {
        val child = GradleObjectConfigurationBuilder()
        action.execute(child)
        values.add(child.build())
    }

    internal fun build(): ListGradleConfigurationValue =
        ListGradleConfigurationValue(Collections.unmodifiableList(ArrayList(values)))
}
