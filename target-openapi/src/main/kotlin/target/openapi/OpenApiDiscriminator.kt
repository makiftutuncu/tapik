package dev.akif.tapik.target.openapi

/**
 * OpenAPI hint for selecting one alternative of a composed schema.
 *
 * @property propertyName wire property containing the discriminating value.
 * @property mapping explicit discriminating values to schema reference URIs in declaration order.
 * @property defaultMapping schema reference URI used when no explicit or implicit mapping applies.
 */
data class OpenApiDiscriminator(
    val propertyName: String,
    val mapping: Map<String, String> = emptyMap(),
    val defaultMapping: String? = null
)
