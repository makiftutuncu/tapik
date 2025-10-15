package dev.akif.app.example

import dev.akif.tapik.codec.StringCodecs
import dev.akif.tapik.http.*
import dev.akif.tapik.jackson.jsonBody
import java.net.URI
import dev.akif.tapik.spring.restclient.*
import org.springframework.web.client.RestClient

val test: HttpEndpoint<Parameters3<Short, Boolean, Long>, Headers1<Int>, JsonBody<Map<String, String>>, Headers2<MediaType, URI>, OutputBodies3<JsonBody<Map<String, String>>, RawBody, StringBody>> =
    HttpEndpoint(
        id = "testEndpoint",
        description = "Test endpoint",
        details = "This is a test endpoint.",
        method = Method.PATCH,
        uri = listOf("api", "{version}", "test"),
        parameters = Parameters3(
            path.short("version"),
            query.boolean("active").optional(default = true),
            QueryParameter(
                name = "size",
                codec = StringCodecs.long("size"),
                required = false,
                default = null
            )
        ),
        inputHeaders = Headers1(header.int("DNT")),
        inputBody = jsonBody<Map<String, String>>("customProperties"),
        outputHeaders = Headers2(
            header.ContentType(MediaType.Json),
            header.Location
        ),
        outputBodies = OutputBodies3(
            OutputBody(
                statusMatcher = anyStatus(Status.OK, Status.CREATED),
                body = jsonBody<Map<String, String>>("success")
            ),
            OutputBody(
                statusMatcher = matchStatus("4xx") { it.code in 400..499 },
                body = rawBody()
            ),
            OutputBody(statusMatcher = unmatchedStatus, body = stringBody())
        )
    )

val bytes = test.sendWithRestClient(
    interpreter = RestClientInterpreter(RestClient.create()),
    version = 1.toShort(),
    size = 2L,
    dnt = 3,
    inputBody = emptyMap()
).select(
    { it.body.toString().toByteArray() },
    { it.body },
    { it.body.toByteArray() }
)
