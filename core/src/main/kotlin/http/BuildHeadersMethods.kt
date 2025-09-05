@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

internal fun buildHeaders(vararg parameters: Pair<String, String>): Map<String, List<String>> =
    listOf(*parameters).groupBy({ it.first }) { it.second }

internal fun <H: Any> buildHeaderPair(pair: Pair<Header<H>, H>): Pair<String, String> {
    val (header, value) = pair
    return header.name to header.codec.encode(value)
}

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies> HttpEndpoint<P, Headers0, IB, OH, OB>.buildInputHeaders(): Map<String, List<String>> =
    emptyMap()

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any> HttpEndpoint<P, Headers1<H1>, IB, OH, OB>.buildInputHeaders(
    h1: H1
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any> HttpEndpoint<P, Headers2<H1, H2>, IB, OH, OB>.buildInputHeaders(
    h1: H1,
    h2: H2
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any> HttpEndpoint<P, Headers3<H1, H2, H3>, IB, OH, OB>.buildInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any> HttpEndpoint<P, Headers4<H1, H2, H3, H4>, IB, OH, OB>.buildInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any> HttpEndpoint<P, Headers5<H1, H2, H3, H4, H5>, IB, OH, OB>.buildInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any> HttpEndpoint<P, Headers6<H1, H2, H3, H4, H5, H6>, IB, OH, OB>.buildInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any> HttpEndpoint<P, Headers7<H1, H2, H3, H4, H5, H6, H7>, IB, OH, OB>.buildInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any> HttpEndpoint<P, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, IB, OH, OB>.buildInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8
): Map<String, List<String>> =
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7),
            buildHeaderPair(item8 to h8)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any> HttpEndpoint<P, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, IB, OH, OB>.buildInputHeaders(
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
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7),
            buildHeaderPair(item8 to h8),
            buildHeaderPair(item9 to h9)
        )
    }

fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any, H10: Any> HttpEndpoint<P, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, IB, OH, OB>.buildInputHeaders(
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
    with(inputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7),
            buildHeaderPair(item8 to h8),
            buildHeaderPair(item9 to h9),
            buildHeaderPair(item10 to h10)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies> HttpEndpoint<P, IH, IB, Headers0, OB>.buildOutputHeaders(): Map<String, List<String>> =
    emptyMap()

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any> HttpEndpoint<P, IH, IB, Headers1<H1>, OB>.buildOutputHeaders(
    h1: H1
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any> HttpEndpoint<P, IH, IB, Headers2<H1, H2>, OB>.buildOutputHeaders(
    h1: H1,
    h2: H2
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any> HttpEndpoint<P, IH, IB, Headers3<H1, H2, H3>, OB>.buildOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any> HttpEndpoint<P, IH, IB, Headers4<H1, H2, H3, H4>, OB>.buildOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any> HttpEndpoint<P, IH, IB, Headers5<H1, H2, H3, H4, H5>, OB>.buildOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any> HttpEndpoint<P, IH, IB, Headers6<H1, H2, H3, H4, H5, H6>, OB>.buildOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any> HttpEndpoint<P, IH, IB, Headers7<H1, H2, H3, H4, H5, H6, H7>, OB>.buildOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any> HttpEndpoint<P, IH, IB, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, OB>.buildOutputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7,
    h8: H8
): Map<String, List<String>> =
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7),
            buildHeaderPair(item8 to h8)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any> HttpEndpoint<P, IH, IB, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, OB>.buildOutputHeaders(
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
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7),
            buildHeaderPair(item8 to h8),
            buildHeaderPair(item9 to h9)
        )
    }

fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any, H10: Any> HttpEndpoint<P, IH, IB, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, OB>.buildOutputHeaders(
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
    with(outputHeaders) {
        buildHeaders(
            buildHeaderPair(item1 to h1),
            buildHeaderPair(item2 to h2),
            buildHeaderPair(item3 to h3),
            buildHeaderPair(item4 to h4),
            buildHeaderPair(item5 to h5),
            buildHeaderPair(item6 to h6),
            buildHeaderPair(item7 to h7),
            buildHeaderPair(item8 to h8),
            buildHeaderPair(item9 to h9),
            buildHeaderPair(item10 to h10)
        )
    }
