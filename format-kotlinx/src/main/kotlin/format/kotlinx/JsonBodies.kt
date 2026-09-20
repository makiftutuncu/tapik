package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.Body
import dev.akif.tapik.MediaType
import dev.akif.tapik.Schema
import dev.akif.tapik.body
import kotlinx.serialization.json.Json

/** Builds a JSON body for [Value], deriving with [registry] unless a caller-owned root [schema] is supplied. */
inline fun <reified Value : Any> jsonBody(
    format: Json = Json.Default,
    mediaType: MediaType = MediaType.Json,
    schema: Schema? = null,
    registry: KotlinxSchemaRegistry = KotlinxSchemaRegistry.Default
): Body<Value> = body(mediaType, jsonFormat(format, schema, registry))
