package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

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
    }
})
