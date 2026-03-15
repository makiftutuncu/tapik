@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Fixed-arity tuple of response variants.
 *
 * The typed tuple keeps the exact header and body shape of each output available to generators even
 * after multiple `.output(...)` calls have been chained together.
 */
typealias Outputs = Tuple<Output<*, *>>

/** Output tuple with no declared response variants yet. */
typealias Outputs0 = Tuple0

/** Output tuple with one response variant. */
typealias Outputs1<O1> = Tuple1<Output<*, *>, O1>

/** Output tuple with two response variants. */
typealias Outputs2<O1, O2> = Tuple2<Output<*, *>, O1, O2>

/** Output tuple with three response variants. */
typealias Outputs3<O1, O2, O3> = Tuple3<Output<*, *>, O1, O2, O3>

/** Output tuple with four response variants. */
typealias Outputs4<O1, O2, O3, O4> = Tuple4<Output<*, *>, O1, O2, O3, O4>

/** Output tuple with five response variants. */
typealias Outputs5<O1, O2, O3, O4, O5> = Tuple5<Output<*, *>, O1, O2, O3, O4, O5>

/** Output tuple with six response variants. */
typealias Outputs6<O1, O2, O3, O4, O5, O6> = Tuple6<Output<*, *>, O1, O2, O3, O4, O5, O6>

/** Output tuple with seven response variants. */
typealias Outputs7<O1, O2, O3, O4, O5, O6, O7> = Tuple7<Output<*, *>, O1, O2, O3, O4, O5, O6, O7>

/** Output tuple with eight response variants. */
typealias Outputs8<O1, O2, O3, O4, O5, O6, O7, O8> = Tuple8<Output<*, *>, O1, O2, O3, O4, O5, O6, O7, O8>

/** Output tuple with nine response variants. */
typealias Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9> = Tuple9<Output<*, *>, O1, O2, O3, O4, O5, O6, O7, O8, O9>

/** Output tuple with ten response variants. */
typealias Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, O10> =
    Tuple10<Output<*, *>, O1, O2, O3, O4, O5, O6, O7, O8, O9, O10>

/** Returns the empty output tuple used before any response variants are declared. */
fun emptyOutputs(): Outputs0 = Outputs0

/** Creates an output tuple with one response variant. */
fun <O1 : Output<*, *>> outputsOf(output1: O1): Outputs1<O1> = Tuple1(output1)

/** Creates an output tuple with two response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2
): Outputs2<O1, O2> = Tuple2(output1, output2)

/** Creates an output tuple with three response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3
): Outputs3<O1, O2, O3> = Tuple3(output1, output2, output3)

/** Creates an output tuple with four response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4
): Outputs4<O1, O2, O3, O4> = Tuple4(output1, output2, output3, output4)

/** Creates an output tuple with five response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5
): Outputs5<O1, O2, O3, O4, O5> = Tuple5(output1, output2, output3, output4, output5)

/** Creates an output tuple with six response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5,
    output6: O6
): Outputs6<O1, O2, O3, O4, O5, O6> = Tuple6(output1, output2, output3, output4, output5, output6)

/** Creates an output tuple with seven response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5,
    output6: O6,
    output7: O7
): Outputs7<O1, O2, O3, O4, O5, O6, O7> = Tuple7(output1, output2, output3, output4, output5, output6, output7)

/** Creates an output tuple with eight response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5,
    output6: O6,
    output7: O7,
    output8: O8
): Outputs8<O1, O2, O3, O4, O5, O6, O7, O8> =
    Tuple8(output1, output2, output3, output4, output5, output6, output7, output8)

/** Creates an output tuple with nine response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5,
    output6: O6,
    output7: O7,
    output8: O8,
    output9: O9
): Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9> =
    Tuple9(output1, output2, output3, output4, output5, output6, output7, output8, output9)

/** Creates an output tuple with ten response variants. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, O10 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5,
    output6: O6,
    output7: O7,
    output8: O8,
    output9: O9,
    output10: O10
): Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, O10> =
    Tuple10(output1, output2, output3, output4, output5, output6, output7, output8, output9, output10)
