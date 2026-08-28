package dev.akif.tapik.test.fixtures.library

import dev.akif.tapik.*
import dev.akif.tapik.format.kotlinx.jsonBody
import kotlinx.serialization.json.Json

/** Library author operations used by Tapik's shared target fixture. */
object Authors : Api() {
    private val requestId = header.uuid("X-Request-Id")
    private val location = header.string("Location")
    private val authors = root / "authors"

    /** Lists authors one page at a time, optionally filtered by name. */
    val list by
        get(
            uri =
                authors +
                    query.string("name").optional() +
                    query.int("page").optional(default = 1),
            summary = "List authors",
            tags = setOf("authors")
        )
            .header(requestId)
            .output(Status.Ok with jsonBody<List<Author>>(format = Json.Default))

    /** Gets one author by its typed identifier. */
    val get by
        get(
            uri = authors / path("authorId", authorIdFormat),
            summary = "Get an author",
            tags = setOf("authors")
        )
            .header(requestId)
            .output(Status.Ok with jsonBody<Author>(format = Json.Default))
            .output(Status.NotFound with noBody)

    /** Creates an author and returns its representation and location. */
    val create by
        post(
            uri = authors,
            summary = "Create an author",
            tags = setOf("authors")
        )
            .header(requestId)
            .input(jsonBody<CreateAuthor>(format = Json.Default))
            .output(
                Status.Created with
                    jsonBody<Author>(format = Json.Default) with
                    headersOf(location)
            )
            .output(Status.BadRequest with noBody)
}
