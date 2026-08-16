package dev.akif.tapik

/** The empty endpoint tag set. */
val noTags: Set<String> = emptySet()

internal fun validatedTags(tags: Set<String>): Set<String> {
    require(tags.none(String::isBlank)) { "Endpoint tags must not be blank" }
    return tags.toSet()
}

/** Appends [tag] to this draft endpoint's set of tags. */
fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, O, Draft>.tag(
        tag: String
    ): Endpoint<P, Q, H, I, O, Draft> = tags(tags + tag)

/** Replaces this draft endpoint's tags with [tags]. */
fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, O, Draft>.tags(
        tags: Set<String>
    ): Endpoint<P, Q, H, I, O, Draft> = copy(tags = validatedTags(tags))
