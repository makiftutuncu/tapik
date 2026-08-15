package dev.akif.tapik.fixtures.library

import dev.akif.tapik.*
import dev.akif.tapik.format.kotlinx.jsonBody
import kotlinx.serialization.json.Json

/** Library book operations used as Tapik's initial end-to-end contract fixture. */
object BooksApi : Api("Books") {
    private val requestId = header.uuid("X-Request-Id")
    private val location = header.string("Location")
    private val books = root / "books"

    /** Lists books one page at a time. */
    val list by
        get(
            uri = books + query.int("page").optional(default = 1),
            summary = "List books",
            tags = setOf("books")
        )
            .header(requestId)
            .output(Status.Ok with jsonBody<List<Book>>(format = Json.Default))

    /** Gets one book by its typed identifier. */
    val get by
        get(
            uri = books / path("bookId", bookIdFormat),
            summary = "Get a book",
            tags = setOf("books")
        )
            .header(requestId)
            .output(Status.Ok with jsonBody<Book>(format = Json.Default))
            .output(Status.NotFound with noBody)

    /** Creates a book and returns its representation and location. */
    val create by
        post(
            uri = books,
            summary = "Create a book",
            tags = setOf("books")
        )
            .header(requestId)
            .input(jsonBody<CreateBook>(format = Json.Default))
            .output(
                Status.Created with
                    jsonBody<Book>(format = Json.Default) with
                    headersOf(location)
            )
            .output(Status.BadRequest with noBody)
}
