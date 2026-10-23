package dev.akif.tapik.plugin.gradle

import java.io.Serializable

/** A serializable value supplied to a custom tapik generation target. */
sealed interface GradleConfigurationValue : Serializable

/** A scalar custom-target configuration [value]. */
data class ScalarGradleConfigurationValue(val value: String) : GradleConfigurationValue

/** An ordered custom-target configuration list. */
data class ListGradleConfigurationValue(val values: List<GradleConfigurationValue>) : GradleConfigurationValue

/** An ordered custom-target configuration object. */
data class ObjectGradleConfigurationValue(val values: Map<String, GradleConfigurationValue>) : GradleConfigurationValue
