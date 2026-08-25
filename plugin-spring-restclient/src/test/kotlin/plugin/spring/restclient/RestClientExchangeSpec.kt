package dev.akif.tapik.plugin.spring.restclient

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RestClientExchangeSpec : FunSpec({
    test("compare request body bytes by content") {
        val first = RestClientRequestBody(MediaType.Json, byteArrayOf(1, 2, 3))
        val second = RestClientRequestBody(MediaType.Json, byteArrayOf(1, 2, 3))

        first.equals(second) shouldBe true
        first.hashCode() shouldBe second.hashCode()
    }

    test("compare response body bytes by content") {
        val first =
            RestClientResponse(
                status = Status.Ok,
                headers = mapOf("X-Request-Id" to listOf("request-1")),
                mediaType = MediaType.Json,
                body = byteArrayOf(1, 2, 3)
            )
        val second = first.copy(body = byteArrayOf(1, 2, 3))

        first.equals(second) shouldBe true
        first.hashCode() shouldBe second.hashCode()
    }

    test("snapshot request body bytes") {
        val bytes = byteArrayOf(1, 2, 3)
        val body = RestClientRequestBody(MediaType.Json, bytes)

        bytes[0] = 9
        body.bytes[1] = 9

        body.bytes.contentEquals(byteArrayOf(1, 2, 3)) shouldBe true
    }

    test("snapshot response headers and body bytes") {
        val headerValues = mutableListOf("request-1")
        val headers = linkedMapOf("X-Request-Id" to headerValues)
        val bytes = byteArrayOf(1, 2, 3)
        val response = RestClientResponse(Status.Ok, headers, MediaType.Json, bytes)

        headerValues.clear()
        headers.clear()
        bytes[0] = 9
        runCatching { (response.headers as MutableMap).clear() }
        runCatching { (response.headers.getValue("X-Request-Id") as MutableList).clear() }
        response.body[1] = 9

        response.headers shouldBe mapOf("X-Request-Id" to listOf("request-1"))
        response.body.contentEquals(byteArrayOf(1, 2, 3)) shouldBe true
    }
})
