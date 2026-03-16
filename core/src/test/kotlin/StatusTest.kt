package dev.akif.tapik

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StatusTest {
    @ParameterizedTest
    @MethodSource("knownStatusCodes")
    fun `of returns status for known code`(
        expected: Status,
        code: Int
    ) {
        assertEquals(expected, Status.of(code))
    }

    @ParameterizedTest
    @MethodSource("unknownStatusCodes")
    fun `of throws for unknown status code`(code: Int) {
        assertFailsWith<IllegalArgumentException> {
            Status.of(code)
        }
    }

    @Test
    fun `anyStatus matches any supplied status`() {
        val matcher = anyStatus(Status.Ok, Status.Created)

        assertTrue(matcher(Status.Ok))
        assertTrue(matcher(Status.Created))
        assertFalse(matcher(Status.BadRequest))
    }

    companion object {
        @JvmStatic
        fun knownStatusCodes(): Stream<Arguments> =
            Stream.of(*Status.entries.map { Arguments.of(it, it.code) }.toTypedArray())

        @JvmStatic
        fun unknownStatusCodes(): Stream<Int> = Stream.of(-1, 0, 199, 399, 499, 999)
    }
}
