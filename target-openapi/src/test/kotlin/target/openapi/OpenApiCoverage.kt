package dev.akif.tapik.target.openapi

import dev.akif.tapik.*
import java.math.BigDecimal
import java.math.BigInteger

/** A schema-interpretation fixture; codec behavior is covered by format integration tests. */
internal object OpenApiCoverage : Api() {
    private val identifier = format.string.named("coverage.Identifier")
    private val state = EnumSchema(listOf("open", "closed"), name = "coverage.State")
    private val record =
        ObjectSchema(
            linkedMapOf(
                "id" to SchemaProperty(identifier.schema, required = true),
                "active" to SchemaProperty(ScalarSchema(SchemaType.BOOLEAN), required = true),
                "count" to SchemaProperty(ScalarSchema(SchemaType.INTEGER, format = "int64"), required = true),
                "state" to SchemaProperty(state, required = true),
                "note" to SchemaProperty(
                    NullableSchema(ScalarSchema(SchemaType.STRING)), required = false, deprecated = true
                ),
                "previous" to SchemaProperty(
                    NullableSchema(ReferenceSchema("coverage.Record").named("coverage.PreviousRecord")), required = false
                ),
                "scores" to SchemaProperty(
                    MapSchema(state, NullableSchema(ScalarSchema(SchemaType.NUMBER))), required = true
                ),
                "events" to SchemaProperty(ArraySchema(eventSchema()), required = true),
                "choice" to SchemaProperty(
                    UnionSchema(listOf(ScalarSchema(SchemaType.STRING), ScalarSchema(SchemaType.INTEGER))), required = false
                )
            ),
            name = "coverage.Record"
        )
    private val json = schemaBody(MediaType.Json, record)
    private val vendorJson = schemaBody(MediaType("application/vnd.tapik.record+json"), record)

    val search by
        query(
            root / "records" / path("id", identifier).description("Record identifier").deprecated() +
                query.string("mode").description("Search mode") +
                query.string("tag").repeated().optional(default = listOf("null", "200")).deprecated() +
                query.int("limit").optional(default = 42) +
                query.bigInteger("cursor").optional(default = BigInteger("123456789012345678901234567890")) +
                query.bigDecimal("weight").optional(default = BigDecimal("1234567890.1234567890123456789")),
            summary = "Search records",
            description = "Search by identifier.\nReturns the matching record.",
            tags = setOf("search", "records")
        )
            .headers(
                headersOf(
                    header.boolean("X-Enabled").fixed(true).description("Feature switch"),
                    header.string("X-Trace").optional().deprecated()
                )
            )
            .input(bodiesOf(json, vendorJson, noBody), description = "Optional search payload")
            .output(
                (statusesOf(Status.Ok, Status(202)) with bodiesOf(json, vendorJson) with
                    headersOf(
                        header.double("X-Rate").optional(default = 0.5).description("Remaining allowance").deprecated(),
                        header.string("X-Mode").fixed("on")
                    ))
                    .description("Matching record")
            )
            .output((statusesIn(400..499) with noBody).description("Invalid search"))
            .output(statusesIn(590..591) with noBody)

    val tunnel by connect(root / "tunnel")
    val probe by head(root / "tunnel")
}

private fun eventSchema(): UnionSchema =
    UnionSchema(
        alternatives = listOf(
            ObjectSchema(
                mapOf("kind" to SchemaProperty(EnumSchema(listOf("edited")), required = true)),
                name = "coverage.Edited"
            ),
            ObjectSchema(
                mapOf("kind" to SchemaProperty(EnumSchema(listOf("other")), required = false)),
                name = "coverage.Other"
            )
        ),
        discriminator = SchemaDiscriminator(
            "kind",
            mapping = linkedMapOf(
                "edited" to ReferenceSchema("coverage.Edited"),
                "other" to ReferenceSchema("coverage.Other")
            ),
            defaultMapping = ReferenceSchema("coverage.Other")
        ),
        name = "coverage.Event"
    )

private fun schemaBody(mediaType: MediaType, schema: Schema): Body<String> =
    body(
        mediaType,
        Format(
            Codec(Decoder { DecodeResult.Success(it.decodeToString()) }, Encoder(String::encodeToByteArray)),
            schema
        )
    )
