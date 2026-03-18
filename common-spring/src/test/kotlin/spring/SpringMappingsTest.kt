package dev.akif.tapik.spring

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class SpringMappingsTest {
    @Test
    fun `converts tapik status to http status`() {
        assertEquals(HttpStatus.CREATED, Status.Created.toHttpStatus())
    }

    @Test
    fun `converts tapik status to http status code`() {
        assertEquals(HttpStatus.CREATED.value(), Status.Created.toHttpStatusCode().value())
    }

    @Test
    fun `converts http status to tapik status`() {
        assertEquals(Status.Ok, HttpStatus.OK.toStatus())
    }

    @Test
    fun `converts http status code to tapik status`() {
        assertEquals(Status.BadRequest, HttpStatusCode.valueOf(400).toStatus())
    }

    @Test
    fun `converts tapik media type to spring media type`() {
        assertEquals("application/json", MediaType.Json.toSpringMediaType().toString())
    }
}
