package dev.akif.tapik.spring.restclient

import dev.akif.tapik.http.Status
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

fun Status.toHttpStatusCode(): HttpStatusCode = HttpStatusCode.valueOf(this.code)

fun HttpStatus.toStatus(): Status = Status.of(this.value())

fun HttpStatusCode.toStatus(): Status = Status.of(this.value())
