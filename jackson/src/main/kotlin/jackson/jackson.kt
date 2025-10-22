package dev.akif.tapik.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.akif.tapik.*

/** Default [ObjectMapper] configured with Jackson Kotlin module support. */
val defaultObjectMapper: ObjectMapper = jacksonObjectMapper()

/**
 * Builds a [JacksonCodec] for the requested type [T].
 *
 * @param T domain type handled by the codec.
 * @param name human readable identifier used in error messages.
 * @param mapper optional mapper override; defaults to [defaultObjectMapper].
 * @return a [JacksonCodec] bound to the requested type.
 * @see defaultObjectMapper
 */
inline fun <reified T : Any> jacksonCodec(
    name: String,
    mapper: ObjectMapper? = null
): JacksonCodec<T> = JacksonCodec(name, mapper ?: defaultObjectMapper, T::class)

/**
 * Builds a JSON [Body] definition using the provided Jackson codec.
 *
 * @param T domain type represented by the body.
 * @param name human readable identifier used in error messages.
 * @param mapper optional mapper override; defaults to [defaultObjectMapper].
 * @return a [JsonBody] registered with a [JacksonCodec] for the requested type.
 * @see jacksonCodec
 */
inline fun <reified T : Any> jsonBody(
    name: String,
    mapper: ObjectMapper? = null
): JsonBody<T> = JsonBody(jacksonCodec<T>(name, mapper))
