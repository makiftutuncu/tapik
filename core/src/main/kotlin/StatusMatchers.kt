package dev.akif.tapik

/** A matcher selecting the HTTP statuses represented by an output. */
sealed interface StatusMatcher {
    /** Returns whether this matcher accepts [status]. */
    fun matches(status: Status): Boolean
}

/** A matcher selecting exactly [status]. */
data class ExactStatus(
    val status: Status
) : StatusMatcher {
    override fun matches(status: Status): Boolean = this.status == status
}

/** A matcher selecting any status in the non-empty [statuses] set. */
@ConsistentCopyVisibility
data class StatusSet internal constructor(
    val statuses: Set<Status>
) : StatusMatcher {
    init {
        require(statuses.isNotEmpty()) { "Status set must not be empty" }
    }

    override fun matches(status: Status): Boolean = status in statuses
}

/** Builds a matcher for [first] and every status in [rest]. */
fun statusesOf(
    first: Status,
    vararg rest: Status
): StatusSet = StatusSet(setOf(first, *rest))

/** A matcher selecting statuses whose numeric codes belong to [range]. */
data class StatusRange(
    val range: IntRange
) : StatusMatcher {
    init {
        require(!range.isEmpty()) { "Status range must not be empty" }
        Status(range.first)
        Status(range.last)
    }

    override fun matches(status: Status): Boolean = status.code in range
}

/** Builds a matcher for the HTTP status codes in [range]. */
fun statusesIn(range: IntRange): StatusRange = StatusRange(range)

/**
 * A custom status matcher with a stable human-readable [description].
 *
 * The [predicate] must be pure and deterministic because tapik evaluates it during contract validation and targets
 * may evaluate it again at runtime.
 */
data class CustomStatus(
    val description: String,
    val predicate: (Status) -> Boolean
) : StatusMatcher {
    init {
        require(description.isNotBlank()) { "Custom status matcher description must not be blank" }
    }

    override fun matches(status: Status): Boolean = predicate(status)
}

/** Builds a custom status matcher from its stable [description] and runtime [predicate]. */
fun statusMatching(
    description: String,
    predicate: (Status) -> Boolean
): CustomStatus = CustomStatus(description, predicate)
