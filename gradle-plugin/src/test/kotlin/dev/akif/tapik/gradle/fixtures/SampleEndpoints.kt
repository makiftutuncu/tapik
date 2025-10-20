package dev.akif.tapik.gradle.fixtures

import dev.akif.tapik.http.Header
import dev.akif.tapik.http.Status
import dev.akif.tapik.http.div
import dev.akif.tapik.http.header
import dev.akif.tapik.http.http
import dev.akif.tapik.http.inputHeader
import dev.akif.tapik.http.outputBody
import dev.akif.tapik.http.outputHeader
import dev.akif.tapik.http.path
import dev.akif.tapik.http.query
import dev.akif.tapik.http.stringBody

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
