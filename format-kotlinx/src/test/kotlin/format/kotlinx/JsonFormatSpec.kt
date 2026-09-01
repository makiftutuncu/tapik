package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class JsonFormatSpec : FunSpec({
    test("encode and decode serializable values") {
        val format = jsonFormat<LibraryBook>()
        val book = libraryBook()

        format.decode(format.encode(book)) shouldBe DecodeResult.Success(book)
        format.decode("{".encodeToByteArray()).shouldBeInstanceOf<DecodeResult.Failure>()
    }

    test("cache formats by Json and serializer identity") {
        val configured = Json { ignoreUnknownKeys = true }

        jsonFormat<LibraryBook>() shouldBeSameInstanceAs jsonFormat<LibraryBook>()
        jsonFormat<LibraryBook>(configured) shouldBeSameInstanceAs jsonFormat<LibraryBook>(configured)
        (jsonFormat<LibraryBook>() === jsonFormat<LibraryBook>(configured)) shouldBe false
    }

    test("attach the selected format and media type when building a body") {
        val configured = Json { prettyPrint = true }
        val mediaType = MediaType("application/vnd.tapik.book+json")
        val body = jsonBody<LibraryBook>(format = configured, mediaType = mediaType)

        body.mediaType shouldBe mediaType
        body.format shouldBeSameInstanceAs jsonFormat<LibraryBook>(configured)
    }

    test("derive primitive enum collection map nullable object and value class schemas") {
        val schema = jsonFormat<LibraryBook>().schema.shouldBeInstanceOf<ObjectSchema>()

        schema.name shouldBe "dev.akif.tapik.format.kotlinx.LibraryBook"
        schema.properties.keys.toList() shouldBe
            listOf("id", "title", "pages", "rating", "genres", "metadata", "author", "available")
        schema.properties.getValue("id").schema shouldBe
            ScalarSchema(
                type = SchemaType.STRING,
                name = "dev.akif.tapik.format.kotlinx.LibraryBookId"
            )
        schema.properties.getValue("pages").schema shouldBe ScalarSchema(SchemaType.INTEGER, "int32")
        schema.properties.getValue("rating").schema shouldBe
            NullableSchema(ScalarSchema(SchemaType.NUMBER, "double"))
        schema.properties.getValue("genres").schema shouldBe
            ArraySchema(
                EnumSchema(
                    values = listOf("FICTION", "HISTORY"),
                    name = "dev.akif.tapik.format.kotlinx.Genre"
                )
            )
        schema.properties.getValue("metadata").schema shouldBe
            MapSchema(
                keys = ScalarSchema(SchemaType.STRING),
                values = ScalarSchema(SchemaType.STRING)
            )
        schema.properties.getValue("author").schema.shouldBeInstanceOf<ObjectSchema>()
        schema.properties.getValue("available").required shouldBe false
        schema.properties.values.none(SchemaProperty::deprecated) shouldBe true
    }

    test("retain recursive objects as references") {
        val schema = jsonFormat<Category>().schema.shouldBeInstanceOf<ObjectSchema>()
        val children = schema.properties.getValue("children").schema.shouldBeInstanceOf<ArraySchema>()

        children.items shouldBe ReferenceSchema("dev.akif.tapik.format.kotlinx.Category")
    }

})

@Serializable
private data class LibraryBook(
    val id: LibraryBookId,
    val title: String,
    val pages: Int,
    val rating: Double?,
    val genres: List<Genre>,
    val metadata: Map<String, String>,
    val author: Author,
    val available: Boolean = true
)

@JvmInline
@Serializable
private value class LibraryBookId(
    val value: String
)

@Serializable
private enum class Genre {
    FICTION,
    HISTORY
}

@Serializable
private data class Author(
    val name: String
)

@Serializable
private data class Category(
    val name: String,
    val children: List<Category>
)

private fun libraryBook(): LibraryBook =
    LibraryBook(
        id = LibraryBookId("book-1"),
        title = "The Left Hand of Darkness",
        pages = 304,
        rating = 4.5,
        genres = listOf(Genre.FICTION),
        metadata = mapOf("language" to "en"),
        author = Author("Ursula K. Le Guin")
    )
