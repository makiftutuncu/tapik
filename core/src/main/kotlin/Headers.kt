@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Fixed-arity tuple of header definitions.
 *
 * Tapik keeps headers in typed tuples so request and response builders can preserve the type of
 * each header slot while still exposing the whole set as a collection when needed.
 */
typealias Headers = Tuple<Header<*>>

/** Header tuple with no entries. */
typealias Headers0 = Tuple0

/** Header tuple with one entry. */
typealias Headers1<H1> = Tuple1<Header<*>, Header<H1>>

/** Header tuple with two entries. */
typealias Headers2<H1, H2> = Tuple2<Header<*>, Header<H1>, Header<H2>>

/** Header tuple with three entries. */
typealias Headers3<H1, H2, H3> = Tuple3<Header<*>, Header<H1>, Header<H2>, Header<H3>>

/** Header tuple with four entries. */
typealias Headers4<H1, H2, H3, H4> =
    Tuple4<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>>

/** Header tuple with five entries. */
typealias Headers5<H1, H2, H3, H4, H5> =
    Tuple5<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>>

/** Header tuple with six entries. */
typealias Headers6<H1, H2, H3, H4, H5, H6> =
    Tuple6<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>>

/** Header tuple with seven entries. */
typealias Headers7<H1, H2, H3, H4, H5, H6, H7> =
    Tuple7<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>>

/** Header tuple with eight entries. */
typealias Headers8<H1, H2, H3, H4, H5, H6, H7, H8> =
    Tuple8<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>>

/** Header tuple with nine entries. */
typealias Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
    Tuple9<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>>

/** Header tuple with ten entries. */
typealias Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
    Tuple10<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>, Header<H10>>

/** Tuple of fixed header values with no entries. */
typealias HeaderValues0 = Tuple0

/** Tuple of fixed header values with one entry. */
typealias HeaderValues1<H1> = Tuple1<HeaderValues<*>, HeaderValues<H1>>

/** Tuple of fixed header values with two entries. */
typealias HeaderValues2<H1, H2> = Tuple2<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>>

/** Tuple of fixed header values with three entries. */
typealias HeaderValues3<H1, H2, H3> =
    Tuple3<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>>

/** Tuple of fixed header values with four entries. */
typealias HeaderValues4<H1, H2, H3, H4> =
    Tuple4<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>>

/** Tuple of fixed header values with five entries. */
typealias HeaderValues5<H1, H2, H3, H4, H5> =
    Tuple5<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>>

/** Tuple of fixed header values with six entries. */
typealias HeaderValues6<H1, H2, H3, H4, H5, H6> =
    Tuple6<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>>

/** Tuple of fixed header values with seven entries. */
typealias HeaderValues7<H1, H2, H3, H4, H5, H6, H7> =
    Tuple7<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>>

/** Tuple of fixed header values with eight entries. */
typealias HeaderValues8<H1, H2, H3, H4, H5, H6, H7, H8> =
    Tuple8<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>, HeaderValues<H8>>

/** Tuple of fixed header values with nine entries. */
typealias HeaderValues9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
    Tuple9<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>, HeaderValues<H8>, HeaderValues<H9>>

/** Tuple of fixed header values with ten entries. */
typealias HeaderValues10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
    Tuple10<HeaderValues<*>, HeaderValues<H1>, HeaderValues<H2>, HeaderValues<H3>, HeaderValues<H4>, HeaderValues<H5>, HeaderValues<H6>, HeaderValues<H7>, HeaderValues<H8>, HeaderValues<H9>, HeaderValues<H10>>

/** Returns the empty header tuple. */
fun emptyHeaders(): Headers0 = Headers0

/** Creates a header tuple with one definition. */
fun <H1 : Any> headersOf(header1: Header<H1>): Headers1<H1> = Tuple1(header1)

/** Creates a header tuple with two definitions. */
fun <H1 : Any, H2 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>
): Headers2<H1, H2> = Tuple2(header1, header2)

/** Creates a header tuple with three definitions. */
fun <H1 : Any, H2 : Any, H3 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>
): Headers3<H1, H2, H3> = Tuple3(header1, header2, header3)

/** Creates a header tuple with four definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>
): Headers4<H1, H2, H3, H4> = Tuple4(header1, header2, header3, header4)

/** Creates a header tuple with five definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>
): Headers5<H1, H2, H3, H4, H5> = Tuple5(header1, header2, header3, header4, header5)

/** Creates a header tuple with six definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>
): Headers6<H1, H2, H3, H4, H5, H6> = Tuple6(header1, header2, header3, header4, header5, header6)

/** Creates a header tuple with seven definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> headersOf(
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>
): Headers7<H1, H2, H3, H4, H5, H6, H7> = Tuple7(header1, header2, header3, header4, header5, header6, header7)

/** Creates a header tuple with eight definitions. */
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

/** Creates a header tuple with nine definitions. */
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

/** Creates a header tuple with ten definitions. */
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

/** Returns the empty tuple of fixed header values. */
fun emptyHeaderValues(): HeaderValues0 = HeaderValues0

/** Creates a tuple containing one fixed header value definition. */
fun <H1 : Any> headerValuesOf(headerValues1: HeaderValues<H1>): HeaderValues1<H1> = Tuple1(headerValues1)

/** Creates a tuple containing two fixed header value definitions. */
fun <H1 : Any, H2 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>
): HeaderValues2<H1, H2> = Tuple2(headerValues1, headerValues2)

/** Creates a tuple containing three fixed header value definitions. */
fun <H1 : Any, H2 : Any, H3 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>
): HeaderValues3<H1, H2, H3> = Tuple3(headerValues1, headerValues2, headerValues3)

/** Creates a tuple containing four fixed header value definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>
): HeaderValues4<H1, H2, H3, H4> = Tuple4(headerValues1, headerValues2, headerValues3, headerValues4)

/** Creates a tuple containing five fixed header value definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> headerValuesOf(
    headerValues1: HeaderValues<H1>,
    headerValues2: HeaderValues<H2>,
    headerValues3: HeaderValues<H3>,
    headerValues4: HeaderValues<H4>,
    headerValues5: HeaderValues<H5>
): HeaderValues5<H1, H2, H3, H4, H5> = Tuple5(headerValues1, headerValues2, headerValues3, headerValues4, headerValues5)

/** Creates a tuple containing six fixed header value definitions. */
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

/** Creates a tuple containing seven fixed header value definitions. */
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

/** Creates a tuple containing eight fixed header value definitions. */
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

/** Creates a tuple containing nine fixed header value definitions. */
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

/** Creates a tuple containing ten fixed header value definitions. */
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
