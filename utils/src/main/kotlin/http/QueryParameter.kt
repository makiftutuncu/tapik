package dev.akif.tapik.http

import dev.akif.tapik.codec.*
import dev.akif.tapik.tuple.*

data class QueryParameter<T>(val name: String, val codec: StringCodec<T>) {
    operator fun <T2> plus(that: QueryParameter<T2>): QueryParameters2<T, T2> = QueryParameters2(this, that)
}

inline fun <reified T> query(
    name: String,
    codec: StringCodec<T> = StringCodecs.default(),
): QueryParameter<T> = QueryParameter(name, codec)

@JvmName("plus1")
operator fun <PathVariables: TupleLike, T1> URIBuilder<PathVariables, QueryParameters0>.plus(parameter: QueryParameter<T1>): URIBuilder<PathVariables, QueryParameters1<T1>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus2")
operator fun <PathVariables: TupleLike, T1, T2> URIBuilder<PathVariables, QueryParameters1<T1>>.plus(parameter: QueryParameter<T2>): URIBuilder<PathVariables, QueryParameters2<T1, T2>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus3")
operator fun <PathVariables: TupleLike, T1, T2, T3> URIBuilder<PathVariables, QueryParameters2<T1, T2>>.plus(parameter: QueryParameter<T3>): URIBuilder<PathVariables, QueryParameters3<T1, T2, T3>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus4")
operator fun <PathVariables: TupleLike, T1, T2, T3, T4> URIBuilder<PathVariables, QueryParameters3<T1, T2, T3>>.plus(parameter: QueryParameter<T4>): URIBuilder<PathVariables, QueryParameters4<T1, T2, T3, T4>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus5")
operator fun <PathVariables: TupleLike, T1, T2, T3, T4, T5> URIBuilder<PathVariables, QueryParameters4<T1, T2, T3, T4>>.plus(parameter: QueryParameter<T5>): URIBuilder<PathVariables, QueryParameters5<T1, T2, T3, T4, T5>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus6")
operator fun <PathVariables: TupleLike, T1, T2, T3, T4, T5, T6> URIBuilder<PathVariables, QueryParameters5<T1, T2, T3, T4, T5>>.plus(parameter: QueryParameter<T6>): URIBuilder<PathVariables, QueryParameters6<T1, T2, T3, T4, T5, T6>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus7")
operator fun <PathVariables: TupleLike, T1, T2, T3, T4, T5, T6, T7> URIBuilder<PathVariables, QueryParameters6<T1, T2, T3, T4, T5, T6>>.plus(parameter: QueryParameter<T7>): URIBuilder<PathVariables, QueryParameters7<T1, T2, T3, T4, T5, T6, T7>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus8")
operator fun <PathVariables: TupleLike, T1, T2, T3, T4, T5, T6, T7, T8> URIBuilder<PathVariables, QueryParameters7<T1, T2, T3, T4, T5, T6, T7>>.plus(parameter: QueryParameter<T8>): URIBuilder<PathVariables, QueryParameters8<T1, T2, T3, T4, T5, T6, T7, T8>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus9")
operator fun <PathVariables: TupleLike, T1, T2, T3, T4, T5, T6, T7, T8, T9> URIBuilder<PathVariables, QueryParameters8<T1, T2, T3, T4, T5, T6, T7, T8>>.plus(parameter: QueryParameter<T9>): URIBuilder<PathVariables, QueryParameters9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)

@JvmName("plus10")
operator fun <PathVariables: TupleLike, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> URIBuilder<PathVariables, QueryParameters9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>.plus(parameter: QueryParameter<T10>): URIBuilder<PathVariables, QueryParameters10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>> =
    URIBuilder(segments, pathVariables, queryParameters + parameter)
