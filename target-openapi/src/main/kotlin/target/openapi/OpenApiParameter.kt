package dev.akif.tapik.target.openapi

/**
 * A path, query, or request-header parameter.
 *
 * @property name parameter name.
 * @property location parameter location.
 * @property description optional detailed description.
 * @property required whether callers must provide the parameter.
 * @property deprecated whether consumers should avoid the parameter.
 * @property schema parameter value schema.
 * @property style optional OpenAPI serialization style.
 * @property explode optional OpenAPI serialization expansion behavior.
 */
data class OpenApiParameter(
    val name: String,
    val location: OpenApiParameterLocation,
    val description: String? = null,
    val required: Boolean,
    val deprecated: Boolean,
    val schema: OpenApiSchema,
    val style: String? = null,
    val explode: Boolean? = null
)
