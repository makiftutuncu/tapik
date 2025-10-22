@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

/** Returns an empty map when the endpoint does not expect input headers. */
fun HttpEndpoint<*, Headers0, *, *, *>.encodeInputHeaders(): Map<String, List<String>> = emptyMap()

/**
 * Encodes input headers for an endpoint expecting one header definition.
 *
 * @param h1 value for the first header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
fun <H1 : Any> HttpEndpoint<*, Headers1<H1>, *, *, *>.encodeInputHeaders(h1: H1): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1)
    )

/**
 * Encodes input headers for an endpoint expecting 2 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
fun <H1 : Any, H2 : Any> HttpEndpoint<*, Headers2<H1, H2>, *, *, *>.encodeInputHeaders(
    h1: H1,
    h2: H2
): Map<String, List<String>> =
    build(
        encodeHeader(inputHeaders.item1 to h1),
        encodeHeader(inputHeaders.item2 to h2)
    )

/**
 * Encodes input headers for an endpoint expecting 3 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes input headers for an endpoint expecting 4 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes input headers for an endpoint expecting 5 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes input headers for an endpoint expecting 6 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes input headers for an endpoint expecting 7 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes input headers for an endpoint expecting 8 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @param h8 value for the 8th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes input headers for an endpoint expecting 9 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @param h8 value for the 8th header.
 * @param h9 value for the 9th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes input headers for an endpoint expecting 10 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @param h8 value for the 8th header.
 * @param h9 value for the 9th header.
 * @param h10 value for the 10th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/** Returns an empty map when the endpoint does not emit output headers. */
fun <IH : Headers, IB : Body<*>, OB : OutputBodies> HttpEndpoint<*, IH, IB, Headers0, OB>.encodeOutputHeaders(): Map<String, List<String>> =
    emptyMap()

/**
 * Encodes output headers for an endpoint producing one header definition.
 *
 * @param h1 value for the first header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any> HttpEndpoint<*, IH, IB, Headers1<H1>, OB>.encodeOutputHeaders(
    h1: H1
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1)
    )

/**
 * Encodes output headers for an endpoint producing 2 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
fun <IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any> HttpEndpoint<*, IH, IB, Headers2<H1, H2>, OB>.encodeOutputHeaders(
    h1: H1,
    h2: H2
): Map<String, List<String>> =
    build(
        encodeHeader(outputHeaders.item1 to h1),
        encodeHeader(outputHeaders.item2 to h2)
    )

/**
 * Encodes output headers for an endpoint producing 3 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes output headers for an endpoint producing 4 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes output headers for an endpoint producing 5 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes output headers for an endpoint producing 6 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes output headers for an endpoint producing 7 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes output headers for an endpoint producing 8 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @param h8 value for the 8th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes output headers for an endpoint producing 9 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @param h8 value for the 8th header.
 * @param h9 value for the 9th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/**
 * Encodes output headers for an endpoint producing 10 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @param h3 value for the third header.
 * @param h4 value for the 4th header.
 * @param h5 value for the 5th header.
 * @param h6 value for the 6th header.
 * @param h7 value for the 7th header.
 * @param h8 value for the 8th header.
 * @param h9 value for the 9th header.
 * @param h10 value for the 10th header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
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

/** Groups encoded header name/value [pairs] by header name to match HTTP expectations. */
private fun build(vararg pairs: Pair<String, String>): Map<String, List<String>> =
    listOf(*pairs).groupBy({ it.first }) { it.second }

/** Serialises a single header [pair] using the configured codec. */
private fun <H : Any> encodeHeader(pair: Pair<Header<H>, H>): Pair<String, String> {
    val (header, value) = pair
    return header.name to header.codec.encode(value)
}
