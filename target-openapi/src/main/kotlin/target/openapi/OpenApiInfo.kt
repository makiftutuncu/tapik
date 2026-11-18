package dev.akif.tapik.target.openapi

/**
 * Required identifying information for an OpenAPI document.
 *
 * @property title human-readable API title.
 * @property version version of the API document, independent of [OpenApiDocument.specificationVersion].
 * @property summary optional short summary.
 * @property description optional detailed description.
 * @throws IllegalArgumentException when [title] or [version] is blank.
 */
data class OpenApiInfo(
    /** Human-readable API title. */
    val title: String,
    /** Version of the API document, independent of [OpenApiDocument.specificationVersion]. */
    val version: String,
    /** Optional short summary. */
    val summary: String? = null,
    /** Optional detailed description. */
    val description: String? = null
) {
    init {
        require(title.isNotBlank()) { "OpenAPI title must not be blank" }
        require(version.isNotBlank()) { "OpenAPI document version must not be blank" }
    }
}
