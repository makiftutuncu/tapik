package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusCodeMappingTest {
    @Test
    fun `converts tapik status to http status`() {
        val status = Status.of(201)
        val httpStatus = status.toHttpStatus()

        assertEquals(201, httpStatus.value())
    }

    @Test
    fun `converts tapik media type to spring media type`() {
        val mediaType = MediaType.Json
        val spring = mediaType.toSpringMediaType()

        assertEquals("application/json", spring.toString())
    }
}
