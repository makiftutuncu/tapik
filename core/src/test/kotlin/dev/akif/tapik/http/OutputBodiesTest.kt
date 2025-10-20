package dev.akif.tapik.http

import kotlin.test.Test
import kotlin.test.assertEquals

class OutputBodiesTest {
    @Test
    fun `output bodies append new items`() {
        val body1 = OutputBody(StatusMatcher.Is(Status.OK), stringBody())
        val body2 = OutputBody(StatusMatcher.Is(Status.CREATED), rawBody())

        val bodies = (OutputBodies0 + body1 + body2)

        assertEquals(listOf(body1, body2), bodies.toList())
    }
}
