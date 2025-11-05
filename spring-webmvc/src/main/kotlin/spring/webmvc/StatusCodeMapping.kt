package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType as SpringMediaType

/**
 * Converts Tapik [Status] definitions into Spring [HttpStatus] instances.
 *
 * @receiver Tapik status value.
 * @return corresponding Spring [HttpStatus].
 */
fun Status.toHttpStatus(): HttpStatus = HttpStatus.valueOf(this.code)

/**
 * Converts Tapik [MediaType] definitions into Spring [MediaType] instances.
 *
 * @receiver Tapik media type.
 * @return corresponding Spring [MediaType].
 */
fun MediaType.toSpringMediaType(): SpringMediaType = SpringMediaType(major, minor)
