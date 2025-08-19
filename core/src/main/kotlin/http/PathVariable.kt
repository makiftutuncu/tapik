package dev.akif.tapik.http

import dev.akif.tapik.codec.*
import dev.akif.tapik.tuple.*
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

data class PathVariable<T : Any>(val name: String, val codec: Codec<T, String>) {
    companion object: Defaults<PathVariable<Unit>, PathVariable<Boolean>, PathVariable<Byte>, PathVariable<Short>, PathVariable<Int>, PathVariable<Long>, PathVariable<Float>, PathVariable<Double>, PathVariable<BigInteger>, PathVariable<BigDecimal>, PathVariable<String>, PathVariable<UUID>> {
        override fun unit(name: String): PathVariable<Unit> = PathVariable(name, StringCodecs.unit(name))
        override fun boolean(name: String): PathVariable<Boolean> = PathVariable(name, StringCodecs.boolean(name))
        override fun byte(name: String): PathVariable<Byte> = PathVariable(name, StringCodecs.byte(name))
        override fun short(name: String): PathVariable<Short> = PathVariable(name, StringCodecs.short(name))
        override fun int(name: String): PathVariable<Int> = PathVariable(name, StringCodecs.int(name))
        override fun long(name: String): PathVariable<Long> = PathVariable(name, StringCodecs.long(name))
        override fun float(name: String): PathVariable<Float> = PathVariable(name, StringCodecs.float(name))
        override fun double(name: String): PathVariable<Double> = PathVariable(name, StringCodecs.double(name))
        override fun bigInteger(name: String): PathVariable<BigInteger> = PathVariable(name, StringCodecs.bigInteger(name))
        override fun bigDecimal(name: String): PathVariable<BigDecimal> = PathVariable(name, StringCodecs.bigDecimal(name))
        override fun string(name: String): PathVariable<String> = PathVariable(name, StringCodecs.string(name))
        override fun uuid(name: String): PathVariable<UUID> = PathVariable(name, StringCodecs.uuid(name))
    }

    operator fun <T2 : Any> plus(that: PathVariable<T2>): PathVariables2<T, T2> = PathVariables2(this, that)
}

inline fun <reified T : Any> path(name: String, codec: StringCodec<T>): PathVariable<T> = PathVariable(name, codec)

@JvmName("div1")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any> URIBuilder<PathVariables0, QueryParameters>.div(variable: PathVariable<T1>): URIBuilder<PathVariables1<T1>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div2")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any> URIBuilder<PathVariables1<T1>, QueryParameters>.div(variable: PathVariable<T2>): URIBuilder<PathVariables2<T1, T2>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div3")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any> URIBuilder<PathVariables2<T1, T2>, QueryParameters>.div(variable: PathVariable<T3>): URIBuilder<PathVariables3<T1, T2, T3>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div4")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any, T4 : Any> URIBuilder<PathVariables3<T1, T2, T3>, QueryParameters>.div(variable: PathVariable<T4>): URIBuilder<PathVariables4<T1, T2, T3, T4>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div5")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> URIBuilder<PathVariables4<T1, T2, T3, T4>, QueryParameters>.div(variable: PathVariable<T5>): URIBuilder<PathVariables5<T1, T2, T3, T4, T5>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div6")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any> URIBuilder<PathVariables5<T1, T2, T3, T4, T5>, QueryParameters>.div(variable: PathVariable<T6>): URIBuilder<PathVariables6<T1, T2, T3, T4, T5, T6>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div7")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any> URIBuilder<PathVariables6<T1, T2, T3, T4, T5, T6>, QueryParameters>.div(variable: PathVariable<T7>): URIBuilder<PathVariables7<T1, T2, T3, T4, T5, T6, T7>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div8")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any> URIBuilder<PathVariables7<T1, T2, T3, T4, T5, T6, T7>, QueryParameters>.div(variable: PathVariable<T8>): URIBuilder<PathVariables8<T1, T2, T3, T4, T5, T6, T7, T8>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div9")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, T9 : Any> URIBuilder<PathVariables8<T1, T2, T3, T4, T5, T6, T7, T8>, QueryParameters>.div(variable: PathVariable<T9>): URIBuilder<PathVariables9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div10")
operator fun <QueryParameters: Tuple<QueryParameter<*>>, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, T9 : Any, T10 : Any> URIBuilder<PathVariables9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, QueryParameters>.div(variable: PathVariable<T10>): URIBuilder<PathVariables10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)
