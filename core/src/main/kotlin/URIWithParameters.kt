@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import dev.akif.tapik.URIWithParameters.Companion.toURI
import java.net.URI

/**
 * Concatenates [segment] to this base path when building an endpoint URI.
 *
 * @receiver base path segment.
 * @param segment literal URI segment to append.
 * @return URI template containing the concatenated path.
 * @see URIWithParameters0
 */
operator fun String.div(segment: String): URIWithParameters0 = URIWithParameters0(listOf(this, segment))

/**
 * Appends a path [variable] placeholder to this base path.
 *
 * @receiver base path segment.
 * @param variable path variable placeholder to append.
 * @return URI template capturing the supplied path variable.
 * @see PathVariable
 */
operator fun <P : Any> String.div(variable: PathVariable<P>): URIWithParameters1<P> =
    URIWithParameters1(listOf(this), variable)

/** Represents a URI template together with the ordered parameters it references.
 * @property uri ordered segments forming the template.
 * @property parameters tuple containing the parameters referenced by the template.
 */
sealed interface URIWithParameters {
    val uri: List<String>
    val parameters: Tuple

    /** Helpers for rendering URI templates. */
    companion object {
        /**
         * Renders the URI template using the provided [segments] and encoded parameter values.
         *
         * @param segments literal URI segments that form the template.
         * @param parametersToValues parameter/value pairs that will be substituted into the template.
         * @return a ready-to-use [URI].
         * @throws IllegalArgumentException if substitution fails to replace required path variables.
         */
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

/** URI template that does not capture any parameters. */
data class URIWithParameters0(
    override val uri: List<String>
) : URIWithParameters,
    AllOf0 {
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended.
     */
    operator fun div(segment: String): URIWithParameters0 = URIWithParameters0(uri + segment)

    /**
     * Appends a path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template that records a single parameter.
     */
    operator fun <P1 : Any> div(variable: PathVariable<P1>): URIWithParameters1<P1> =
        URIWithParameters1(uri + "{${variable.name}}", variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template that records a single query parameter.
     */
    operator fun <P1 : Any> plus(parameter: QueryParameter<P1>): URIWithParameters1<P1> =
        URIWithParameters1(uri, parameter)

    override val parameters: Tuple0 =
        Tuple0

    /** Builds a concrete [URI] without substituting any parameters. */
    fun toURI(): URI = toURI(uri)
}

/** URI template that captures a single parameter. */
data class URIWithParameters1<P1 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>
) : URIWithParameters,
    AllOf1<Parameter<P1>> {
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining the captured parameter.
     */
    operator fun div(segment: String): URIWithParameters1<P1> = URIWithParameters1(uri + segment, item1)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the original and new path parameters.
     */
    operator fun <P2 : Any> div(variable: PathVariable<P2>): URIWithParameters2<P1, P2> =
        URIWithParameters2(uri + "{${variable.name}}", item1, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the original path parameter and the query parameter.
     */
    operator fun <P2 : Any> plus(parameter: QueryParameter<P2>): URIWithParameters2<P1, P2> =
        URIWithParameters2(uri, item1, parameter)

    override val parameters: Tuple1<Parameter<P1>> =
        Tuple1(item1)

    /**
     * Builds a concrete [URI] after substituting [p1].
     *
     * @param p1 value to substitute for the captured path parameter.
     * @return concrete [URI] with the parameter applied.
     */
    fun toURI(p1: P1): URI =
        toURI(
            uri,
            item1 to item1.codec.encode(p1)
        )
}

/** URI template that captures two parameters. */
data class URIWithParameters2<P1 : Any, P2 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>
) : URIWithParameters,
    AllOf2<Parameter<P1>, Parameter<P2>> {
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters2<P1, P2> = URIWithParameters2(uri + segment, item1, item2)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the original and new path parameters.
     */
    operator fun <P3 : Any> div(variable: PathVariable<P3>): URIWithParameters3<P1, P2, P3> =
        URIWithParameters3(uri + "{${variable.name}}", item1, item2, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the original path parameters and the query parameter.
     */
    operator fun <P3 : Any> plus(parameter: QueryParameter<P3>): URIWithParameters3<P1, P2, P3> =
        URIWithParameters3(uri, item1, item2, parameter)

    override val parameters: Tuple2<Parameter<P1>, Parameter<P2>> =
        Tuple2(item1, item2)

    /**
     * Builds a concrete [URI] after substituting [p1] and [p2].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @return concrete [URI] with both parameters applied.
     */
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

/** URI template that captures three parameters. */
data class URIWithParameters3<P1 : Any, P2 : Any, P3 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>
) : URIWithParameters,
    AllOf3<Parameter<P1>, Parameter<P2>, Parameter<P3>> {
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters3<P1, P2, P3> =
        URIWithParameters3(uri + segment, item1, item2, item3)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the original and new path parameters.
     */
    operator fun <P4 : Any> div(variable: PathVariable<P4>): URIWithParameters4<P1, P2, P3, P4> =
        URIWithParameters4(uri + "{${variable.name}}", item1, item2, item3, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the path parameters and the query parameter.
     */
    operator fun <P4 : Any> plus(parameter: QueryParameter<P4>): URIWithParameters4<P1, P2, P3, P4> =
        URIWithParameters4(uri, item1, item2, item3, parameter)

    override val parameters: Tuple3<Parameter<P1>, Parameter<P2>, Parameter<P3>> =
        Tuple3(item1, item2, item3)

    /**
     * Builds a concrete [URI] after substituting [p1], [p2], and [p3].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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

/** URI template that captures four parameters. */
data class URIWithParameters4<P1 : Any, P2 : Any, P3 : Any, P4 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>
) : URIWithParameters,
    AllOf4<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>> {
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters4<P1, P2, P3, P4> =
        URIWithParameters4(uri + segment, item1, item2, item3, item4)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the existing and new path parameters.
     */
    operator fun <P5 : Any> div(variable: PathVariable<P5>): URIWithParameters5<P1, P2, P3, P4, P5> =
        URIWithParameters5(uri + "{${variable.name}}", item1, item2, item3, item4, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the path parameters and the query parameter.
     */
    operator fun <P5 : Any> plus(parameter: QueryParameter<P5>): URIWithParameters5<P1, P2, P3, P4, P5> =
        URIWithParameters5(uri, item1, item2, item3, item4, parameter)

    override val parameters: Tuple4<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>> =
        Tuple4(item1, item2, item3, item4)

    /**
     * Builds a concrete [URI] after substituting [p1], [p2], [p3], and [p4].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @param p4 value substituting the fourth captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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

/** URI template that captures five parameters. */
data class URIWithParameters5<P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any>(
    override val uri: List<String>,
    override val item1: Parameter<P1>,
    override val item2: Parameter<P2>,
    override val item3: Parameter<P3>,
    override val item4: Parameter<P4>,
    override val item5: Parameter<P5>
) : URIWithParameters,
    AllOf5<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>> {
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters5<P1, P2, P3, P4, P5> =
        URIWithParameters5(uri + segment, item1, item2, item3, item4, item5)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the existing and new path parameters.
     */
    operator fun <P6 : Any> div(variable: PathVariable<P6>): URIWithParameters6<P1, P2, P3, P4, P5, P6> =
        URIWithParameters6(uri + "{${variable.name}}", item1, item2, item3, item4, item5, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the path parameters and the query parameter.
     */
    operator fun <P6 : Any> plus(parameter: QueryParameter<P6>): URIWithParameters6<P1, P2, P3, P4, P5, P6> =
        URIWithParameters6(uri, item1, item2, item3, item4, item5, parameter)

    override val parameters: Tuple5<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>> =
        Tuple5(item1, item2, item3, item4, item5)

    /**
     * Builds a concrete [URI] after substituting [p1] through [p5].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @param p4 value substituting the fourth captured parameter.
     * @param p5 value substituting the fifth captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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

/** URI template that captures six parameters. */
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
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters6<P1, P2, P3, P4, P5, P6> =
        URIWithParameters6(uri + segment, item1, item2, item3, item4, item5, item6)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the existing and new path parameters.
     */
    operator fun <P7 : Any> div(variable: PathVariable<P7>): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> =
        URIWithParameters7(uri + "{${variable.name}}", item1, item2, item3, item4, item5, item6, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the path parameters and the query parameter.
     */
    operator fun <P7 : Any> plus(parameter: QueryParameter<P7>): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> =
        URIWithParameters7(uri, item1, item2, item3, item4, item5, item6, parameter)

    override val parameters:
        Tuple6<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>> =
        Tuple6(item1, item2, item3, item4, item5, item6)

    /**
     * Builds a concrete [URI] after substituting [p1] through [p6].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @param p4 value substituting the fourth captured parameter.
     * @param p5 value substituting the fifth captured parameter.
     * @param p6 value substituting the sixth captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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

/** URI template that captures seven parameters. */
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
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> =
        URIWithParameters7(uri + segment, item1, item2, item3, item4, item5, item6, item7)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the existing and new path parameters.
     */
    operator fun <P8 : Any> div(variable: PathVariable<P8>): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
        URIWithParameters8(uri + "{${variable.name}}", item1, item2, item3, item4, item5, item6, item7, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the path parameters and the query parameter.
     */
    operator fun <P8 : Any> plus(parameter: QueryParameter<P8>): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
        URIWithParameters8(uri, item1, item2, item3, item4, item5, item6, item7, parameter)

    override val parameters:
        Tuple7<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>> =
        Tuple7(item1, item2, item3, item4, item5, item6, item7)

    /**
     * Builds a concrete [URI] after substituting [p1] through [p7].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @param p4 value substituting the fourth captured parameter.
     * @param p5 value substituting the fifth captured parameter.
     * @param p6 value substituting the sixth captured parameter.
     * @param p7 value substituting the seventh captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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

/** URI template that captures eight parameters. */
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
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
        URIWithParameters8(uri + segment, item1, item2, item3, item4, item5, item6, item7, item8)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the existing and new path parameters.
     */
    operator fun <P9 : Any> div(variable: PathVariable<P9>): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
        URIWithParameters9(uri + "{${variable.name}}", item1, item2, item3, item4, item5, item6, item7, item8, variable)

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the path parameters and the query parameter.
     */
    operator fun <P9 : Any> plus(
        parameter: QueryParameter<P9>
    ): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
        URIWithParameters9(uri, item1, item2, item3, item4, item5, item6, item7, item8, parameter)

    override val parameters:
        Tuple8<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>> =
        Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

    /**
     * Builds a concrete [URI] after substituting [p1] through [p8].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @param p4 value substituting the fourth captured parameter.
     * @param p5 value substituting the fifth captured parameter.
     * @param p6 value substituting the sixth captured parameter.
     * @param p7 value substituting the seventh captured parameter.
     * @param p8 value substituting the eighth captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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

/** URI template that captures nine parameters. */
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
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
        URIWithParameters9(uri + segment, item1, item2, item3, item4, item5, item6, item7, item8, item9)

    /**
     * Appends another path [variable] placeholder to the URI.
     *
     * @param variable path parameter to capture.
     * @return URI template capturing the existing and new path parameters.
     */
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

    /**
     * Appends a query [parameter] to the URI.
     *
     * @param parameter query parameter to capture.
     * @return URI template capturing the path parameters and the query parameter.
     */
    operator fun <P10 : Any> plus(
        parameter: QueryParameter<P10>
    ): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
        URIWithParameters10(uri, item1, item2, item3, item4, item5, item6, item7, item8, item9, parameter)

    override val parameters:
        Tuple9<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>> =
        Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    /**
     * Builds a concrete [URI] after substituting [p1] through [p9].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @param p4 value substituting the fourth captured parameter.
     * @param p5 value substituting the fifth captured parameter.
     * @param p6 value substituting the sixth captured parameter.
     * @param p7 value substituting the seventh captured parameter.
     * @param p8 value substituting the eighth captured parameter.
     * @param p9 value substituting the ninth captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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

/** URI template that captures ten parameters. */
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
    /**
     * Appends an additional literal path [segment].
     *
     * @param segment literal segment to append.
     * @return URI template with the segment appended while retaining captured parameters.
     */
    operator fun div(segment: String): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
        URIWithParameters10(uri + segment, item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override val parameters:
        Tuple10<Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>, Parameter<P10>> =
        Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    /**
     * Builds a concrete [URI] after substituting [p1] through [p10].
     *
     * @param p1 value substituting the first captured parameter.
     * @param p2 value substituting the second captured parameter.
     * @param p3 value substituting the third captured parameter.
     * @param p4 value substituting the fourth captured parameter.
     * @param p5 value substituting the fifth captured parameter.
     * @param p6 value substituting the sixth captured parameter.
     * @param p7 value substituting the seventh captured parameter.
     * @param p8 value substituting the eighth captured parameter.
     * @param p9 value substituting the ninth captured parameter.
     * @param p10 value substituting the tenth captured parameter.
     * @return concrete [URI] with all parameters applied.
     */
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
