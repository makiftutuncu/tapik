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

fun stringBody(codec: ByteArrayCodec<String> = StringCodecs.string("string").toByteArrayCodec()): StringBody =
    StringBody(codec)

fun rawBody(
    mediaType: MediaType? = null,
    codec: ByteArrayCodec<ByteArray> = ByteArrayCodec.identity("bytes")
): RawBody = RawBody(mediaType, codec)

inline fun <reified T : Any> jsonBody(codec: ByteArrayCodec<T>): JsonBody<T> = JsonBody(codec)
