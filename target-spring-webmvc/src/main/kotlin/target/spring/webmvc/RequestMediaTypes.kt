package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.MediaType
import dev.akif.tapik.common.spring.toSpringMediaType
import org.springframework.http.InvalidMediaTypeException

/** Returns whether an actual request `Content-Type` is compatible with [expected]. */
fun matchesRequestMediaType(
    actual: String?,
    expected: MediaType
): Boolean =
    try {
        actual != null && org.springframework.http.MediaType.parseMediaType(actual).isCompatibleWith(expected.toSpringMediaType())
    } catch (_: InvalidMediaTypeException) {
        false
    }
