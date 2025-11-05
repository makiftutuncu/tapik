@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Heterogeneous tuple describing parameter definitions.
 *
 * Construct instances via `Parameters(...)` when convenient.
 */
typealias Parameters = Tuple<Parameter<*>>

/** Zero-arity tuple. */
typealias Parameters0 = Tuple0

/** Parameters containing a single definition. */
typealias Parameters1<P1> = Tuple1<Parameter<*>, Parameter<P1>>

/** Parameters containing two definitions. */
typealias Parameters2<P1, P2> = Tuple2<Parameter<*>, Parameter<P1>, Parameter<P2>>

/** Parameters containing three definitions. */
typealias Parameters3<P1, P2, P3> =
    Tuple3<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>>

/** Parameters containing four definitions. */
typealias Parameters4<P1, P2, P3, P4> =
    Tuple4<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>>

/** Parameters containing five definitions. */
typealias Parameters5<P1, P2, P3, P4, P5> =
    Tuple5<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>>

/** Parameters containing six definitions. */
typealias Parameters6<P1, P2, P3, P4, P5, P6> =
    Tuple6<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>>

/** Parameters containing seven definitions. */
typealias Parameters7<P1, P2, P3, P4, P5, P6, P7> =
    Tuple7<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>>

/** Parameters containing eight definitions. */
typealias Parameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
    Tuple8<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>>

/** Parameters containing nine definitions. */
typealias Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
    Tuple9<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>>

/** Parameters containing ten definitions. */
typealias Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
    Tuple10<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>, Parameter<P10>>
