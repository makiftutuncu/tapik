package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SchemaSpec : FunSpec({
    test("represent compound and nullable schemas") {
        val identifier = ScalarSchema(SchemaType.STRING, name = "BookId")
        val genre = EnumSchema(listOf("FICTION", "HISTORY"), name = "Genre")
        val book =
            ObjectSchema(
                properties =
                    linkedMapOf(
                        "id" to SchemaProperty(identifier, required = true, deprecated = true),
                        "genres" to SchemaProperty(ArraySchema(genre), required = true),
                        "metadata" to
                            SchemaProperty(
                                MapSchema(
                                    keys = ScalarSchema(SchemaType.STRING),
                                    values = ScalarSchema(SchemaType.STRING)
                                ),
                                required = false
                            ),
                        "rating" to
                            SchemaProperty(
                                NullableSchema(ScalarSchema(SchemaType.NUMBER, format = "double")),
                                required = true
                            )
                    ),
                name = "Book"
            )

        book.properties.keys.toList() shouldBe listOf("id", "genres", "metadata", "rating")
        book.properties.getValue("id").deprecated shouldBe true
        book.properties.getValue("genres").deprecated shouldBe false
        book.named("LibraryBook").name shouldBe "LibraryBook"
        ReferenceSchema("Book").reference shouldBe "Book"
    }

    test("validate compound schema values") {
        shouldThrow<IllegalArgumentException> { EnumSchema(emptyList()) }
        shouldThrow<IllegalArgumentException> { EnumSchema(listOf("BOOK", "BOOK")) }
        shouldThrow<IllegalArgumentException> {
            ObjectSchema(mapOf(" " to SchemaProperty(ScalarSchema(SchemaType.STRING), true)))
        }
        shouldThrow<IllegalArgumentException> { ReferenceSchema("") }
    }
})
