@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import dev.akif.tapik.codec.Defaults
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.codec.StringCodecs
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.util.UUID
import dev.akif.tapik.http.StringCodecs as HttpStringCodecs

sealed interface Parameter<T : Any> {
    val name: String
    val codec: StringCodec<T>
    val position: ParameterPosition
}

data class PathVariable<P : Any>(
    override val name: String,
    override val codec: StringCodec<P>
) : Parameter<P> {
    override val position: ParameterPosition = ParameterPosition.Path

    companion object : Defaults<PathVariable<Unit>, PathVariable<Boolean>, PathVariable<Byte>, PathVariable<Short>, PathVariable<Int>, PathVariable<Long>, PathVariable<Float>, PathVariable<Double>, PathVariable<BigInteger>, PathVariable<BigDecimal>, PathVariable<String>, PathVariable<UUID>> {
        override fun unit(name: String): PathVariable<Unit> = PathVariable(name, StringCodecs.unit(name))

        override fun boolean(name: String): PathVariable<Boolean> = PathVariable(name, StringCodecs.boolean(name))

        override fun byte(name: String): PathVariable<Byte> = PathVariable(name, StringCodecs.byte(name))

        override fun short(name: String): PathVariable<Short> = PathVariable(name, StringCodecs.short(name))

        override fun int(name: String): PathVariable<Int> = PathVariable(name, StringCodecs.int(name))

        override fun long(name: String): PathVariable<Long> = PathVariable(name, StringCodecs.long(name))

        override fun float(name: String): PathVariable<Float> = PathVariable(name, StringCodecs.float(name))

        override fun double(name: String): PathVariable<Double> = PathVariable(name, StringCodecs.double(name))

        override fun bigInteger(name: String): PathVariable<BigInteger> =
            PathVariable(name, StringCodecs.bigInteger(name))

        override fun bigDecimal(name: String): PathVariable<BigDecimal> =
            PathVariable(name, StringCodecs.bigDecimal(name))

        override fun string(name: String): PathVariable<String> = PathVariable(name, StringCodecs.string(name))

        override fun uuid(name: String): PathVariable<UUID> = PathVariable(name, StringCodecs.uuid(name))
    }
}

data class QueryParameter<Q : Any>(
    override val name: String,
    override val codec: StringCodec<Q>
) : Parameter<Q> {
    override val position: ParameterPosition = ParameterPosition.Query

    companion object : Defaults<QueryParameter<Unit>, QueryParameter<Boolean>, QueryParameter<Byte>, QueryParameter<Short>, QueryParameter<Int>, QueryParameter<Long>, QueryParameter<Float>, QueryParameter<Double>, QueryParameter<BigInteger>, QueryParameter<BigDecimal>, QueryParameter<String>, QueryParameter<UUID>> {
        override fun unit(name: String): QueryParameter<Unit> = QueryParameter(name, StringCodecs.unit(name))

        override fun boolean(name: String): QueryParameter<Boolean> = QueryParameter(name, StringCodecs.boolean(name))

        override fun byte(name: String): QueryParameter<Byte> = QueryParameter(name, StringCodecs.byte(name))

        override fun short(name: String): QueryParameter<Short> = QueryParameter(name, StringCodecs.short(name))

        override fun int(name: String): QueryParameter<Int> = QueryParameter(name, StringCodecs.int(name))

        override fun long(name: String): QueryParameter<Long> = QueryParameter(name, StringCodecs.long(name))

        override fun float(name: String): QueryParameter<Float> = QueryParameter(name, StringCodecs.float(name))

        override fun double(name: String): QueryParameter<Double> = QueryParameter(name, StringCodecs.double(name))

        override fun bigInteger(name: String): QueryParameter<BigInteger> =
            QueryParameter(name, StringCodecs.bigInteger(name))

        override fun bigDecimal(name: String): QueryParameter<BigDecimal> =
            QueryParameter(name, StringCodecs.bigDecimal(name))

        override fun string(name: String): QueryParameter<String> = QueryParameter(name, StringCodecs.string(name))

        override fun uuid(name: String): QueryParameter<UUID> = QueryParameter(name, StringCodecs.uuid(name))
    }
}

sealed interface Header<H : Any> : Parameter<H> {
    companion object : Defaults<Header<Unit>, Header<Boolean>, Header<Byte>, Header<Short>, Header<Int>, Header<Long>, Header<Float>, Header<Double>, Header<BigInteger>, Header<BigDecimal>, Header<String>, Header<UUID>> {
        override fun unit(name: String): Header<Unit> = HeaderInput(name, StringCodecs.unit(name))

        override fun boolean(name: String): Header<Boolean> = HeaderInput(name, StringCodecs.boolean(name))

        override fun byte(name: String): Header<Byte> = HeaderInput(name, StringCodecs.byte(name))

        override fun short(name: String): Header<Short> = HeaderInput(name, StringCodecs.short(name))

        override fun int(name: String): Header<Int> = HeaderInput(name, StringCodecs.int(name))

        override fun long(name: String): Header<Long> = HeaderInput(name, StringCodecs.long(name))

        override fun float(name: String): Header<Float> = HeaderInput(name, StringCodecs.float(name))

        override fun double(name: String): Header<Double> = HeaderInput(name, StringCodecs.double(name))

        override fun bigInteger(name: String): Header<BigInteger> = HeaderInput(name, StringCodecs.bigInteger(name))

        override fun bigDecimal(name: String): Header<BigDecimal> = HeaderInput(name, StringCodecs.bigDecimal(name))

        override fun string(name: String): Header<String> = HeaderInput(name, StringCodecs.string(name))

        override fun uuid(name: String): Header<UUID> = HeaderInput(name, StringCodecs.uuid(name))

        const val ACCEPT = "Accept"
        const val CONTENT_TYPE = "Content-Type"
        const val LOCATION = "Location"

        val Accept: Header<MediaType> = HeaderInput(ACCEPT, HttpStringCodecs.mediaType(ACCEPT))
        val ContentType: Header<MediaType> = HeaderInput(CONTENT_TYPE, HttpStringCodecs.mediaType(CONTENT_TYPE))
        val Location: Header<URI> = HeaderInput(LOCATION, HttpStringCodecs.uri(LOCATION))
    }

    operator fun invoke(vararg values: H): Header<H> = HeaderValues(name, codec, values.toList())
}

data class HeaderInput<H : Any>(
    override val name: String,
    override val codec: StringCodec<H>
) : Header<H> {
    override val position: ParameterPosition = ParameterPosition.Header
}

data class HeaderValues<H : Any>(
    override val name: String,
    override val codec: StringCodec<H>,
    val values: List<H>
) : Header<H> {
    override val position: ParameterPosition = ParameterPosition.Header
}
