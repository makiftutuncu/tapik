package dev.akif.tapik.spring.restclient

import dev.akif.tapik.Status
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

/**
 * Converts Tapik [Status] definitions into Spring [HttpStatusCode] instances.
 *
 * @receiver Tapik status value.
 * @return corresponding Spring [HttpStatusCode].
 */
fun Status.toHttpStatusCode(): HttpStatusCode = HttpStatusCode.valueOf(this.code)

/**
 * Converts a Spring [HttpStatus] into a Tapik [Status].
 *
 * @receiver Spring [HttpStatus] value.
 * @return equivalent Tapik [Status].
 */
fun HttpStatus.toStatus(): Status = Status.of(this.value())

/**
 * Converts a Spring [HttpStatusCode] into a Tapik [Status].
 *
 * @receiver Spring [HttpStatusCode] value.
 * @return equivalent Tapik [Status].
 */
fun HttpStatusCode.toStatus(): Status = Status.of(this.value())
