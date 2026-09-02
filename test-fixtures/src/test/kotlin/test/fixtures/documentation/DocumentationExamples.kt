package dev.akif.tapik.test.fixtures.documentation

import dev.akif.tapik.*
import dev.akif.tapik.format.kotlinx.jsonBody
import dev.akif.tapik.test.fixtures.library.Book
import dev.akif.tapik.test.fixtures.library.CreateBook

// tag::contract-documentation[]
class DocumentedBooks : Api() {
    private val requestId =
        header.uuid("X-Request-Id")
            .description("Correlates the request across services.")

    private val location =
        header.string("Location")
            .description("URI of the created book.")

    // tag::complete-endpoint[]
    val create by
        post(
            uri = root / "books",
            summary = "Create a book",
            description = "Adds a book to the library.",
            tags = setOf("books")
        )
            .tag("write")
            .header(requestId)
            .input(jsonBody<CreateBook>(), description = "The new book.")
            .output(
                (Status.Created with jsonBody<Book>() with headersOf(location))
                    .description("The created book.")
            )
    // end::complete-endpoint[]
}
// end::contract-documentation[]
