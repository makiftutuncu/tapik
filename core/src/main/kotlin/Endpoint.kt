package dev.akif.tapik

/**
 * Represents a fully-defined endpoint surface that can be consumed by code generators.
 *
 * @param URIWithParameters the URI template together with its path and query parameters.
 * @param InputHeaders tuple encapsulating inbound header definitions.
 * @param InputBody definition of the expected request body.
 * @param Outputs outbound body alternatives grouped by status matcher.
 * @property id unique identifier inferred from the property name.
 * @property description short summary describing the endpoint intent.
 * @property details optional long-form documentation such as business rules.
 * @property uriWithParameters compiled URI template with captured parameters.
 * @property inputHeaders headers expected on the request.
 * @property inputBody expected request body definition.
 * @property outputs candidate response bodies matched against returned status codes.
 */
abstract class Endpoint<out URIWithParameters, out InputHeaders, out InputBody, out Outputs> {
    internal abstract val id: String
    internal abstract val description: String?
    internal abstract val details: String?
    internal abstract val uriWithParameters: URIWithParameters
    internal abstract val inputHeaders: InputHeaders
    internal abstract val inputBody: InputBody
    internal abstract val outputs: Outputs
}
