package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HttpEndpointTest {
    private val inputHeader = Header.string("X-Request-Id")
    private val outputHeader = Header.int("X-Count")

    private val getUser by endpoint(description = "Get a user", details = "Loads a user by identifier") {
        get("users")
    }

    private val postItem by endpoint(description = "desc", details = "details") {
        post("items")
            .input(inputHeader) { stringBody() }
            .output(Status.CREATED, headersOf(outputHeader)) {
                rawBody(mediaType = MediaType.Json)
            }
    }

    @Test
    fun `http endpoint builder chooses HTTP verb`() {
        val endpoint = getUser

        assertEquals("getUser", endpoint.id)
        assertEquals(Method.GET, endpoint.method)
        assertEquals(listOf("users"), endpoint.path)
        assertEquals("Get a user", endpoint.description)
        assertEquals("Loads a user by identifier", endpoint.details)
    }

    @Test
    fun `endpoint builders accumulate headers bodies and outputs`() {
        val endpoint = postItem

        assertEquals(Method.POST, endpoint.method)
        assertEquals(listOf("items"), endpoint.path)
        assertEquals(listOf(inputHeader), endpoint.input.headers.toList())
        assertIs<StringBody>(endpoint.input.body)
        val bodies = endpoint.outputs.toList()
        assertEquals(1, bodies.size)
        val output = bodies.first()
        assertTrue(output.statusMatcher(Status.CREATED))
        assertEquals(listOf(outputHeader), output.headers.toList())
        assertEquals(MediaType.Json, (output.body as RawBody).mediaType)
    }
}
