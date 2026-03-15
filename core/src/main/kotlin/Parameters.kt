@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Fixed-arity tuple of URI parameters.
 *
 * Tapik keeps parameters in a typed tuple instead of a plain list so later DSL stages, generators,
 * and interpreters can recover both the order and the Kotlin type of each captured value.
 */
typealias Parameters = Tuple<Parameter<*>>

/** Parameter tuple with no captured parameters. */
typealias Parameters0 = Tuple0

/** Parameter tuple with one captured parameter. */
typealias Parameters1<P1> = Tuple1<Parameter<*>, Parameter<P1>>

/** Parameter tuple with two captured parameters. */
typealias Parameters2<P1, P2> = Tuple2<Parameter<*>, Parameter<P1>, Parameter<P2>>

/** Parameter tuple with three captured parameters. */
typealias Parameters3<P1, P2, P3> =
    Tuple3<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>>

/** Parameter tuple with four captured parameters. */
typealias Parameters4<P1, P2, P3, P4> =
    Tuple4<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>>

/** Parameter tuple with five captured parameters. */
typealias Parameters5<P1, P2, P3, P4, P5> =
    Tuple5<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>>

/** Parameter tuple with six captured parameters. */
typealias Parameters6<P1, P2, P3, P4, P5, P6> =
    Tuple6<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>>

/** Parameter tuple with seven captured parameters. */
typealias Parameters7<P1, P2, P3, P4, P5, P6, P7> =
    Tuple7<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>>

/** Parameter tuple with eight captured parameters. */
typealias Parameters8<P1, P2, P3, P4, P5, P6, P7, P8> =
    Tuple8<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>>

/** Parameter tuple with nine captured parameters. */
typealias Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> =
    Tuple9<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>>

/** Parameter tuple with ten captured parameters. */
typealias Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> =
    Tuple10<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>, Parameter<P10>>

/** Returns the empty parameter tuple used by routes with no path or query inputs. */
fun emptyParameters(): Parameters0 = Parameters0

/** Creates a parameter tuple with one captured parameter. */
fun <P1 : Any> parametersOf(parameter1: Parameter<P1>): Parameters1<P1> = Tuple1(parameter1)

/** Creates a parameter tuple with two captured parameters. */
fun <P1 : Any, P2 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>
): Parameters2<P1, P2> = Tuple2(parameter1, parameter2)

/** Creates a parameter tuple with three captured parameters. */
fun <P1 : Any, P2 : Any, P3 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>
): Parameters3<P1, P2, P3> = Tuple3(parameter1, parameter2, parameter3)

/** Creates a parameter tuple with four captured parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>
): Parameters4<P1, P2, P3, P4> = Tuple4(parameter1, parameter2, parameter3, parameter4)

/** Creates a parameter tuple with five captured parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>
): Parameters5<P1, P2, P3, P4, P5> = Tuple5(parameter1, parameter2, parameter3, parameter4, parameter5)

/** Creates a parameter tuple with six captured parameters. */
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> parametersOf(
    parameter1: Parameter<P1>,
    parameter2: Parameter<P2>,
    parameter3: Parameter<P3>,
    parameter4: Parameter<P4>,
    parameter5: Parameter<P5>,
    parameter6: Parameter<P6>
): Parameters6<P1, P2, P3, P4, P5, P6> = Tuple6(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6)

/** Creates a parameter tuple with seven captured parameters. */
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

/** Creates a parameter tuple with eight captured parameters. */
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

/** Creates a parameter tuple with nine captured parameters. */
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

/** Creates a parameter tuple with ten captured parameters. */
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
