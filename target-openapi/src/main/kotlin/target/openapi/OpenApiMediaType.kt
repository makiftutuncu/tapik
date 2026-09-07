package dev.akif.tapik.target.openapi

/**
 * The schema applying to a complete media representation.
 *
 * @property schema schema of the complete representation.
 */
data class OpenApiMediaType(
    val schema: OpenApiSchema
)
