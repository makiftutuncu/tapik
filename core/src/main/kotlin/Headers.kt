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
