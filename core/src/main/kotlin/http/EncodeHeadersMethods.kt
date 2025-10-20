@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

fun HttpEndpoint<*, Headers0, *, *, *>.encodeInputHeaders(): Map<String, List<String>> = emptyMap()

fun <H1 : Any> HttpEndpoint<*, Headers1<H1>, *, *, *>.encodeInputHeaders(h1: H1): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1)
    )

fun <H1 : Any, H2 : Any> HttpEndpoint<*, Headers2<H1, H2>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2)
    )

fun <H1 : Any, H2 : Any, H3 : Any> HttpEndpoint<*, Headers3<H1, H2, H3>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3)
    )

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> HttpEndpoint<*, Headers4<H1, H2, H3, H4>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3),
        encodeHeader(inputHeaders.item4 to h4)
    )

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> HttpEndpoint<*, Headers5<H1, H2, H3, H4, H5>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3),
        encodeHeader(inputHeaders.item4 to h4),
        encodeHeader(inputHeaders.item5 to h5)
    )

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> HttpEndpoint<*, Headers6<H1, H2, H3, H4, H5, H6>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3),
        encodeHeader(inputHeaders.item4 to h4),
        encodeHeader(inputHeaders.item5 to h5),
        encodeHeader(inputHeaders.item6 to h6)
    )

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> HttpEndpoint<*, Headers7<H1, H2, H3, H4, H5, H6, H7>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3),
        encodeHeader(inputHeaders.item4 to h4),
        encodeHeader(inputHeaders.item5 to h5),
        encodeHeader(inputHeaders.item6 to h6),
        encodeHeader(inputHeaders.item7 to h7)
    )

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> HttpEndpoint<*, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3),
        encodeHeader(inputHeaders.item4 to h4),
        encodeHeader(inputHeaders.item5 to h5),
        encodeHeader(inputHeaders.item6 to h6),
        encodeHeader(inputHeaders.item7 to h7),
        encodeHeader(inputHeaders.item8 to h8)
    )

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> HttpEndpoint<*, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8,
    h9: H9
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3),
        encodeHeader(inputHeaders.item4 to h4),
        encodeHeader(inputHeaders.item5 to h5),
        encodeHeader(inputHeaders.item6 to h6),
        encodeHeader(inputHeaders.item7 to h7),
        encodeHeader(inputHeaders.item8 to h8),
        encodeHeader(inputHeaders.item9 to h9)
    )

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> HttpEndpoint<*, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8,
    h9: H9,
    h10: H10
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2),
        encodeHeader(inputHeaders.item3 to h3),
        encodeHeader(inputHeaders.item4 to h4),
        encodeHeader(inputHeaders.item5 to h5),
        encodeHeader(inputHeaders.item6 to h6),
        encodeHeader(inputHeaders.item7 to h7),
        encodeHeader(inputHeaders.item8 to h8),
        encodeHeader(inputHeaders.item9 to h9),
        encodeHeader(inputHeaders.item10 to h10)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies> HttpEndpoint<*, IH, IB, Headers0, OB>.encodeOutputHeaders(): Map<String, List<String>> =
    emptyMap()

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any> HttpEndpoint<*, IH, IB, Headers1<H1>, OB>.encodeOutputHeaders(
    h1: H1
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any> HttpEndpoint<*, IH, IB, Headers2<H1, H2>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any> HttpEndpoint<*, IH, IB, Headers3<H1, H2, H3>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any> HttpEndpoint<*, IH, IB, Headers4<H1, H2, H3, H4>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3),
        encodeHeader(outputHeaders.item4 to h4)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> HttpEndpoint<*, IH, IB, Headers5<H1, H2, H3, H4, H5>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3),
        encodeHeader(outputHeaders.item4 to h4),
        encodeHeader(outputHeaders.item5 to h5)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> HttpEndpoint<*, IH, IB, Headers6<H1, H2, H3, H4, H5, H6>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3),
        encodeHeader(outputHeaders.item4 to h4),
        encodeHeader(outputHeaders.item5 to h5),
        encodeHeader(outputHeaders.item6 to h6)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> HttpEndpoint<*, IH, IB, Headers7<H1, H2, H3, H4, H5, H6, H7>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3),
        encodeHeader(outputHeaders.item4 to h4),
        encodeHeader(outputHeaders.item5 to h5),
        encodeHeader(outputHeaders.item6 to h6),
        encodeHeader(outputHeaders.item7 to h7)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> HttpEndpoint<*, IH, IB, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3),
        encodeHeader(outputHeaders.item4 to h4),
        encodeHeader(outputHeaders.item5 to h5),
        encodeHeader(outputHeaders.item6 to h6),
        encodeHeader(outputHeaders.item7 to h7),
        encodeHeader(outputHeaders.item8 to h8)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> HttpEndpoint<*, IH, IB, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8,
    h9: H9
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3),
        encodeHeader(outputHeaders.item4 to h4),
        encodeHeader(outputHeaders.item5 to h5),
        encodeHeader(outputHeaders.item6 to h6),
        encodeHeader(outputHeaders.item7 to h7),
        encodeHeader(outputHeaders.item8 to h8),
        encodeHeader(outputHeaders.item9 to h9)
    )

fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> HttpEndpoint<*, IH, IB, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8,
    h9: H9,
    h10: H10
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2),
        encodeHeader(outputHeaders.item3 to h3),
        encodeHeader(outputHeaders.item4 to h4),
        encodeHeader(outputHeaders.item5 to h5),
        encodeHeader(outputHeaders.item6 to h6),
        encodeHeader(outputHeaders.item7 to h7),
        encodeHeader(outputHeaders.item8 to h8),
        encodeHeader(outputHeaders.item9 to h9),
        encodeHeader(outputHeaders.item10 to h10)
    )

private fun build(vararg pairs: Pair<String, String>): Map<String, List<String>> =
    listOf(*pairs).groupBy({ it.first }) { it.second }

private fun <H : Any> encodeHeader(pair: Pair<Header<H>, H>): Pair<String, String> {
    val (header, value) = pair
    return header.name to header.codec.encode(value)
}
