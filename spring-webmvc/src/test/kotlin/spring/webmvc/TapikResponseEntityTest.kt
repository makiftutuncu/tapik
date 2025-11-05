package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.*
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TapikResponseEntityTest {
    object SampleEndpoints {
        val user by http {
            get
                .uri("api" / "users" / path.uuid("id"))
                .output(headers = { Headers1(Header.Location) }) { stringBody() }
                .output(Status.NOT_FOUND) { dev.akif.tapik.EmptyBody }
        }

        val ping by http {
            get
                .uri("health" / "ping")
                .output { stringBody() }
        }
    }

    @Test
    fun `converts oneOf response with headers and body`() {
        val response =
            OneOf2.Option1(
                Response1(
                    status = Status.OK,
                    body = "ok",
                    header1 = listOf(URI("https://example.com/users/1"))
                )
            )

        val entity = SampleEndpoints.user.toResponseEntity(response)

        assertEquals(200, entity.statusCode.value())
        assertEquals("ok", entity.body)
        assertEquals("text/plain", entity.headers.contentType?.toString())
        assertEquals(listOf("https://example.com/users/1"), entity.headers["Location"])
    }

    @Test
    fun `converts oneOf response without body`() {
        val response =
            OneOf2.Option2(
                ResponseWithoutBody0(Status.NOT_FOUND)
            )

        val entity = SampleEndpoints.user.toResponseEntity(response)

        assertEquals(404, entity.statusCode.value())
        assertNull(entity.body)
        assertNull(entity.headers.contentType)
        assertNull(entity.headers["Location"])
    }

    @Test
    fun `converts single output response`() {
        val response =
            Response0(
                status = Status.OK,
                body = "pong"
            )

        val entity = SampleEndpoints.ping.toResponseEntity(response)

        assertEquals(200, entity.statusCode.value())
        assertEquals("pong", entity.body)
        assertEquals(MediaType.PlainText.toString(), entity.headers.contentType?.toString())
    }
}
