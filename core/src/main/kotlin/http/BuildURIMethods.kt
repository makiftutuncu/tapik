// This file is auto-generated. Do not edit manually as your changes will be lost.
@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import java.net.URI

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies> HttpEndpoint<Parameters0, IH, IB, OH, OB>.buildURI(): URI =
    build(uri)

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any> HttpEndpoint<Parameters1<P1>, IH, IB, OH, OB>.buildURI(
    p1: P1
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any> HttpEndpoint<Parameters2<P1, P2>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any> HttpEndpoint<Parameters3<P1, P2, P3>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any, P4 : Any> HttpEndpoint<Parameters4<P1, P2, P3, P4>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3),
        parameters.item4 to parameters.item4.codec.encode(p4)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> HttpEndpoint<Parameters5<P1, P2, P3, P4, P5>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3),
        parameters.item4 to parameters.item4.codec.encode(p4),
        parameters.item5 to parameters.item5.codec.encode(p5)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> HttpEndpoint<Parameters6<P1, P2, P3, P4, P5, P6>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3),
        parameters.item4 to parameters.item4.codec.encode(p4),
        parameters.item5 to parameters.item5.codec.encode(p5),
        parameters.item6 to parameters.item6.codec.encode(p6)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> HttpEndpoint<Parameters7<P1, P2, P3, P4, P5, P6, P7>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3),
        parameters.item4 to parameters.item4.codec.encode(p4),
        parameters.item5 to parameters.item5.codec.encode(p5),
        parameters.item6 to parameters.item6.codec.encode(p6),
        parameters.item7 to parameters.item7.codec.encode(p7)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> HttpEndpoint<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3),
        parameters.item4 to parameters.item4.codec.encode(p4),
        parameters.item5 to parameters.item5.codec.encode(p5),
        parameters.item6 to parameters.item6.codec.encode(p6),
        parameters.item7 to parameters.item7.codec.encode(p7),
        parameters.item8 to parameters.item8.codec.encode(p8)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> HttpEndpoint<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>, IH, IB, OH, OB>.buildURI(
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
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3),
        parameters.item4 to parameters.item4.codec.encode(p4),
        parameters.item5 to parameters.item5.codec.encode(p5),
        parameters.item6 to parameters.item6.codec.encode(p6),
        parameters.item7 to parameters.item7.codec.encode(p7),
        parameters.item8 to parameters.item8.codec.encode(p8),
        parameters.item9 to parameters.item9.codec.encode(p9)
    )

fun <IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies, P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, IH, IB, OH, OB>.buildURI(
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
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2),
        parameters.item3 to parameters.item3.codec.encode(p3),
        parameters.item4 to parameters.item4.codec.encode(p4),
        parameters.item5 to parameters.item5.codec.encode(p5),
        parameters.item6 to parameters.item6.codec.encode(p6),
        parameters.item7 to parameters.item7.codec.encode(p7),
        parameters.item8 to parameters.item8.codec.encode(p8),
        parameters.item9 to parameters.item9.codec.encode(p9),
        parameters.item10 to parameters.item10.codec.encode(p10)
    )

private fun build(
    segments: List<String>,
    vararg parameters: Pair<Parameter<*>, String>
): URI {
    val map = parameters.groupBy({ it.first.position }) { (parameter, value) -> parameter.name to value }

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
