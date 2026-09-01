package com.example.library.contract

import dev.akif.tapik.*
import dev.akif.tapik.format.jackson.jsonBody

/** Operations concerning authors. */
class Authors : Api() {
    private val requestId = header.string("X-Request-Id")
    private val client = header.string("X-Client").fixed("tapik")
    private val apiVersion = header.string("X-API-Version").fixed("1")
    private val location = header.string("Location")
    private val xmlError =
        body(
            MediaType.Xml,
            Format(
                codec =
                    Codec(
                        decoder = Decoder { bytes -> DecodeResult.Success(bytes.decodeToString()) },
                        encoder = Encoder(String::encodeToByteArray)
                    ),
                schema = ScalarSchema(SchemaType.STRING)
            )
        )

    val list by
        get(
            root / "authors" +
                query.string("name").repeated().optional() +
                query.int("page").optional(default = 1)
        )
            .header(requestId)
            .header(client)
            .output(Status.Ok with jsonBody<List<Author>>() with headersOf(apiVersion))
            .output(Status.NotFound with xmlError)
            .output(Status.NoContent with noBody)

    val create by
        post(root / "authors")
            .header(requestId)
            .input(jsonBody<CreateAuthor>())
            .output(Status.Created with jsonBody<Author>() with headersOf(location))
            .output(Status.BadRequest with noBody)
}
