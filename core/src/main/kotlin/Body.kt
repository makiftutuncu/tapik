package dev.akif.tapik

import dev.akif.tapik.codec.*
import dev.akif.tapik.codec.StringCodecs

/**
 * Describes how an endpoint body is represented on the wire.
 *
 * A body couples the semantic payload type [T] with the codec and media type needed to encode and
 * decode it. Request and response definitions both use this contract.
 *
 * @param T payload type exposed to callers and generated clients.
 * @property mediaType advertised content type when one is known.
 * @property codec encoder/decoder that turns [T] into raw bytes and back.
 */
sealed interface Body<T : Any> {
    /** Preferred media type for the body, or `null` when the payload is not tied to a specific one. */
    val mediaType: MediaType?

    /** Codec responsible for the byte-level representation of the payload. */
    val codec: ByteArrayCodec<T>

    /** Short diagnostic name used in generated code and codec error messages. */
    val name: String

    /**
     * Encodes [value] into its wire representation.
     *
     * This delegates to [codec], except for bodies such as [EmptyBody] that intentionally suppress
     * any payload and therefore return `null`.
     */
    fun bytes(value: T): ByteArray? = codec.encode(value)
}

/** Body definition for requests or responses that must not carry content. */
data object EmptyBody : Body<Unit> {
    override val mediaType: MediaType? = null

    override val codec: ByteArrayCodec<Unit> = ByteArrayCodec.nullable("empty", { ByteArray(0) }) { }

    override val name: String = "empty"

    override fun bytes(value: Unit): ByteArray? = null
}

/** Text body encoded through a string-aware byte codec. */
data class StringBody(
    override val codec: ByteArrayCodec<String>,
    override val name: String
) : Body<String> {
    override val mediaType: MediaType = MediaType.PlainText
}

/** Opaque binary body whose meaning is defined entirely by the supplied codec and media type. */
data class RawBody(
    override val mediaType: MediaType?,
    override val codec: ByteArrayCodec<ByteArray>,
    override val name: String
) : Body<ByteArray>

/** Structured body advertised as JSON. */
data class JsonBody<T : Any>(
    override val codec: ByteArrayCodec<T>,
    override val name: String
) : Body<T> {
    override val mediaType: MediaType = MediaType.Json
}

/** Creates a plain-text body backed by Tapik's default string codec. */
fun stringBody(name: String = "string"): StringBody = stringBody(StringCodecs.string(name).toByteArrayCodec(), name)

/** Creates a plain-text body that delegates encoding and decoding to [codec]. */
fun stringBody(
    codec: ByteArrayCodec<String>,
    name: String = "string"
): StringBody = StringBody(codec, name)

/** Creates a binary body that passes bytes through unchanged. */
fun rawBody(
    name: String = "bytes",
    mediaType: MediaType? = null
): RawBody = rawBody(ByteArrayCodec.identity(name), mediaType, name)

/** Creates a binary body whose wire representation is controlled by [codec]. */
fun rawBody(
    codec: ByteArrayCodec<ByteArray>,
    mediaType: MediaType? = null,
    name: String = "bytes"
): RawBody = RawBody(mediaType, codec, name)

/** Returns the singleton body definition used when no payload is allowed. */
fun emptyBody(): EmptyBody = EmptyBody
