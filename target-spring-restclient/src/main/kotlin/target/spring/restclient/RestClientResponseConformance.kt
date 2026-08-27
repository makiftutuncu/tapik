package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.MediaType
import dev.akif.tapik.common.spring.toSpringMediaType

/**
 * Validates response body framing and selects the compatible declared media type.
 *
 * Declared media types are considered in order. `null` is returned only when [allowsNoBody] is true and [response]
 * contains neither a media type nor bytes.
 *
 * @throws IllegalStateException when the response does not conform to the declared body alternatives.
 */
fun selectResponseBodyMediaType(
    response: RestClientResponse,
    offered: List<MediaType>,
    allowsNoBody: Boolean,
    endpointId: String
): MediaType? {
    val actual = response.mediaType
    if (actual == null) {
        check(response.body.isEmpty()) { "Response body for $endpointId is missing Content-Type" }
        check(allowsNoBody) {
            "Response from $endpointId is missing Content-Type; expected one of ${offered.rendered()}"
        }
        return null
    }

    val selected =
        offered.firstOrNull { declared ->
            actual.toSpringMediaType().isCompatibleWith(declared.toSpringMediaType())
        }
    if (selected != null) return selected

    check(offered.isNotEmpty()) {
        "Unexpected response media type $actual for bodyless response from $endpointId"
    }
    error("Unexpected response media type $actual for $endpointId, expected one of ${offered.rendered()}")
}

private fun List<MediaType>.rendered(): String = joinToString(prefix = "[", postfix = "]")
