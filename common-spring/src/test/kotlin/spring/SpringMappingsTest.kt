package dev.akif.tapik.spring

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import dev.akif.tapik.TapikResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    @Test
    fun `converts spring headers to tapik header map`() {
        val headers =
            HttpHeaders().apply {
                addAll("X-Trace-Id", listOf("trace-1", "trace-2"))
                add("Content-Type", "application/json")
            }

        val tapikHeaders = headers.toTapikHeaders()

        assertEquals(listOf("trace-1", "trace-2"), tapikHeaders["X-Trace-Id"])
        assertEquals(listOf("application/json"), tapikHeaders["Content-Type"])
    }

    @Test
    fun `builds response entity from tapik response metadata`() {
        val response = object : TapikResponse {}

        val entity =
            response.toResponseEntity(
                status = Status.Created,
                headers = mapOf("X-Trace-Id" to listOf("trace-1", "trace-2")),
                mediaType = MediaType.Json,
                body = """{"id":1}""".encodeToByteArray()
            )

        assertEquals(HttpStatus.CREATED, entity.statusCode)
        assertEquals("application/json", entity.headers.contentType.toString())
        assertEquals(listOf("trace-1", "trace-2"), entity.headers["X-Trace-Id"])
        assertContentEquals("""{"id":1}""".encodeToByteArray(), entity.body as ByteArray)
    }

    @Test
    fun `builds empty response entity when body is absent`() {
        val response = object : TapikResponse {}

        val entity = response.toResponseEntity(status = Status.NoContent)

        assertEquals(HttpStatus.NO_CONTENT, entity.statusCode)
        assertNull(entity.body)
    }
}
