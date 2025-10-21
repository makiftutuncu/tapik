package dev.akif.tapik.http

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

    companion object {
        @JvmStatic
        fun knownStatusCodes(): Stream<Arguments> =
            Stream.of(*Status.entries.map { Arguments.of(it, it.code) }.toTypedArray())

        @JvmStatic
        fun unknownStatusCodes(): Stream<Int> = Stream.of(-1, 0, 199, 399, 499, 999)
    }
}
