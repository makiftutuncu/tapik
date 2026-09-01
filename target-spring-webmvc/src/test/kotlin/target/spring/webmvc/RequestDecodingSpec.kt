package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.http.server.PathContainer
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.pattern.PathPatternParser

class RequestDecodingSpec : FunSpec({
    test("decode Spring's terminal wildcard as remaining path segments") {
        val pattern = PathPatternParser.defaultInstance.parse("/files/{*path}")
        val match = requireNotNull(pattern.matchAndExtract(PathContainer.parsePath("/files/draft/reports")))

        decodeRequest(path.remaining("path").format, match.uriVariables.getValue("path"), "Files.download path path") shouldBe
            listOf("draft", "reports")
    }

    test("decode scalar repeated and body request values") {
        decodeRequest(stringFormat, "book", "Books.get query title") shouldBe "book"
        decodeRequest(stringsFormat, listOf("fiction", "history"), "Books.list query tag") shouldBe
            listOf("fiction", "history")
        decodeRequest(bytesFormat, "book".encodeToByteArray(), "Books.create body") shouldBe "book"
    }

    test("turn decoding failures into bad requests") {
        val failure = shouldThrow<ResponseStatusException> {
            decodeRequest(failingBytesFormat, byteArrayOf(), "Books.create body")
        }

        failure.statusCode shouldBe HttpStatus.BAD_REQUEST
        failure.reason shouldBe "Cannot decode Books.create body: invalid body"
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

private val stringsFormat: Format<List<String>, List<String>> =
    Format(
        codec =
            Codec(
                decoder = Decoder { value -> DecodeResult.Success(value) },
                encoder = Encoder { value -> value }
            ),
        schema = ArraySchema(stringSchema)
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
