package dev.akif.tapik

/**
 * Fully-configured HTTP endpoint ready for code generation.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param I inbound definition coupling headers with the request body.
 * @param O outbound response definitions paired with status matchers.
 * @property id unique identifier inferred from the property name.
 * @property description short summary describing the endpoint intent.
 * @property details optional long-form documentation such as business rules.
 * @property method HTTP verb associated with the endpoint.
 * @property path ordered URI segments forming the template.
 * @property parameters tuple capturing referenced path and query parameters.
 * @property input request definition including headers and body.
 * @property outputs candidate response definitions matched by status.
 */
@ConsistentCopyVisibility
data class HttpEndpoint<out P : Parameters, out I : Input<*, *>, out O : Outputs> internal constructor(
    val id: String,
    val description: String?,
    val details: String?,
    val method: Method,
    val path: List<String>,
    val parameters: P,
    val input: I,
    val outputs: O
)
