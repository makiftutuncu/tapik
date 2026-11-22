package dev.akif.tapik.plugin.gradle

import java.io.Serializable

/** A serializable value supplied to a custom tapik generation target. */
sealed interface GradleConfigurationValue : Serializable

/** A scalar custom-target configuration [value].
 *
 * @property value configured text value.
 */
data class ScalarGradleConfigurationValue(
    /** Configured text value. */
    val value: String
) : GradleConfigurationValue

/** An ordered custom-target configuration list.
 *
 * @property values list items in declaration order.
 */
data class ListGradleConfigurationValue(
    /** List items in declaration order. */
    val values: List<GradleConfigurationValue>
) : GradleConfigurationValue

/** An ordered custom-target configuration object.
 *
 * @property values named fields in declaration order.
 */
data class ObjectGradleConfigurationValue(
    /** Named fields in declaration order. */
    val values: Map<String, GradleConfigurationValue>
) : GradleConfigurationValue
