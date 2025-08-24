package dev.akif.tapik.http

import java.net.URI

fun HttpEndpoint<Parameters0, *, *>.buildURI(): URI =
    buildURI(uri)

fun <P1 : Any> HttpEndpoint<Parameters1<P1>, *, *>.buildURI(p1: P1): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1)) }

fun <P1 : Any, P2 : Any>
        HttpEndpoint<Parameters2<P1, P2>, *, *>.buildURI(p1: P1, p2: P2): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2)) }

fun <P1 : Any, P2 : Any, P3 : Any>
        HttpEndpoint<Parameters3<P1, P2, P3>, *, *>.buildURI(p1: P1, p2: P2, p3: P3): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3)) }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any>
        HttpEndpoint<Parameters4<P1, P2, P3, P4>, *, *>.buildURI(p1: P1, p2: P2, p3: P3, p4: P4): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3), item4 to item4.codec.encode(p4)) }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any>
        HttpEndpoint<Parameters5<P1, P2, P3, P4, P5>, *, *>.buildURI(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3), item4 to item4.codec.encode(p4), item5 to item5.codec.encode(p5)) }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any>
        HttpEndpoint<Parameters6<P1, P2, P3, P4, P5, P6>, *, *>.buildURI(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3), item4 to item4.codec.encode(p4), item5 to item5.codec.encode(p5), item6 to item6.codec.encode(p6)) }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any>
        HttpEndpoint<Parameters7<P1, P2, P3, P4, P5, P6, P7>, *, *>.buildURI(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3), item4 to item4.codec.encode(p4), item5 to item5.codec.encode(p5), item6 to item6.codec.encode(p6), item7 to item7.codec.encode(p7)) }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any>
        HttpEndpoint<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>, *, *>.buildURI(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3), item4 to item4.codec.encode(p4), item5 to item5.codec.encode(p5), item6 to item6.codec.encode(p6), item7 to item7.codec.encode(p7), item8 to item8.codec.encode(p8)) }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any>
        HttpEndpoint<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>, *, *>.buildURI(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3), item4 to item4.codec.encode(p4), item5 to item5.codec.encode(p5), item6 to item6.codec.encode(p6), item7 to item7.codec.encode(p7), item8 to item8.codec.encode(p8), item9 to item9.codec.encode(p9)) }

fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any>
        HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, *, *>.buildURI(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9, p10: P10): URI =
    with(parameters) { buildURI(uri, item1 to item1.codec.encode(p1), item2 to item2.codec.encode(p2), item3 to item3.codec.encode(p3), item4 to item4.codec.encode(p4), item5 to item5.codec.encode(p5), item6 to item6.codec.encode(p6), item7 to item7.codec.encode(p7), item8 to item8.codec.encode(p8), item9 to item9.codec.encode(p9), item10 to item10.codec.encode(p10)) }

private fun buildURI(segments: List<String>, vararg parameters: Pair<Parameter<*>, String>): URI {
    val map = parameters.groupBy({ it.first.position }) { (parameter, value) -> parameter.name to value }

    val path = map[ParameterPosition.Path].orEmpty().fold(segments.joinToString(separator = "/", prefix = "/")) { path, (name, value) ->
        path.replace("{$name}", value)
    }

    val query = map[ParameterPosition.Query].orEmpty().joinToString(separator = "&") { (name, value) -> "$name=$value" }.takeIf { it.isNotEmpty() }

    return URI.create(listOfNotNull(path, query).joinToString("?"))
}
