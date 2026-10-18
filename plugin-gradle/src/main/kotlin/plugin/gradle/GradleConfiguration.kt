package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ConfigurationValue
import dev.akif.tapik.common.plugin.ListConfigurationValue
import dev.akif.tapik.common.plugin.ObjectConfigurationValue
import dev.akif.tapik.common.plugin.ScalarConfigurationValue
import dev.akif.tapik.common.plugin.TargetConfiguration

internal fun Map<String, GradleConfigurationValue>.toTargetConfiguration(): TargetConfiguration =
    TargetConfiguration(mapValuesTo(linkedMapOf()) { (_, value) -> value.toConfigurationValue() })

private fun GradleConfigurationValue.toConfigurationValue(): ConfigurationValue =
    when (this) {
        is ScalarGradleConfigurationValue -> ScalarConfigurationValue(value)
        is ListGradleConfigurationValue ->
            ListConfigurationValue(values.map(GradleConfigurationValue::toConfigurationValue))
        is ObjectGradleConfigurationValue ->
            ObjectConfigurationValue(
                values.mapValuesTo(linkedMapOf()) { (_, value) -> value.toConfigurationValue() }
            )
    }
