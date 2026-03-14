@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/** Returns an empty map when the request does not expect headers. */
fun Input<Headers0, *>.encodeInputHeaders(): Map<String, List<String>> = emptyMap()

/**
 * Encodes input headers for an endpoint expecting one header definition.
 *
 * @param h1 value for the first header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
fun <H1 : Any> Input<Headers1<H1>, *>.encodeInputHeaders(h1: H1): Map<String, List<String>> =
    build(
        encodeHeader(headers.item1 to h1)
    )

/**
 * Encodes input headers for an endpoint expecting 2 header definitions.
 *
 * @param h1 value for the first header.
 * @param h2 value for the second header.
 * @return map of header names to their encoded values.
 * @see encodeHeader
 */
fun <H1 : Any, H2 : Any> Input<Headers2<H1, H2>, *>.encodeInputHeaders(
    h1: H1,
    h2: H2
): Map<String, List<String>> =
    build(
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2)
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
fun <H1 : Any, H2 : Any, H3 : Any> Input<Headers3<H1, H2, H3>, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3
): Map<String, List<String>> =
    build(
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3)
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
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> Input<Headers4<H1, H2, H3, H4>, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4
): Map<String, List<String>> =
    build(
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3),
        encodeHeader(headers.item4 to h4)
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
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> Input<Headers5<H1, H2, H3, H4, H5>, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5
): Map<String, List<String>> =
    build(
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3),
        encodeHeader(headers.item4 to h4),
        encodeHeader(headers.item5 to h5)
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
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> Input<Headers6<H1, H2, H3, H4, H5, H6>, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6
): Map<String, List<String>> =
    build(
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3),
        encodeHeader(headers.item4 to h4),
        encodeHeader(headers.item5 to h5),
        encodeHeader(headers.item6 to h6)
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
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> Input<Headers7<H1, H2, H3, H4, H5, H6, H7>, *>.encodeInputHeaders(
    h1: H1,
    h2: H2,
    h3: H3,
    h4: H4,
    h5: H5,
    h6: H6,
    h7: H7
): Map<String, List<String>> =
    build(
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3),
        encodeHeader(headers.item4 to h4),
        encodeHeader(headers.item5 to h5),
        encodeHeader(headers.item6 to h6),
        encodeHeader(headers.item7 to h7)
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
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> Input<Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, *>.encodeInputHeaders(
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
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3),
        encodeHeader(headers.item4 to h4),
        encodeHeader(headers.item5 to h5),
        encodeHeader(headers.item6 to h6),
        encodeHeader(headers.item7 to h7),
        encodeHeader(headers.item8 to h8)
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
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> Input<Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, *>.encodeInputHeaders(
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
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3),
        encodeHeader(headers.item4 to h4),
        encodeHeader(headers.item5 to h5),
        encodeHeader(headers.item6 to h6),
        encodeHeader(headers.item7 to h7),
        encodeHeader(headers.item8 to h8),
        encodeHeader(headers.item9 to h9)
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
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> Input<Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, *>.encodeInputHeaders(
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
        encodeHeader(headers.item1 to h1),
        encodeHeader(headers.item2 to h2),
        encodeHeader(headers.item3 to h3),
        encodeHeader(headers.item4 to h4),
        encodeHeader(headers.item5 to h5),
        encodeHeader(headers.item6 to h6),
        encodeHeader(headers.item7 to h7),
        encodeHeader(headers.item8 to h8),
        encodeHeader(headers.item9 to h9),
        encodeHeader(headers.item10 to h10)
    )

/** Groups encoded header name/value [pairs] by header name to match HTTP expectations. */
private fun build(vararg pairs: Pair<String, String>): Map<String, List<String>> =
    listOf(*pairs).groupBy({ it.first }) { it.second }

/** Serialises a single header [pair] using the configured codec. */
private fun <H : Any> encodeHeader(pair: Pair<Header<H>, H>): Pair<String, String> {
    val (header, value) = pair
    return header.name to header.codec.encode(value)
}
