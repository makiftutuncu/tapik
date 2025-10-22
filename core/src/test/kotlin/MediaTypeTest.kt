package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MediaTypeTest {
    @Test
    fun `of returns predefined media types when available`() {
        val plainText = MediaType.of("text", "plain")
        val json = MediaType.of("application", "json")

        assertIs<MediaType.PlainText>(plainText)
        assertIs<MediaType.Json>(json)
        assertEquals("text/plain", plainText.toString())
        assertEquals("application/json", json.toString())
    }

    @Test
    fun `of falls back to custom`() {
        val svg = MediaType.of("image", "svg+xml")

        assertEquals("image", (svg as MediaType.Custom).major)
        assertEquals("svg+xml", svg.minor)
        assertEquals("image/svg+xml", svg.toString())
    }
}
