package dev.akif.tapik

import dev.akif.tapik.codec.StringCodecs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HelpersExtensionsTest {
    private object TestHelpers : Helpers

    @Test
    fun `asQueryParameter casts successfully`() {
        val parameter = QueryParameter.int("limit").optional(20)

        val cast =
            with(TestHelpers) {
                parameter.asQueryParameter<Int>()
            }

        assertEquals(parameter, cast)
    }

    @Test
    fun `asQueryParameter fails fast when types mismatch`() {
        val error =
            assertFailsWith<IllegalStateException> {
                with(TestHelpers) {
                    "not a parameter".asQueryParameter<Int>()
                }
            }

        assertTrue(error.message!!.contains("Expected QueryParameter but got"))
    }

    @Test
    fun `getDefaultOrFail returns configured query parameter default`() {
        val parameter = QueryParameter.int("limit").optional(20)

        val defaultValue =
            with(TestHelpers) {
                parameter.getDefaultOrFail()
            }

        assertEquals(20, defaultValue)
    }

    @Test
    fun `getDefaultOrFail throws when query parameter default is missing`() {
        val parameter = QueryParameter.int("limit")

        assertFailsWith<IllegalArgumentException> {
            with(TestHelpers) {
                parameter.getDefaultOrFail()
            }
        }
    }

    @Test
    fun `getFirstValueOrFail returns head of header values`() {
        val values = HeaderValues("X-Tapik", StringCodecs.string("X-Tapik"), listOf("first", "second"))

        val first =
            with(TestHelpers) {
                values.getFirstValueOrFail()
            }

        assertEquals("first", first)
    }

    @Test
    fun `getFirstValueOrFail throws for empty header values`() {
        val emptyValues = HeaderValues("X-Tapik", StringCodecs.string("X-Tapik"), emptyList())

        assertFailsWith<IllegalArgumentException> {
            with(TestHelpers) {
                emptyValues.getFirstValueOrFail()
            }
        }
    }
}
