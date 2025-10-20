@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import dev.akif.tapik.*
import dev.akif.tapik.http.URIWithParameters.Companion.toURI
import java.net.URI

operator fun String.div(segment: String): URIWithParameters0 = URIWithParameters0(listOf(this, segment))

operator fun <P : Any> String.div(variable: PathVariable<P>): URIWithParameters1<P> =
    URIWithParameters1(listOf(this), variable)

sealed interface URIWithParameters {
    val uri: List<String>
    val parameters: Tuple

    companion object {
        fun toURI(
            segments: List<String>,
            vararg parametersToValues: Pair<Parameter<*>, String>
        ): URI {
            val map =
                parametersToValues.groupBy(
                    { it.first.position }
                ) { (parameter, value) -> parameter.name to value }

            val path =
                map[ParameterPosition.Path]
                    .orEmpty()
                    .fold(segments.joinToString(separator = "/", prefix = "/")) { path, (name, value) ->
                        path.replace("{$name}", value)
                    }

            val query =
                map[ParameterPosition.Query]
                    .orEmpty()
                    .joinToString(separator = "&") { (name, value) -> "$name=$value" }
                    .takeIf { it.isNotEmpty() }

            return URI.create(listOfNotNull(path, query).joinToString("?"))
        }
    }
}

data class URIWithParameters0(
    override val uri: List<String>
) : URIWithParameters,
    AllOf0 {
    operator fun div(segment: String): URIWithParameters0 = URIWithParameters0(uri + segment)

    operator fun <P1 : Any> div(variable: PathVariable<P1>): URIWithParameters1<P1> =
        URIWithParameters1(uri + "{${variable.name}}", variable)

    operator fun <P1 : Any> plus(parameter: QueryParameter<P1>): URIWithParameters1<P1> =
        URIWithParameters1(uri, parameter)

    override val parameters: Tuple0 =
        Tuple0

    fun toURI(): URI = toURI(uri)
}

data class URIWithParameters1<P1 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>
) : URIWithParameters,
    AllOf1<Parameter<P1>> {
    operator fun div(segment: String): URIWithParameters1<P1> = URIWithParameters1(uri + segment, item1)

    operator fun <P2 : Any> div(variable: PathVariable<P2>): URIWithParameters2<P1, P2> =
        URIWithParameters2(uri + "{${variable.name}}", item1, variable)

    operator fun <P2 : Any> plus(parameter: QueryParameter<P2>): URIWithParameters2<P1, P2> =
        URIWithParameters2(uri, item1, parameter)

    override val parameters: Tuple1<Parameter<P1>> =
        Tuple1(item1)

    fun toURI(p1: P1): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1)
        )
}

data class URIWithParameters2<P1 : Any, P2 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>
) : URIWithParameters,
    AllOf2<Parameter<P1>, Parameter<P2>> {
    operator fun div(segment: String): URIWithParameters2<P1, P2> = URIWithParameters2(uri + segment, item1, item2)

    operator fun <P3 : Any> div(variable: PathVariable<P3>): URIWithParameters3<P1, P2, P3> =
        URIWithParameters3(uri + "{${variable.name}}", item1, item2, variable)

    operator fun <P3 : Any> plus(parameter: QueryParameter<P3>): URIWithParameters3<P1, P2, P3> =
        URIWithParameters3(uri, item1, item2, parameter)

    override val parameters: Tuple2<Parameter<P1>, Parameter<P2>> =
        Tuple2(item1, item2)

    fun toURI(
        p1: P1,
        p2: P2
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2)
        )
}

data class URIWithParameters3<P1 : Any, P2 : Any, P3 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>
) : URIWithParameters,
    AllOf3<Parameter<P1>, Parameter<P2>, Parameter<P3>> {
    operator fun div(segment: String): URIWithParameters3<P1, P2, P3> =
        URIWithParameters3(uri + segment, item1, item2, item3)

    operator fun <P4 : Any> div(variable: PathVariable<P4>): URIWithParameters4<P1, P2, P3, P4> =
        URIWithParameters4(uri + "{${variable.name}}", item1, item2, item3, variable)

    operator fun <P4 : Any> plus(parameter: QueryParameter<P4>): URIWithParameters4<P1, P2, P3, P4> =
        URIWithParameters4(uri, item1, item2, item3, parameter)

    override val parameters: Tuple3<Parameter<P1>, Parameter<P2>, Parameter<P3>> =
        Tuple3(item1, item2, item3)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3)
        )
}

