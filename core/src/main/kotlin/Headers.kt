@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Heterogeneous tuple describing header definitions.
 *
 * Construct instances via `Headers(...)`.
 */
typealias Headers = Tuple<Header<*>>

/** Empty header tuple. */
typealias Headers0 = Tuple0

/** Header tuple with one element. */
typealias Headers1<H1> = Tuple1<Header<*>, Header<H1>>

/** Header tuple with two elements. */
typealias Headers2<H1, H2> = Tuple2<Header<*>, Header<H1>, Header<H2>>

/** Header tuple with three elements. */
typealias Headers3<H1, H2, H3> = Tuple3<Header<*>, Header<H1>, Header<H2>, Header<H3>>

/** Header tuple with four elements. */
typealias Headers4<H1, H2, H3, H4> =
    Tuple4<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>>

/** Header tuple with five elements. */
typealias Headers5<H1, H2, H3, H4, H5> =
    Tuple5<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>>

/** Header tuple with six elements. */
typealias Headers6<H1, H2, H3, H4, H5, H6> =
    Tuple6<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>>

/** Header tuple with seven elements. */
typealias Headers7<H1, H2, H3, H4, H5, H6, H7> =
    Tuple7<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>>

/** Header tuple with eight elements. */
typealias Headers8<H1, H2, H3, H4, H5, H6, H7, H8> =
    Tuple8<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>>

/** Header tuple with nine elements. */
typealias Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
    Tuple9<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>>

/** Header tuple with ten elements. */
typealias Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
    Tuple10<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>, Header<H10>>

/** Decoded header tuple with no values. */
typealias HeaderValues0 = Tuple0

/** Decoded header tuple with one [HeaderValues] instance. */
typealias HeaderValues1<H1> = Tuple1<HeaderValues<*>, HeaderValues<H1>>

/** Decoded header tuple with two [HeaderValues] instances. */
typealias HeaderValues2<H1, H2> = Tuple2<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>>

/** Decoded header tuple with three [HeaderValues] instances. */
typealias HeaderValues3<H1, H2, H3> =
    Tuple3<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>>

/** Decoded header tuple with four [HeaderValues] instances. */
typealias HeaderValues4<H1, H2, H3, H4> =
    Tuple4<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>>

/** Decoded header tuple with five [HeaderValues] instances. */
typealias HeaderValues5<H1, H2, H3, H4, H5> =
    Tuple5<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>>

/** Decoded header tuple with six [HeaderValues] instances. */
typealias HeaderValues6<H1, H2, H3, H4, H5, H6> =
    Tuple6<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>>

/** Decoded header tuple with seven [HeaderValues] instances. */
typealias HeaderValues7<H1, H2, H3, H4, H5, H6, H7> =
    Tuple7<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>>

/** Decoded header tuple with eight [HeaderValues] instances. */
typealias HeaderValues8<H1, H2, H3, H4, H5, H6, H7, H8> =
    Tuple8<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>, HeaderValues<H8>>

/** Decoded header tuple with nine [HeaderValues] instances. */
typealias HeaderValues9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
    Tuple9<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>, HeaderValues<H8>, HeaderValues<H9>>

/** Decoded header tuple with ten [HeaderValues] instances. */
typealias HeaderValues10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
    Tuple10<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>, HeaderValues<H8>, HeaderValues<H9>, HeaderValues<H10>>

/**
 * Returns an empty header tuple.
 *
 * @return an empty [Headers0].
 */
fun emptyHeaders(): Headers0 = Headers0

/**
 * Returns a header tuple with one header.
 *
 * @param H1 type of the first header.
 * @param header1 the first header.
 * @return a [Headers1] containing [header1].
 */
fun <H1 : Any> headersOf(header1: Header<H1>): Headers1<H1> = Tuple1(header1)

/**
 * Returns a header tuple with two headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @return a [Headers2] containing [header1] and [header2].
 */
fun <H1 : Any, H2 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>
): Headers2<H1, H2> = Tuple2(header1, header2)

/**
 * Returns a header tuple with three headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @return a [Headers3] containing [header1], [header2], and [header3].
 */
fun <H1 : Any, H2 : Any, H3 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>
): Headers3<H1, H2, H3> = Tuple3(header1, header2, header3)

