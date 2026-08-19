package dev.akif.tapik.spring.restclient

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
})
