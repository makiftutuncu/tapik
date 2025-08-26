package dev.akif.tapik.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.akif.tapik.http.*

val defaultObjectMapper: ObjectMapper = jacksonObjectMapper()

inline fun <reified T : Any> jacksonCodec(
    name: String,
    mapper: ObjectMapper? = null
): JacksonCodec<T> = JacksonCodec(name, mapper ?: defaultObjectMapper, T::class)

inline fun <reified T : Any> jsonBody(
    name: String,
    mapper: ObjectMapper? = null
): JsonBody<T> = jsonBody(jacksonCodec<T>(name, mapper))
