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
