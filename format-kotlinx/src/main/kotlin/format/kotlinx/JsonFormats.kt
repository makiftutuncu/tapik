package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

private object JsonFormatCache {
    val formats: WeakIdentityPairCache<Json, KSerializer<*>, ByteArrayFormat<*>> = WeakIdentityPairCache()
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
                            try {
                                DecodeResult.Success(format.decodeFromString(serializer, bytes.decodeToString()))
                            } catch (cause: Exception) {
                                DecodeResult.Failure(
                                    DecodeError(
                                        message = cause.message ?: "JSON decoding failed",
                                        cause = cause
                                    )
                                )
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
