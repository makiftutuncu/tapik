package dev.akif.tapik

/**
 * An immutable URI definition with exact path-variable and query-parameter tuple types.
 *
 * @param P path-variable tuple type.
 * @param Q query-parameter tuple type.
 * @property segments normalized, already-encoded path segments.
 * @property paths path variables in declaration order.
 * @property queries query parameters in declaration order.
 */
@ConsistentCopyVisibility
data class Uri<out P : Paths, out Q : Queries> internal constructor(
    val segments: List<PathSegment>,
    val paths: P,
    val queries: Q
) {
    /** Returns the complete URI template. */
    override fun toString(): String {
        val renderedPath =
            if (segments.isEmpty()) {
                "/"
            } else {
                segments.joinToString(separator = "/", prefix = "/") { segment ->
                    when (segment) {
                        is PathSegment.Literal -> segment.value
                        is PathVariable<*> -> "{${segment.name}}"
                    }
                }
            }

        if (queries.values.isEmpty()) {
            return renderedPath
        }

        return queries.values.joinToString(separator = "&", prefix = "$renderedPath?") { parameter ->
            "${parameter.name}={${parameter.name}}"
        }
    }
}

/** The root URI from which all URI definitions start. */
val root: Uri<Paths0, Queries0> =
    Uri(
        segments = emptyList(),
        paths = Paths0,
        queries = Queries0
    )
