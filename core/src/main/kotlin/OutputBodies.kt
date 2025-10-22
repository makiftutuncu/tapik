@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/** Tuple-like container describing output body variants for an endpoint. */
sealed interface OutputBodies {
    /**
     * Lists the output bodies captured in declaration order.
     *
     * @return immutable view of the registered output body definitions.
     */
    fun toList(): List<OutputBody<*>>
}

/** Empty collection of output bodies. */
data object OutputBodies0 : OutputBodies, AllOf0 {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item1 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB1 : Body<*>> plus(item1: OutputBody<OB1>): OutputBodies1<OB1> = OutputBodies1(item1)

    override fun toList(): List<OutputBody<*>> = emptyList()
}

/** Output bodies tuple with one element. */
data class OutputBodies1<OB1 : Body<*>>(
    override val item1: OutputBody<OB1>
) : OutputBodies,
    AllOf1<OutputBody<OB1>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item2 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB2 : Body<*>> plus(item2: OutputBody<OB2>): OutputBodies2<OB1, OB2> = OutputBodies2(item1, item2)

    override fun toList(): List<OutputBody<*>> = listOf(item1)
}

/** Output bodies tuple with two elements. */
data class OutputBodies2<OB1 : Body<*>, OB2 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>
) : OutputBodies,
    AllOf2<OutputBody<OB1>, OutputBody<OB2>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item3 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB3 : Body<*>> plus(item3: OutputBody<OB3>): OutputBodies3<OB1, OB2, OB3> =
        OutputBodies3(item1, item2, item3)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2)
}

/** Output bodies tuple with three elements. */
data class OutputBodies3<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>
) : OutputBodies,
    AllOf3<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item4 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB4 : Body<*>> plus(item4: OutputBody<OB4>): OutputBodies4<OB1, OB2, OB3, OB4> =
        OutputBodies4(item1, item2, item3, item4)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3)
}

/** Output bodies tuple with four elements. */
data class OutputBodies4<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>
) : OutputBodies,
    AllOf4<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item5 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB5 : Body<*>> plus(item5: OutputBody<OB5>): OutputBodies5<OB1, OB2, OB3, OB4, OB5> =
        OutputBodies5(item1, item2, item3, item4, item5)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4)
}

/** Output bodies tuple with five elements. */
data class OutputBodies5<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>
) : OutputBodies,
    AllOf5<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item6 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB6 : Body<*>> plus(item6: OutputBody<OB6>): OutputBodies6<OB1, OB2, OB3, OB4, OB5, OB6> =
        OutputBodies6(item1, item2, item3, item4, item5, item6)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5)
}

/** Output bodies tuple with six elements. */
data class OutputBodies6<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>, OB6 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>,
    override val item6: OutputBody<OB6>
) : OutputBodies,
    AllOf6<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>, OutputBody<OB6>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item7 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB7 : Body<*>> plus(item7: OutputBody<OB7>): OutputBodies7<OB1, OB2, OB3, OB4, OB5, OB6, OB7> =
        OutputBodies7(item1, item2, item3, item4, item5, item6, item7)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6)
}

/** Output bodies tuple with seven elements. */
data class OutputBodies7<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>, OB6 : Body<*>, OB7 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>,
    override val item6: OutputBody<OB6>,
    override val item7: OutputBody<OB7>
) : OutputBodies,
    AllOf7<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>, OutputBody<OB6>, OutputBody<OB7>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item8 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB8 : Body<*>> plus(item8: OutputBody<OB8>): OutputBodies8<OB1, OB2, OB3, OB4, OB5, OB6, OB7, OB8> =
        OutputBodies8(item1, item2, item3, item4, item5, item6, item7, item8)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

/** Output bodies tuple with eight elements. */
data class OutputBodies8<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>, OB6 : Body<*>, OB7 : Body<*>, OB8 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>,
    override val item6: OutputBody<OB6>,
    override val item7: OutputBody<OB7>,
    override val item8: OutputBody<OB8>
) : OutputBodies,
    AllOf8<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>, OutputBody<OB6>, OutputBody<OB7>, OutputBody<OB8>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item9 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB9 : Body<*>> plus(
        item9: OutputBody<OB9>
    ): OutputBodies9<OB1, OB2, OB3, OB4, OB5, OB6, OB7, OB8, OB9> =
        OutputBodies9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

/** Output bodies tuple with nine elements. */
data class OutputBodies9<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>, OB6 : Body<*>, OB7 : Body<*>, OB8 : Body<*>, OB9 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>,
    override val item6: OutputBody<OB6>,
    override val item7: OutputBody<OB7>,
    override val item8: OutputBody<OB8>,
    override val item9: OutputBody<OB9>
) : OutputBodies,
    AllOf9<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>, OutputBody<OB6>, OutputBody<OB7>, OutputBody<OB8>, OutputBody<OB9>> {
    /**
     * Appends an additional output body definition to this tuple.
     *
     * @param item10 output body to append.
     * @return tuple extended with the supplied output body.
     * @see OutputBodies.toList
     */
    operator fun <OB10 : Body<*>> plus(
        item10: OutputBody<OB10>
    ): OutputBodies10<OB1, OB2, OB3, OB4, OB5, OB6, OB7, OB8, OB9, OB10> =
        OutputBodies10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

/** Output bodies tuple with ten elements. */
data class OutputBodies10<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>, OB6 : Body<*>, OB7 : Body<*>, OB8 : Body<*>, OB9 : Body<*>, OB10 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>,
    override val item6: OutputBody<OB6>,
    override val item7: OutputBody<OB7>,
    override val item8: OutputBody<OB8>,
    override val item9: OutputBody<OB9>,
    override val item10: OutputBody<OB10>
) : OutputBodies,
    AllOf10<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>, OutputBody<OB6>, OutputBody<OB7>, OutputBody<OB8>, OutputBody<OB9>, OutputBody<OB10>> {
    override fun toList(): List<OutputBody<*>> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
}
