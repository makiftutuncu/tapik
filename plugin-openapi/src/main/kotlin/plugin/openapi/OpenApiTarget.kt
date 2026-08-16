package dev.akif.tapik.plugin.openapi

import dev.akif.tapik.plugin.core.*

/** Host-neutral OpenAPI generation target. */
object OpenApiTarget : GenerationTarget {
    override val id: String = "openapi"

    /**
     * Generates one OpenAPI JSON artifact for every API in [request].
     *
     * Supported scalar configuration values are `version`, `pretty`, `componentNaming`, and `output`. The `version`
     * value is required. The default output template is `{api}.openapi.json`.
     *
     * @param request APIs and host-neutral OpenAPI target configuration.
     * @return one documentation artifact per API in request order.
     * @throws IllegalArgumentException when configuration is missing or invalid.
     */
    override fun generate(request: GenerationRequest): GenerationResult {
        val configuration = OpenApiTargetConfiguration.from(request.configuration)
        val artifacts =
            request.apis.map { api ->
                GeneratedArtifact(
                    relativePath = configuration.output.replace(API_PLACEHOLDER, api.id),
                    mediaType = "application/json",
                    kind = ArtifactKind.DOCUMENTATION,
                    content =
                        OpenApi
                            .from(
                                api = api,
                                version = configuration.version,
                                componentNaming = configuration.componentNaming
                            ).toJson(pretty = configuration.pretty)
                )
            }
        return GenerationResult(artifacts)
    }
}

private data class OpenApiTargetConfiguration(
    val version: String,
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

            val pretty =
                when (val value = configuration.scalar("pretty")) {
                    null,
                    "true" -> true
                    "false" -> false
                    else ->
                        throw IllegalArgumentException(
                            "OpenAPI target 'pretty' must be true or false, but was '$value'"
                        )
                }
            val componentNaming =
                when (val value = configuration.scalar("componentNaming")) {
                    null,
                    "simple" -> OpenApiComponentNaming.Simple
                    "qualified" -> OpenApiComponentNaming.Qualified
                    else ->
                        throw IllegalArgumentException(
                            "OpenAPI target 'componentNaming' must be simple or qualified, but was '$value'"
                        )
                }
            val output = configuration.scalar("output") ?: DEFAULT_OUTPUT
            require(output.isNotBlank()) { "OpenAPI target 'output' must not be blank" }

            return OpenApiTargetConfiguration(version, pretty, componentNaming, output)
        }
    }
}

private fun TargetConfiguration.scalar(name: String): String? =
    when (val value = this[name]) {
        null -> null
        is ScalarConfigurationValue -> value.value
        else -> throw IllegalArgumentException("OpenAPI target '$name' must be a scalar value")
    }

private val SUPPORTED_CONFIGURATION: Set<String> = setOf("version", "pretty", "componentNaming", "output")
private const val API_PLACEHOLDER: String = "{api}"
private const val DEFAULT_OUTPUT: String = "$API_PLACEHOLDER.openapi.json"
