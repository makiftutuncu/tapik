package dev.akif.tapik

/**
 * An immutable URI definition with exact path-variable and query-parameter tuple types.
 *
 * @param P path-variable tuple type.
 * @param Q query-parameter tuple type.
 * @property path normalized, already-encoded path segments.
 * @property pathVariables path variables in declaration order.
 * @property queryParameters query parameters in declaration order.
 */
@ConsistentCopyVisibility
data class Uri<out P : Paths, out Q : Queries> internal constructor(
    val path: List<PathSegment>,
    val pathVariables: P,
    val queryParameters: Q
) {
    /** Returns the complete URI template. */
    override fun toString(): String =
        if (path.isEmpty()) {
            "/"
        } else {
            path.joinToString(separator = "/", prefix = "/") { segment ->
                when (segment) {
                    is PathSegment.Literal -> segment.value
                    is PathVariable<*> -> "{${segment.name}}"
                }
            }
        }
}

/** The root URI from which all URI definitions start. */
val root: Uri<Paths0, Queries0> =
    Uri(
        path = emptyList(),
        pathVariables = Paths0,
        queryParameters = Queries0
    )
