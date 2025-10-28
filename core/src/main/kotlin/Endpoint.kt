package dev.akif.tapik

/**
 * Represents a fully-defined endpoint surface that can be consumed by code generators.
 *
 * @param U the URI template together with its path and query parameters.
 * @param I inbound definition coupling headers with the request body.
 * @param O outbound response definitions pairing status matchers with headers and body.
 * @property id unique identifier inferred from the property name.
 * @property description short summary describing the endpoint intent.
 * @property details optional long-form documentation such as business rules.
 * @property uriWithParameters compiled URI template with captured parameters.
 * @property input request definition including headers and body.
 * @property outputs candidate response bodies matched against returned status codes.
 */
abstract class Endpoint<out U, out I : Input<*, *>, out O> {
    internal abstract val id: String
    internal abstract val description: String?
    internal abstract val details: String?
    internal abstract val uriWithParameters: U
    internal abstract val input: I
    internal abstract val outputs: O
}
