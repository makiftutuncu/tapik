@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Entry point for building an HTTP endpoint.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param I inbound definition coupling headers with the request body.
 * @param O outbound response definitions paired with status matchers.
 * @property id unique identifier inferred from the property name.
 * @property description optional short description of the endpoint.
 * @property details optional long-form description.
 * @property method HTTP verb associated with the endpoint.
 * @property path ordered URI segments forming the template.
 * @property parameters tuple capturing referenced path and query parameters.
 * @property input request definition including headers and body.
 * @property outputs candidate response definitions matched by status.
 */
data class HttpEndpointBuildingContext<P : Parameters, I : Input<*, *>, O : Outputs>(
    internal val id: String,
    internal val description: String?,
    internal val details: String?,
    internal val method: Method,
    internal val path: List<String>,
    internal val parameters: P,
    internal val input: I,
    internal val outputs: O
) {
    internal fun <I1 : Input<*, *>, O1 : Outputs> modifiedAs(
        input: I1,
        outputs: O1
    ): HttpEndpointBuildingContext<P, I1, O1> =
        HttpEndpointBuildingContext(
            id = id,
            description = description,
            details = details,
            method = method,
            path = path,
            parameters = parameters,
            input = input,
            outputs = outputs
        )
}
