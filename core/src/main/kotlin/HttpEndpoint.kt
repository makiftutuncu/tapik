package dev.akif.tapik

/**
 * Immutable endpoint contract produced by the DSL.
 *
 * This is the value Tapik generators and interpreters consume once the staged builders have
 * collected the method, URI template, request shape, and possible responses into one object.
 *
 * @param P typed tuple of path and query parameters referenced by the endpoint.
 * @param I request contract covering headers and body.
 * @param O response variants paired with status matchers.
 * @property id stable endpoint identifier, usually derived from the declaring property name.
 * @property description short summary surfaced in generated documentation.
 * @property details optional longer notes such as usage rules or caveats.
 * @property method HTTP method handled by the endpoint.
 * @property path literal URI segments, with path variables represented separately in [parameters].
 * @property parameters typed parameter tuple captured while building the URI template.
 * @property input request headers and body expected by the endpoint.
 * @property outputs response variants the endpoint may produce.
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
