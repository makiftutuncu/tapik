package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.DecodeResult
import dev.akif.tapik.StringFormat

/** Decodes a response [bytes] value or fails with its [endpointId] and body location. */
fun <Value : Any> decodeResponseBody(
    format: ByteArrayFormat<Value>,
    bytes: ByteArray,
    endpointId: String
): Value = decodeResponse(format.decode(bytes), endpointId, "body")

/** Decodes a response header [value] or fails with its [endpointId] and header location. */
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
