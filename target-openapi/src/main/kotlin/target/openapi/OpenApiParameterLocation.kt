package dev.akif.tapik.target.openapi

/**
 * Supported OpenAPI parameter locations.
 *
 * @property value wire value used by OpenAPI.
 */
enum class OpenApiParameterLocation(
    /** Wire value used by OpenAPI. */
    val value: String
) {
    /** URI path-template parameter. */
    PATH("path"),

    /** URI query parameter. */
    QUERY("query"),

    /** HTTP request-header parameter. */
    HEADER("header")
}
