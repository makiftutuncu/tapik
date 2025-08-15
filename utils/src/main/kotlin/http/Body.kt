package dev.akif.tapik.http

import dev.akif.tapik.codec.*

sealed interface Body<T> {
    val mediaType: MediaType?
}

data object EmptyBody : Body<EmptyBody> {
    override val mediaType: MediaType? = null
}

data object StringBody: Body<String> {
    override val mediaType: MediaType = MediaType.PlainText
}

data class RawBody(override val mediaType: MediaType?): Body<ByteArray>

data class JsonBody<T>(val codec: StringCodec<T>): Body<T> {
    override val mediaType: MediaType = MediaType.Json
}

fun rawBody(mediaType: MediaType? = null): RawBody =
    RawBody(mediaType)

inline fun <reified T> jsonBody(codec: StringCodec<T> = StringCodecs.default()): JsonBody<T> =
    JsonBody(codec)
