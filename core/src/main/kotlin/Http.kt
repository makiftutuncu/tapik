@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import dev.akif.tapik.codec.StringCodec
import kotlin.properties.ReadOnlyProperty

/** Root URI used as the starting point for building endpoint paths. */
val root: URIWithParameters0 = emptyList<String>() to Parameters0

/**
 * Creates a path parameter with the given [name] and [codec].
 *
 * @param name parameter name as it appears inside the URI template.
 * @param codec codec used to encode and decode parameter values.
 * @return path variable definition that is required by default.
 * @see PathVariable
 */
inline fun <reified P : Any> path(
    name: String,
    codec: StringCodec<P>
): PathVariable<P> = PathVariable(name, codec)

/** Shorthand accessor to factory helpers for path parameters. */
val path: PathVariable.Companion = PathVariable.Companion

/**
 * Creates a query parameter with the given [name] and [codec].
 *
 * @param name query parameter name.
 * @param codec codec used to encode and decode the parameter value.
 * @return query parameter definition.
 * @see QueryParameter.optional
 */
fun <Q : Any> query(
    name: String,
    codec: StringCodec<Q>
): QueryParameter<Q> = QueryParameter(name, codec, required = false, default = null)

/**
 * Creates a query parameter with the given [name], [codec], and a default.
 *
 * @param name query parameter name.
 * @param codec codec used to encode and decode the parameter value.
 * @param default optional default value used when callers omit the parameter.
 * @return query parameter definition honouring the required/default combination.
 * @see QueryParameter.optional
 */
fun <Q : Any> query(
    name: String,
    codec: StringCodec<Q>,
    default: Q
): QueryParameter<Q> = QueryParameter(name, codec, required = false, default)

/** Shorthand accessor to factory helpers for query parameters. */
val query: QueryParameter.Companion = QueryParameter.Companion

/**
 * Creates a header definition with the given [name] and [codec].
 *
 * @param name header name using canonical casing.
 * @param codec codec used to encode and decode header values.
 * @return header definition that requires a value by default.
 * @see Header
 */
inline fun <reified H : Any> header(
    name: String,
    codec: StringCodec<H>
): Header<H> = HeaderInput(name, codec)

/** Shorthand accessor to factory helpers for headers. */
val header: Header.Companion = Header.Companion

/**
 * Matches any of the provided HTTP statuses.
 *
 * @param first status that must always be matched.
 * @param rest additional statuses that will be included in the matcher.
 * @return matcher that succeeds when the response status is contained in the provided set.
 * @see StatusMatcher.AnyOf
 */
fun anyStatus(
    first: Status,
    vararg rest: Status
): StatusMatcher = StatusMatcher.AnyOf(setOf(first, *rest))

/**
 * Builds a [StatusMatcher] using an arbitrary [predicate] with [description].
 *
 * @param description human readable description that surfaces in generated code and documentation.
 * @param predicate predicate invoked to determine whether a status matches.
 * @return matcher delegating matching logic to [predicate].
 * @see StatusMatcher.Predicate
 */
fun matchStatus(
    description: String,
    predicate: (Status) -> Boolean
): StatusMatcher = StatusMatcher.Predicate(description, predicate)

/** Status matcher that never matches any status code. */
val unmatchedStatus: StatusMatcher =
    StatusMatcher.Unmatched

/**
 * Declares an HTTP endpoint builder that captures property metadata at declaration time.
 *
 * The returned delegate instantiates a [HttpEndpointWithoutMethod] so the caller can chain
 * verb, URI, header, and body builders before materialising a [HttpEndpoint].
 *
 * @param description optional short description shown in generated documentation.
 * @param details optional long-form documentation such as business rules or references.
 * @param builder DSL block building the endpoint structure.
 * @return property delegate that records endpoint metadata using the owning property name.
 * @see HttpEndpoint
 */
fun <T, P : Parameters, I : Input<*, *>, O : Outputs> http(
    description: String? = null,
    details: String? = null,
    builder: HttpEndpointWithoutMethod.() -> HttpEndpoint<P, I, O>
): ReadOnlyProperty<T, HttpEndpoint<P, I, O>> =
    ReadOnlyProperty { _, property ->
        HttpEndpointWithoutMethod(id = property.name, description = description, details = details).builder()
    }

/** Endpoint definition before an HTTP verb has been chosen.
 * @property id unique identifier inferred from the property name.
 * @property description optional short description of the endpoint.
 * @property details optional long-form description.
 */
data class HttpEndpointWithoutMethod(
    override val id: String,
    override val description: String?,
    override val details: String?
) : Endpoint<Parameters0, Input<Headers0, EmptyBody>, Outputs0>() {
    override val path: List<String> = root.first
    override val parameters: Parameters0 = root.second
    override val input: Input<Headers0, EmptyBody> = Input(Headers0, EmptyBody)
    override val outputs: Outputs0 = Outputs0

    /** Builder for a GET endpoint with this metadata retained. */
    val get: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.GET)

    /** Builder for a HEAD endpoint with this metadata retained. */
    val head: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.HEAD)

    /** Builder for a POST endpoint with this metadata retained. */
    val post: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.POST)

    /** Builder for a PUT endpoint with this metadata retained. */
    val put: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.PUT)

    /** Builder for a PATCH endpoint with this metadata retained. */
    val patch: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.PATCH)

    /** Builder for a DELETE endpoint with this metadata retained. */
    val delete: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.DELETE)

    /** Builder for an OPTIONS endpoint with this metadata retained. */
    val options: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.OPTIONS)

    /** Builder for a TRACE endpoint with this metadata retained. */
    val trace: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.TRACE)
}

/** Endpoint definition with method but without URI information.
 * @property method HTTP verb chosen for the endpoint.
 */
data class HttpEndpointWithoutURI(
    override val id: String,
    override val description: String?,
    override val details: String?,
    private val method: Method
) : Endpoint<Parameters0, Input<Headers0, EmptyBody>, Outputs0>() {
    override val path: List<String> = root.first
    override val parameters: Parameters0 = root.second
    override val input: Input<Headers0, EmptyBody> = Input(Headers0, EmptyBody)
    override val outputs: Outputs0 = Outputs0

    /**
     * Finalises the endpoint by supplying concrete [path] together with [parameters].
     *
     * @param uriWithParameters URI template together with captured parameters.
     * @return fully-configured [HttpEndpoint] ready for header/body configuration.
     */
    fun <P : Parameters> uri(
        uriWithParameters: URIWithParameters<P>
    ): HttpEndpoint<P, Input<Headers0, EmptyBody>, Outputs0> =
        HttpEndpoint(
            id = this.id,
            description = this.description,
            details = this.details,
            method = this.method,
            path = uriWithParameters.first,
            parameters = uriWithParameters.second,
            input = this.input,
            outputs = Outputs0
        )
}

/** Fully-configured HTTP endpoint ready for code generation.
 * @property method HTTP verb associated with the endpoint.
 * @property path ordered path segments that form the template.
 * @property parameters tuple capturing referenced path and query parameters.
 * @property input request definition including headers and body.
 * @property outputs candidate response definitions matched by status.
 */
data class HttpEndpoint<out P : Parameters, out I : Input<*, *>, out O : Outputs>(
    public override val id: String,
    public override val description: String?,
    public override val details: String?,
    val method: Method,
    public override val path: List<String>,
    public override val parameters: P,
    public override val input: I,
    public override val outputs: O
) : Endpoint<P, I, O>()
