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
