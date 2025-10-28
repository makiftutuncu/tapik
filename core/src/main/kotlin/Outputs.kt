@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/** Tuple-like container describing response variants pairing status matchers with headers and body definitions. */
sealed interface Outputs {
    /**
     * Lists the response definitions captured in declaration order.
     *
     * @return immutable view of the registered response definitions.
     */
    fun toList(): List<Output<*, *>>
}

/** Empty collection of outputs. */
data object Outputs0 : Outputs, AllOf0 {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item1 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O1 : Output<*, *>> plus(item1: O1): Outputs1<O1> = Outputs1(item1)

    override fun toList(): List<Output<*, *>> = emptyList()
}

/** Outputs tuple with one element. */
data class Outputs1<O1 : Output<*, *>>(
    override val item1: O1
) : Outputs,
    AllOf1<O1> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item2 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O2 : Output<*, *>> plus(item2: O2): Outputs2<O1, O2> = Outputs2(item1, item2)

    override fun toList(): List<Output<*, *>> = listOf(item1)
}

/** Outputs tuple with two elements. */
data class Outputs2<O1 : Output<*, *>, O2 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2
) : Outputs,
    AllOf2<O1, O2> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item3 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O3 : Output<*, *>> plus(item3: O3): Outputs3<O1, O2, O3> = Outputs3(item1, item2, item3)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2)
}

/** Outputs tuple with three elements. */
data class Outputs3<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3
) : Outputs,
    AllOf3<O1, O2, O3> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item4 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O4 : Output<*, *>> plus(item4: O4): Outputs4<O1, O2, O3, O4> = Outputs4(item1, item2, item3, item4)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2, item3)
}

/** Outputs tuple with four elements. */
data class Outputs4<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3,
    override val item4: O4
) : Outputs,
    AllOf4<O1, O2, O3, O4> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item5 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O5 : Output<*, *>> plus(item5: O5): Outputs5<O1, O2, O3, O4, O5> =
        Outputs5(item1, item2, item3, item4, item5)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2, item3, item4)
}

/** Outputs tuple with five elements. */
data class Outputs5<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3,
    override val item4: O4,
    override val item5: O5
) : Outputs,
    AllOf5<O1, O2, O3, O4, O5> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item6 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O6 : Output<*, *>> plus(item6: O6): Outputs6<O1, O2, O3, O4, O5, O6> =
        Outputs6(item1, item2, item3, item4, item5, item6)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2, item3, item4, item5)
}

/** Outputs tuple with six elements. */
data class Outputs6<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3,
    override val item4: O4,
    override val item5: O5,
    override val item6: O6
) : Outputs,
    AllOf6<O1, O2, O3, O4, O5, O6> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item7 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O7 : Output<*, *>> plus(item7: O7): Outputs7<O1, O2, O3, O4, O5, O6, O7> =
        Outputs7(item1, item2, item3, item4, item5, item6, item7)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2, item3, item4, item5, item6)
}

/** Outputs tuple with seven elements. */
data class Outputs7<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3,
    override val item4: O4,
    override val item5: O5,
    override val item6: O6,
    override val item7: O7
) : Outputs,
    AllOf7<O1, O2, O3, O4, O5, O6, O7> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item8 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O8 : Output<*, *>> plus(item8: O8): Outputs8<O1, O2, O3, O4, O5, O6, O7, O8> =
        Outputs8(item1, item2, item3, item4, item5, item6, item7, item8)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

/** Outputs tuple with eight elements. */
data class Outputs8<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3,
    override val item4: O4,
    override val item5: O5,
    override val item6: O6,
    override val item7: O7,
    override val item8: O8
) : Outputs,
    AllOf8<O1, O2, O3, O4, O5, O6, O7, O8> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item9 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O9 : Output<*, *>> plus(item9: O9): Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9> =
        Outputs9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

/** Outputs tuple with nine elements. */
data class Outputs9<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3,
    override val item4: O4,
    override val item5: O5,
    override val item6: O6,
    override val item7: O7,
    override val item8: O8,
    override val item9: O9
) : Outputs,
    AllOf9<O1, O2, O3, O4, O5, O6, O7, O8, O9> {
    /**
     * Appends an additional output definition to this tuple.
     *
     * @param item10 output to append.
     * @return tuple extended with the supplied output.
     * @see Outputs.toList
     */
    operator fun <O10 : Output<*, *>> plus(item10: O10): Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, O10> =
        Outputs10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override fun toList(): List<Output<*, *>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

/** Outputs tuple with ten elements. */
data class Outputs10<O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, O10 : Output<*, *>>(
    override val item1: O1,
    override val item2: O2,
    override val item3: O3,
    override val item4: O4,
    override val item5: O5,
    override val item6: O6,
    override val item7: O7,
    override val item8: O8,
    override val item9: O9,
    override val item10: O10
) : Outputs,
    AllOf10<O1, O2, O3, O4, O5, O6, O7, O8, O9, O10> {
    override fun toList(): List<Output<*, *>> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
}
