package dev.akif.tapik

/** Strategy used to decide which output variant matches a response status. */
sealed interface StatusMatcher : (Status) -> Boolean {
    /** Matches one exact status code. */
    data class Is(
        /** Status that selects the output. */
        val status: Status
    ) : StatusMatcher {
        /** Returns `true` only when the candidate status equals [status]. */
        override fun invoke(p1: Status): Boolean = status == p1
    }

    /** Matches any status contained in [statuses]. */
    data class AnyOf(
        /** Set of statuses that select the output. */
        val statuses: Set<Status>
    ) : StatusMatcher {
        /** Returns `true` when the candidate status is one of [statuses]. */
        override fun invoke(p1: Status): Boolean = p1 in statuses
    }

    /**
     * Custom matcher backed by an arbitrary predicate.
     *
     * [description] exists because the predicate itself cannot be rendered meaningfully in generated
     * documentation, error messages, or code.
     */
    data class Predicate(
        /** Human-readable explanation of the matching rule. */
        val description: String,
        /** Callback that decides whether a status selects the output. */
        val predicate: (Status) -> Boolean
    ) : StatusMatcher {
        /** Delegates matching to [predicate]. */
        override fun invoke(p1: Status): Boolean = predicate(p1)
    }

    /** Matcher that never succeeds, typically used as a sentinel. */
    data object Unmatched : StatusMatcher {
        /** Always returns `false`, so this matcher never selects an output. */
        override fun invoke(p1: Status): Boolean = false
    }
}