/**
 * Returns a header tuple with four headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param H4 type of the fourth header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @param header4 the fourth header.
 * @return a [Headers4] containing [header1] through [header4].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>
): Headers4<H1, H2, H3, H4> = Tuple4(header1, header2, header3, header4)

/**
 * Returns a header tuple with five headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param H4 type of the fourth header.
 * @param H5 type of the fifth header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @param header4 the fourth header.
 * @param header5 the fifth header.
 * @return a [Headers5] containing [header1] through [header5].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>
): Headers5<H1, H2, H3, H4, H5> = Tuple5(header1, header2, header3, header4, header5)

/**
 * Returns a header tuple with six headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param H4 type of the fourth header.
 * @param H5 type of the fifth header.
 * @param H6 type of the sixth header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @param header4 the fourth header.
 * @param header5 the fifth header.
 * @param header6 the sixth header.
 * @return a [Headers6] containing [header1] through [header6].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>
): Headers6<H1, H2, H3, H4, H5, H6> = Tuple6(header1, header2, header3, header4, header5, header6)

/**
 * Returns a header tuple with seven headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param H4 type of the fourth header.
 * @param H5 type of the fifth header.
 * @param H6 type of the sixth header.
 * @param H7 type of the seventh header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @param header4 the fourth header.
 * @param header5 the fifth header.
 * @param header6 the sixth header.
 * @param header7 the seventh header.
 * @return a [Headers7] containing [header1] through [header7].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>
): Headers7<H1, H2, H3, H4, H5, H6, H7> = Tuple7(header1, header2, header3, header4, header5, header6, header7)

/**
 * Returns a header tuple with eight headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param H4 type of the fourth header.
 * @param H5 type of the fifth header.
 * @param H6 type of the sixth header.
 * @param H7 type of the seventh header.
 * @param H8 type of the eighth header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @param header4 the fourth header.
 * @param header5 the fifth header.
 * @param header6 the sixth header.
 * @param header7 the seventh header.
 * @param header8 the eighth header.
 * @return a [Headers8] containing [header1] through [header8].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>
): Headers8<H1, H2, H3, H4, H5, H6, H7, H8> =
    Tuple8(header1, header2, header3, header4, header5, header6, header7, header8)

/**
 * Returns a header tuple with nine headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param H4 type of the fourth header.
 * @param H5 type of the fifth header.
 * @param H6 type of the sixth header.
 * @param H7 type of the seventh header.
 * @param H8 type of the eighth header.
 * @param H9 type of the ninth header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @param header4 the fourth header.
 * @param header5 the fifth header.
 * @param header6 the sixth header.
 * @param header7 the seventh header.
 * @param header8 the eighth header.
 * @param header9 the ninth header.
 * @return a [Headers9] containing [header1] through [header9].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>
): Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
    Tuple9(header1, header2, header3, header4, header5, header6, header7, header8, header9)

/**
 * Returns a header tuple with ten headers.
 *
 * @param H1 type of the first header.
 * @param H2 type of the second header.
 * @param H3 type of the third header.
 * @param H4 type of the fourth header.
 * @param H5 type of the fifth header.
 * @param H6 type of the sixth header.
 * @param H7 type of the seventh header.
 * @param H8 type of the eighth header.
 * @param H9 type of the ninth header.
 * @param H10 type of the tenth header.
 * @param header1 the first header.
 * @param header2 the second header.
 * @param header3 the third header.
 * @param header4 the fourth header.
 * @param header5 the fifth header.
 * @param header6 the sixth header.
 * @param header7 the seventh header.
 * @param header8 the eighth header.
 * @param header9 the ninth header.
 * @param header10 the tenth header.
 * @return a [Headers10] containing [header1] through [header10].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>
): Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
    Tuple10(header1, header2, header3, header4, header5, header6, header7, header8, header9, header10)

/**
 * Returns an empty header values tuple.
 *
 * @return an empty [HeaderValues0].
 */
fun emptyHeaderValues(): HeaderValues0 = HeaderValues0

/**
 * Returns a header values tuple with one value.
 *
 * @param H1 type of the first header value.
 * @param headerValues1 the first header value.
 * @return a [HeaderValues1] containing [headerValues1].
 */
fun <H1 : Any> headerValuesOf(headerValues1: HeaderValues<H1>): HeaderValues1<H1> = Tuple1(headerValues1)

/**
 * Returns a header values tuple with two values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @return a [HeaderValues2] containing [headerValues1] and [headerValues2].
 */
fun <H1 : Any, H2 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>
): HeaderValues2<H1, H2> = Tuple2(headerValues1, headerValues2)

/**
 * Returns a header values tuple with three values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @return a [HeaderValues3] containing [headerValues1], [headerValues2], and [headerValues3].
 */
fun <H1 : Any, H2 : Any, H3 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>
): HeaderValues3<H1, H2, H3> = Tuple3(headerValues1, headerValues2, headerValues3)

