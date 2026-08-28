package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.MediaType
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class WebMvcResponsesSpec : FunSpec({
    test("match compatible request media types and reject invalid values") {
        matchesRequestMediaType("application/json;charset=UTF-8", MediaType.Json) shouldBe true
        matchesRequestMediaType("invalid", MediaType.Json) shouldBe false
        matchesRequestMediaType(null, MediaType.Json) shouldBe false
    }

    test("construct encoded Spring responses") {
        val response =
            webMvcResponse(
                status = 201,
                headers = mapOf("Location" to listOf("/books/1")),
                body = MediaType.Json to "{}".encodeToByteArray()
            )

        response.statusCode.value() shouldBe 201
        response.headers.getFirst("Location") shouldBe "/books/1"
        response.headers.contentType.toString() shouldBe "application/json"
        response.body?.decodeToString() shouldBe "{}"
    }

    test("construct target-specific request failures") {
        shouldThrow<ResponseStatusException> { webMvcBadRequest("invalid") }.statusCode shouldBe
            HttpStatus.BAD_REQUEST
        shouldThrow<ResponseStatusException> {
            webMvcUnsupportedMediaType("text/plain", "Books.create")
        }.statusCode shouldBe HttpStatus.UNSUPPORTED_MEDIA_TYPE
        shouldThrow<ResponseStatusException> {
            webMvcNotAcceptable("application/xml", "Books.get")
        }.statusCode shouldBe HttpStatus.NOT_ACCEPTABLE
    }
})
