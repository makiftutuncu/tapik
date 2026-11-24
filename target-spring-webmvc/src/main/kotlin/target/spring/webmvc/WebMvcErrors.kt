package dev.akif.tapik.target.spring.webmvc

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

/**
 * Throws the Spring exception that produces `400 Bad Request`.
 *
 * @param message response message.
 * @throws org.springframework.web.server.ResponseStatusException always.
 */
fun webMvcBadRequest(message: String): Nothing =
    throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)

/**
 * Throws the Spring exception that produces `415 Unsupported Media Type`.
 *
 * @param actual received request `Content-Type`, when present.
 * @param endpointId endpoint identifier included in the response message.
 * @throws org.springframework.web.server.ResponseStatusException always.
 */
fun webMvcUnsupportedMediaType(
    actual: String?,
    endpointId: String
): Nothing =
    throw ResponseStatusException(
        HttpStatus.UNSUPPORTED_MEDIA_TYPE,
        "Unsupported Content-Type $actual for $endpointId"
    )

/**
 * Throws the Spring exception that produces `406 Not Acceptable`.
 *
 * @param accept received `Accept` header, when present.
 * @param endpointId endpoint identifier included in the response message.
 * @throws org.springframework.web.server.ResponseStatusException always.
 */
fun webMvcNotAcceptable(
    accept: String?,
    endpointId: String
): Nothing =
    throw ResponseStatusException(
        HttpStatus.NOT_ACCEPTABLE,
        "No response body matches Accept $accept for $endpointId"
    )
