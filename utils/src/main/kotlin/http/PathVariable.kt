package dev.akif.tapik.http

import dev.akif.tapik.codec.*
import dev.akif.tapik.tuple.*

data class PathVariable<T>(val name: String, val codec: StringCodec<T>) {
    operator fun <T2> plus(that: PathVariable<T2>): PathVariables2<T, T2> = PathVariables2(this, that)
}

inline fun <reified T> path(
    name: String,
    codec: StringCodec<T> = StringCodecs.default(),
): PathVariable<T> = PathVariable(name, codec)

@JvmName("div1")
operator fun <QueryParameters: TupleLike, T1> URIBuilder<PathVariables0, QueryParameters>.div(variable: PathVariable<T1>): URIBuilder<PathVariables1<T1>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div2")
operator fun <QueryParameters: TupleLike, T1, T2> URIBuilder<PathVariables1<T1>, QueryParameters>.div(variable: PathVariable<T2>): URIBuilder<PathVariables2<T1, T2>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div3")
operator fun <QueryParameters: TupleLike, T1, T2, T3> URIBuilder<PathVariables2<T1, T2>, QueryParameters>.div(variable: PathVariable<T3>): URIBuilder<PathVariables3<T1, T2, T3>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div4")
operator fun <QueryParameters: TupleLike, T1, T2, T3, T4> URIBuilder<PathVariables3<T1, T2, T3>, QueryParameters>.div(variable: PathVariable<T4>): URIBuilder<PathVariables4<T1, T2, T3, T4>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div5")
operator fun <QueryParameters: TupleLike, T1, T2, T3, T4, T5> URIBuilder<PathVariables4<T1, T2, T3, T4>, QueryParameters>.div(variable: PathVariable<T5>): URIBuilder<PathVariables5<T1, T2, T3, T4, T5>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div6")
operator fun <QueryParameters: TupleLike, T1, T2, T3, T4, T5, T6> URIBuilder<PathVariables5<T1, T2, T3, T4, T5>, QueryParameters>.div(variable: PathVariable<T6>): URIBuilder<PathVariables6<T1, T2, T3, T4, T5, T6>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div7")
operator fun <QueryParameters: TupleLike, T1, T2, T3, T4, T5, T6, T7> URIBuilder<PathVariables6<T1, T2, T3, T4, T5, T6>, QueryParameters>.div(variable: PathVariable<T7>): URIBuilder<PathVariables7<T1, T2, T3, T4, T5, T6, T7>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div8")
operator fun <QueryParameters: TupleLike, T1, T2, T3, T4, T5, T6, T7, T8> URIBuilder<PathVariables7<T1, T2, T3, T4, T5, T6, T7>, QueryParameters>.div(variable: PathVariable<T8>): URIBuilder<PathVariables8<T1, T2, T3, T4, T5, T6, T7, T8>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div9")
operator fun <QueryParameters: TupleLike, T1, T2, T3, T4, T5, T6, T7, T8, T9> URIBuilder<PathVariables8<T1, T2, T3, T4, T5, T6, T7, T8>, QueryParameters>.div(variable: PathVariable<T9>): URIBuilder<PathVariables9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)

@JvmName("div10")
operator fun <QueryParameters: TupleLike, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> URIBuilder<PathVariables9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, QueryParameters>.div(variable: PathVariable<T10>): URIBuilder<PathVariables10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>, QueryParameters> =
    URIBuilder(segments + "{${variable.name}}", pathVariables + variable, queryParameters)
