package dev.akif.tapik.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dev.akif.tapik.*

/** Default [ObjectMapper] configured with Jackson Kotlin module support. */
val defaultObjectMapper: ObjectMapper = jacksonObjectMapper()

/**
 * Builds a [JacksonCodec] for the requested type [T].
 *
 * Generic type arguments from [T] are preserved, so codecs such as `jacksonCodec<List<User>>()`
 * still decode elements as `User` instead of raw map values.
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
): JacksonCodec<T> {
    val resolvedMapper = mapper ?: defaultObjectMapper
    val sourceType = resolvedMapper.typeFactory.constructType(object : TypeReference<T>() {})
    return JacksonCodec(name, resolvedMapper, T::class, sourceType)
}

/**
 * Builds a JSON [Body] definition using the provided Jackson codec.
 *
 * Generic type arguments from [T] are preserved for runtime decoding.
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
): JsonBody<T> = JsonBody(jacksonCodec<T>(name, mapper), name)
