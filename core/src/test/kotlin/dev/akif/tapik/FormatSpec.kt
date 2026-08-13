package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs

class FormatSpec : FunSpec({
    test("retain structured decode failure details") {
        val cause = IllegalArgumentException("invalid value")
        val error = DecodeError(message = "Cannot decode value", location = "book.id", cause = cause)

        error.message shouldBe "Cannot decode value"
        error.location shouldBe "book.id"
        error.cause shouldBeSameInstanceAs cause
    }

    test("require at least one error in a decode failure") {
        shouldThrow<IllegalArgumentException> { DecodeResult.Failure(emptyList()) }
    }

    test("decode and encode through a format") {
        val format = identityStringFormat()

        format.decode("book-1") shouldBe DecodeResult.Success("book-1")
        format.encode("book-1") shouldBe "book-1"
    }

    test("safely transform a format while preserving its representation and schema") {
        val transformed =
            identityStringFormat().transform(
                decode = ::BookId,
                encode = BookId::value
            )

        transformed.decode("book-1") shouldBe DecodeResult.Success(BookId("book-1"))
        transformed.encode(BookId("book-1")) shouldBe "book-1"
        transformed.schema shouldBe ScalarSchema(type = SchemaType.STRING)

        val failure = transformed.decode("") as DecodeResult.Failure
        failure.errors.single().cause.shouldBeInstanceOf<IllegalArgumentException>()
        failure.errors.single().cause?.message shouldBe "Book ID must not be empty"
    }

    test("provide an explicit transformation variant that propagates exceptions") {
        val transformed =
            identityStringFormat().transformOrThrow(
                decode = ::BookId,
                encode = BookId::value
            )

        shouldThrow<IllegalArgumentException> { transformed.decode("") }
    }

    test("preserve existing decode failures through transformations") {
        val error = DecodeError("Original failure", location = "source")
        val failing: Format<String, String> =
            Format(
                codec = Codec(decoder = Decoder { DecodeResult.Failure(error) }, encoder = Encoder<String, String> { it }),
                schema = ScalarSchema(SchemaType.STRING)
            )

        val transformed = failing.transform(decode = ::BookId, encode = BookId::value)

        transformed.decode("ignored") shouldBe DecodeResult.Failure(error)
    }

    test("assign a schema name immutably") {
        val original = identityStringFormat()
        val named = original.named("BookId")

        original.schema shouldBe ScalarSchema(type = SchemaType.STRING)
        named.schema shouldBe ScalarSchema(type = SchemaType.STRING, name = "BookId")
    }

    test("lift a format to repeated values") {
        val repeated = format.int.repeated()

        repeated.decode(listOf("1", "2", "3")) shouldBe DecodeResult.Success(listOf(1, 2, 3))
        repeated.encode(listOf(1, 2, 3)) shouldBe listOf("1", "2", "3")
        repeated.schema shouldBe ArraySchema(items = format.int.schema)
    }

    test("accumulate failures while decoding repeated values") {
        val failure = format.int.repeated().decode(listOf("first", "2", "third")) as DecodeResult.Failure

        failure.errors.map(DecodeError::message) shouldBe
            listOf("Cannot decode 'first' as Int", "Cannot decode 'third' as Int")
    }
})

private fun identityStringFormat(): Format<String, String> =
    Format(
        codec = Codec(decoder = Decoder { DecodeResult.Success(it) }, encoder = Encoder { it }),
        schema = ScalarSchema(SchemaType.STRING)
    )

private data class BookId(
    val value: String
) {
    init {
        require(value.isNotEmpty()) { "Book ID must not be empty" }
    }
}
