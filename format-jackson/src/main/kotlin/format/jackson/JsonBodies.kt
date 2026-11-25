package dev.akif.tapik.format.jackson

import dev.akif.tapik.Body
import dev.akif.tapik.MediaType
import dev.akif.tapik.Schema
import dev.akif.tapik.body
import tools.jackson.databind.ObjectMapper

/**
 * Builds a JSON body for [Value], deriving with [registry] unless a caller-owned root [schema] is supplied.
 *
 * @param format mapper used to encode and decode values.
 * @param mediaType media type written in the endpoint definition.
 * @param schema optional caller-supplied schema for the value.
 * @param registry custom schemas used during schema derivation.
 * @return a body whose format encodes values as JSON.
 * @throws dev.akif.tapik.common.format.SchemaDerivationException when schema derivation is required but unsupported.
 */
inline fun <reified Value : Any> jsonBody(
    format: ObjectMapper = DefaultObjectMapper,
    mediaType: MediaType = MediaType.Json,
    schema: Schema? = null,
    registry: JacksonSchemaRegistry = JacksonSchemaRegistry.Default
): Body<Value> = body(mediaType, jsonFormat(format, schema, registry))
