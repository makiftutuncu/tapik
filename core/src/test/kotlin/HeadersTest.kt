package dev.akif.tapik.http

import kotlin.test.Test
import kotlin.test.assertEquals

class HeadersTest {
    @Test
    fun `headers containers accumulate values`() {
        val h1 = Header.string("X-First")
        val h2 = Header.int("X-Second")

        val headers = (Headers0 + h1 + h2)

        assertEquals(listOf(h1, h2), headers.toList())
    }
}
