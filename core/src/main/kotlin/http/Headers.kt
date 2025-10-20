@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import dev.akif.tapik.*

sealed interface Headers {
    fun toList(): List<Header<*>>
}

data object Headers0 : Headers, AllOf0 {
    operator fun <H1 : Any> plus(item1: Header<H1>): Headers1<H1> = Headers1(item1)

    override fun toList(): List<Header<*>> = emptyList()
}

data class Headers1<H1 : Any>(
    override val item1: Header<H1>
) : Headers,
    AllOf1<Header<H1>> {
    operator fun <H2 : Any> plus(item2: Header<H2>): Headers2<H1, H2> = Headers2(item1, item2)

    override fun toList(): List<Header<*>> = listOf(item1)
}

data class Headers2<H1 : Any, H2 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>
) : Headers,
    AllOf2<Header<H1>, Header<H2>> {
    operator fun <H3 : Any> plus(item3: Header<H3>): Headers3<H1, H2, H3> = Headers3(item1, item2, item3)

    override fun toList(): List<Header<*>> = listOf(item1, item2)
}

data class Headers3<H1 : Any, H2 : Any, H3 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>
) : Headers,
    AllOf3<Header<H1>, Header<H2>, Header<H3>> {
    operator fun <H4 : Any> plus(item4: Header<H4>): Headers4<H1, H2, H3, H4> = Headers4(item1, item2, item3, item4)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3)
}

data class Headers4<H1 : Any, H2 : Any, H3 : Any, H4 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>
) : Headers,
    AllOf4<Header<H1>, Header<H2>, Header<H3>, Header<H4>> {
    operator fun <H5 : Any> plus(item5: Header<H5>): Headers5<H1, H2, H3, H4, H5> =
        Headers5(item1, item2, item3, item4, item5)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4)
}

data class Headers5<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>
) : Headers,
    AllOf5<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>> {
    operator fun <H6 : Any> plus(item6: Header<H6>): Headers6<H1, H2, H3, H4, H5, H6> =
        Headers6(item1, item2, item3, item4, item5, item6)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5)
}

data class Headers6<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>,
    override val item6: Header<H6>
) : Headers,
    AllOf6<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>> {
    operator fun <H7 : Any> plus(item7: Header<H7>): Headers7<H1, H2, H3, H4, H5, H6, H7> =
        Headers7(item1, item2, item3, item4, item5, item6, item7)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6)
}

data class Headers7<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>,
    override val item6: Header<H6>,
    override val item7: Header<H7>
) : Headers,
    AllOf7<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>> {
    operator fun <H8 : Any> plus(item8: Header<H8>): Headers8<H1, H2, H3, H4, H5, H6, H7, H8> =
        Headers8(item1, item2, item3, item4, item5, item6, item7, item8)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

data class Headers8<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>,
    override val item6: Header<H6>,
    override val item7: Header<H7>,
    override val item8: Header<H8>
) : Headers,
    AllOf8<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>> {
    operator fun <H9 : Any> plus(item9: Header<H9>): Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
        Headers9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

data class Headers9<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>,
    override val item6: Header<H6>,
    override val item7: Header<H7>,
    override val item8: Header<H8>,
    override val item9: Header<H9>
) : Headers,
    AllOf9<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>> {
    operator fun <H10 : Any> plus(item10: Header<H10>): Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
        Headers10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

data class Headers10<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>,
    override val item6: Header<H6>,
    override val item7: Header<H7>,
    override val item8: Header<H8>,
    override val item9: Header<H9>,
    override val item10: Header<H10>
) : Headers,
    AllOf10<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>, Header<H10>> {
    override fun toList(): List<Header<*>> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
}