data class URIWithParameters4<P1 : Any, P2 : Any, P3 : Any, P4 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>
) : URIWithParameters,
    AllOf4<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>> {
    operator fun div(segment: String): URIWithParameters4<P1, P2, P3, P4> =
        URIWithParameters4(uri + segment, item1, item2, item3, item4)

    operator fun <P5 : Any> div(variable: PathVariable<P5>): URIWithParameters5<P1, P2, P3, P4, P5> =
        URIWithParameters5(uri + "{${variable.name}}", item1, item2, item3, item4, variable)

    operator fun <P5 : Any> plus(parameter: QueryParameter<P5>): URIWithParameters5<P1, P2, P3, P4, P5> =
        URIWithParameters5(uri, item1, item2, item3, item4, parameter)

    override val parameters: Tuple4<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>> =
        Tuple4(item1, item2, item3, item4)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3),
            item4 to item4.codec.encode(p4)
        )
}

data class URIWithParameters5<P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>,
    override val item5: Parameter<P5>
) : URIWithParameters,
    AllOf5<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>> {
    operator fun div(segment: String): URIWithParameters5<P1, P2, P3, P4, P5> =
        URIWithParameters5(uri + segment, item1, item2, item3, item4, item5)

    operator fun <P6 : Any> div(variable: PathVariable<P6>): URIWithParameters6<P1, P2, P3, P4, P5, P6> =
        URIWithParameters6(uri + "{${variable.name}}", item1, item2, item3, item4, item5, variable)

    operator fun <P6 : Any> plus(parameter: QueryParameter<P6>): URIWithParameters6<P1, P2, P3, P4, P5, P6> =
        URIWithParameters6(uri, item1, item2, item3, item4, item5, parameter)

    override val parameters: Tuple5<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>> =
        Tuple5(item1, item2, item3, item4, item5)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3),
            item4 to item4.codec.encode(p4),
            item5 to item5.codec.encode(p5)
        )
}

data class URIWithParameters6<P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>,
    override val item5: Parameter<P5>,
    override val item6: Parameter<P6>
) : URIWithParameters,
    AllOf6<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>> {
    operator fun div(segment: String): URIWithParameters6<P1, P2, P3, P4, P5, P6> =
        URIWithParameters6(uri + segment, item1, item2, item3, item4, item5, item6)

    operator fun <P7 : Any> div(variable: PathVariable<P7>): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> =
        URIWithParameters7(uri + "{${variable.name}}", item1, item2, item3, item4, item5, item6, variable)

    operator fun <P7 : Any> plus(parameter: QueryParameter<P7>): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> =
        URIWithParameters7(uri, item1, item2, item3, item4, item5, item6, parameter)

    override val parameters:
        Tuple6<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>> =
        Tuple6(item1, item2, item3, item4, item5, item6)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3),
            item4 to item4.codec.encode(p4),
            item5 to item5.codec.encode(p5),
            item6 to item6.codec.encode(p6)
        )
}

data class URIWithParameters7<P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>,
    override val item5: Parameter<P5>,
    override val item6: Parameter<P6>,
    override val item7: Parameter<P7>
) : URIWithParameters,
    AllOf7<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>> {
    operator fun div(segment: String): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> =
        URIWithParameters7(uri + segment, item1, item2, item3, item4, item5, item6, item7)

    operator fun <P8 : Any> div(variable: PathVariable<P8>): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
        URIWithParameters8(uri + "{${variable.name}}", item1, item2, item3, item4, item5, item6, item7, variable)

    operator fun <P8 : Any> plus(parameter: QueryParameter<P8>): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
        URIWithParameters8(uri, item1, item2, item3, item4, item5, item6, item7, parameter)

    override val parameters:
        Tuple7<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>> =
        Tuple7(item1, item2, item3, item4, item5, item6, item7)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3),
            item4 to item4.codec.encode(p4),
            item5 to item5.codec.encode(p5),
            item6 to item6.codec.encode(p6),
            item7 to item7.codec.encode(p7)
        )
}

