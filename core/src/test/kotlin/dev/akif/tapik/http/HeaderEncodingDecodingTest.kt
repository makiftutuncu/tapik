package dev.akif.tapik.http

import arrow.core.getOrElse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class HeaderEncodingDecodingTest {
    private val inputHeader = Header.string("X-Request-Id")
    private val outputHeader = Header.int("X-Count")

    @Test
    fun `encode input headers serializes using codecs`() {
        val endpoint =
            HttpEndpoint(
                id = "test",
                description = null,
                details = null,
                method = Method.GET,
                uriWithParameters = root,
                inputHeaders = Headers1(inputHeader),
                inputBody = EmptyBody,
                outputHeaders = Headers0,
                outputBodies = OutputBodies0
            )

        val encoded = endpoint.encodeInputHeaders("abc123")

        assertEquals(mapOf("X-Request-Id" to listOf("abc123")), encoded)
    }

    @Test
    fun `encode output headers reuses codecs`() {
        val endpoint =
            HttpEndpoint(
                id = "test",
                description = null,
                details = null,
                method = Method.POST,
                uriWithParameters = root / "items",
                inputHeaders = Headers0,
                inputBody = EmptyBody,
                outputHeaders = Headers1(outputHeader),
                outputBodies = OutputBodies0
            )

        val encoded = endpoint.encodeOutputHeaders(5)

        assertEquals(mapOf("X-Count" to listOf("5")), encoded)
    }

    @Test
    fun `decode headers returns decoded lists`() {
        val headers =
            mapOf(
                "X-Count" to listOf("1", "2"),
                "X-Request-Id" to listOf("req-1")
            )

        val decoded =
            decodeHeaders2(headers, outputHeader, inputHeader).getOrElse { errors ->
                fail("Decoding failed: $errors")
            }

        assertEquals(listOf(1, 2), decoded.item1)
        assertEquals(listOf("req-1"), decoded.item2)
    }

    @Test
    fun `decode headers accumulates errors for missing required values`() {
        val result = decodeHeaders1(emptyMap(), inputHeader)

        val errors =
            result.fold(
                { it },
                { fail("Decoding should have failed") }
            )

        assertTrue(errors.all { it.contains("Required header") })
    }

    @Test
    fun `decode headers accumulates codec errors`() {
        val headers = mapOf("X-Count" to listOf("not-a-number"))

        val result = decodeHeaders1(headers, outputHeader)

        val errors =
            result.fold(
                { it },
                { fail("Decoding should have failed") }
            )

        assertEquals(1, errors.size)
    }
}
