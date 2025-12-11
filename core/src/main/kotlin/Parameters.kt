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

/** Returns an empty parameters tuple. */
fun emptyParameters(): Parameters0 = Parameters0

/** Returns a parameters tuple with one parameter. */
fun <P1 : Any> parametersOf(parameter1: Parameter<P1>): Parameters1<P1> = Tuple1(parameter1)

/** Returns a parameters tuple with two parameters. */
fun <P1 : Any, P2 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>
): Parameters2<P1, P2> = Tuple2(parameter1, parameter2)

/** Returns a parameters tuple with three parameters. */
fun <P1 : Any, P2 : Any, P3 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>
): Parameters3<P1, P2, P3> = Tuple3(parameter1, parameter2, parameter3)

/** Returns a parameters tuple with four parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>
): Parameters4<P1, P2, P3, P4> = Tuple4(parameter1, parameter2, parameter3, parameter4)

/** Returns a parameters tuple with five parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>
): Parameters5<P1, P2, P3, P4, P5> = Tuple5(parameter1, parameter2, parameter3, parameter4, parameter5)

/** Returns a parameters tuple with six parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>,
    parameter6: Parameter<P6>
): Parameters6<P1, P2, P3, P4, P5, P6> = Tuple6(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6)

/** Returns a parameters tuple with seven parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>,
    parameter6: Parameter<P6>,
    parameter7: Parameter<P7>
): Parameters7<P1, P2, P3, P4, P5, P6, P7> =
    Tuple7(
        parameter1,
        parameter2,
        parameter3,
        parameter4,
        parameter5,
        parameter6,
        parameter7
    )

/** Returns a parameters tuple with eight parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>,
    parameter6: Parameter<P6>,
    parameter7: Parameter<P7>,
    parameter8: Parameter<P8>
): Parameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
    Tuple8(
        parameter1,
        parameter2,
        parameter3,
        parameter4,
        parameter5,
        parameter6,
        parameter7,
        parameter8
    )

/** Returns a parameters tuple with nine parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>,
    parameter6: Parameter<P6>,
    parameter7: Parameter<P7>,
    parameter8: Parameter<P8>,
    parameter9: Parameter<P9>
): Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
    Tuple9(
        parameter1,
        parameter2,
        parameter3,
        parameter4,
        parameter5,
        parameter6,
        parameter7,
        parameter8,
        parameter9
    )

/** Returns a parameters tuple with ten parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>,
    parameter6: Parameter<P6>,
    parameter7: Parameter<P7>,
    parameter8: Parameter<P8>,
    parameter9: Parameter<P9>,
    parameter10: Parameter<P10>
): Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
    Tuple10(
        parameter1,
        parameter2,
        parameter3,
        parameter4,
        parameter5,
        parameter6,
        parameter7,
        parameter8,
        parameter9,
        parameter10
    )
