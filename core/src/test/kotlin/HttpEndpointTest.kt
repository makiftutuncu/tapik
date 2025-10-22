package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HttpEndpointTest {
    @Test
    fun `http endpoint without method chooses HTTP verb`() {
        val builder = HttpEndpointWithoutMethod("getUser", "Get a user", "Loads a user by identifier")
        val endpoint = builder.get.uri(root / "users")

        assertEquals("getUser", endpoint.id)
        assertEquals(Method.GET, endpoint.method)
        assertEquals(listOf("users"), endpoint.uriWithParameters.uri)
        assertEquals("Get a user", endpoint.description)
        assertEquals("Loads a user by identifier", endpoint.details)
    }

    @Test
    fun `endpoint builders accumulate headers bodies and outputs`() {
        val base = HttpEndpointWithoutMethod("test", "desc", "details").post.uri(root / "items")

        val inputHeader = Header.string("X-Request-Id")
        val outputHeader = Header.int("X-Count")

        val endpoint =
            base
                .inputHeader(inputHeader)
                .inputBody { stringBody() }
                .outputHeader(outputHeader)
                .outputBody(Status.CREATED) { rawBody(mediaType = MediaType.Json) }

        assertEquals(Method.POST, endpoint.method)
        assertEquals(listOf("items"), endpoint.uriWithParameters.uri)
        assertEquals(listOf(inputHeader), endpoint.inputHeaders.toList())
        assertIs<StringBody>(endpoint.inputBody)
        assertEquals(listOf(outputHeader), endpoint.outputHeaders.toList())
        val bodies = endpoint.outputBodies.toList()
        assertEquals(1, bodies.size)
        assertTrue(bodies.first().statusMatcher(Status.CREATED))
        assertEquals(MediaType.Json, (bodies.first().body as RawBody).mediaType)
    }
}
