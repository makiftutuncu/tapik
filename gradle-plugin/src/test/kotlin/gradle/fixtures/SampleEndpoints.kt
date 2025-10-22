package dev.akif.tapik.gradle.fixtures

import dev.akif.tapik.*

object SampleEndpoints {
    val user by http(description = "Get user", details = "Fetch a user by id and optional page number.") {
        get
            .uri("api" / "users" / path.uuid("userId") + query.int("page").optional(1))
            .inputHeader(header.string("X-Request-ID"))
            .outputHeader(Header.Location)
            .outputBody { stringBody() }
            .outputBody(Status.NOT_FOUND) { stringBody() }
    }
}
