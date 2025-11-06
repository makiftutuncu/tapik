package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParameterTest {
    @Test
    fun `path variable defaults`() {
        val variable = PathVariable.int("userId")

        assertEquals("userId", variable.name)
        assertEquals(ParameterPosition.Path, variable.position)
        assertTrue(variable.required)
        assertEquals("42", variable.codec.encode(42))
    }

    @Test
    fun `query parameter defaults to required without default`() {
        val parameter = QueryParameter.string("search")

        assertTrue(parameter.required)
        assertEquals(ParameterPosition.Query, parameter.position)
    }

    @Test
    fun `query parameter optional helpers update required flag and default`() {
        val required = QueryParameter.int("limit")
        val optionalWithDefault = required.optional(10)

        assertFalse(optionalWithDefault.required)
        assertEquals(10, optionalWithDefault.default)

        assertFailsWith<IllegalArgumentException> {
            required.copy(required = false, default = null)
        }
    }
}
