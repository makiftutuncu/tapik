package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.MediaType
import dev.akif.tapik.common.spring.toSpringMediaType
import org.springframework.http.InvalidMediaTypeException

/**
 * Returns whether the request `Content-Type` is compatible with [expected].
 *
 * A missing or invalid content type returns `false`.
 *
 * @param actual received `Content-Type` header value.
 * @param expected endpoint media type.
 * @return `true` when both media types are compatible.
 */
fun matchesRequestMediaType(
    actual: String?,
    expected: MediaType
): Boolean =
    try {
        actual != null && org.springframework.http.MediaType.parseMediaType(actual).isCompatibleWith(expected.toSpringMediaType())
    } catch (_: InvalidMediaTypeException) {
        false
    }
