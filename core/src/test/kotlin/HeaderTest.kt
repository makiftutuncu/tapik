package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HeaderTest {
    @Test
    fun `header input is required by default`() {
        val header = Header.string("X-Request-Id")

        assertEquals("X-Request-Id", header.name)
        assertTrue(header.required)
        assertEquals("request-id", header.codec.encode("request-id"))
    }

    @Test
    fun `header values become optional`() {
        val headerValues = Header.string("X-Group")("first", "second")

        assertEquals("X-Group", headerValues.name)
        assertFalse(headerValues.required)
        assertEquals(listOf("first", "second"), headerValues.values)
    }

    @Test
    fun `companion exposes well known headers`() {
        assertEquals(Header.ACCEPT_VALUE, Header.Accept.name)
        assertEquals(Header.CONTENT_TYPE_VALUE, Header.ContentType.name)
        assertEquals(Header.LOCATION_VALUE, Header.Location.name)
        assertEquals(Header.AUTHORIZATION_VALUE, Header.Authorization.name)
        assertEquals(Header.CONTENT_LENGTH_VALUE, Header.ContentLength.name)
        assertEquals(Header.REFERER_VALUE, Header.Referer.name)
    }
}
