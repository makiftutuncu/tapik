package dev.akif.tapik

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
        assertEquals("empty", EmptyBody.name)
    }

    @Test
    fun `string body encodes text as UTF-8`() {
        val body = stringBody("payload")

        assertEquals(MediaType.PlainText, body.mediaType)
        assertEquals("payload", body.name)
        assertContentEquals("hello".toByteArray(), body.bytes("hello"))
    }

    @Test
    fun `raw body keeps provided bytes`() {
        val body = rawBody()
        val payload = "raw".toByteArray()

        assertNull(body.mediaType)
        assertEquals("bytes", body.name)
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

        val body = JsonBody(codec, "json")
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
        assertEquals("string", body.name)
        assertContentEquals("OK".toByteArray(), bytes)
    }
}
