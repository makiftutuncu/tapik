package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.DecodeResult
import dev.akif.tapik.Format
import dev.akif.tapik.StringFormat

/**
 * Decodes one raw string request value.
 *
 * @param format format for the request value.
 * @param raw encoded request value.
 * @param location human-readable request location included in an error message.
 * @return the decoded value.
 * @throws org.springframework.web.server.ResponseStatusException with `400 Bad Request` when [format] returns a
 * decoding failure. Exceptions thrown by the format decoder propagate unchanged.
 */
fun <Value : Any> decodeRequest(
    format: StringFormat<Value>,
    raw: String,
    location: String
): Value = decodeRequest(format.decode(raw), location)

/**
 * Decodes repeated raw string request values.
 *
 * @param format format for the repeated request value.
 * @param raw encoded request values in received order.
 * @param location human-readable request location included in an error message.
 * @return the decoded value.
 * @throws org.springframework.web.server.ResponseStatusException with `400 Bad Request` when [format] returns a
 * decoding failure. Exceptions thrown by the format decoder propagate unchanged.
 */
fun <Value : Any> decodeRequest(
    format: Format<Value, List<String>>,
    raw: List<String>,
    location: String
): Value = decodeRequest(format.decode(raw), location)

/**
 * Decodes one raw request body.
 *
 * @param format format for the request body.
 * @param raw encoded request body.
 * @param location human-readable request location included in an error message.
 * @return the decoded value.
 * @throws org.springframework.web.server.ResponseStatusException with `400 Bad Request` when [format] returns a
 * decoding failure. Exceptions thrown by the format decoder propagate unchanged.
 */
fun <Value : Any> decodeRequest(
    format: ByteArrayFormat<Value>,
    raw: ByteArray,
    location: String
): Value = decodeRequest(format.decode(raw), location)

private fun <Value : Any> decodeRequest(
    result: DecodeResult<Value>,
    location: String
): Value =
    when (result) {
        is DecodeResult.Success -> result.value
        is DecodeResult.Failure ->
            webMvcBadRequest("Cannot decode $location: " + result.errors.joinToString { it.message })
    }
