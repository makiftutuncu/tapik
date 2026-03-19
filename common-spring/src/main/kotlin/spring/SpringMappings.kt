package dev.akif.tapik.spring

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType as SpringMediaType

/**
 * Converts Tapik [Status] definitions into Spring [HttpStatus] instances.
 *
 * @receiver Tapik status value.
 * @return corresponding Spring [HttpStatus].
 */
fun Status.toHttpStatus(): HttpStatus = HttpStatus.valueOf(code)

/**
 * Converts Tapik [Status] definitions into Spring [HttpStatusCode] instances.
 *
 * @receiver Tapik status value.
 * @return corresponding Spring [HttpStatusCode].
 */
fun Status.toHttpStatusCode(): HttpStatusCode = HttpStatusCode.valueOf(code)

/**
 * Converts a Spring [HttpStatus] into a Tapik [Status].
 *
 * @receiver Spring [HttpStatus] value.
 * @return equivalent Tapik [Status].
 */
fun HttpStatus.toStatus(): Status = Status.of(value())

/**
 * Converts a Spring [HttpStatusCode] into a Tapik [Status].
 *
 * @receiver Spring [HttpStatusCode] value.
 * @return equivalent Tapik [Status].
 */
fun HttpStatusCode.toStatus(): Status = Status.of(value())

/**
 * Converts Spring [HttpHeaders] into the plain header map shape expected by Tapik generators.
 *
 * @receiver Spring response headers.
 * @return immutable map of header names to all of their string values.
 */
fun HttpHeaders.toTapikHeaders(): Map<String, List<String>> =
    buildMap {
        this@toTapikHeaders.forEach { name, values ->
            put(name, values.toList())
        }
    }

/**
 * Converts Tapik [MediaType] definitions into Spring [MediaType] instances.
 *
 * @receiver Tapik media type.
 * @return corresponding Spring [MediaType].
 */
fun MediaType.toSpringMediaType(): SpringMediaType = SpringMediaType(major, minor)
