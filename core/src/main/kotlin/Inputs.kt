@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/** Tuple-like container describing request variants pairing headers with body definitions. */
sealed interface Inputs {
    /**
     * Lists the request definitions captured in declaration order.
     *
     * @return immutable view of the registered request definitions.
     */
    fun toList(): List<Input<*, *>>
}

/** Empty collection of inputs. */
data object Inputs0 : Inputs, AllOf0 {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item1 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I1 : Input<*, *>> plus(item1: I1): Inputs1<I1> = Inputs1(item1)

    override fun toList(): List<Input<*, *>> = emptyList()
}

/** Inputs tuple with one element. */
data class Inputs1<I1 : Input<*, *>>(
    override val item1: I1
) : Inputs,
    AllOf1<I1> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item2 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I2 : Input<*, *>> plus(item2: I2): Inputs2<I1, I2> = Inputs2(item1, item2)

    override fun toList(): List<Input<*, *>> = listOf(item1)
}

/** Inputs tuple with two elements. */
data class Inputs2<I1 : Input<*, *>, I2 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2
) : Inputs,
    AllOf2<I1, I2> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item3 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I3 : Input<*, *>> plus(item3: I3): Inputs3<I1, I2, I3> = Inputs3(item1, item2, item3)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2)
}

/** Inputs tuple with three elements. */
data class Inputs3<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3
) : Inputs,
    AllOf3<I1, I2, I3> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item4 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I4 : Input<*, *>> plus(item4: I4): Inputs4<I1, I2, I3, I4> = Inputs4(item1, item2, item3, item4)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2, item3)
}

/** Inputs tuple with four elements. */
data class Inputs4<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3,
    override val item4: I4
) : Inputs,
    AllOf4<I1, I2, I3, I4> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item5 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I5 : Input<*, *>> plus(item5: I5): Inputs5<I1, I2, I3, I4, I5> =
        Inputs5(item1, item2, item3, item4, item5)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2, item3, item4)
}

/** Inputs tuple with five elements. */
data class Inputs5<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>, I5 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3,
    override val item4: I4,
    override val item5: I5
) : Inputs,
    AllOf5<I1, I2, I3, I4, I5> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item6 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I6 : Input<*, *>> plus(item6: I6): Inputs6<I1, I2, I3, I4, I5, I6> =
        Inputs6(item1, item2, item3, item4, item5, item6)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2, item3, item4, item5)
}

/** Inputs tuple with six elements. */
data class Inputs6<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>, I5 : Input<*, *>, I6 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3,
    override val item4: I4,
    override val item5: I5,
    override val item6: I6
) : Inputs,
    AllOf6<I1, I2, I3, I4, I5, I6> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item7 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I7 : Input<*, *>> plus(item7: I7): Inputs7<I1, I2, I3, I4, I5, I6, I7> =
        Inputs7(item1, item2, item3, item4, item5, item6, item7)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2, item3, item4, item5, item6)
}

/** Inputs tuple with seven elements. */
data class Inputs7<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>, I5 : Input<*, *>, I6 : Input<*, *>, I7 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3,
    override val item4: I4,
    override val item5: I5,
    override val item6: I6,
    override val item7: I7
) : Inputs,
    AllOf7<I1, I2, I3, I4, I5, I6, I7> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item8 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I8 : Input<*, *>> plus(item8: I8): Inputs8<I1, I2, I3, I4, I5, I6, I7, I8> =
        Inputs8(item1, item2, item3, item4, item5, item6, item7, item8)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

/** Inputs tuple with eight elements. */
data class Inputs8<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>, I5 : Input<*, *>, I6 : Input<*, *>, I7 : Input<*, *>, I8 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3,
    override val item4: I4,
    override val item5: I5,
    override val item6: I6,
    override val item7: I7,
    override val item8: I8
) : Inputs,
    AllOf8<I1, I2, I3, I4, I5, I6, I7, I8> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item9 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I9 : Input<*, *>> plus(item9: I9): Inputs9<I1, I2, I3, I4, I5, I6, I7, I8, I9> =
        Inputs9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

/** Inputs tuple with nine elements. */
data class Inputs9<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>, I5 : Input<*, *>, I6 : Input<*, *>, I7 : Input<*, *>, I8 : Input<*, *>, I9 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3,
    override val item4: I4,
    override val item5: I5,
    override val item6: I6,
    override val item7: I7,
    override val item8: I8,
    override val item9: I9
) : Inputs,
    AllOf9<I1, I2, I3, I4, I5, I6, I7, I8, I9> {
    /**
     * Appends an additional input definition to this tuple.
     *
     * @param item10 input to append.
     * @return tuple extended with the supplied input.
     * @see Inputs.toList
     */
    operator fun <I10 : Input<*, *>> plus(item10: I10): Inputs10<I1, I2, I3, I4, I5, I6, I7, I8, I9, I10> =
        Inputs10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override fun toList(): List<Input<*, *>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

/** Inputs tuple with ten elements. */
data class Inputs10<I1 : Input<*, *>, I2 : Input<*, *>, I3 : Input<*, *>, I4 : Input<*, *>, I5 : Input<*, *>, I6 : Input<*, *>, I7 : Input<*, *>, I8 : Input<*, *>, I9 : Input<*, *>, I10 : Input<*, *>>(
    override val item1: I1,
    override val item2: I2,
    override val item3: I3,
    override val item4: I4,
    override val item5: I5,
    override val item6: I6,
    override val item7: I7,
    override val item8: I8,
    override val item9: I9,
    override val item10: I10
) : Inputs,
    AllOf10<I1, I2, I3, I4, I5, I6, I7, I8, I9, I10> {
    override fun toList(): List<Input<*, *>> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
}
