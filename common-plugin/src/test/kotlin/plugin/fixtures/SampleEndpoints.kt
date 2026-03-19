package dev.akif.tapik.plugin.fixtures

import dev.akif.tapik.*
import dev.akif.tapik.codec.Codec

object SampleEndpoints : API {
    data class EncodedHeaderValue(
        val raw: String
    )

    private val encodedHeaderCodec =
        Codec.unsafe<EncodedHeaderValue, String>(
            name = "encodedHeaderValue",
            encoder = { value -> "encoded:${value.raw}" },
            decoder = { value -> EncodedHeaderValue(value.removePrefix("encoded:")) }
        )

    val user by endpoint(description = "Get user", details = "Fetch a user by id and optional page number.") {
        get("api" / "users" / path.uuid("userId") + query.int("page").optional(1))
            .input(header.string("X-Request-ID"))
            .output(headers = headersOf(Header.Location)) { stringBody() }
            .output(Status.NotFound) { stringBody() }
    }

    val fixedHeaders by endpoint(description = "Uses fixed header values.") {
        get("api" / "fixed-headers")
            .input(HeaderInput("X-Encoded", encodedHeaderCodec)(EncodedHeaderValue("alpha"), EncodedHeaderValue("beta")))
            .output { emptyBody() }
    }
}
