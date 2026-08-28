package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class ResponseDecodingSpec : FunSpec({
    test("decode response bodies and headers through their formats") {
        decodeResponseBody(bytesFormat, "book".encodeToByteArray(), "Books.get") shouldBe "book"
        decodeResponseHeader(stringFormat, "book", "Books.get") shouldBe "book"
    }

    test("identify response decoding failures by endpoint and location") {
        val failure = shouldThrow<IllegalStateException> {
            decodeResponseBody(failingBytesFormat, byteArrayOf(), "Books.get")
        }

        failure.message shouldContain "Cannot decode response body for Books.get: invalid body"
    }

    test("require exactly one encoded fixed response header") {
        val response =
            RestClientResponse(
                status = Status.Ok,
                headers = mapOf("x-api-version" to listOf("1")),
                mediaType = null,
                body = byteArrayOf()
            )

        requireFixedResponseHeader(response, "X-API-Version", stringFormat, "1", "Books.list")

        shouldThrow<IllegalStateException> {
            requireFixedResponseHeader(response, "X-API-Version", stringFormat, "2", "Books.list")
        }.message shouldContain "expected exactly [2], got [1]"
    }
})

private val stringSchema: Schema = ScalarSchema(SchemaType.STRING)

private val stringFormat: StringFormat<String> =
    Format(
        codec =
            Codec(
                decoder = Decoder { value -> DecodeResult.Success(value) },
                encoder = Encoder { value -> value }
            ),
        schema = stringSchema
    )

private val bytesFormat: ByteArrayFormat<String> =
    Format(
        codec =
            Codec(
                decoder = Decoder { value -> DecodeResult.Success(value.decodeToString()) },
                encoder = Encoder(String::encodeToByteArray)
            ),
        schema = stringSchema
    )

private val failingBytesFormat: ByteArrayFormat<String> =
    Format(
        codec =
            Codec(
                decoder = Decoder { DecodeResult.Failure(DecodeError("invalid body")) },
                encoder = Encoder(String::encodeToByteArray)
            ),
        schema = stringSchema
    )
