package dev.akif.tapik

import arrow.core.getOrElse
import dev.akif.tapik.codec.StringCodecs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.fail

class QueryParameterTest {
    @Test
    fun `optional requires a default value`() {
        val parameter = QueryParameter.string("name")

        assertFailsWith<IllegalArgumentException> {
            parameter.copy(required = false, default = null)
        }
    }

    @Test
    fun `optional with default keeps codec and default value`() {
        val parameter = QueryParameter.int("limit")

        val optional = parameter.optional(25)

        assertFalse(optional.required)
        assertEquals(25, optional.default)
    }

    @Test
    fun `query helper requires default when parameter optional`() {
        assertFailsWith<IllegalArgumentException> {
            QueryParameter(
                name = "page",
                codec = StringCodecs.int("page"),
                required = false,
                default = null
            )
        }
    }

    @Test
    fun `optional with default copies name`() {
        val parameter = QueryParameter.uuid("id")

        val uuid =
            parameter.codec
                .decode("123e4567-e89b-12d3-a456-426614174000")
                .getOrElse { fail("Failed to decode UUID for default: $it") }
        val optional = parameter.optional(uuid)

        assertEquals("id", optional.name)
    }
}
