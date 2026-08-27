package dev.akif.tapik.format.jackson

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import dev.akif.tapik.*
import dev.akif.tapik.common.format.SchemaDerivationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import tools.jackson.databind.PropertyNamingStrategies
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule

class JsonFormatSpec : FunSpec({
    test("encode and decode Kotlin values") {
        val format = jsonFormat<LibraryBook>()
        val book = libraryBook()

        format.decode(format.encode(book)) shouldBe DecodeResult.Success(book)
        format.decode("{".encodeToByteArray()).shouldBeInstanceOf<DecodeResult.Failure>()
    }

    test("cache formats by ObjectMapper identity and Kotlin type") {
        val configured = jacksonObjectMapper()

        jsonFormat<LibraryBook>() shouldBeSameInstanceAs jsonFormat<LibraryBook>()
        jsonFormat<LibraryBook>(configured) shouldBeSameInstanceAs jsonFormat<LibraryBook>(configured)
        (jsonFormat<LibraryBook>() === jsonFormat<LibraryBook>(configured)) shouldBe false
        (jsonFormat<LibraryBook>(configured) === jsonFormat<Author>(configured)) shouldBe false
        (jsonFormat<List<String>>(configured) as Any === jsonFormat<List<String?>>(configured) as Any) shouldBe false
    }

    test("attach the selected format and media type when building a body") {
        val configured = jacksonObjectMapper()
        val mediaType = MediaType("application/vnd.tapik.book+json")
        val body = jsonBody<LibraryBook>(format = configured, mediaType = mediaType)

        body.mediaType shouldBe mediaType
        body.format shouldBeSameInstanceAs jsonFormat<LibraryBook>(configured)
    }

    test("apply ObjectMapper configuration to both codec and schema") {
        val configured =
            jsonMapper {
                addModule(kotlinModule())
                propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            }
        val format = jsonFormat<NamingBook>(configured)
        val value = NamingBook("A Wizard of Earthsea")

        format.encode(value).decodeToString() shouldBe "{\"book_title\":\"A Wizard of Earthsea\"}"
        format.decode(format.encode(value)) shouldBe DecodeResult.Success(value)
        format.schema.shouldBeInstanceOf<ObjectSchema>().properties.keys shouldBe setOf("book_title")
    }

    test("derive Jackson names primitives enums collections maps nullable objects defaults and value classes") {
        val schema = jsonFormat<LibraryBook>().schema.shouldBeInstanceOf<ObjectSchema>()

        schema.name shouldBe "dev.akif.tapik.format.jackson.LibraryBook"
        schema.properties.keys.toList() shouldBe
            listOf("book_id", "title", "pages", "rating", "genres", "metadata", "author", "available", "legacy")
        schema.properties.getValue("book_id").schema shouldBe
            ScalarSchema(
                type = SchemaType.STRING,
                name = "dev.akif.tapik.format.jackson.LibraryBookId"
            )
        schema.properties.getValue("pages").schema shouldBe ScalarSchema(SchemaType.INTEGER, "int32")
        schema.properties.getValue("rating").schema shouldBe
            NullableSchema(ScalarSchema(SchemaType.NUMBER, "double"))
        schema.properties.getValue("genres").schema shouldBe
            ArraySchema(
                EnumSchema(
                    values = listOf("fiction", "history"),
                    name = "dev.akif.tapik.format.jackson.Genre"
                )
            )
        schema.properties.getValue("metadata").schema shouldBe
            MapSchema(
                keys = ScalarSchema(SchemaType.STRING),
                values = ScalarSchema(SchemaType.STRING)
            )
        schema.properties.getValue("author").schema.shouldBeInstanceOf<ObjectSchema>()
        schema.properties.getValue("rating").required shouldBe true
        schema.properties.getValue("available").required shouldBe false
        schema.properties.getValue("legacy").deprecated shouldBe true
        schema.properties.containsKey("internalNote") shouldBe false
    }

    test("retain recursive objects as references") {
        val schema = jsonFormat<Category>().schema.shouldBeInstanceOf<ObjectSchema>()
        val children = schema.properties.getValue("children").schema.shouldBeInstanceOf<ArraySchema>()

        children.items shouldBe ReferenceSchema("dev.akif.tapik.format.jackson.Category")
    }

    test("fail format construction for unsupported sealed types") {
        shouldThrow<SchemaDerivationException> { jsonFormat<LibraryItem>() }
    }
})

private data class LibraryBook(
    @JsonProperty("book_id")
    val id: LibraryBookId,
    val title: String,
    val pages: Int,
    val rating: Double?,
    val genres: List<Genre>,
    val metadata: Map<String, String>,
    val author: Author,
    val available: Boolean = true,
    @Deprecated("Use another field")
    val legacy: String = "",
    @JsonIgnore
    val internalNote: String = "hidden"
)

@JvmInline
internal value class LibraryBookId(
    val value: String
)

private enum class Genre {
    @JsonProperty("fiction")
    FICTION,

    @JsonProperty("history")
    HISTORY
}

private data class Author(
    val name: String
)

private data class Category(
    val name: String,
    val children: List<Category>
)

private data class NamingBook(
    val bookTitle: String
)

private sealed interface LibraryItem {
    data class Book(
        val title: String
    ) : LibraryItem
}

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
