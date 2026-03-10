package dev.akif.tapik.codec

import arrow.core.getOrElse
import dev.akif.tapik.ValueClass
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class StringCodecsTest {
    @Test
    fun `ofValueClass round trips value classes`() {
        val codec = StringCodecs.ofValueClass<String, UserId>()

        val decoded = codec.decode("user-1").getOrElse { fail("Unexpected decode failure: $it") }

        assertEquals(UserId("user-1"), decoded)
        assertEquals("user-1", codec.encode(decoded))
    }

    @Test
    fun `ofValueClass uses custom string conversion when encoding`() {
        val codec = StringCodecs.ofValueClass<Int, ItemId>(toString = { "item-$it" })

        assertEquals("item-42", codec.encode(ItemId(42)))
    }

    @ParameterizedTest
    @MethodSource("validBooleanInputs")
    fun `boolean codec strictly decodes accepted values`(
        input: String,
        expected: Boolean
    ) {
        val codec = StringCodecs.boolean("active")

        val decoded = codec.decode(input).getOrElse { fail("Unexpected decode failure: $it") }

        assertEquals(expected, decoded)
    }

    @ParameterizedTest
    @MethodSource("invalidBooleanInputs")
    fun `boolean codec rejects invalid values`(input: String) {
        val codec = StringCodecs.boolean("active")

        val errors =
            codec
                .decode(input)
                .fold(
                    ifLeft = { it },
                    ifRight = { fail("Expected decoding failure but succeeded with $it") }
                )

        assertTrue(errors.first().contains("Cannot decode 'active'"))
    }

    @ParameterizedTest
    @MethodSource("invalidNumberInputs")
    fun `bigInteger codec fails fast with descriptive error`(input: String) {
        val codec = StringCodecs.bigInteger("count")

        val errors =
            codec
                .decode(input)
                .fold(
                    ifLeft = { it },
                    ifRight = { fail("Expected decoding failure but succeeded with $it") }
                )

        assertTrue(errors.first().contains("Cannot decode 'count' as BigInteger"))
    }

    @ParameterizedTest
    @MethodSource("uuidInputs")
    fun `uuid codec round trips`(id: String) {
        val codec = StringCodecs.uuid("id")

        val decoded = codec.decode(id).getOrElse { fail("Unexpected decode failure: $it") }
        assertEquals(id, codec.encode(decoded))
    }

    companion object {
        @JvmStatic
        fun validBooleanInputs(): Stream<Arguments> =
            Stream.of(
                Arguments.of("true", true),
                Arguments.of("false", false)
            )

        @JvmStatic
        fun invalidBooleanInputs(): Stream<String> = Stream.of("yes", "1", "")

        @JvmStatic
        fun invalidNumberInputs(): Stream<String> = Stream.of("not-a-number", "123abc", "")

        @JvmStatic
        fun uuidInputs(): Stream<String> =
            Stream.of(
                "123e4567-e89b-12d3-a456-426614174000",
                "ffffffff-ffff-ffff-ffff-ffffffffffff"
            )
    }
}

@JvmInline
value class UserId(
    override val value: String
) : ValueClass<String>

@JvmInline
value class ItemId(
    override val value: Int
) : ValueClass<Int>
