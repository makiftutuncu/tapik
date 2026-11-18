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
    /** Parameter name. */
    val name: String,
    /** Parameter location. */
    val location: OpenApiParameterLocation,
    /** Optional detailed description. */
    val description: String? = null,
    /** Whether callers must provide the parameter. */
    val required: Boolean,
    /** Whether consumers should avoid the parameter. */
    val deprecated: Boolean,
    /** Parameter value schema. */
    val schema: OpenApiSchema,
    /** Optional OpenAPI serialization style. */
    val style: String? = null,
    /** Optional OpenAPI serialization expansion behavior. */
    val explode: Boolean? = null
)
