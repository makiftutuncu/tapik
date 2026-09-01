package dev.akif.tapik.common.plugin

import dev.akif.tapik.PathSegment
import dev.akif.tapik.PathVariable
import dev.akif.tapik.CustomStatus
import dev.akif.tapik.ExactStatus
import dev.akif.tapik.Status
import dev.akif.tapik.StatusMatcher
import dev.akif.tapik.StatusRange
import dev.akif.tapik.StatusSet
import dev.akif.tapik.Uri

/** Returns this URI's path template without its query parameters. */
fun Uri<*, *>.pathTemplate(): String =
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

/** Returns the stable Kotlin variant name for this HTTP status. */
fun Status.kotlinVariantName(): String =
    when (this) {
        Status.Ok -> "Ok"
        Status.Created -> "Created"
        Status.NoContent -> "NoContent"
        Status.BadRequest -> "BadRequest"
        Status.NotFound -> "NotFound"
        Status.Conflict -> "Conflict"
        Status.InternalServerError -> "InternalServerError"
        else -> "Status$code"
    }

/** Returns the stable preferred Kotlin variant name for this status matcher. */
fun StatusMatcher.kotlinVariantName(): String =
    when (this) {
        is ExactStatus -> status.kotlinVariantName()
        is StatusSet -> statuses.joinToString(separator = "Or") { status -> status.kotlinVariantName() }
        is StatusRange -> "Status${range.first}To${range.last}"
        is CustomStatus -> description.upperCamel()
    }

/** The source-facing Tapik [Status] type used by Kotlin generation targets. */
val statusKotlinSourceType: KotlinSourceType =
    KotlinSourceType(
        source = "dev.akif.tapik.Status",
        expandedType = KotlinType(KotlinClassClassifier("dev.akif.tapik.Status"))
    )
