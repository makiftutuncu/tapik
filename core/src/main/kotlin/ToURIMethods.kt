@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import java.net.URI

/**
 * Represents a URI template as a pair of literal path [List] segments and captured parameters [P].
 */
typealias URIWithParameters<P> = Pair<List<String>, P>

/** URI template that captures no parameters. */
typealias URIWithParameters0 = URIWithParameters<Parameters0>

/** URI template that captures a single [Parameter]. */
typealias URIWithParameters1<P1> = URIWithParameters<Parameters1<P1>>

/** URI template that captures two [Parameter] values. */
typealias URIWithParameters2<P1, P2> = URIWithParameters<Parameters2<P1, P2>>

/** URI template that captures three [Parameter] values. */
typealias URIWithParameters3<P1, P2, P3> = URIWithParameters<Parameters3<P1, P2, P3>>

/** URI template that captures four [Parameter] values. */
typealias URIWithParameters4<P1, P2, P3, P4> = URIWithParameters<Parameters4<P1, P2, P3, P4>>

/** URI template that captures five [Parameter] values. */
typealias URIWithParameters5<P1, P2, P3, P4, P5> = URIWithParameters<Parameters5<P1, P2, P3, P4, P5>>

/** URI template that captures six [Parameter] values. */
typealias URIWithParameters6<P1, P2, P3, P4, P5, P6> = URIWithParameters<Parameters6<P1, P2, P3, P4, P5, P6>>

/** URI template that captures seven [Parameter] values. */
typealias URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> = URIWithParameters<Parameters7<P1, P2, P3, P4, P5, P6, P7>>

/** URI template that captures eight [Parameter] values. */
typealias URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> = URIWithParameters<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>>

/** URI template that captures nine [Parameter] values. */
typealias URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> = URIWithParameters<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>>

/** URI template that captures ten [Parameter] values. */
typealias URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> = URIWithParameters<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>>

/**
 * Renders this URI template to a concrete [URI] without substituting any parameters.
 *
 * @receiver path segments that form the URI template.
 * @return resolved [URI] containing the composed path.
 */
fun URIWithParameters0.toURI(): URI = renderURI(first)

/**
 * Renders this URI template to a concrete [URI] by substituting [p1].
 *
 * @receiver path segments and parameter definition comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any> URIWithParameters1<P1>.toURI(p1: P1): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1] and [p2].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any> URIWithParameters2<P1, P2>.toURI(
    p1: P1,
    p2: P2
): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1], [p2], and [p3].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any> URIWithParameters3<P1, P2, P3>.toURI(
    p1: P1,
    p2: P2,
    p3: P3
): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1], [p2], [p3], and [p4].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> URIWithParameters4<P1, P2, P3, P4>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4
): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3),
        second.item4 to second.item4.codec.encode(p4)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1] through [p5].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> URIWithParameters5<P1, P2, P3, P4, P5>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5
): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3),
        second.item4 to second.item4.codec.encode(p4),
        second.item5 to second.item5.codec.encode(p5)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1] through [p6].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> URIWithParameters6<P1, P2, P3, P4, P5, P6>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6
): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3),
        second.item4 to second.item4.codec.encode(p4),
        second.item5 to second.item5.codec.encode(p5),
        second.item6 to second.item6.codec.encode(p6)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1] through [p7].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> URIWithParameters7<P1, P2, P3, P4, P5, P6, P7>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7
): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3),
        second.item4 to second.item4.codec.encode(p4),
        second.item5 to second.item5.codec.encode(p5),
        second.item6 to second.item6.codec.encode(p6),
        second.item7 to second.item7.codec.encode(p7)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1] through [p8].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @param p8 value encoded for the eighth captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8
): URI =
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3),
        second.item4 to second.item4.codec.encode(p4),
        second.item5 to second.item5.codec.encode(p5),
        second.item6 to second.item6.codec.encode(p6),
        second.item7 to second.item7.codec.encode(p7),
        second.item8 to second.item8.codec.encode(p8)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1] through [p9].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @param p8 value encoded for the eighth captured parameter.
 * @param p9 value encoded for the ninth captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>.toURI(
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
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3),
        second.item4 to second.item4.codec.encode(p4),
        second.item5 to second.item5.codec.encode(p5),
        second.item6 to second.item6.codec.encode(p6),
        second.item7 to second.item7.codec.encode(p7),
        second.item8 to second.item8.codec.encode(p8),
        second.item9 to second.item9.codec.encode(p9)
    )

/**
 * Renders this URI template to a concrete [URI] by substituting [p1] through [p10].
 *
 * @receiver path segments and parameter definitions comprising the template.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @param p8 value encoded for the eighth captured parameter.
 * @param p9 value encoded for the ninth captured parameter.
 * @param p10 value encoded for the tenth captured parameter.
 * @return resolved [URI] containing substituted values.
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>.toURI(
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
    renderURI(
        first,
        second.item1 to second.item1.codec.encode(p1),
        second.item2 to second.item2.codec.encode(p2),
        second.item3 to second.item3.codec.encode(p3),
        second.item4 to second.item4.codec.encode(p4),
        second.item5 to second.item5.codec.encode(p5),
        second.item6 to second.item6.codec.encode(p6),
        second.item7 to second.item7.codec.encode(p7),
        second.item8 to second.item8.codec.encode(p8),
        second.item9 to second.item9.codec.encode(p9),
        second.item10 to second.item10.codec.encode(p10)
    )

/**
 * Resolves this endpoint's path into a concrete [URI] when no parameters are captured.
 *
 * @receiver fully configured endpoint whose template has no captured parameters.
 * @return resolved [URI].
 */
