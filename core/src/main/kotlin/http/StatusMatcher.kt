package dev.akif.tapik.http

/**
 * Strategy for determining whether an HTTP status matches an output body definition.
 */
sealed interface StatusMatcher : (Status) -> Boolean {
    /** Matches a single [status] exactly. */
    data class Is(
        val status: Status
    ) : StatusMatcher {
        override fun invoke(p1: Status): Boolean = status == p1
    }

    /** Matches against any status contained in [statuses]. */
    data class AnyOf(
        val statuses: Set<Status>
    ) : StatusMatcher {
        override fun invoke(p1: Status): Boolean = p1 in statuses
    }

    /** Delegates to an arbitrary [predicate] with a human-readable [description]. */
    data class Predicate(
        val description: String,
        val predicate: (Status) -> Boolean
    ) : StatusMatcher {
        override fun invoke(p1: Status): Boolean = predicate(p1)
    }

    /** Matches nothing, often used as a sentinel default. */
    data object Unmatched : StatusMatcher {
        override fun invoke(p1: Status): Boolean = false
    }
}
