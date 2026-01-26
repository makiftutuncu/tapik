package dev.akif.tapik.plugin.fixtures

import dev.akif.tapik.*

object SampleEndpoints : API {
    val user by endpoint(description = "Get user", details = "Fetch a user by id and optional page number.") {
        get("api" / "users" / path.uuid("userId") + query.int("page").optional(1))
            .input(header.string("X-Request-ID"))
            .output(headers = headersOf(Header.Location)) { stringBody() }
            .output(Status.NOT_FOUND) { stringBody() }
    }
}
