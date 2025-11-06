package dev.akif.tapik

import dev.akif.tapik.codec.StringCodecs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ClientExtensionsTest {
    private object TestClient : Client

    @Test
    fun `asQueryParameter casts successfully`() {
        val parameter = QueryParameter.int("limit").optional(20)

        val cast =
            with(TestClient) {
                parameter.asQueryParameter<Int>()
            }

        assertEquals(parameter, cast)
    }

    @Test
    fun `asQueryParameter fails fast when types mismatch`() {
        val error =
            assertFailsWith<IllegalStateException> {
                with(TestClient) {
                    "not a parameter".asQueryParameter<Int>()
                }
            }

        assertTrue(error.message!!.contains("Expected QueryParameter but got"))
    }

    @Test
    fun `getDefaultOrFail returns configured default`() {
        val parameter = QueryParameter.int("limit").optional(15)

        val value =
            with(TestClient) {
                parameter.getDefaultOrFail()
            }

        assertEquals(15, value)
    }

    @Test
    fun `getDefaultOrFail throws when default is missing`() {
        val parameter = QueryParameter.int("limit")

        assertFailsWith<IllegalArgumentException> {
            with(TestClient) {
                parameter.getDefaultOrFail()
            }
        }
    }

    @Test
    fun `getFirstValueOrFail returns head of header values`() {
        val values = HeaderValues("X-Tapik", StringCodecs.string("X-Tapik"), listOf("first", "second"))

        val first =
            with(TestClient) {
                values.getFirstValueOrFail()
            }

        assertEquals("first", first)
    }

    @Test
    fun `getFirstValueOrFail throws for empty header values`() {
        val emptyValues = HeaderValues("X-Tapik", StringCodecs.string("X-Tapik"), emptyList())

        assertFailsWith<IllegalArgumentException> {
            with(TestClient) {
                emptyValues.getFirstValueOrFail()
            }
        }
    }
}
