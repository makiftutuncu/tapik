package dev.akif.tapik.codec

import arrow.core.getOrElse
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class CodecTest {
    @Test
    fun `identity codec encodes and decodes without modification`() {
        val codec = Codec.identity<Int>("int")

        val encoded = codec.encode(42)
        assertEquals(42, encoded)

        val decoded = codec.decode(42)
        assertEquals(42, decoded.getOrElse { fail("Unexpected left: $it") })
    }

    @Test
    fun `nullable codec propagates decoding errors`() {
        val codec = Codec.nullable<Int, String>("int", Int::toString) { it.toIntOrNull() }

        val success = codec.decode("7")
        assertEquals(7, success.getOrElse { fail("Unexpected left: $it") })

        val failure = codec.decode("NaN")
        val errors =
            failure.fold(
                ifLeft = { it },
                ifRight = { fail("Expected decoding failure but succeeded with $it") }
            )
        assertTrue(errors.all { it.contains("Cannot decode 'int'") })
    }

    @Test
    fun `unsafe codec wraps thrown exceptions into descriptive errors`() {
        val codec =
            Codec.unsafe<Int, String>("int", Int::toString) {
                if (it == "boom") error("boom")
                it.toInt()
            }

        val failure = codec.decode("boom")
        val errors =
            failure.fold(
                ifLeft = { it },
                ifRight = { fail("Expected decoding failure but succeeded with $it") }
            )
        assertTrue(errors.first().contains("Cannot decode 'int' as Int: boom"))
    }

    @Test
    fun `unsafeTransformSource converts source type bidirectionally`() {
        val base = Codec.identity<Int>("int")

        val transformed = base.unsafeTransformSource(from = Int::toLong, to = Long::toInt)

        val decoded = transformed.decode(10)
        assertEquals(10L, decoded.getOrElse { fail("Unexpected left: $it") })

        val encoded = transformed.encode(25L)
        assertEquals(25, encoded)
    }

    @Test
    fun `unsafeTransformTarget converts target type bidirectionally`() {
        val base = Codec.identity<Int>("int")

        val transformed =
            base.unsafeTransformTarget(
                from = String::toInt,
                to = Int::toString
            )

        val decoded = transformed.decode("11")
        assertEquals(11, decoded.getOrElse { fail("Unexpected left: $it") })

        val encoded = transformed.encode(12)
        assertEquals("12", encoded)
    }

    @Test
    fun `string codec can be converted to byte array codec`() {
        val stringCodec = StringCodecs.string("value")
        val byteCodec = stringCodec.toByteArrayCodec()

        val payload = "tapik"
        val encoded = byteCodec.encode(payload)
        assertContentEquals(payload.toByteArray(), encoded)

        val decoded = byteCodec.decode(payload.toByteArray())
        assertEquals(payload, decoded.getOrElse { fail("Unexpected left: $it") })
    }
}
