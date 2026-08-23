package dev.akif.tapik.spring.restclient

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class RestClientResponseConformanceSpec : FunSpec({
    test("select the first compatible declared response media type") {
        val response = response(MediaType("application/json;charset=UTF-8"), "{}".encodeToByteArray())

        selectResponseBodyMediaType(
            response = response,
            offered = listOf(MediaType.Json, MediaType("application/*")),
            allowsNoBody = false,
            endpointId = "Books.get"
        ) shouldBe MediaType.Json
    }

    test("select noBody only for empty bytes without Content-Type") {
        selectResponseBodyMediaType(
            response = response(mediaType = null, body = byteArrayOf()),
            offered = listOf(MediaType.Json),
            allowsNoBody = true,
            endpointId = "Books.get"
        ) shouldBe null
    }

    test("reject response bytes without Content-Type") {
        shouldThrow<IllegalStateException> {
            selectResponseBodyMediaType(
                response = response(mediaType = null, body = "{}".encodeToByteArray()),
                offered = listOf(MediaType.Json),
                allowsNoBody = true,
                endpointId = "Books.get"
            )
        }.message shouldContain "missing Content-Type"
    }

    test("reject Content-Type for a bodyless-only output") {
        shouldThrow<IllegalStateException> {
            selectResponseBodyMediaType(
                response = response(mediaType = MediaType.Json, body = byteArrayOf()),
                offered = emptyList(),
                allowsNoBody = true,
                endpointId = "Books.delete"
            )
        }.message shouldContain "bodyless"
    }

    test("require a declared representation when noBody is unavailable") {
        shouldThrow<IllegalStateException> {
            selectResponseBodyMediaType(
                response = response(mediaType = null, body = byteArrayOf()),
                offered = listOf(MediaType.Json),
                allowsNoBody = false,
                endpointId = "Books.get"
            )
        }.message shouldContain "missing Content-Type"
    }

    test("reject incompatible response media types") {
        shouldThrow<IllegalStateException> {
            selectResponseBodyMediaType(
                response = response(mediaType = MediaType.Xml, body = "<book/>".encodeToByteArray()),
                offered = listOf(MediaType.Json),
                allowsNoBody = false,
                endpointId = "Books.get"
            )
        }.message shouldContain "expected one of [application/json]"
    }
})

private fun response(
    mediaType: MediaType?,
    body: ByteArray
): RestClientResponse =
    RestClientResponse(
        status = Status.Ok,
        headers = emptyMap(),
        mediaType = mediaType,
        body = body
    )
