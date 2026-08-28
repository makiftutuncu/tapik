package dev.akif.tapik.target.openapi

import dev.akif.tapik.common.plugin.*

/** Host-neutral OpenAPI generation target. */
object OpenApiTarget : GenerationTarget {
    override val id: String = "openapi"

    /**
     * Generates one OpenAPI artifact for every API in [request].
     *
     * Supported scalar configuration values are `version`, `format`, `pretty`, `componentNaming`, and `output`. The
     * `version` value is required. YAML is the default format and uses output template `{api}.openapi.yml`.
     *
     * @param request APIs and host-neutral OpenAPI target configuration.
     * @return one documentation artifact per API in request order.
     * @throws IllegalArgumentException when configuration is missing or invalid.
     */
    override fun generate(request: GenerationRequest): GenerationResult {
        val configuration = OpenApiTargetConfiguration.from(request.configuration)
        val artifacts =
            request.apis.map { api ->
                val document =
                    OpenApi.from(
                        api = api,
                        version = configuration.version,
                        componentNaming = configuration.componentNaming
                    )
                GeneratedArtifact(
                    relativePath = configuration.output.replace(API_PLACEHOLDER, api.id),
                    mediaType = configuration.format.mediaType,
                    kind = ArtifactKind.DOCUMENTATION,
                    content = configuration.format.render(document, configuration.pretty)
                )
            }
        return GenerationResult(artifacts)
    }
}
private const val API_PLACEHOLDER: String = "{api}"
