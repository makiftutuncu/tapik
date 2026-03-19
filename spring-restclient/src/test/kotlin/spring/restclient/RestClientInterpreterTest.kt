package dev.akif.tapik.spring.restclient

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import dev.akif.tapik.Method
import dev.akif.tapik.Output
import dev.akif.tapik.Status
import dev.akif.tapik.StatusMatcher
import dev.akif.tapik.emptyHeaders
import dev.akif.tapik.jackson.jsonBody
import dev.akif.tapik.stringBody
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

class RestClientInterpreterTest {
    private data class User(
        val id: Int,
        val name: String
    )

    private lateinit var server: WireMockServer
    private lateinit var interpreter: RestClientInterpreter

    @BeforeTest
    fun setup() {
        server = WireMockServer(WireMockConfiguration.options().dynamicPort())
        server.start()
        interpreter = RestClientInterpreter(RestClient.builder().baseUrl(server.baseUrl()).build())
        server.resetAll()
    }

    @AfterTest
    fun tearDown() {
        if (this::server.isInitialized) {
            server.stop()
        }
    }

    @Test
    fun `send returns response when status and content type match`() {
        server.stubFor(
            get(urlEqualTo("/users"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"id":1,"name":"Tapik"}""")
                )
        )

        val outputs = listOf(Output(StatusMatcher.Is(Status.Ok), emptyHeaders(), jsonBody<Map<String, Any?>>("user")))

        val response =
            interpreter.send(
                method = Method.GET,
                uri = URI.create("/users"),
                inputHeaders = emptyMap(),
                inputBodyContentType = null,
                inputBody = null,
                outputs = outputs
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("""{"id":1,"name":"Tapik"}""", response.body?.decodeToString())
    }

    @Test
    fun `send preserves generic jackson body types for generated client decoding`() {
        server.stubFor(
            get(urlEqualTo("/users"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("""[{"id":1,"name":"Tapik"},{"id":2,"name":"Codex"}]""")
                )
        )

        val body = jsonBody<List<User>>("users")
        val outputs = listOf(Output(StatusMatcher.Is(Status.Ok), emptyHeaders(), body))

        val response =
            interpreter.send(
                method = Method.GET,
                uri = URI.create("/users"),
                inputHeaders = emptyMap(),
                inputBodyContentType = null,
                inputBody = null,
                outputs = outputs
            )

        val decoded =
            body.codec.decode(requireNotNull(response.body)).fold(
                ifLeft = { fail("Unexpected decode failure: $it") },
                ifRight = { it }
            )

        assertEquals(listOf(User(1, "Tapik"), User(2, "Codex")), decoded)
        assertIs<User>(decoded.first())
    }

    @Test
    fun `send fails when content type mismatches expected media type`() {
        server.stubFor(
            get(urlEqualTo("/users"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "text/plain")
                        .withBody("plain text")
                )
        )

        val outputs = listOf(Output(StatusMatcher.Is(Status.Ok), emptyHeaders(), jsonBody<Map<String, Any?>>("user")))

        val exception =
            assertFailsWith<RestClientResponseException> {
                interpreter.send(
                    method = Method.GET,
                    uri = URI.create("/users"),
                    inputHeaders = emptyMap(),
                    inputBodyContentType = null,
                    inputBody = null,
                    outputs = outputs
                )
            }

        assertTrue(exception.message!!.contains("Unexpected content type"), exception.message)
    }

    @Test
    fun `send fails when status code does not match any expectations`() {
        server.stubFor(
            get(urlEqualTo("/users"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")
                )
        )

        val outputs =
            listOf(
                Output(StatusMatcher.Is(Status.Ok), emptyHeaders(), stringBody()),
                Output(StatusMatcher.AnyOf(setOf(Status.Created, Status.NoContent)), emptyHeaders(), stringBody()),
                Output(StatusMatcher.Predicate("status >= 500") { it.code >= 500 }, emptyHeaders(), stringBody())
            )

        val exception =
            assertFailsWith<RestClientResponseException> {
                interpreter.send(
                    method = Method.GET,
                    uri = URI.create("/users"),
                    inputHeaders = emptyMap(),
                    inputBodyContentType = null,
                    inputBody = null,
                    outputs = outputs
                )
            }

        assertTrue(
            exception.message!!.contains("expecting [Ok, Created, NoContent, status >= 500]"),
            exception.message
        )
    }
}
