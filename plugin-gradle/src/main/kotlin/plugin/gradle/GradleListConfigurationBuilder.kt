package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import java.util.Collections

/** Builds an ordered list value for custom-target configuration. */
class GradleListConfigurationBuilder {
    private val values = mutableListOf<GradleConfigurationValue>()

    /** Appends a textual scalar [value]. */
    fun value(value: String) {
        values.add(ScalarGradleConfigurationValue(value))
    }

    /** Appends a Boolean scalar [value]. */
    fun value(value: Boolean) {
        values.add(ScalarGradleConfigurationValue(value.toString()))
    }

    /** Appends a numeric scalar [value]. */
    fun value(value: Number) {
        values.add(ScalarGradleConfigurationValue(value.toString()))
    }

    /** Appends an ordered textual [values] list. */
    fun list(vararg values: String) {
        this.values.add(ListGradleConfigurationValue(values.map(::ScalarGradleConfigurationValue)))
    }

    /** Appends an ordered, structurally typed list configured by [action]. */
    fun list(action: Action<in GradleListConfigurationBuilder>) {
        val child = GradleListConfigurationBuilder()
        action.execute(child)
        values.add(child.build())
    }

    /** Appends an object configured by [action]. */
    fun objectValue(action: Action<in GradleObjectConfigurationBuilder>) {
        val child = GradleObjectConfigurationBuilder()
        action.execute(child)
        values.add(child.build())
    }

    internal fun build(): ListGradleConfigurationValue =
        ListGradleConfigurationValue(Collections.unmodifiableList(ArrayList(values)))
}
