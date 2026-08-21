package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class OutputSpec : FunSpec({
    test("represent the implicit empty 200 response") {
        val output = DefaultOutput.values.single() as Output<*, *, *>

        output.matcher shouldBe ExactStatus(Status.Ok)
        output.bodies.values shouldBe listOf(noBody)
        output.headers shouldBeSameInstanceAs noHeaders
    }

    test("build an exact output in status body header order") {
        val json = body(MediaType.Json, outputBookFormat())
        val location = header.string("Location")
        val output: Output<
            ExactStatus,
            Bodies1<Body<OutputBook>>,
            Headers1<Header<String, Required>>
        > = Status.Created with json with headersOf(location)

        output.matcher.matches(Status.Created) shouldBe true
        output.matcher.matches(Status.Ok) shouldBe false
        output.bodies.values shouldBe listOf(json)
        output.headers.values shouldBe listOf(location)
    }

    test("support body alternatives and bodyless outputs") {
        val json = body(MediaType.Json, outputBookFormat())
        val xml = body(MediaType.Xml, outputBookFormat())
        val alternatives = Status.Ok with bodiesOf(json, xml)
        val empty: Output<ExactStatus, Bodies1<NoBody>, Headers0> = Status.NoContent with noBody

        alternatives.bodies.values shouldBe listOf(json, xml)
        empty.bodies.values shouldBe listOf(noBody)
    }

    test("validate body alternatives at the output boundary") {
        val json = body(MediaType.Json, outputBookFormat())
        val invalid = Bodies2(json, json)

        shouldThrow<IllegalArgumentException> { Status.Ok with invalid }
    }

    test("validate headers at the output boundary") {
        val invalid = Headers2(header.string("X-Request-Id"), header.uuid("x-request-id"))

        shouldThrow<IllegalArgumentException> { Status.Ok with noBody with invalid }
    }

    test("replace the default output then append explicit outputs") {
        val json = body(MediaType.Json, outputBookFormat())
        val ok = Status.Ok with json
        val notFound = Status.NotFound with noBody
        val books =
            object : Api("Books") {
                val get by get(root / "books" / path.uuid("bookId")).output(ok).output(notFound)
            }
        val get: Endpoint<
            Paths1<java.util.UUID>,
            Queries0,
            Headers0,
            NoInput,
            Outputs2<
                Output<ExactStatus, Bodies1<Body<OutputBook>>, Headers0>,
                Output<ExactStatus, Bodies1<NoBody>, Headers0>
            >,
            Ready
        > = books.get

        get.outputs.values shouldBe listOf(ok, notFound)
    }

    test("reject duplicate exact statuses") {
        shouldThrow<IllegalArgumentException> {
            object : Api("Books") {
                val invalid by
                    get(root / "books")
                        .output(Status.Ok with noBody)
                        .output(Status.Ok with body(MediaType.Json, outputBookFormat()))
            }
        }
    }
})

private fun outputBookFormat(): ByteArrayFormat<OutputBook> =
    Format(
        codec =
            Codec(
                decoder = Decoder { DecodeResult.Success(OutputBook(it.decodeToString())) },
                encoder = Encoder { it.title.encodeToByteArray() }
            ),
        schema = ScalarSchema(SchemaType.STRING)
    )

private data class OutputBook(
    val title: String
)
