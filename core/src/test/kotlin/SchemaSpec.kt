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

    test("snapshot schema collections") {
        val values = mutableListOf("FICTION", "HISTORY")
        val properties =
            linkedMapOf(
                "genre" to SchemaProperty(EnumSchema(values), required = true)
            )
        val genre = EnumSchema(values)
        val book = ObjectSchema(properties)

        values.clear()
        properties.clear()
        runCatching { (genre.values as MutableList).clear() }
        runCatching { (book.properties as MutableMap).clear() }

        genre.values shouldBe listOf("FICTION", "HISTORY")
        book.properties.keys shouldBe setOf("genre")
    }

    test("represent union and discriminator schemas") {
        val cat = ObjectSchema(emptyMap(), name = "types.Cat")
        val dog = ObjectSchema(emptyMap(), name = "types.Dog")
        val alternatives = mutableListOf<Schema>(cat, dog)
        val mappings =
            linkedMapOf(
                "cat" to ReferenceSchema("types.Cat"),
                "dog" to ReferenceSchema("types.Dog")
            )
        val discriminator =
            SchemaDiscriminator(
                propertyName = "kind",
                mapping = mappings,
                defaultMapping = ReferenceSchema("types.Dog")
            )
        val animal = UnionSchema(alternatives, discriminator, name = "types.Animal")

        alternatives.clear()
        mappings.clear()
        runCatching { (animal.alternatives as MutableList<*>).clear() }
        runCatching { (discriminator.mapping as MutableMap<*, *>).clear() }

        animal.alternatives shouldBe listOf(cat, dog)
        discriminator.mapping.keys.toList() shouldBe listOf("cat", "dog")
        discriminator.defaultMapping shouldBe ReferenceSchema("types.Dog")
        animal.named("Animal").name shouldBe "Animal"
    }

    test("validate union and discriminator schemas") {
        val string = ScalarSchema(SchemaType.STRING)

        shouldThrow<IllegalArgumentException> { UnionSchema(listOf(string)) }
        shouldThrow<IllegalArgumentException> { UnionSchema(listOf(string, string)) }
        shouldThrow<IllegalArgumentException> { SchemaDiscriminator(" ") }
        shouldThrow<IllegalArgumentException> {
            SchemaDiscriminator("kind", mapOf(" " to ReferenceSchema("Cat")))
        }
    }
})
