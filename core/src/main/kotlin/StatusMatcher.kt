package dev.akif.tapik

/**
 * Strategy for determining whether an HTTP status matches an output body definition.
 */
sealed interface StatusMatcher : (Status) -> Boolean {
    /** Matches a single [status] exactly. */
    data class Is(
        /** Status that must match for this matcher to evaluate to `true`. */
        val status: Status
    ) : StatusMatcher {
        /**
         * Evaluates whether [p1] matches the configured [status].
         *
         * @param p1 status under evaluation.
         * @return `true` when [p1] equals [status].
         */
        override fun invoke(p1: Status): Boolean = status == p1
    }

    /** Matches against any status contained in [statuses]. */
    data class AnyOf(
        /** Set of statuses that match this predicate. */
        val statuses: Set<Status>
    ) : StatusMatcher {
        /**
         * Evaluates whether [p1] is present inside [statuses].
         *
         * @param p1 status under evaluation.
         * @return `true` when [p1] is included in [statuses].
         */
        override fun invoke(p1: Status): Boolean = p1 in statuses
    }

    /** Delegates to an arbitrary [predicate] with a human-readable [description]. */
    data class Predicate(
        /** Human-readable explanation used in generated documentation. */
        val description: String,
        /** Callback used to evaluate the matcher. */
        val predicate: (Status) -> Boolean
    ) : StatusMatcher {
        /**
         * Delegates status matching to [predicate].
         *
         * @param p1 status under evaluation.
         * @return `true` when [predicate] accepts [p1].
         */
        override fun invoke(p1: Status): Boolean = predicate(p1)
    }

    /** Matches nothing, often used as a sentinel default. */
    data object Unmatched : StatusMatcher {
        /**
         * Always returns `false` because the matcher never succeeds.
         *
         * @param p1 ignored status argument.
         * @return `false`.
         */
        override fun invoke(p1: Status): Boolean = false
    }
}
