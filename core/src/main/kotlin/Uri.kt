package dev.akif.tapik

/**
 * An immutable URI definition with exact path-value and query-parameter tuple types.
 *
 * Path variable names must be unique among paths, and query parameter names must be unique among queries. A remaining
 * path, when present, is the final path value.
 *
 * Literal path fragments use already-encoded text. Leading and trailing slashes are removed; empty fragments and
 * repeated internal separators are rejected.
 *
 * Add path fragments and variables before query parameters. The DSL supports up to eight path values and eight query
 * parameters.
 *
 * @param P path-value tuple type.
 * @param Q query-parameter tuple type.
 * @property paths path values in declaration order, with unique names.
 * @property queries query parameters in declaration order, with unique names.
 */
class Uri<out P : Paths, out Q : Queries> internal constructor(
    segments: List<PathSegment>,
    /** Path values in declaration order. */
    val paths: P,
    /** Query parameters in declaration order. */
    val queries: Q
) {
    /** Unmodifiable snapshot of normalized path segments, whose literal values are already encoded. */
    val segments: List<PathSegment> = segments.snapshotList()

    /** Returns [segments] for destructuring. */
    operator fun component1(): List<PathSegment> = segments

    /** Returns [paths] for destructuring. */
    operator fun component2(): P = paths

    /** Returns [queries] for destructuring. */
    operator fun component3(): Q = queries

    /** Returns a copy, snapshotting structural collection inputs. */
    internal fun copy(
        segments: List<PathSegment> = this.segments,
        paths: @UnsafeVariance P = this.paths,
        queries: @UnsafeVariance Q = this.queries
    ): Uri<P, Q> = Uri(segments, paths, queries)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is Uri<*, *> &&
                segments == other.segments &&
                paths == other.paths &&
                queries == other.queries)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int {
        var result = segments.hashCode()
        result = 31 * result + paths.hashCode()
        result = 31 * result + queries.hashCode()
        return result
    }

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
                        is RemainingPath -> "{*${segment.name}}"
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
