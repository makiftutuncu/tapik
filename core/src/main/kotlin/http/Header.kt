package dev.akif.tapik.http

import dev.akif.tapik.codec.*
import dev.akif.tapik.tuple.*
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.util.UUID

data class Header<T>(val name: String, val codec: StringCodec<T>, val values: List<T> = emptyList()) {
    companion object: Defaults<Header<Unit>, Header<Boolean>, Header<Byte>, Header<Short>, Header<Int>, Header<Long>, Header<Float>, Header<Double>, Header<BigInteger>, Header<BigDecimal>, Header<String>, Header<UUID>> {
        override fun unit(name: String): Header<Unit> = Header(name, StringCodecs.unit(name))
        override fun boolean(name: String): Header<Boolean> = Header(name, StringCodecs.boolean(name))
        override fun byte(name: String): Header<Byte> = Header(name, StringCodecs.byte(name))
        override fun short(name: String): Header<Short> = Header(name, StringCodecs.short(name))
        override fun int(name: String): Header<Int> = Header(name, StringCodecs.int(name))
        override fun long(name: String): Header<Long> = Header(name, StringCodecs.long(name))
        override fun float(name: String): Header<Float> = Header(name, StringCodecs.float(name))
        override fun double(name: String): Header<Double> = Header(name, StringCodecs.double(name))
        override fun bigInteger(name: String): Header<BigInteger> = Header(name, StringCodecs.bigInteger(name))
        override fun bigDecimal(name: String): Header<BigDecimal> = Header(name, StringCodecs.bigDecimal(name))
        override fun string(name: String): Header<String> = Header(name, StringCodecs.string(name))
        override fun uuid(name: String): Header<UUID> = Header(name, StringCodecs.uuid(name))

        const val ACCEPT = "Accept"
        const val CONTENT_TYPE = "Content-Type"
        const val LOCATION = "Location"

        val Accept: Header<MediaType> = Header(ACCEPT, Codecs.mediaTypeString(ACCEPT))
        val ContentType: Header<MediaType> = Header(CONTENT_TYPE, Codecs.mediaTypeString(CONTENT_TYPE))
        val Location: Header<URI> = Header(LOCATION, Codecs.uriString(LOCATION))
    }

    operator fun <T2> plus(that: Header<T2>): Headers2<T, T2> = Headers2(this, that)

    operator fun invoke(vararg values: T): Header<T> = copy(values = values.toList())
}

inline fun <reified T> header(name: String, codec: StringCodec<T>): Header<T> = Header(name, codec)

fun <PathVariables : Tuple, QueryParameters: Tuple, H> HttpEndpoint<PathVariables, QueryParameters, Headers0, EmptyBody, Outputs0>.headers(
    header: Header<H>
): HttpEndpoint<PathVariables, QueryParameters, Headers1<H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = Headers1(header),
        input = this.input,
        outputs = this.outputs
    )

fun <PathVariables : Tuple, QueryParameters: Tuple, Headers: Tuple> HttpEndpoint<PathVariables, QueryParameters, Headers0, EmptyBody, Outputs0>.headers(
    headers: Headers
): HttpEndpoint<PathVariables, QueryParameters, Headers, EmptyBody, Outputs0> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = headers,
        input = this.input,
        outputs = this.outputs
    )
