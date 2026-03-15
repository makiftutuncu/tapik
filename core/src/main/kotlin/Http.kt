@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import arrow.core.None
import arrow.core.Some
import dev.akif.tapik.codec.StringCodec

/**
 * Defines a required path placeholder.
 *
 * Use the returned [PathVariable] inside a URI template such as `"users" / path.long("id")`.
 * Tapik renders it as `{name}` in the path while retaining [codec] so generators know the runtime
 * type of the captured segment.
 */
inline fun <reified P : Any> path(
    name: String,
    codec: StringCodec<P>
): PathVariable<P> = PathVariable(name, codec)

/** Shortcut to the predefined [PathVariable] factory methods such as [PathVariable.Companion.long]. */
val path: PathVariable.Companion = PathVariable.Companion

/**
 * Defines a required query parameter.
 *
 * Add it to a URI template with `+` so the query parameter becomes part of the endpoint contract
 * without affecting the literal path segments.
 */
fun <Q : Any> query(
    name: String,
    codec: StringCodec<Q>
): QueryParameter<Q> = QueryParameter(name, codec, default = None)

/**
 * Defines an optional query parameter by recording the [default] to use when callers omit it.
 */
fun <Q : Any> query(
    name: String,
    codec: StringCodec<Q>,
    default: Q?
): QueryParameter<Q> = QueryParameter(name, codec, default = Some(default))

/** Shortcut to the predefined [QueryParameter] factory methods such as [QueryParameter.Companion.int]. */
val query: QueryParameter.Companion = QueryParameter.Companion

/**
 * Defines a required header slot.
 *
 * Use the returned [Header] in `input(...)` when callers must provide the header, or call it as a
 * function later to pin concrete values for an optional response header.
 */
inline fun <reified H : Any> header(
    name: String,
    codec: StringCodec<H>
): Header<H> = HeaderInput(name, codec)

/** Shortcut to Tapik's predefined [Header] factory methods and standard header definitions. */
val header: Header.Companion = Header.Companion

/**
 * Matches any of the supplied statuses.
 *
 * This is useful when multiple concrete status codes share the same response shape.
 */
fun anyStatus(
    first: Status,
    vararg rest: Status
): StatusMatcher = StatusMatcher.AnyOf(setOf(first, *rest))

/**
 * Builds a custom status matcher described by [description].
 *
 * Use this when the matching rule matters to the endpoint contract but cannot be expressed as one
 * fixed status or a small set of statuses.
 */
fun matchStatus(
    description: String,
    predicate: (Status) -> Boolean
): StatusMatcher = StatusMatcher.Predicate(description, predicate)

/** Status matcher that never succeeds, typically used as a sentinel or placeholder. */
val unmatchedStatus: StatusMatcher =
    StatusMatcher.Unmatched
