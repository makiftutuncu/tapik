@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import dev.akif.tapik.codec.StringCodec

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
