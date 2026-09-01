package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class DocumentationSpec : FunSpec({
    test("initialize endpoint documentation from builder arguments") {
        val books =
            object : Api("Books") {
                val create by
                    post(
                        uri = root / "books",
                        summary = "Create a book",
                        description = "Adds a book to the library."
                    )
            }

        books.create.documentation shouldBe
            EndpointDocumentation(
                summary = "Create a book",
                description = "Adds a book to the library."
            )
    }

    test("replace documentation with last-value-wins modifiers") {
        val books =
            object : Api("Books") {
                val create by
                    post(root / "books", description = "Initial description")
                        .summary("Initial summary")
                        .document(summary = "Final summary")
                        .description("Final description")
            }

        books.create.documentation shouldBe
            EndpointDocumentation(
                summary = "Final summary",
                description = "Final description"
            )
    }

    test("reject blank documentation") {
        shouldThrow<IllegalArgumentException> { EndpointDocumentation(summary = " ") }
        shouldThrow<IllegalArgumentException> { EndpointDocumentation(description = "") }
        shouldThrow<IllegalArgumentException> { ParameterDocumentation(description = " ") }
        shouldThrow<IllegalArgumentException> { RequestBodyDocumentation(description = "") }
        shouldThrow<IllegalArgumentException> { ResponseDocumentation(description = " ") }
    }

    test("document parameters without changing their concrete behavior") {
        val bookId = path.uuid("bookId").description("Book identifier").deprecated()
        val tags = query.string("tag").description("Tag filter").deprecated().repeated().optional()
        val requestId = header.uuid("X-Request-Id").description("Request trace").deprecated().optional()

        bookId.documentation shouldBe ParameterDocumentation("Book identifier", deprecated = true)
        tags.documentation shouldBe ParameterDocumentation("Tag filter", deprecated = true)
        tags.presence shouldBe Optional
        requestId.documentation shouldBe ParameterDocumentation("Request trace", deprecated = true)
        requestId.presence shouldBe Optional
    }

    test("document a request body and response alternative") {
        val books =
            object : Api("Books") {
                val create by
                    post(root / "books")
                        .input(documentedBody, description = "Initial request body")
                        .requestBodyDescription("Book to create")
                        .output((Status.Created with noBody).description("Book created"))
            }

        books.create.input.documentation shouldBe RequestBodyDocumentation("Book to create")
        books.create.outputs._1.documentation shouldBe ResponseDocumentation("Book created")
    }
})

private val documentedBody: Body<UUID> =
    body(
        mediaType = MediaType.Json,
        format =
            Format(
                codec =
                    Codec(
                        decoder = Decoder { value -> DecodeResult.Success(UUID.fromString(value.decodeToString())) },
                        encoder = Encoder { value -> value.toString().encodeToByteArray() }
                    ),
                schema = format.uuid.schema
            )
    )
