package dev.akif.tapik.common.spring

import dev.akif.tapik.MediaType
import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.MediaType as SpringMediaType

/**
 * Selects one of the [offered] response representations for an HTTP [accept] value.
 *
 * The most specific matching range determines each representation's effective quality. Representations are then
 * ordered by quality, matching-range specificity, and declaration order. A missing or blank [accept] selects the first
 * representation.
 *
 * @return the selected representation, or `null` when none is acceptable or [accept] is invalid.
 */
fun selectResponseMediaType(
    accept: String?,
    offered: List<MediaType>
): MediaType? {
    if (offered.isEmpty()) return null
    if (accept.isNullOrBlank()) return offered.first()

    val requested =
        try {
            SpringMediaType.parseMediaTypes(accept)
        } catch (_: InvalidMediaTypeException) {
            return null
        }
    val candidates =
        offered.mapIndexedNotNull { index, mediaType ->
            val representation =
                try {
                    mediaType.toSpringMediaType()
                } catch (_: InvalidMediaTypeException) {
                    return@mapIndexedNotNull null
                }
            val range = requested.effectiveRange(representation) ?: return@mapIndexedNotNull null
            if (range.mediaType.qualityValue <= 0.0) return@mapIndexedNotNull null
            ResponseCandidate(mediaType, index, range)
        }

    return candidates.maxWithOrNull(RESPONSE_CANDIDATE_COMPARATOR)?.mediaType
}

private data class AcceptedRange(
    val mediaType: SpringMediaType,
    val index: Int
) {
    val specificity: Int =
        when {
            mediaType.isWildcardType -> 0
            mediaType.isWildcardSubtype -> 1
            else -> 2
        }
    val parameterCount: Int = mediaType.parameters.keys.count { name -> name != QUALITY_PARAMETER }
}

private data class ResponseCandidate(
    val mediaType: MediaType,
    val index: Int,
    val range: AcceptedRange
)

private fun List<SpringMediaType>.effectiveRange(offered: SpringMediaType): AcceptedRange? =
    withIndex()
        .mapNotNull { (index, requested) ->
            AcceptedRange(requested, index).takeIf { requested.accepts(offered) }
        }.maxWithOrNull(ACCEPTED_RANGE_COMPARATOR)

private fun SpringMediaType.accepts(offered: SpringMediaType): Boolean =
    isCompatibleWith(offered) &&
        parameters
            .filterKeys { name -> name != QUALITY_PARAMETER }
            .all { (name, value) -> offered.parameters[name] == value }

private val ACCEPTED_RANGE_COMPARATOR: Comparator<AcceptedRange> =
    compareBy<AcceptedRange> { it.specificity }
        .thenBy { it.parameterCount }
        .thenBy { -it.index }

private val RESPONSE_CANDIDATE_COMPARATOR: Comparator<ResponseCandidate> =
    compareBy<ResponseCandidate> { it.range.mediaType.qualityValue }
        .thenBy { it.range.specificity }
        .thenBy { it.range.parameterCount }
        .thenBy { -it.index }

private const val QUALITY_PARAMETER: String = "q"
