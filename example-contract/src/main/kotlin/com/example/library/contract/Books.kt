package com.example.library.contract

import dev.akif.tapik.*
import dev.akif.tapik.format.jackson.jsonBody

/** A book returned by the library API. */
data class Book(
    val isbn: String,
    val title: String
)

/** Operations concerning books. */
object Books : Api() {
    val list by
        get(root / "books")
            .output(Status.Ok with jsonBody<List<Book>>())
}
