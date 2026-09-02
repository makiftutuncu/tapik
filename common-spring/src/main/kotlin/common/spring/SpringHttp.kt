package dev.akif.tapik.common.spring

import dev.akif.tapik.MediaType
import dev.akif.tapik.Method
import dev.akif.tapik.Status
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType as SpringMediaType

/** Converts this tapik method to Spring's HTTP method representation. */
fun Method.toHttpMethod(): HttpMethod = HttpMethod.valueOf(name)

/**
 * Converts this Spring HTTP method to tapik's standard method representation.
 *
 * @throws IllegalArgumentException when this is not a standard method supported by tapik.
 */
fun HttpMethod.toMethod(): Method = Method.valueOf(name())

/** Converts this tapik status to Spring's complete HTTP status-code representation. */
fun Status.toHttpStatusCode(): HttpStatusCode = HttpStatusCode.valueOf(code)

/** Converts this Spring HTTP status code to a tapik status. */
fun HttpStatusCode.toStatus(): Status = Status(value())

/**
 * Parses this complete tapik media type as a Spring media type.
 *
 * @throws org.springframework.http.InvalidMediaTypeException when [MediaType.value] is invalid.
 */
fun MediaType.toSpringMediaType(): SpringMediaType = SpringMediaType.parseMediaType(value)

/** Converts this Spring media type to tapik's complete string representation. */
fun SpringMediaType.toMediaType(): MediaType = MediaType(toString())

/** Returns an immutable copy of every header name and value. */
fun HttpHeaders.toTapikHeaders(): Map<String, List<String>> =
    buildMap {
        this@toTapikHeaders.forEach { name, values -> put(name, values.toList()) }
    }
