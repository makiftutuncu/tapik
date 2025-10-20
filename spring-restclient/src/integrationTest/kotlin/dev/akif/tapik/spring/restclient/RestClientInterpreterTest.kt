package dev.akif.tapik.spring.restclient

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import dev.akif.tapik.http.Method
import dev.akif.tapik.http.OutputBody
import dev.akif.tapik.http.Status
import dev.akif.tapik.http.StatusMatcher
import dev.akif.tapik.http.stringBody
import dev.akif.tapik.jackson.jsonBody
import java.net.URI
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

class RestClientInterpreterTest {
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

        val outputBodies = listOf(OutputBody(StatusMatcher.Is(Status.OK), jsonBody<Map<String, Any?>>("user")))

        val response =
            interpreter.send(
                method = Method.GET,
                uri = URI.create("/users"),
                inputHeaders = emptyMap(),
                inputBodyContentType = null,
                inputBody = null,
                outputBodies = outputBodies
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("""{"id":1,"name":"Tapik"}""", response.body?.decodeToString())
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

        val outputBodies = listOf(OutputBody(StatusMatcher.Is(Status.OK), jsonBody<Map<String, Any?>>("user")))

        val exception =
            assertFailsWith<RestClientResponseException> {
                interpreter.send(
                    method = Method.GET,
                    uri = URI.create("/users"),
                    inputHeaders = emptyMap(),
                    inputBodyContentType = null,
                    inputBody = null,
                    outputBodies = outputBodies
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

        val outputBodies =
            listOf(
                OutputBody(StatusMatcher.Is(Status.OK), stringBody()),
                OutputBody(StatusMatcher.AnyOf(setOf(Status.CREATED, Status.NO_CONTENT)), stringBody()),
                OutputBody(StatusMatcher.Predicate("status >= 500") { it.code >= 500 }, stringBody())
            )

        val exception =
            assertFailsWith<RestClientResponseException> {
                interpreter.send(
                    method = Method.GET,
                    uri = URI.create("/users"),
                    inputHeaders = emptyMap(),
                    inputBodyContentType = null,
                    inputBody = null,
                    outputBodies = outputBodies
                )
            }

        assertTrue(exception.message!!.contains("expecting [OK, CREATED, NO_CONTENT, status >= 500]"), exception.message)
    }
}
