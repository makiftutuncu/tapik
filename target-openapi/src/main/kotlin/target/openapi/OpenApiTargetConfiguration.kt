package dev.akif.tapik.target.openapi

import dev.akif.tapik.common.plugin.ScalarConfigurationValue
import dev.akif.tapik.common.plugin.TargetConfiguration

internal data class OpenApiTargetConfiguration(
    val version: String,
    val format: OpenApiOutputFormat,
    val pretty: Boolean,
    val componentNaming: OpenApiComponentNaming,
    val output: String
) {
    companion object {
        fun from(configuration: TargetConfiguration): OpenApiTargetConfiguration {
            val unknown = configuration.values.keys - SUPPORTED_CONFIGURATION
            require(unknown.isEmpty()) {
                "Unknown OpenAPI target configuration: ${unknown.sorted().joinToString()}"
            }

            val version = configuration.scalar("version")
            require(!version.isNullOrBlank()) { "OpenAPI target configuration requires 'version'" }

            val format = OpenApiOutputFormat.from(configuration.scalar("format"))
            val pretty = configuration.pretty()
            val componentNaming = configuration.componentNaming()
            val output = configuration.scalar("output") ?: format.defaultOutput
            require(output.isNotBlank()) { "OpenAPI target 'output' must not be blank" }

            return OpenApiTargetConfiguration(version, format, pretty, componentNaming, output)
        }
    }
}

private fun TargetConfiguration.pretty(): Boolean =
    when (val value = scalar("pretty")) {
        null,
        "true" -> true
        "false" -> false
        else -> throw IllegalArgumentException("OpenAPI target 'pretty' must be true or false, but was '$value'")
    }

private fun TargetConfiguration.componentNaming(): OpenApiComponentNaming =
    when (val value = scalar("componentNaming")) {
        null,
        "simple" -> OpenApiComponentNaming.Simple
        "qualified" -> OpenApiComponentNaming.Qualified
        else ->
            throw IllegalArgumentException(
                "OpenAPI target 'componentNaming' must be simple or qualified, but was '$value'"
            )
    }

private fun TargetConfiguration.scalar(name: String): String? =
    when (val value = this[name]) {
        null -> null
        is ScalarConfigurationValue -> value.value
        else -> throw IllegalArgumentException("OpenAPI target '$name' must be a scalar value")
    }

private val SUPPORTED_CONFIGURATION: Set<String> = setOf("version", "format", "pretty", "componentNaming", "output")
