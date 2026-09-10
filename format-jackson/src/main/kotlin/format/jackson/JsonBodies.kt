package dev.akif.tapik.format.jackson

import dev.akif.tapik.Body
import dev.akif.tapik.MediaType
import dev.akif.tapik.Schema
import dev.akif.tapik.body
import tools.jackson.databind.ObjectMapper

/** Builds a JSON body for [Value], attaching [format] and deriving its schema unless [schema] is supplied. */
inline fun <reified Value : Any> jsonBody(
    format: ObjectMapper = DefaultObjectMapper,
    mediaType: MediaType = MediaType.Json,
    schema: Schema? = null
): Body<Value> = body(mediaType, jsonFormat(format, schema))
