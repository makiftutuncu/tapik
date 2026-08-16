package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.util.concurrent.ConcurrentHashMap

private class JsonFormatKey(
    val format: Json,
    val serializer: KSerializer<*>
) {
    override fun equals(other: Any?): Boolean =
        other is JsonFormatKey && format === other.format && serializer === other.serializer

    override fun hashCode(): Int =
        31 * System.identityHashCode(format) + System.identityHashCode(serializer)
}

private object JsonFormatCache {
    val formats: ConcurrentHashMap<JsonFormatKey, ByteArrayFormat<*>> = ConcurrentHashMap()
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
    JsonFormatCache.formats.computeIfAbsent(JsonFormatKey(format, serializer)) {
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
