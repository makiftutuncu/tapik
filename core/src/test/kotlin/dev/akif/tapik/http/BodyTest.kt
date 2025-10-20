package dev.akif.tapik.http

import dev.akif.tapik.codec.ByteArrayCodec
import dev.akif.tapik.codec.Codec
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BodyTest {
    @Test
    fun `empty body exposes no media type or bytes`() {
        assertNull(EmptyBody.mediaType)
        assertNull(EmptyBody.bytes(Unit))
    }

    @Test
    fun `string body encodes text as UTF-8`() {
        val body = stringBody("payload")

        assertEquals(MediaType.PlainText, body.mediaType)
        assertContentEquals("hello".toByteArray(), body.bytes("hello"))
    }

    @Test
    fun `raw body keeps provided bytes`() {
        val body = rawBody()
        val payload = "raw".toByteArray()

        assertNull(body.mediaType)
        assertContentEquals(payload, body.bytes(payload))
    }

    @Test
    fun `json body enforces application json media type`() {
        val codec: ByteArrayCodec<Map<String, Int>> =
            Codec.nullable<Map<String, Int>, ByteArray>(
                name = "map",
                encoder = { map ->
                    map.entries.joinToString(separator = ";") { (key, value) -> "$key=$value" }.toByteArray()
                },
                decoder = { bytes ->
                    runCatching {
                        String(bytes)
                            .takeIf { it.isNotEmpty() }
                            ?.split(";")
                            ?.associate { entry ->
                                val (key, value) = entry.split("=")
                                key to value.toInt()
                            }
                    }.getOrNull()
                }
            )

        val body = JsonBody(codec)
        val payload = mapOf("answer" to 42)

        assertEquals(MediaType.Json, body.mediaType)
        val decoded =
            body.codec.decode(body.bytes(payload)!!).fold(
                { error -> error("Decoding failed: $error") },
                { it }
            )
        assertEquals(payload, decoded)
    }

    @Test
    fun `string body factory accepts custom codec`() {
        val upperCasingCodec =
            Codec.nullable<String, ByteArray>(
                name = "upper",
                encoder = { it.uppercase().toByteArray() },
                decoder = { String(it) }
            )

        val body = stringBody(upperCasingCodec)
        val bytes = body.bytes("ok")!!
        assertContentEquals("OK".toByteArray(), bytes)
    }
}