data class URIWithParameters8<P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>,
    override val item5: Parameter<P5>,
    override val item6: Parameter<P6>,
    override val item7: Parameter<P7>,
    override val item8: Parameter<P8>
) : URIWithParameters,
    AllOf8<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>> {
    operator fun div(segment: String): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
        URIWithParameters8(uri + segment, item1, item2, item3, item4, item5, item6, item7, item8)

    operator fun <P9 : Any> div(variable: PathVariable<P9>): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
        URIWithParameters9(uri + "{${variable.name}}", item1, item2, item3, item4, item5, item6, item7, item8, variable)

    operator fun <P9 : Any> plus(
        parameter: QueryParameter<P9>
    ): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
        URIWithParameters9(uri, item1, item2, item3, item4, item5, item6, item7, item8, parameter)

    override val parameters:
        Tuple8<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>> =
        Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3),
            item4 to item4.codec.encode(p4),
            item5 to item5.codec.encode(p5),
            item6 to item6.codec.encode(p6),
            item7 to item7.codec.encode(p7),
            item8 to item8.codec.encode(p8)
        )
}

data class URIWithParameters9<P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>,
    override val item5: Parameter<P5>,
    override val item6: Parameter<P6>,
    override val item7: Parameter<P7>,
    override val item8: Parameter<P8>,
    override val item9: Parameter<P9>
) : URIWithParameters,
    AllOf9<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>> {
    operator fun div(segment: String): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
        URIWithParameters9(uri + segment, item1, item2, item3, item4, item5, item6, item7, item8, item9)

    operator fun <P10 : Any> div(
        variable: PathVariable<P10>
    ): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
        URIWithParameters10(
            uri + "{${variable.name}}",
            item1,
            item2,
            item3,
            item4,
            item5,
            item6,
            item7,
            item8,
            item9,
            variable
        )

    operator fun <P10 : Any> plus(
        parameter: QueryParameter<P10>
    ): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
        URIWithParameters10(uri, item1, item2, item3, item4, item5, item6, item7, item8, item9, parameter)

    override val parameters:
        Tuple9<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>> =
        Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3),
            item4 to item4.codec.encode(p4),
            item5 to item5.codec.encode(p5),
            item6 to item6.codec.encode(p6),
            item7 to item7.codec.encode(p7),
            item8 to item8.codec.encode(p8),
            item9 to item9.codec.encode(p9)
        )
}

data class URIWithParameters10<P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>,
    override val item5: Parameter<P5>,
    override val item6: Parameter<P6>,
    override val item7: Parameter<P7>,
    override val item8: Parameter<P8>,
    override val item9: Parameter<P9>,
    override val item10: Parameter<P10>
) : URIWithParameters,
    AllOf10<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>, Parameter<P10>> {
    operator fun div(segment: String): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
        URIWithParameters10(uri + segment, item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override val parameters:
        Tuple10<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>, Parameter<P10>> =
        Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    fun toURI(
        p1: P1,
        p2: P2,
        p3: P3,
        p4: P4,
        p5: P5,
        p6: P6,
        p7: P7,
        p8: P8,
        p9: P9,
        p10: P10
    ): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1),
            item2 to item2.codec.encode(p2),
            item3 to item3.codec.encode(p3),
            item4 to item4.codec.encode(p4),
            item5 to item5.codec.encode(p5),
            item6 to item6.codec.encode(p6),
            item7 to item7.codec.encode(p7),
            item8 to item8.codec.encode(p8),
            item9 to item9.codec.encode(p9),
            item10 to item10.codec.encode(p10)
        )
}
