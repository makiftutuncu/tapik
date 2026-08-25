package dev.akif.tapik.plugin.spring.restclient

import dev.akif.tapik.MediaType
import dev.akif.tapik.Method
import dev.akif.tapik.Status
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType as SpringMediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.content
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity
import org.springframework.web.client.RestClient
import java.net.URI

class RestClientTransportSpec : FunSpec({
    test("exchange encoded request and return raw response") {
        val builder = RestClient.builder().baseUrl("https://library.example")
        val server = MockRestServiceServer.bindTo(builder).build()
        val transport = RestClientTransport(builder.build())
        val requestBody = "{\"title\":\"Kotlin\"}".encodeToByteArray()
        val responseBody = "{\"id\":\"book-1\"}".encodeToByteArray()

        server
            .expect(requestTo("https://library.example/books/book-1?author=a&author=b"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("X-Request-Id", "request-1"))
            .andExpect(content().bytes(requestBody))
            .andRespond(
                withCreatedEntity(URI("/books/book-1"))
                    .contentType(SpringMediaType.APPLICATION_JSON)
                    .body(responseBody)
            )

        val response =
            transport.exchange(
                method = Method.POST,
                uri = { uriBuilder ->
                    uriBuilder
                        .path("/books/{bookId}")
                        .queryParam("author", "a", "b")
                        .build("book-1")
                },
                headers = mapOf("X-Request-Id" to listOf("request-1")),
                body = RestClientRequestBody(MediaType.Json, requestBody)
            )

        response.status shouldBe Status.Created
        response.headers shouldContain ("Location" to listOf("/books/book-1"))
        response.mediaType shouldBe MediaType.Json
        response.body.contentEquals(responseBody) shouldBe true
        server.verify()
    }
})
