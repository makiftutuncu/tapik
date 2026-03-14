package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HelpersExtensionsTest {
    @Test
    fun `asQueryParameter casts successfully`() {
        val parameter = QueryParameter.int("limit").optional(20)

        val cast = parameter.asQueryParameter<Int>()

        assertEquals(parameter, cast)
    }

    @Test
    fun `asQueryParameter fails fast when types mismatch`() {
        val error =
            assertFailsWith<IllegalStateException> {
                "not a parameter".asQueryParameter<Int>()
            }

        assertTrue(error.message!!.contains("Expected QueryParameter but got"))
    }

    @Test
    fun `getDefaultOrFail returns configured query parameter default`() {
        val parameter = QueryParameter.int("limit").optional(20)

        val defaultValue = parameter.getDefaultOrFail()

        assertEquals(20, defaultValue)
    }

    @Test
    fun `getDefaultOrFail throws when query parameter default is missing`() {
        val parameter = QueryParameter.int("limit")

        assertFailsWith<IllegalArgumentException> {
            parameter.getDefaultOrFail()
        }
    }

    @Test
    fun `getFirstValueOrFail returns head of header values`() {
        val header = Header.string("X-Tapik")
        val values = HeaderValues("X-Tapik", header.codec, listOf("first", "second"))

        val first = values.getFirstValueOrFail()

        assertEquals("first", first)
    }

    @Test
    fun `getFirstValueOrFail throws for empty header values`() {
        val header = Header.string("X-Tapik")
        val emptyValues = HeaderValues("X-Tapik", header.codec, emptyList())

        assertFailsWith<IllegalArgumentException> {
            emptyValues.getFirstValueOrFail()
        }
    }
}
