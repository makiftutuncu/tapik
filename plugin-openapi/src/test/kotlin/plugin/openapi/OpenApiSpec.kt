package dev.akif.tapik.plugin.openapi

import dev.akif.tapik.*
import dev.akif.tapik.fixtures.library.Books
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class OpenApiSpec : FunSpec({
    val document = OpenApi.from(Books, version = "0.6.0")

    test("interpret API identity paths and operations") {
        document.specificationVersion shouldBe "3.2.0"
        document.info shouldBe OpenApiInfo(title = "Books", version = "0.6.0")
        document.paths.keys.toList() shouldContainExactly listOf("/books", "/books/{bookId}")

        val books = document.paths.getValue("/books")
        books.operations.keys.toList() shouldContainExactly listOf(Method.GET, Method.POST)
        books.operations.getValue(Method.GET).operationId shouldBe "Books.list"
        books.operations.getValue(Method.POST).operationId shouldBe "Books.create"
        document.paths.getValue("/books/{bookId}").operations.getValue(Method.GET).operationId shouldBe
            "Books.get"
    }

    test("interpret parameters in contract order") {
        val list = document.paths.getValue("/books").operations.getValue(Method.GET)
        list.parameters.map { it.name } shouldContainExactly listOf("page", "authorId", "X-Request-Id")

        val page = list.parameters[0]
        page.location shouldBe OpenApiParameterLocation.QUERY
        page.required shouldBe false
        page.schema.types shouldBe listOf("integer")
        page.schema.format shouldBe "int32"
        page.schema.defaultValue.toString() shouldBe "1"

        val authorIds = list.parameters[1]
        authorIds.required shouldBe false
        authorIds.style shouldBe "form"
        authorIds.explode shouldBe true
        authorIds.schema.types shouldBe listOf("array")
        authorIds.schema.items?.reference shouldBe "#/components/schemas/AuthorId"

        val get = document.paths.getValue("/books/{bookId}").operations.getValue(Method.GET)
        get.parameters.map { it.name } shouldContainExactly listOf("bookId", "X-Request-Id")
        get.parameters.first().required shouldBe true
        get.parameters.first().schema.reference shouldBe
            "#/components/schemas/BookId"
    }

    test("interpret request and response bodies headers and statuses") {
        val create = document.paths.getValue("/books").operations.getValue(Method.POST)
        val requestBody = requireNotNull(create.requestBody)
        requestBody.required shouldBe true
        requestBody.content.getValue("application/json").schema.reference shouldBe
            "#/components/schemas/CreateBook"

        create.responses.keys.toList() shouldContainExactly listOf("201", "400")
        val created = create.responses.getValue("201")
        created.description shouldBe "Created"
        created.content.getValue("application/json").schema.reference shouldBe
            "#/components/schemas/Book"
        created.headers.getValue("Location").schema.types shouldBe listOf("string")
        create.responses.getValue("400").content shouldBe emptyMap()
    }

    test("hoist every named schema and preserve property flags") {
        document.components.schemas.keys.toList() shouldContainExactly
            listOf(
                "AuthorId",
                "BookId",
                "Author",
                "Book",
                "CreateBook"
            )

        val book = document.components.schemas.getValue("Book")
        book.types shouldBe listOf("object")
        book.required shouldContainExactly listOf("id", "title", "authors")
        book.properties.getValue("authors").items?.reference shouldBe
            "#/components/schemas/Author"
    }

    test("render deterministic OpenAPI 3.2 JSON") {
        val rendered = document.toJson(pretty = false)
        val root = Json.parseToJsonElement(rendered).jsonObject

        root.getValue("openapi").jsonPrimitive.content shouldBe "3.2.0"
        root.getValue("info").jsonObject.getValue("title").jsonPrimitive.content shouldBe "Books"
        root.getValue("paths").jsonObject.keys.toList() shouldContainExactly listOf("/books", "/books/{bookId}")
        document.toJson(pretty = false) shouldBe rendered
    }

    test("generate the complete Books API document") {
        val expected =
            requireNotNull(OpenApiSpec::class.java.getResource("/openapi/books-api.json"))
                .readText()
                .trimEnd()

        document.toJson() shouldBe expected
    }

    test("render CONNECT through OpenAPI 3.2 additional operations") {
        val connections =
            object : Api("Connections") {
                val open by connect(root / "tunnel").output(Status.Ok with noBody)
            }
        val root =
            Json.parseToJsonElement(OpenApi.from(connections, version = "1").toJson(pretty = false)).jsonObject
        val tunnel = root.getValue("paths").jsonObject.getValue("/tunnel").jsonObject

        tunnel
            .getValue("additionalOperations")
            .jsonObject
            .getValue("CONNECT")
            .jsonObject
            .getValue("operationId")
            .jsonPrimitive
            .content shouldBe "Connections.open"
    }

    test("reject duplicate operations for one path") {
        val duplicate =
            object : Api("Duplicate") {
                val one by get(root / "books")
                val two by get(root / "books")
            }

        shouldThrow<OpenApiGenerationException> { OpenApi.from(duplicate, version = "1") }
    }

    test("reject differently named paths with the same templated hierarchy") {
        val ambiguous =
            object : Api("Ambiguous") {
                val byId by get(root / "books" / path.string("id"))
                val byTitle by post(root / "books" / path.string("title"))
            }

        shouldThrow<OpenApiGenerationException> { OpenApi.from(ambiguous, version = "1") }
    }

    test("translate request and response headers literally") {
        val requestHeader =
            object : Api("RequestHeader") {
                val endpoint by get(root / "books").header(header.string("Authorization"))
            }
        val responseHeader =
            object : Api("ResponseHeader") {
                val endpoint by
                    get(root / "books")
                        .output(Status.Ok with noBody with headersOf(header.string("Content-Type")))
            }

        val requestOperation =
            OpenApi.from(requestHeader, version = "1").paths.getValue("/books").operations.getValue(Method.GET)
        val response =
            OpenApi.from(responseHeader, version = "1").paths.getValue("/books").operations.getValue(Method.GET)

        requestOperation.parameters.single().name shouldBe "Authorization"
        response.responses.getValue("200").headers.keys shouldBe setOf("Content-Type")
    }

    test("allow qualified component naming") {
        val qualified =
            OpenApi.from(
                api = Books,
                version = "0.6.0",
                componentNaming = OpenApiComponentNaming.Qualified
            )

        qualified.components.schemas.keys shouldBe
            setOf(
                "AuthorId",
                "dev.akif.tapik.fixtures.library.BookId",
                "dev.akif.tapik.fixtures.library.AuthorId",
                "dev.akif.tapik.fixtures.library.Author",
                "dev.akif.tapik.fixtures.library.Book",
                "BookId",
                "dev.akif.tapik.fixtures.library.CreateBook"
            )
    }

    test("reject invalid and conflicting schema component names") {
        val invalidName =
            object : Api("InvalidName") {
                val endpoint by
                    get(root / "books" + query("value", format.string.named("invalid/name")))
            }
        val conflictingDefinitions =
            object : Api("ConflictingDefinitions") {
                val endpoint by
                    get(
                        root / "books" +
                            query("text", format.string.named("Shared")) +
                            query("number", format.int.named("Shared"))
                    )
            }

        shouldThrow<OpenApiGenerationException> { OpenApi.from(invalidName, version = "1") }
        shouldThrow<OpenApiGenerationException> { OpenApi.from(conflictingDefinitions, version = "1") }
    }

    test("reject unresolved schema references") {
        val missing = format.string.copy(schema = ReferenceSchema("Missing"))
        val unresolved =
            object : Api("Unresolved") {
                val endpoint by get(root / "books" + query("value", missing))
            }

        shouldThrow<OpenApiGenerationException> { OpenApi.from(unresolved, version = "1") }
    }
})
