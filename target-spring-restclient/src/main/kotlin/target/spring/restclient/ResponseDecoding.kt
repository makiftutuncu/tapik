package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.DecodeResult
import dev.akif.tapik.StringFormat

/**
 * Decodes response [bytes] with [format].
 *
 * @param format format for the response body.
 * @param bytes encoded response body.
 * @param endpointId endpoint identifier included in an error message.
 * @return the decoded body value.
 * @throws IllegalStateException when [format] returns a decoding failure. Exceptions thrown by the format decoder
 * propagate unchanged.
 */
fun <Value : Any> decodeResponseBody(
    format: ByteArrayFormat<Value>,
    bytes: ByteArray,
    endpointId: String
): Value = decodeResponse(format.decode(bytes), endpointId, "body")

/**
 * Decodes response header [value] with [format].
 *
 * @param format format for the response header.
 * @param value encoded header value.
 * @param endpointId endpoint identifier included in an error message.
 * @return the decoded header value.
 * @throws IllegalStateException when [format] returns a decoding failure. Exceptions thrown by the format decoder
 * propagate unchanged.
 */
fun <Value : Any> decodeResponseHeader(
    format: StringFormat<Value>,
    value: String,
    endpointId: String
): Value = decodeResponse(format.decode(value), endpointId, "header")

private fun <Value : Any> decodeResponse(
    result: DecodeResult<Value>,
    endpointId: String,
    location: String
): Value =
    when (result) {
        is DecodeResult.Success -> result.value
        is DecodeResult.Failure ->
            error("Cannot decode response $location for $endpointId: " + result.errors.joinToString { it.message })
    }
