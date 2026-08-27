package dev.akif.tapik.format.jackson

import dev.akif.tapik.Body
import dev.akif.tapik.MediaType
import dev.akif.tapik.body
import tools.jackson.databind.ObjectMapper

/** Builds a JSON body for [Value], attaching its concrete [format] immediately. */
inline fun <reified Value : Any> jsonBody(
    format: ObjectMapper = DefaultObjectMapper,
    mediaType: MediaType = MediaType.Json
): Body<Value> = body(mediaType, jsonFormat(format))
