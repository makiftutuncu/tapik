package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import dev.akif.tapik.common.format.FormatCacheKeyEquality
import dev.akif.tapik.common.format.SchemaDerivationException
import dev.akif.tapik.common.format.WeakFormatCache
import dev.akif.tapik.common.format.decodeCatching
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

private object JsonFormatCache {
    val formats: WeakFormatCache<Json, KSerializer<*>, ByteArrayFormat<*>> =
        WeakFormatCache(FormatCacheKeyEquality.IDENTITY)
}

/**
 * Builds or reuses a JSON byte-array format for [serializer] and [format].
 *
 * @throws SchemaDerivationException when the serializer's descriptor is unsupported.
 */
@Suppress("UNCHECKED_CAST")
fun <Value : Any> jsonFormat(
    format: Json,
    serializer: KSerializer<Value>
): ByteArrayFormat<Value> =
    JsonFormatCache.formats.getOrPut(format, serializer) {
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
            schema = deriveSchema(serializer)
        )
    } as ByteArrayFormat<Value>

/** Builds or reuses a JSON byte-array format for [Value]. */
inline fun <reified Value : Any> jsonFormat(
    format: Json = Json.Default
): ByteArrayFormat<Value> = jsonFormat(format, serializer<Value>())
