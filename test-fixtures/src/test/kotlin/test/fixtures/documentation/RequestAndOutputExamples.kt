package dev.akif.tapik.test.fixtures.documentation

import dev.akif.tapik.*
import dev.akif.tapik.format.kotlinx.jsonBody
import dev.akif.tapik.test.fixtures.library.Book
import dev.akif.tapik.test.fixtures.library.CreateBook
import kotlinx.serialization.json.Json

// tag::request-input[]
class BookRequests : Api() {
    private val requestId = header.uuid("X-Request-Id")
    private val source = header.string("X-Source").fixed("library")
    private val trace = header.string("X-Trace").optional()
    private val json = jsonBody<CreateBook>(format = Json.Default)
    private val vendorJson =
        jsonBody<CreateBook>(
            format = Json.Default,
            mediaType = MediaType("application/vnd.library.book+json")
        )

    val create by
        post(root / "books")
            .headers(headersOf(requestId, source))
            .header(trace)
            .input(
                bodiesOf(json, vendorJson, noBody),
                description = "A book represented as standard or vendor JSON."
            )
}
// end::request-input[]

// tag::response-output[]
class BookResponses : Api() {
    private val location = header.string("Location")

    val create by
        post(root / "books")
            .output(
                (Status.Created with jsonBody<Book>() with headersOf(location))
                    .description("The created book.")
            )
            .output(statusesIn(400..499) with noBody)
            .output(
                statusMatching("server error") { status -> status.code in 500..599 } with noBody
            )
}
// end::response-output[]
