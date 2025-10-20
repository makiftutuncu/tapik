@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import dev.akif.tapik.*

sealed interface OutputBodies {
    fun toList(): List<OutputBody<*>>
}

data object OutputBodies0 : OutputBodies, AllOf0 {
    operator fun <OB1 : Body<*>> plus(item1: OutputBody<OB1>): OutputBodies1<OB1> = OutputBodies1(item1)

    override fun toList(): List<OutputBody<*>> = emptyList()
}

data class OutputBodies1<OB1 : Body<*>>(
    override val item1: OutputBody<OB1>
) : OutputBodies,
    AllOf1<OutputBody<OB1>> {
    operator fun <OB2 : Body<*>> plus(item2: OutputBody<OB2>): OutputBodies2<OB1, OB2> = OutputBodies2(item1, item2)

    override fun toList(): List<OutputBody<*>> = listOf(item1)
}

data class OutputBodies2<OB1 : Body<*>, OB2 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>
) : OutputBodies,
    AllOf2<OutputBody<OB1>, OutputBody<OB2>> {
    operator fun <OB3 : Body<*>> plus(item3: OutputBody<OB3>): OutputBodies3<OB1, OB2, OB3> =
        OutputBodies3(item1, item2, item3)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2)
}

data class OutputBodies3<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>
) : OutputBodies,
    AllOf3<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>> {
    operator fun <OB4 : Body<*>> plus(item4: OutputBody<OB4>): OutputBodies4<OB1, OB2, OB3, OB4> =
        OutputBodies4(item1, item2, item3, item4)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3)
}

data class OutputBodies4<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>
) : OutputBodies,
    AllOf4<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>> {
    operator fun <OB5 : Body<*>> plus(item5: OutputBody<OB5>): OutputBodies5<OB1, OB2, OB3, OB4, OB5> =
        OutputBodies5(item1, item2, item3, item4, item5)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4)
}

data class OutputBodies5<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>
) : OutputBodies,
    AllOf5<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>> {
    operator fun <OB6 : Body<*>> plus(item6: OutputBody<OB6>): OutputBodies6<OB1, OB2, OB3, OB4, OB5, OB6> =
        OutputBodies6(item1, item2, item3, item4, item5, item6)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5)
}

data class OutputBodies6<OB1 : Body<*>, OB2 : Body<*>, OB3 : Body<*>, OB4 : Body<*>, OB5 : Body<*>, OB6 : Body<*>>(
    override val item1: OutputBody<OB1>,
    override val item2: OutputBody<OB2>,
    override val item3: OutputBody<OB3>,
    override val item4: OutputBody<OB4>,
    override val item5: OutputBody<OB5>,
    override val item6: OutputBody<OB6>
) : OutputBodies,
    AllOf6<OutputBody<OB1>, OutputBody<OB2>, OutputBody<OB3>, OutputBody<OB4>, OutputBody<OB5>, OutputBody<OB6>> {
    operator fun <OB7 : Body<*>> plus(item7: OutputBody<OB7>): OutputBodies7<OB1, OB2, OB3, OB4, OB5, OB6, OB7> =
        OutputBodies7(item1, item2, item3, item4, item5, item6, item7)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6)
}

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
    operator fun <OB8 : Body<*>> plus(item8: OutputBody<OB8>): OutputBodies8<OB1, OB2, OB3, OB4, OB5, OB6, OB7, OB8> =
        OutputBodies8(item1, item2, item3, item4, item5, item6, item7, item8)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

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
    operator fun <OB9 : Body<*>> plus(
        item9: OutputBody<OB9>
    ): OutputBodies9<OB1, OB2, OB3, OB4, OB5, OB6, OB7, OB8, OB9> =
        OutputBodies9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

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
    operator fun <OB10 : Body<*>> plus(
        item10: OutputBody<OB10>
    ): OutputBodies10<OB1, OB2, OB3, OB4, OB5, OB6, OB7, OB8, OB9, OB10> =
        OutputBodies10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override fun toList(): List<OutputBody<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

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
