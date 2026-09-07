package dev.akif.tapik.target.openapi

/**
 * Supported OpenAPI parameter locations.
 *
 * @property value wire value used by OpenAPI.
 */
enum class OpenApiParameterLocation(
    val value: String
) {
    /** URI path-template parameter. */
    PATH("path"),

    /** URI query parameter. */
    QUERY("query"),

    /** HTTP request-header parameter. */
    HEADER("header")
}
