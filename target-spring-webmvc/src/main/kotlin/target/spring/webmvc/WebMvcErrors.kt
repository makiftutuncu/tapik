package dev.akif.tapik.target.spring.webmvc

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/** Responds with `400 Bad Request` and [message]. */
fun webMvcBadRequest(message: String): Nothing =
    throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)

/** Responds with `415 Unsupported Media Type` for [actual] request content on [endpointId]. */
fun webMvcUnsupportedMediaType(
    actual: String?,
    endpointId: String
): Nothing =
    throw ResponseStatusException(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        "Unsupported Content-Type $actual for $endpointId"
    )

/** Responds with `406 Not Acceptable` for the request [accept] value on [endpointId]. */
fun webMvcNotAcceptable(
    accept: String?,
    endpointId: String
): Nothing =
    throw ResponseStatusException(
        HttpStatus.NOT_ACCEPTABLE,
        "No response body matches Accept $accept for $endpointId"
    )
