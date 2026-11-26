package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.Body
import dev.akif.tapik.MediaType
import dev.akif.tapik.Schema
import dev.akif.tapik.body
import kotlinx.serialization.json.Json

/**
 * Builds a JSON body for [Value]. Without a caller-supplied root [schema], its schema is derived with [registry].
 * Supplying a root schema bypasses derivation and format caching.
 *
 * @param format JSON settings used to encode and decode values.
 * @param mediaType media type written in the endpoint definition.
 * @param schema optional caller-supplied schema for the value.
 * @param registry custom schemas used during schema derivation.
 * @return a body whose format encodes values as JSON.
 * @throws dev.akif.tapik.common.format.SchemaDerivationException when schema derivation is required but unsupported.
 */
inline fun <reified Value : Any> jsonBody(
    format: Json = Json.Default,
    mediaType: MediaType = MediaType.Json,
    schema: Schema? = null,
    registry: KotlinxSchemaRegistry = KotlinxSchemaRegistry.Default
): Body<Value> = body(mediaType, jsonFormat(format, schema, registry))
