package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals

class OutputsTest {
    @Test
    fun `outputs append new items`() {
        val body1 = Output(StatusMatcher.Is(Status.OK), Headers0, stringBody())
        val body2 = Output(StatusMatcher.Is(Status.CREATED), Headers0, rawBody())

        val bodies = (Outputs0 + body1 + body2)

        assertEquals(listOf(body1, body2), bodies.toList())
    }
}
