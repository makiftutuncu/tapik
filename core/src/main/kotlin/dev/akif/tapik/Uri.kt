package dev.akif.tapik

/** Marker shared by typed path variables stored in a [Uri]. */
sealed interface PathVariable

/** Marker shared by typed query parameters stored in a [Uri]. */
sealed interface QueryParameter

/** An ordered, heterogeneous tuple of path variables. */
typealias Paths = Tuple<PathVariable>

/** An ordered, heterogeneous tuple of query parameters. */
typealias Queries = Tuple<QueryParameter>

/**
 * An immutable URI definition with exact path-parameter and query-parameter tuple types.
 *
 * @param P path-parameter tuple type.
 * @param Q query-parameter tuple type.
 * @property path normalized, already-encoded path segments.
 * @property pathParameters path parameters in declaration order.
 * @property queryParameters query parameters in declaration order.
 */
@ConsistentCopyVisibility
data class Uri<out P : Paths, out Q : Queries> internal constructor(
    val path: List<String>,
    val pathParameters: P,
    val queryParameters: Q
)

/** The root URI from which all URI definitions start. */
val root: Uri<Tuple0, Tuple0> =
    Uri(
        path = emptyList(),
        pathParameters = Tuple0,
        queryParameters = Tuple0
    )

/** Appends the non-empty segments in an already-encoded [fragment]. */
operator fun <P : Paths, Q : Queries> Uri<P, Q>.div(fragment: String): Uri<P, Q> {
    val normalizedFragment = fragment.trim('/')

    require(normalizedFragment.isNotEmpty()) { "URI path fragment must contain at least one segment" }

    val newSegments = normalizedFragment.split('/')
    require(newSegments.none(String::isEmpty)) {
        "URI path fragment must not contain empty segments: '$fragment'"
    }

    return Uri(
        path = path + newSegments,
        pathParameters = pathParameters,
        queryParameters = queryParameters
    )
}
