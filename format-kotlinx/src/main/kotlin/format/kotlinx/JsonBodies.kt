package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.Body
import dev.akif.tapik.MediaType
import dev.akif.tapik.body
import kotlinx.serialization.json.Json

/** Builds a JSON body for [Value], attaching its concrete [format] immediately. */
inline fun <reified Value : Any> jsonBody(
    format: Json = Json.Default,
    mediaType: MediaType = MediaType.Json
): Body<Value> = body(mediaType, jsonFormat(format))
