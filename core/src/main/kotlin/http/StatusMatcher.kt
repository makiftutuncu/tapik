package dev.akif.tapik.http

sealed interface StatusMatcher : (Status) -> Boolean {
    data class Is(
        val status: Status
    ) : StatusMatcher {
        override fun invoke(p1: Status): Boolean = status == p1
    }

    data class AnyOf(
        val statuses: Set<Status>
    ) : StatusMatcher {
        override fun invoke(p1: Status): Boolean = p1 in statuses
    }

    data class Predicate(
        val description: String,
        val predicate: (Status) -> Boolean
    ) : StatusMatcher {
        override fun invoke(p1: Status): Boolean = predicate(p1)
    }

    data object Unmatched : StatusMatcher {
        override fun invoke(p1: Status): Boolean = false
    }
}
