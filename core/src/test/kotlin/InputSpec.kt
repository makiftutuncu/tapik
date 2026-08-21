package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class InputSpec : FunSpec({
    test("attach one body to a draft endpoint") {
        val json = body(MediaType.Json, createBookFormat())
        val books =
            object : Api("Books") {
                val create by post(root / "books").input(json)
            }
        val create: Endpoint<
            Paths0,
            Queries0,
            Headers0,
            BodyInput<Bodies1<Body<CreateBook>>>,
            DefaultOutput,
            Ready
        > = books.create

        create.input.bodies.values shouldBe listOf(json)
    }

    test("attach alternative bodies to a draft endpoint") {
        val json = body(MediaType.Json, createBookFormat())
        val alternatives = bodiesOf(json, noBody)
        val books =
            object : Api("Books") {
                val create by post(root / "books").input(alternatives)
            }

        books.create.input.bodies shouldBeSameInstanceAs alternatives
    }

    test("validate body alternatives at the bulk input boundary") {
        val json = body(MediaType.Json, createBookFormat())
        val invalid = Bodies2(json, json)

        shouldThrow<IllegalArgumentException> {
            object : Api("Books") {
                val create by post(root / "books").input(invalid)
            }
        }
    }
})

private fun createBookFormat(): ByteArrayFormat<CreateBook> =
    Format(
        codec =
            Codec(
                decoder = Decoder { DecodeResult.Success(CreateBook(it.decodeToString())) },
                encoder = Encoder { it.title.encodeToByteArray() }
            ),
        schema = ScalarSchema(SchemaType.STRING)
    )

private data class CreateBook(
    val title: String
)
