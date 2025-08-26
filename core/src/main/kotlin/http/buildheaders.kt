@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

internal fun buildHeaders(vararg parameters: Pair<String, String>?): Map<String, List<String>> =
    listOfNotNull(*parameters).groupBy({
        it.first
    }, { it.second })

internal fun <P : Any> buildPairIfHeader(pair: Pair<Parameter<P>, P>): Pair<String, String>? {
    val (parameter, value) = pair
    return parameter.takeIf { it.position == ParameterPosition.Header }?.let { it.name to it.codec.encode(value) }
}

fun HttpEndpoint<Parameters0, *, *>.buildHeaders(): Map<String, List<String>> = buildHeaders()

fun <P1 : Any> HttpEndpoint<Parameters1<P1>, *, *>.buildHeaders(p1: P1): Map<String, List<String>> =
    with(parameters) { buildHeaders(buildPairIfHeader(item1 to p1)) }

fun <P1 : Any, P2 : Any> HttpEndpoint<Parameters2<P1, P2>, *, *>.buildHeaders(
    p1: P1,
    p2: P2
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(
                item2 to p2
            )
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any> HttpEndpoint<Parameters3<P1, P2, P3>, *, *>.buildHeaders(
    p1: P1,
    p2: P2,
    p3: P3
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(
                item2 to p2
            ),
            buildPairIfHeader(item3 to p3)
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> HttpEndpoint<Parameters4<P1, P2, P3, P4>, *, *>.buildHeaders(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(item2 to p2),
            buildPairIfHeader(item3 to p3),
            buildPairIfHeader(
                item4 to p4
            )
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> HttpEndpoint<Parameters5<P1, P2, P3, P4, P5>, *, *>.buildHeaders(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(item2 to p2),
            buildPairIfHeader(item3 to p3),
            buildPairIfHeader(
                item4 to p4
            ),
            buildPairIfHeader(item5 to p5)
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> HttpEndpoint<Parameters6<P1, P2, P3, P4, P5, P6>, *, *>.buildHeaders(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(item2 to p2),
            buildPairIfHeader(item3 to p3),
            buildPairIfHeader(
                item4 to p4
            ),
            buildPairIfHeader(item5 to p5),
            buildPairIfHeader(item6 to p6)
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> HttpEndpoint<Parameters7<P1, P2, P3, P4, P5, P6, P7>, *, *>.buildHeaders(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(item2 to p2),
            buildPairIfHeader(item3 to p3),
            buildPairIfHeader(
                item4 to p4
            ),
            buildPairIfHeader(item5 to p5),
            buildPairIfHeader(item6 to p6),
            buildPairIfHeader(item7 to p7)
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> HttpEndpoint<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>, *, *>.buildHeaders(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(item2 to p2),
            buildPairIfHeader(item3 to p3),
            buildPairIfHeader(
                item4 to p4
            ),
            buildPairIfHeader(item5 to p5),
            buildPairIfHeader(item6 to p6),
            buildPairIfHeader(item7 to p7),
            buildPairIfHeader(
                item8 to p8
            )
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> HttpEndpoint<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>, *, *>.buildHeaders(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(item2 to p2),
            buildPairIfHeader(item3 to p3),
            buildPairIfHeader(
                item4 to p4
            ),
            buildPairIfHeader(item5 to p5),
            buildPairIfHeader(item6 to p6),
            buildPairIfHeader(item7 to p7),
            buildPairIfHeader(
                item8 to p8
            ),
            buildPairIfHeader(item9 to p9)
        )
    }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, *, *>.buildHeaders(
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
): Map<String, List<String>> =
    with(parameters) {
        buildHeaders(
            buildPairIfHeader(item1 to p1),
            buildPairIfHeader(item2 to p2),
            buildPairIfHeader(item3 to p3),
            buildPairIfHeader(
                item4 to p4
            ),
            buildPairIfHeader(item5 to p5),
            buildPairIfHeader(item6 to p6),
            buildPairIfHeader(item7 to p7),
            buildPairIfHeader(
                item8 to p8
            ),
            buildPairIfHeader(item9 to p9),
            buildPairIfHeader(item10 to p10)
        )
    }
