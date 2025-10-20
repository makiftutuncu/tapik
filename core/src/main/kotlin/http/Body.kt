package dev.akif.tapik.http

import dev.akif.tapik.codec.*
import dev.akif.tapik.codec.StringCodecs

sealed interface Body<T : Any> {
    val mediaType: MediaType?
    val codec: ByteArrayCodec<T>

    fun bytes(value: T): ByteArray? = codec.encode(value)
}

data object EmptyBody : Body<Unit> {
    override val mediaType: MediaType? = null

    override val codec: ByteArrayCodec<Unit> = ByteArrayCodec.nullable("empty", { ByteArray(0) }) { }

    override fun bytes(value: Unit): ByteArray? = null
}

data class StringBody(
    override val codec: ByteArrayCodec<String>
) : Body<String> {
    override val mediaType: MediaType = MediaType.PlainText
}

data class RawBody(
    override val mediaType: MediaType?,
    override val codec: ByteArrayCodec<ByteArray>
) : Body<ByteArray>

data class JsonBody<T : Any>(
    override val codec: ByteArrayCodec<T>
) : Body<T> {
    override val mediaType: MediaType = MediaType.Json
}

fun stringBody(name: String = "string"): StringBody = stringBody(StringCodecs.string(name).toByteArrayCodec())

fun stringBody(codec: ByteArrayCodec<String>): StringBody = StringBody(codec)

fun rawBody(
    name: String = "bytes",
    mediaType: MediaType? = null
): RawBody = rawBody(ByteArrayCodec.identity(name), mediaType)

fun rawBody(
    codec: ByteArrayCodec<ByteArray>,
    mediaType: MediaType? = null
): RawBody = RawBody(mediaType, codec)