/**
 * Returns a header values tuple with four values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param H4 type of the fourth header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @param headerValues4 the fourth header value.
 * @return a [HeaderValues4] containing [headerValues1] through [headerValues4].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>
): HeaderValues4<H1, H2, H3, H4> = Tuple4(headerValues1, headerValues2, headerValues3, headerValues4)

/**
 * Returns a header values tuple with five values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param H4 type of the fourth header value.
 * @param H5 type of the fifth header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @param headerValues4 the fourth header value.
 * @param headerValues5 the fifth header value.
 * @return a [HeaderValues5] containing [headerValues1] through [headerValues5].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>,
    headerValues5: HeaderValues<H5>
): HeaderValues5<H1, H2, H3, H4, H5> = Tuple5(headerValues1, headerValues2, headerValues3, headerValues4, headerValues5)

/**
 * Returns a header values tuple with six values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param H4 type of the fourth header value.
 * @param H5 type of the fifth header value.
 * @param H6 type of the sixth header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @param headerValues4 the fourth header value.
 * @param headerValues5 the fifth header value.
 * @param headerValues6 the sixth header value.
 * @return a [HeaderValues6] containing [headerValues1] through [headerValues6].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>,
    headerValues5: HeaderValues<H5>,
    headerValues6: HeaderValues<H6>
): HeaderValues6<H1, H2, H3, H4, H5, H6> =
    Tuple6(
        headerValues1,
        headerValues2,
        headerValues3,
        headerValues4,
        headerValues5,
        headerValues6
    )

/**
 * Returns a header values tuple with seven values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param H4 type of the fourth header value.
 * @param H5 type of the fifth header value.
 * @param H6 type of the sixth header value.
 * @param H7 type of the seventh header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @param headerValues4 the fourth header value.
 * @param headerValues5 the fifth header value.
 * @param headerValues6 the sixth header value.
 * @param headerValues7 the seventh header value.
 * @return a [HeaderValues7] containing [headerValues1] through [headerValues7].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>,
    headerValues5: HeaderValues<H5>,
    headerValues6: HeaderValues<H6>,
    headerValues7: HeaderValues<H7>
): HeaderValues7<H1, H2, H3, H4, H5, H6, H7> =
    Tuple7(
        headerValues1,
        headerValues2,
        headerValues3,
        headerValues4,
        headerValues5,
        headerValues6,
        headerValues7
    )

/**
 * Returns a header values tuple with eight values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param H4 type of the fourth header value.
 * @param H5 type of the fifth header value.
 * @param H6 type of the sixth header value.
 * @param H7 type of the seventh header value.
 * @param H8 type of the eighth header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @param headerValues4 the fourth header value.
 * @param headerValues5 the fifth header value.
 * @param headerValues6 the sixth header value.
 * @param headerValues7 the seventh header value.
 * @param headerValues8 the eighth header value.
 * @return a [HeaderValues8] containing [headerValues1] through [headerValues8].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>,
    headerValues5: HeaderValues<H5>,
    headerValues6: HeaderValues<H6>,
    headerValues7: HeaderValues<H7>,
    headerValues8: HeaderValues<H8>
): HeaderValues8<H1, H2, H3, H4, H5, H6, H7, H8> =
    Tuple8(
        headerValues1,
        headerValues2,
        headerValues3,
        headerValues4,
        headerValues5,
        headerValues6,
        headerValues7,
        headerValues8
    )

/**
 * Returns a header values tuple with nine values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param H4 type of the fourth header value.
 * @param H5 type of the fifth header value.
 * @param H6 type of the sixth header value.
 * @param H7 type of the seventh header value.
 * @param H8 type of the eighth header value.
 * @param H9 type of the ninth header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @param headerValues4 the fourth header value.
 * @param headerValues5 the fifth header value.
 * @param headerValues6 the sixth header value.
 * @param headerValues7 the seventh header value.
 * @param headerValues8 the eighth header value.
 * @param headerValues9 the ninth header value.
 * @return a [HeaderValues9] containing [headerValues1] through [headerValues9].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>,
    headerValues5: HeaderValues<H5>,
    headerValues6: HeaderValues<H6>,
    headerValues7: HeaderValues<H7>,
    headerValues8: HeaderValues<H8>,
    headerValues9: HeaderValues<H9>
): HeaderValues9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
    Tuple9(
        headerValues1,
        headerValues2,
        headerValues3,
        headerValues4,
        headerValues5,
        headerValues6,
        headerValues7,
        headerValues8,
        headerValues9
    )

/**
 * Returns a header values tuple with ten values.
 *
 * @param H1 type of the first header value.
 * @param H2 type of the second header value.
 * @param H3 type of the third header value.
 * @param H4 type of the fourth header value.
 * @param H5 type of the fifth header value.
 * @param H6 type of the sixth header value.
 * @param H7 type of the seventh header value.
 * @param H8 type of the eighth header value.
 * @param H9 type of the ninth header value.
 * @param H10 type of the tenth header value.
 * @param headerValues1 the first header value.
 * @param headerValues2 the second header value.
 * @param headerValues3 the third header value.
 * @param headerValues4 the fourth header value.
 * @param headerValues5 the fifth header value.
 * @param headerValues6 the sixth header value.
 * @param headerValues7 the seventh header value.
 * @param headerValues8 the eighth header value.
 * @param headerValues9 the ninth header value.
 * @param headerValues10 the tenth header value.
 * @return a [HeaderValues10] containing [headerValues1] through [headerValues10].
 */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>,
    headerValues5: HeaderValues<H5>,
    headerValues6: HeaderValues<H6>,
    headerValues7: HeaderValues<H7>,
    headerValues8: HeaderValues<H8>,
    headerValues9: HeaderValues<H9>,
    headerValues10: HeaderValues<H10>
): HeaderValues10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
    Tuple10(
        headerValues1,
        headerValues2,
        headerValues3,
        headerValues4,
        headerValues5,
        headerValues6,
        headerValues7,
        headerValues8,
        headerValues9,
        headerValues10
    )
