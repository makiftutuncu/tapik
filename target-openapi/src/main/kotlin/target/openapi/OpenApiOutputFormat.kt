package dev.akif.tapik.target.openapi

internal enum class OpenApiOutputFormat(
    val mediaType: String,
    val defaultOutput: String
) {
    YAML("application/yaml", "{api}.openapi.yml") {
        override fun render(document: OpenApiDocument, pretty: Boolean): String = document.toYaml()
    },
    JSON("application/json", "{api}.openapi.json") {
        override fun render(document: OpenApiDocument, pretty: Boolean): String = document.toJson(pretty)
    };

    abstract fun render(document: OpenApiDocument, pretty: Boolean): String

    companion object {
        fun from(value: String?): OpenApiOutputFormat =
            when (value) {
                null,
                "yaml" -> YAML
                "json" -> JSON
                else -> throw IllegalArgumentException("OpenAPI target 'format' must be yaml or json, but was '$value'")
            }
    }
}
