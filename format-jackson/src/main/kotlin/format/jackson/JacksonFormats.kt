package dev.akif.tapik.format.jackson

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.Codec
import dev.akif.tapik.Decoder
import dev.akif.tapik.Encoder
import dev.akif.tapik.Format
import dev.akif.tapik.Schema
import dev.akif.tapik.common.format.FormatCacheKeyEquality
import dev.akif.tapik.common.format.SchemaDerivationException
import dev.akif.tapik.common.format.WeakFormatCache
import dev.akif.tapik.common.format.decodeCatching
import tools.jackson.databind.ObjectMapper
import tools.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType
import kotlin.reflect.typeOf

private object JacksonFormatCache {
    val formats: WeakFormatCache<ObjectMapper, KType, ByteArrayFormat<*>> =
        WeakFormatCache(FormatCacheKeyEquality.STRUCTURAL)
}

@PublishedApi
internal val DefaultObjectMapper: ObjectMapper = jacksonObjectMapper()

/**
 * Builds or reuses a JSON byte-array format for [type] and [format].
 * An explicit [schema] bypasses derivation and caching; the caller owns its agreement with the mapper's wire shape.
 *
 * @throws SchemaDerivationException when the Kotlin type or Jackson shape is unsupported.
 */
@Suppress("UNCHECKED_CAST")
fun <Value : Any> jsonFormat(
    format: ObjectMapper,
    type: KType,
    schema: Schema? = null
): ByteArrayFormat<Value> {
    if (schema != null) return createJsonFormat(format, type, schema)
    return JacksonFormatCache.formats.getOrPut(format, type) {
        createJsonFormat<Value>(format, type, deriveSchema(format, type))
    } as ByteArrayFormat<Value>
}

@Suppress("UNCHECKED_CAST")
private fun <Value : Any> createJsonFormat(format: ObjectMapper, type: KType, schema: Schema): ByteArrayFormat<Value> {
    val jacksonType = format.typeFactory.constructType(type.javaType)
    return Format(
        codec =
            Codec(
                decoder =
                    Decoder { bytes ->
                        decodeCatching("JSON decoding failed") {
                            val value = format.readValue<Any>(bytes, jacksonType)
                            requireNotNull(value) { "Jackson decoded null for non-null type '$type'" }
                            value as Value
                        }
                    },
                encoder = Encoder(format::writeValueAsBytes)
            ),
        schema = schema
    )
}

/** Builds a JSON format for [Value], inferring its schema unless a caller-owned [schema] is supplied. */
inline fun <reified Value : Any> jsonFormat(
    format: ObjectMapper = DefaultObjectMapper,
    schema: Schema? = null
): ByteArrayFormat<Value> = jsonFormat(format, typeOf<Value>(), schema)
