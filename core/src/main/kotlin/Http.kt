@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import dev.akif.tapik.codec.StringCodec
import kotlin.properties.ReadOnlyProperty

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
 * Builds an [HttpEndpoint] with an explicit [id] using the DSL.
 *
 * @param P tuple capturing path and query parameters.
 * @param I inbound input definition for headers and body.
 * @param O outbound response definitions.
 * @param id unique identifier to assign to the endpoint (usually the property name).
 * @param description optional short description shown in generated documentation.
 * @param details optional long-form documentation such as business rules or references.
 * @param builder DSL block invoked on a fresh [HttpEndpointVerbBuildingContext].
 * @return fully materialised [HttpEndpoint] built from the provided [builder].
 */
fun <P : Parameters, I : Input<*, *>, O : Outputs> endpoint(
    id: String,
    description: String? = null,
    details: String? = null,
    builder: HttpEndpointVerbBuildingContext.() -> HttpEndpointBuildingContext<P, I, O>
): HttpEndpoint<P, I, O> {
    val ctx = HttpEndpointVerbBuildingContext(id, description, details).builder()
    return HttpEndpoint(
        id = ctx.id,
        description = ctx.description,
        details = ctx.details,
        method = ctx.method,
        path = ctx.path,
        parameters = ctx.parameters,
        input = ctx.input,
        outputs = ctx.outputs
    )
}

/**
 * Declares an HTTP endpoint builder that captures property metadata at declaration time.
 *
 * The returned delegate instantiates a [HttpEndpointVerbBuildingContext] so the caller can choose
 * the verb and URI together, then chain header and body builders before materialising a [HttpEndpoint].
 *
 * @param T owner type that will receive the property.
 * @param P tuple capturing path and query parameters.
 * @param I inbound input definition for headers and body.
 * @param O outbound response definitions.
 * @param description optional short description shown in generated documentation.
 * @param details optional long-form documentation such as business rules or references.
 * @param builder DSL block building the endpoint structure using [HttpEndpointVerbBuildingContext].
 * @return property delegate that records endpoint metadata using the owning property name and produces [HttpEndpoint].
 * @see HttpEndpoint
 */
fun <T, P : Parameters, I : Input<*, *>, O : Outputs> endpoint(
    description: String? = null,
    details: String? = null,
    builder: HttpEndpointVerbBuildingContext.() -> HttpEndpointBuildingContext<P, I, O>
): ReadOnlyProperty<T, HttpEndpoint<P, I, O>> =
    ReadOnlyProperty { _, property ->
        endpoint(
            id = property.name,
            description = description,
            details = details,
            builder = builder
        )
    }
