@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Heterogeneous tuple describing response variants.
 *
 * Construct instances via `Outputs(...)` when convenient.
 */
typealias Outputs = Tuple<Output<*, *>>

/** Empty collection of outputs. */
typealias Outputs0 = Tuple0

/** Outputs tuple with one element. */
typealias Outputs1<O1> = Tuple1<Output<*, *>, O1>

/** Outputs tuple with two elements. */
typealias Outputs2<O1, O2> = Tuple2<Output<*, *>, O1, O2>

/** Outputs tuple with three elements. */
typealias Outputs3<O1, O2, O3> = Tuple3<Output<*, *>, O1, O2, O3>

/** Outputs tuple with four elements. */
typealias Outputs4<O1, O2, O3, O4> = Tuple4<Output<*, *>, O1, O2, O3, O4>

/** Outputs tuple with five elements. */
typealias Outputs5<O1, O2, O3, O4, O5> = Tuple5<Output<*, *>, O1, O2, O3, O4, O5>

/** Outputs tuple with six elements. */
typealias Outputs6<O1, O2, O3, O4, O5, O6> = Tuple6<Output<*, *>, O1, O2, O3, O4, O5, O6>

/** Outputs tuple with seven elements. */
typealias Outputs7<O1, O2, O3, O4, O5, O6, O7> = Tuple7<Output<*, *>, O1, O2, O3, O4, O5, O6, O7>

/** Outputs tuple with eight elements. */
typealias Outputs8<O1, O2, O3, O4, O5, O6, O7, O8> = Tuple8<Output<*, *>, O1, O2, O3, O4, O5, O6, O7, O8>

/** Outputs tuple with nine elements. */
typealias Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9> = Tuple9<Output<*, *>, O1, O2, O3, O4, O5, O6, O7, O8, O9>

/** Outputs tuple with ten elements. */
typealias Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, O10> =
    Tuple10<Output<*, *>, O1, O2, O3, O4, O5, O6, O7, O8, O9, O10>

/** Returns an empty outputs tuple. */
fun emptyOutputs(): Outputs0 = Outputs0

/** Returns an outputs tuple with one output. */
fun <O1 : Output<*, *>> outputsOf(output1: O1): Outputs1<O1> = Tuple1(output1)

/** Returns an outputs tuple with two outputs. */
fun <O1 : Output<*, *>, O2 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2
): Outputs2<O1, O2> = Tuple2(output1, output2)

/** Returns an outputs tuple with three outputs. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3
): Outputs3<O1, O2, O3> = Tuple3(output1, output2, output3)

/** Returns an outputs tuple with four outputs. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4
): Outputs4<O1, O2, O3, O4> = Tuple4(output1, output2, output3, output4)

/** Returns an outputs tuple with five outputs. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5
): Outputs5<O1, O2, O3, O4, O5> = Tuple5(output1, output2, output3, output4, output5)

/** Returns an outputs tuple with six outputs. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5,
    output6: O6
): Outputs6<O1, O2, O3, O4, O5, O6> = Tuple6(output1, output2, output3, output4, output5, output6)

/** Returns an outputs tuple with seven outputs. */
fun <O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>> outputsOf(
    output1: O1,
    output2: O2,
    output3: O3,
    output4: O4,
    output5: O5,
    output6: O6,
    output7: O7
): Outputs7<O1, O2, O3, O4, O5, O6, O7> = Tuple7(output1, output2, output3, output4, output5, output6, output7)

/** Returns an outputs tuple with eight outputs. */
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

/** Returns an outputs tuple with nine outputs. */
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

/** Returns an outputs tuple with ten outputs. */
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
