package dev.akif.tapik.http

import arrow.core.getOrElse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.fail

class QueryParameterTest {
    @Test
    fun `optional creates non-required parameter without default`() {
        val parameter = QueryParameter.string("name")

        val optional = parameter.optional

        assertFalse(optional.required)
        assertNull(optional.default)
        assertEquals(parameter.codec, optional.codec)
    }

    @Test
    fun `optional with default keeps codec and default value`() {
        val parameter = QueryParameter.int("limit")

        val optional = parameter.optional(25)

        assertFalse(optional.required)
        assertEquals(25, optional.default)
    }

    @Test
    fun `optional with default copies name`() {
        val parameter = QueryParameter.uuid("id")

        val uuid =
            parameter.codec.decode("123e4567-e89b-12d3-a456-426614174000")
                .getOrElse { fail("Failed to decode UUID for default: $it") }
        val optional = parameter.optional(uuid)

        assertEquals("id", optional.name)
    }
}
