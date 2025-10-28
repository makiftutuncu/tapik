package dev.akif.tapik.plugin.fixtures

import dev.akif.tapik.*

object SampleEndpoints {
    val user by http(description = "Get user", details = "Fetch a user by id and optional page number.") {
        get
            .uri("api" / "users" / path.uuid("userId") + query.int("page").optional(1))
            .input(header.string("X-Request-ID"))
            .output(
                headers = { Headers1(Header.Location) }
            ) { stringBody() }
            .output(Status.NOT_FOUND) { stringBody() }
    }
}