fun HttpEndpoint<Parameters0, *, *>.toURI(): URI = (path to parameters).toURI()

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1].
 *
 * @receiver endpoint capturing a single parameter.
 * @param p1 value encoded for the captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any> HttpEndpoint<Parameters1<P1>, *, *>.toURI(p1: P1): URI = (path to parameters).toURI(p1)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] and [p2].
 *
 * @receiver endpoint capturing two parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any> HttpEndpoint<Parameters2<P1, P2>, *, *>.toURI(
    p1: P1,
    p2: P2
): URI = (path to parameters).toURI(p1, p2)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1], [p2], and [p3].
 *
 * @receiver endpoint capturing three parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any> HttpEndpoint<Parameters3<P1, P2, P3>, *, *>.toURI(
    p1: P1,
    p2: P2,
    p3: P3
): URI = (path to parameters).toURI(p1, p2, p3)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] through [p4].
 *
 * @receiver endpoint capturing four parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> HttpEndpoint<Parameters4<P1, P2, P3, P4>, *, *>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4
): URI = (path to parameters).toURI(p1, p2, p3, p4)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] through [p5].
 *
 * @receiver endpoint capturing five parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> HttpEndpoint<Parameters5<P1, P2, P3, P4, P5>, *, *>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5
): URI = (path to parameters).toURI(p1, p2, p3, p4, p5)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] through [p6].
 *
 * @receiver endpoint capturing six parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> HttpEndpoint<Parameters6<P1, P2, P3, P4, P5, P6>, *, *>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6
): URI = (path to parameters).toURI(p1, p2, p3, p4, p5, p6)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] through [p7].
 *
 * @receiver endpoint capturing seven parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> HttpEndpoint<Parameters7<P1, P2, P3, P4, P5, P6, P7>, *, *>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7
): URI = (path to parameters).toURI(p1, p2, p3, p4, p5, p6, p7)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] through [p8].
 *
 * @receiver endpoint capturing eight parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @param p8 value encoded for the eighth captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> HttpEndpoint<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>, *, *>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8
): URI = (path to parameters).toURI(p1, p2, p3, p4, p5, p6, p7, p8)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] through [p9].
 *
 * @receiver endpoint capturing nine parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @param p8 value encoded for the eighth captured parameter.
 * @param p9 value encoded for the ninth captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> HttpEndpoint<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>, *, *>.toURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9
): URI = (path to parameters).toURI(p1, p2, p3, p4, p5, p6, p7, p8, p9)

/**
 * Resolves this endpoint's path into a concrete [URI] by substituting [p1] through [p10].
 *
 * @receiver endpoint capturing ten parameters.
 * @param p1 value encoded for the first captured parameter.
 * @param p2 value encoded for the second captured parameter.
 * @param p3 value encoded for the third captured parameter.
 * @param p4 value encoded for the fourth captured parameter.
 * @param p5 value encoded for the fifth captured parameter.
 * @param p6 value encoded for the sixth captured parameter.
 * @param p7 value encoded for the seventh captured parameter.
 * @param p8 value encoded for the eighth captured parameter.
 * @param p9 value encoded for the ninth captured parameter.
 * @param p10 value encoded for the tenth captured parameter.
 * @return resolved [URI].
 */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, *, *>.toURI(
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
): URI = (path to parameters).toURI(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)

private fun renderURI(
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
            .fold(segments.joinToString(separator = "/", prefix = "/")) { current, (name, value) ->
                current.replace("{$name}", value)
            }

    val query =
        map[ParameterPosition.Query]
            .orEmpty()
            .joinToString(separator = "&") { (name, value) -> "$name=$value" }
            .takeIf { it.isNotEmpty() }

    return URI.create(listOfNotNull(path, query).joinToString("?"))
}
