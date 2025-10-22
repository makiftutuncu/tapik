package dev.akif.tapik

import dev.akif.tapik.codec.*
import dev.akif.tapik.codec.StringCodecs

/**
 * Base contract for describing HTTP bodies flowing through an endpoint.
 *
 * @property mediaType preferred media type for the encoded payload when known.
 * @property codec encoder/decoder responsible for transforming between payload instances and raw bytes.
 */
sealed interface Body<T : Any> {
    /** Preferred media type for the body if known. */
    val mediaType: MediaType?

    /** Codec used to transform between payload instances and raw bytes. */
    val codec: ByteArrayCodec<T>

    /**
     * Encodes [value] into a [ByteArray].
     *
     * @param value instance to be encoded.
     * @return encoded bytes or `null` when the body intentionally carries no content.
     * @throws IllegalArgumentException when the codec refuses to encode [value].
     * @see Codec.encode
     */
    fun bytes(value: T): ByteArray? = codec.encode(value)
}

/** Sentinel body representing the absence of content. */
data object EmptyBody : Body<Unit> {
    override val mediaType: MediaType? = null

    override val codec: ByteArrayCodec<Unit> = ByteArrayCodec.nullable("empty", { ByteArray(0) }) { }

    override fun bytes(value: Unit): ByteArray? = null
}

/** Text body coupled with a string codec. */
data class StringBody(
    override val codec: ByteArrayCodec<String>
) : Body<String> {
    override val mediaType: MediaType = MediaType.PlainText
}

/** Arbitrary binary body described by a codec and optional media type. */
data class RawBody(
    override val mediaType: MediaType?,
    override val codec: ByteArrayCodec<ByteArray>
) : Body<ByteArray>

/** Structured payload encoded and decoded via JSON. */
data class JsonBody<T : Any>(
    override val codec: ByteArrayCodec<T>
) : Body<T> {
    override val mediaType: MediaType = MediaType.Json
}

/**
 * Creates a text body using a named string codec.
 *
 * @param name friendly name used in error messages when encoding/decoding fails.
 * @return a [StringBody] backed by a codec that targets UTF-8 strings.
 * @see StringBody
 */
fun stringBody(name: String = "string"): StringBody = stringBody(StringCodecs.string(name).toByteArrayCodec())

/**
 * Creates a text body using the provided [codec].
 *
 * @param codec encoder/decoder that understands the textual payload.
 * @return a [StringBody] using the supplied codec.
 */
fun stringBody(codec: ByteArrayCodec<String>): StringBody = StringBody(codec)

/**
 * Creates a raw body that uses an identity codec and optional media type.
 *
 * @param name friendly name used in error messages when encoding/decoding fails.
 * @param mediaType media type advertised for the body, if any.
 * @return a [RawBody] that passes bytes through unchanged.
 */
fun rawBody(
    name: String = "bytes",
    mediaType: MediaType? = null
): RawBody = rawBody(ByteArrayCodec.identity(name), mediaType)

/**
 * Creates a raw body using the provided [codec] and optional [mediaType].
 *
 * @param codec encoder/decoder that understands how to transform the payload bytes.
 * @param mediaType media type advertised for the body, if any.
 * @return a [RawBody] that delegates encoding/decoding to [codec].
 */
fun rawBody(
    codec: ByteArrayCodec<ByteArray>,
    mediaType: MediaType? = null
): RawBody = RawBody(mediaType, codec)
