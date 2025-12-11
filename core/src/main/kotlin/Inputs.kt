@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Heterogeneous tuple describing request input variants.
 *
 * Construct instances via `Inputs(...)` when convenient.
 */
typealias Inputs = Tuple<Input<*, *>>

/** Empty collection of inputs. */
typealias Inputs0 = Tuple0

/** Inputs tuple with one element. */
typealias Inputs1<I1> = Tuple1<Input<*, *>, I1>

/** Inputs tuple with two elements. */
typealias Inputs2<I1, I2> = Tuple2<Input<*, *>, I1, I2>

/** Inputs tuple with three elements. */
typealias Inputs3<I1, I2, I3> = Tuple3<Input<*, *>, I1, I2, I3>

/** Inputs tuple with four elements. */
typealias Inputs4<I1, I2, I3, I4> = Tuple4<Input<*, *>, I1, I2, I3, I4>

/** Inputs tuple with five elements. */
typealias Inputs5<I1, I2, I3, I4, I5> = Tuple5<Input<*, *>, I1, I2, I3, I4, I5>

/** Inputs tuple with six elements. */
typealias Inputs6<I1, I2, I3, I4, I5, I6> = Tuple6<Input<*, *>, I1, I2, I3, I4, I5, I6>

/** Inputs tuple with seven elements. */
typealias Inputs7<I1, I2, I3, I4, I5, I6, I7> = Tuple7<Input<*, *>, I1, I2, I3, I4, I5, I6, I7>

/** Inputs tuple with eight elements. */
typealias Inputs8<I1, I2, I3, I4, I5, I6, I7, I8> = Tuple8<Input<*, *>, I1, I2, I3, I4, I5, I6, I7, I8>

/** Inputs tuple with nine elements. */
typealias Inputs9<I1, I2, I3, I4, I5, I6, I7, I8, I9> = Tuple9<Input<*, *>, I1, I2, I3, I4, I5, I6, I7, I8, I9>

/** Inputs tuple with ten elements. */
typealias Inputs10<I1, I2, I3, I4, I5, I6, I7, I8, I9, I10> =
    Tuple10<Input<*, *>, I1, I2, I3, I4, I5, I6, I7, I8, I9, I10>

/** Returns an empty inputs tuple. */
fun emptyInputs(): Inputs0 = Inputs0

/** Returns an inputs tuple with one input. */
fun <I1 : Input<*, *>> inputsOf(input1: I1): Inputs1<I1> = Tuple1(input1)

/** Returns an inputs tuple with two inputs. */
fun <I1 : Input<*, *>, I2 : Input<*, *>> inputsOf(
    input1: I1,
    input2: I2
): Inputs2<I1, I2> = Tuple2(input1, input2)

/** Returns an inputs tuple with three inputs. */
fun <I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3
): Inputs3<I1, I2, I3> = Tuple3(input1, input2, input3)

/** Returns an inputs tuple with four inputs. */
fun <I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3,
    input4: I4
): Inputs4<I1, I2, I3, I4> = Tuple4(input1, input2, input3, input4)

/** Returns an inputs tuple with five inputs. */
fun <I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>, I5 : Input<*, *>> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3,
    input4: I4,
    input5: I5
): Inputs5<I1, I2, I3, I4, I5> = Tuple5(input1, input2, input3, input4, input5)

/** Returns an inputs tuple with six inputs. */
fun <
    I1 : Input<*, *>,
    I2 : Input<*, *>,
    I3 : Input<*, *>,
    I4 : Input<*, *>,
    I5 : Input<*, *>,
    I6 : Input<*, *>
> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3,
    input4: I4,
    input5: I5,
    input6: I6
): Inputs6<I1, I2, I3, I4, I5, I6> = Tuple6(input1, input2, input3, input4, input5, input6)

/** Returns an inputs tuple with seven inputs. */
fun <
    I1 : Input<*, *>,
    I2 : Input<*, *>,
    I3 : Input<*, *>,
    I4 : Input<*, *>,
    I5 : Input<*, *>,
    I6 : Input<*, *>,
    I7 : Input<*, *>
> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3,
    input4: I4,
    input5: I5,
    input6: I6,
    input7: I7
): Inputs7<I1, I2, I3, I4, I5, I6, I7> = Tuple7(input1, input2, input3, input4, input5, input6, input7)

/** Returns an inputs tuple with eight inputs. */
fun <
    I1 : Input<*, *>,
    I2 : Input<*, *>,
    I3 : Input<*, *>,
    I4 : Input<*, *>,
    I5 : Input<*, *>,
    I6 : Input<*, *>,
    I7 : Input<*, *>,
    I8 : Input<*, *>
> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3,
    input4: I4,
    input5: I5,
    input6: I6,
    input7: I7,
    input8: I8
): Inputs8<I1, I2, I3, I4, I5, I6, I7, I8> = Tuple8(input1, input2, input3, input4, input5, input6, input7, input8)

/** Returns an inputs tuple with nine inputs. */
fun <
    I1 : Input<*, *>,
    I2 : Input<*, *>,
    I3 : Input<*, *>,
    I4 : Input<*, *>,
    I5 : Input<*, *>,
    I6 : Input<*, *>,
    I7 : Input<*, *>,
    I8 : Input<*, *>,
    I9 : Input<*, *>
> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3,
    input4: I4,
    input5: I5,
    input6: I6,
    input7: I7,
    input8: I8,
    input9: I9
): Inputs9<I1, I2, I3, I4, I5, I6, I7, I8, I9> =
    Tuple9(input1, input2, input3, input4, input5, input6, input7, input8, input9)

/** Returns an inputs tuple with ten inputs. */
fun <
    I1 : Input<*, *>,
    I2 : Input<*, *>,
    I3 : Input<*, *>,
    I4 : Input<*, *>,
    I5 : Input<*, *>,
    I6 : Input<*, *>,
    I7 : Input<*, *>,
    I8 : Input<*, *>,
    I9 : Input<*, *>,
    I10 : Input<*, *>
> inputsOf(
    input1: I1,
    input2: I2,
    input3: I3,
    input4: I4,
    input5: I5,
    input6: I6,
    input7: I7,
    input8: I8,
    input9: I9,
    input10: I10
): Inputs10<I1, I2, I3, I4, I5, I6, I7, I8, I9, I10> =
    Tuple10(input1, input2, input3, input4, input5, input6, input7, input8, input9, input10)
