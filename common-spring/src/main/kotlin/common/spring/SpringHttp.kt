package dev.akif.tapik.common.spring

import dev.akif.tapik.MediaType
import dev.akif.tapik.Method
import dev.akif.tapik.Status
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType as SpringMediaType

/**
 * Converts this tapik method to Spring's HTTP method representation.
 *
 * @return the Spring method with the same name.
 */
fun Method.toHttpMethod(): HttpMethod = HttpMethod.valueOf(name)

/**
 * Converts this Spring HTTP method to tapik's standard method representation.
 *
 * @return the tapik method with the same name.
 * @throws IllegalArgumentException when this is not a standard method supported by tapik.
 */
fun HttpMethod.toMethod(): Method = Method.valueOf(name())

/**
 * Converts this tapik status to Spring's HTTP status-code representation.
 *
 * @return a Spring status code with the same numeric value.
 */
fun Status.toHttpStatusCode(): HttpStatusCode = HttpStatusCode.valueOf(code)

/**
 * Converts this Spring HTTP status code to a tapik status.
 *
 * @return a tapik status with the same numeric value.
 */
fun HttpStatusCode.toStatus(): Status = Status(value())

/**
 * Parses this complete tapik media type as a Spring media type.
 *
 * @throws org.springframework.http.InvalidMediaTypeException when Spring cannot support a parameter, such as an
 * unknown charset. Neutral syntax has already been validated by [MediaType].
 *
 * @return the Spring media type with the same value and parameters.
 */
fun MediaType.toSpringMediaType(): SpringMediaType = SpringMediaType.parseMediaType(value)

/**
 * Converts this Spring media type to tapik's complete string representation.
 *
 * @return a tapik media type containing the Spring media type's serialized value.
 */
fun SpringMediaType.toMediaType(): MediaType = MediaType(toString())

/**
 * Returns an immutable copy of every header name and value.
 *
 * Each value list is copied as well.
 *
 * @return copied headers represented as tapik string values.
 */
fun HttpHeaders.toTapikHeaders(): Map<String, List<String>> =
    buildMap {
        this@toTapikHeaders.forEach { name, values -> put(name, values.toList()) }
    }
