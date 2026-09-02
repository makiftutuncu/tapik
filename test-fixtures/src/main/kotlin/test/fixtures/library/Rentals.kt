package dev.akif.tapik.test.fixtures.library

import dev.akif.tapik.*
import dev.akif.tapik.format.kotlinx.jsonBody
import kotlinx.serialization.json.Json

/** Library rental operations used by tapik's shared target fixture. */
object Rentals : Api() {
    private val requestId = header.uuid("X-Request-Id")
    private val location = header.string("Location")
    private val rentals = root / "rentals"

    /** Lists rentals, optionally filtered by book and due date. */
    val list by
        get(
            uri =
                rentals +
                    query("bookId", bookIdFormat).optional() +
                    query.localDate("dueBefore").optional(),
            summary = "List rentals",
            tags = setOf("rentals")
        )
            .header(requestId)
            .output(Status.Ok with jsonBody<List<Rental>>(format = Json.Default))

    /** Gets one rental by its typed identifier. */
    val get by
        get(
            uri = rentals / path("rentalId", rentalIdFormat),
            summary = "Get a rental",
            tags = setOf("rentals")
        )
            .header(requestId)
            .output(Status.Ok with jsonBody<Rental>(format = Json.Default))
            .output(Status.NotFound with noBody)

    /** Creates a rental and returns its representation and location. */
    val create by
        post(
            uri = rentals,
            summary = "Create a rental",
            tags = setOf("rentals")
        )
            .header(requestId)
            .input(jsonBody<CreateRental>(format = Json.Default))
            .output(
                Status.Created with
                    jsonBody<Rental>(format = Json.Default) with
                    headersOf(location)
            )
            .output(Status.BadRequest with noBody)
            .output(Status.Conflict with noBody)

    /** Marks a rental as returned. */
    val returnBook by
        post(
            uri = rentals / path("rentalId", rentalIdFormat) / "return",
            summary = "Return a rental",
            tags = setOf("rentals")
        )
            .header(requestId)
            .output(Status.NoContent with noBody)
            .output(Status.NotFound with noBody)
            .output(Status.Conflict with noBody)
}
