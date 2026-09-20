package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import dev.akif.tapik.common.format.FormatCacheKeyEquality
import dev.akif.tapik.common.format.SchemaDerivationException
import dev.akif.tapik.common.format.WeakScopedFormatCache
import dev.akif.tapik.common.format.decodeCatching
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

private object JsonFormatCache {
    val formats: WeakScopedFormatCache<KotlinxSchemaRegistry, Json, KSerializer<*>, ByteArrayFormat<*>> =
        WeakScopedFormatCache(FormatCacheKeyEquality.IDENTITY)
}

/**
 * Builds or reuses a JSON byte-array format for [serializer], [format], and [registry].
 * An explicit [schema] bypasses derivation and caching.
 *
 * @throws SchemaDerivationException when the serializer's descriptor is unsupported.
 */
@Suppress("UNCHECKED_CAST")
fun <Value : Any> jsonFormat(
    format: Json,
    serializer: KSerializer<Value>,
    schema: Schema? = null,
    registry: KotlinxSchemaRegistry = KotlinxSchemaRegistry.Default
): ByteArrayFormat<Value> {
    if (schema != null) return createJsonFormat(format, serializer, schema)
    return JsonFormatCache.formats.getOrPut(registry, format, serializer) {
        createJsonFormat(format, serializer, deriveSchema(format, serializer, registry))
    } as ByteArrayFormat<Value>
}

private fun <Value : Any> createJsonFormat(
    format: Json,
    serializer: KSerializer<Value>,
    schema: Schema
): ByteArrayFormat<Value> =
    Format(
        codec =
            Codec(
                decoder =
                    Decoder { bytes ->
                        decodeCatching("JSON decoding failed") {
                            format.decodeFromString(serializer, bytes.decodeToString())
                        }
                    },
                encoder = Encoder { value -> format.encodeToString(serializer, value).encodeToByteArray() }
            ),
        schema = schema
    )

/** Builds or reuses a JSON byte-array format for [Value], [format], and [registry]. */
inline fun <reified Value : Any> jsonFormat(
    format: Json = Json.Default,
    schema: Schema? = null,
    registry: KotlinxSchemaRegistry = KotlinxSchemaRegistry.Default
): ByteArrayFormat<Value> = jsonFormat(format, serializer<Value>(), schema, registry)
