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
    /** Optional detailed description. */
    val description: String? = null,
    /** Whether the response always contains the header. */
    val required: Boolean,
    /** Whether consumers should avoid the header. */
    val deprecated: Boolean,
    /** Header value schema. */
    val schema: OpenApiSchema
)
