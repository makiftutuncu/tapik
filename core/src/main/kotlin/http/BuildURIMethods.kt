// This file is auto-generated. Do not edit manually as your changes will be lost.
package dev.akif.tapik.http

import java.net.URI

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies> HttpEndpoint<Parameters0, IH, IB, OH, OB>.buildURI(): URI =
    build(uri)

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any> HttpEndpoint<Parameters1<P1>, IH, IB, OH, OB>.buildURI(
    p1: P1
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1)
    )

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any> HttpEndpoint<Parameters2<P1, P2>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2
): URI =
    build(
        uri,
        parameters.item1 to parameters.item1.codec.encode(p1),
        parameters.item2 to parameters.item2.codec.encode(p2)
    )

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any> HttpEndpoint<Parameters3<P1, P2, P3>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any> HttpEndpoint<Parameters4<P1, P2, P3, P4>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any> HttpEndpoint<Parameters5<P1, P2, P3, P4, P5>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any> HttpEndpoint<Parameters6<P1, P2, P3, P4, P5, P6>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any> HttpEndpoint<Parameters7<P1, P2, P3, P4, P5, P6, P7>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any> HttpEndpoint<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any> HttpEndpoint<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, IH, IB, OH, OB>.buildURI(
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

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, P11: Any> HttpEndpoint<Parameters11<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11
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
        parameters.item10 to parameters.item10.codec.encode(p10),
        parameters.item11 to parameters.item11.codec.encode(p11)
    )

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, P11: Any, P12: Any> HttpEndpoint<Parameters12<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12
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
        parameters.item10 to parameters.item10.codec.encode(p10),
        parameters.item11 to parameters.item11.codec.encode(p11),
        parameters.item12 to parameters.item12.codec.encode(p12)
    )

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, P11: Any, P12: Any, P13: Any> HttpEndpoint<Parameters13<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13
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
        parameters.item10 to parameters.item10.codec.encode(p10),
        parameters.item11 to parameters.item11.codec.encode(p11),
        parameters.item12 to parameters.item12.codec.encode(p12),
        parameters.item13 to parameters.item13.codec.encode(p13)
    )

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, P11: Any, P12: Any, P13: Any, P14: Any> HttpEndpoint<Parameters14<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14
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
        parameters.item10 to parameters.item10.codec.encode(p10),
        parameters.item11 to parameters.item11.codec.encode(p11),
        parameters.item12 to parameters.item12.codec.encode(p12),
        parameters.item13 to parameters.item13.codec.encode(p13),
        parameters.item14 to parameters.item14.codec.encode(p14)
    )

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, P11: Any, P12: Any, P13: Any, P14: Any, P15: Any> HttpEndpoint<Parameters15<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15
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
        parameters.item10 to parameters.item10.codec.encode(p10),
        parameters.item11 to parameters.item11.codec.encode(p11),
        parameters.item12 to parameters.item12.codec.encode(p12),
        parameters.item13 to parameters.item13.codec.encode(p13),
        parameters.item14 to parameters.item14.codec.encode(p14),
        parameters.item15 to parameters.item15.codec.encode(p15)
    )

fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, P11: Any, P12: Any, P13: Any, P14: Any, P15: Any, P16: Any> HttpEndpoint<Parameters16<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16>, IH, IB, OH, OB>.buildURI(
    p1: P1,
    p2: P2,
    p3: P3,
    p4: P4,
    p5: P5,
    p6: P6,
    p7: P7,
    p8: P8,
    p9: P9,
    p10: P10,
    p11: P11,
    p12: P12,
    p13: P13,
    p14: P14,
    p15: P15,
    p16: P16
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
        parameters.item10 to parameters.item10.codec.encode(p10),
        parameters.item11 to parameters.item11.codec.encode(p11),
        parameters.item12 to parameters.item12.codec.encode(p12),
        parameters.item13 to parameters.item13.codec.encode(p13),
        parameters.item14 to parameters.item14.codec.encode(p14),
        parameters.item15 to parameters.item15.codec.encode(p15),
        parameters.item16 to parameters.item16.codec.encode(p16)
    )

private fun build(segments: List<String>, vararg parameters: Pair<Parameter<*>, String>): URI {
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