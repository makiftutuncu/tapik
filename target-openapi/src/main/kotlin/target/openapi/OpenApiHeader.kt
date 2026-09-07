package dev.akif.tapik.target.openapi

/**
 * A response header.
 *
 * @property description optional detailed description.
 * @property required whether the response always contains the header.
 * @property deprecated whether consumers should avoid the header.
 * @property schema header value schema.
 */
data class OpenApiHeader(
    val description: String? = null,
    val required: Boolean,
    val deprecated: Boolean,
    val schema: OpenApiSchema
)
