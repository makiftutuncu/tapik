package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class BodySpec : FunSpec({
    test("represent media types as evolvable values") {
        MediaType.Json.toString() shouldBe "application/json"
        MediaType.Xml.toString() shouldBe "application/xml"
        MediaType.PlainText.toString() shouldBe "text/plain"
        MediaType.OctetStream.toString() shouldBe "application/octet-stream"
        MediaType("application/vnd.tapik+json").toString() shouldBe "application/vnd.tapik+json"
        shouldThrow<IllegalArgumentException> { MediaType("") }
        shouldThrow<IllegalArgumentException> { MediaType("   ") }
    }

    test("combine a media type and byte-array format in a body") {
        val format = bookFormat()
        val json = body(MediaType.Json, format)

        json.mediaType shouldBe MediaType.Json
        json.format shouldBeSameInstanceAs format
    }

    test("group alternative representations of the same model") {
        val json = body(MediaType.Json, bookFormat())
        val xml = body(MediaType.Xml, bookFormat())
        val bodies: Bodies2<Body<Book>, Body<Book>> = bodiesOf(json, xml)

        bodies.values shouldBe listOf(json, xml)
    }

    test("represent optional content with noBody as the final alternative") {
        val json = body(MediaType.Json, bookFormat())
        val bodies: Bodies2<Body<Book>, NoBody> = bodiesOf(json, noBody)

        noBody shouldBeSameInstanceAs NoBody
        bodies.values shouldBe listOf(json, noBody)
    }

    test("reject duplicate media types") {
        shouldThrow<IllegalArgumentException> {
            bodiesOf(body(MediaType.Json, bookFormat()), body(MediaType.Json, bookFormat()))
        }
    }

    test("reject semantically duplicate media types without rejecting distinct parameter values") {
        listOf(
            "APPLICATION/JSON" to "application/json",
            "text/plain;Charset=\"UTF-8\";profile=Book" to "TEXT/PLAIN;profile=Book;charset=utf-8"
        ).forEach { (first, second) ->
            shouldThrow<IllegalArgumentException> {
                bodiesOf(body(MediaType(first), bookFormat()), body(MediaType(second), bookFormat()))
            }
        }
        bodiesOf(
            body(MediaType("application/json;profile=Book"), bookFormat()),
            body(MediaType("application/json;profile=book"), bookFormat())
        ).values.size shouldBe 2
    }

    test("support eight body alternatives") {
        val bodies =
            bodiesOf(
                body(MediaType("application/one"), bookFormat()),
                body(MediaType("application/two"), bookFormat()),
                body(MediaType("application/three"), bookFormat()),
                body(MediaType("application/four"), bookFormat()),
                body(MediaType("application/five"), bookFormat()),
                body(MediaType("application/six"), bookFormat()),
                body(MediaType("application/seven"), bookFormat()),
                noBody
            )

        bodies.values.size shouldBe 8
    }
})

private fun bookFormat(): ByteArrayFormat<Book> =
    Format(
        codec =
            Codec(
                decoder = Decoder { DecodeResult.Success(Book(it.decodeToString())) },
                encoder = Encoder { it.title.encodeToByteArray() }
            ),
        schema = ScalarSchema(SchemaType.STRING)
    )

private data class Book(
    val title: String
)
