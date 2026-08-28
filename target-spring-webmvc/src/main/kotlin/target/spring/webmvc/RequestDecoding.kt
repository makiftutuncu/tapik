package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.DecodeResult
import dev.akif.tapik.Format
import dev.akif.tapik.StringFormat

/** Decodes one raw string request value or responds with `400 Bad Request`. */
fun <Value : Any> decodeRequest(
    format: StringFormat<Value>,
    raw: String,
    location: String
): Value = decodeRequest(format.decode(raw), location)

/** Decodes repeated raw string request values or responds with `400 Bad Request`. */
fun <Value : Any> decodeRequest(
    format: Format<Value, List<String>>,
    raw: List<String>,
    location: String
): Value = decodeRequest(format.decode(raw), location)

/** Decodes one raw request body or responds with `400 Bad Request`. */
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
