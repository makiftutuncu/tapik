package dev.akif.tapik.http

import dev.akif.tapik.codec.*
import dev.akif.tapik.tuple.*
import java.net.URI

data class Header<T>(val name: String, val values: List<T>, val codec: StringCodec<T>) {
    companion object {
        const val ACCEPT = "Accept"
        const val CONTENT_TYPE = "Content-Type"
        const val LOCATION = "Location"

        fun Accept(vararg values: MediaType): Header<MediaType> = header(ACCEPT, *values)
        fun ContentType(vararg values: MediaType): Header<MediaType> = header(CONTENT_TYPE, *values)
        fun Location(vararg values: URI): Header<URI> = header(LOCATION, *values)
    }

    operator fun <T2> plus(that: Header<T2>): Headers2<T, T2> = Headers2(this, that)
}

inline fun <reified T> header(
    name: String,
    vararg values: T,
    codec: StringCodec<T> = StringCodecs.default(),
): Header<T> = Header(name, values.toList(), codec)

fun <PathVariables : TupleLike, QueryParameters: TupleLike, H> HttpEndpoint<PathVariables, QueryParameters, Headers0, EmptyBody, Outputs0>.headers(
    header: Header<H>
): HttpEndpoint<PathVariables, QueryParameters, Headers1<H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = Headers1(header),
        input = this.input,
        outputs = this.outputs
    )

fun <PathVariables : TupleLike, QueryParameters: TupleLike, Headers: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers0, EmptyBody, Outputs0>.headers(
    headers: Headers
): HttpEndpoint<PathVariables, QueryParameters, Headers, EmptyBody, Outputs0> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = headers,
        input = this.input,
        outputs = this.outputs
    )
