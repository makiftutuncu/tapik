@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/** Tuple-like container describing ordered header definitions. */
sealed interface Headers {
    /**
     * Lists the headers contained in the tuple in declaration order.
     *
     * @return immutable view of the captured header definitions.
     */
    fun toList(): List<Header<*>>
}

/** Empty header tuple. */
data object Headers0 : Headers, AllOf0 {
    /**
     * Appends a header to this tuple.
     *
     * @param item1 header to append.
     * @return tuple containing a single header.
     */
    operator fun <H1 : Any> plus(item1: Header<H1>): Headers1<H1> = Headers1(item1)

    override fun toList(): List<Header<*>> = emptyList()
}

/** Header tuple with one element. */
data class Headers1<H1 : Any>(
    override val item1: Header<H1>
) : Headers,
    AllOf1<Header<H1>> {
    /**
     * Appends a header to this tuple.
     *
     * @param item2 header to append.
     * @return tuple containing two headers.
     */
    operator fun <H2 : Any> plus(item2: Header<H2>): Headers2<H1, H2> = Headers2(item1, item2)

    override fun toList(): List<Header<*>> = listOf(item1)
}

/** Header tuple with two elements. */
data class Headers2<H1 : Any, H2 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>
) : Headers,
    AllOf2<Header<H1>, Header<H2>> {
    /**
     * Appends a header to this tuple.
     *
     * @param item3 header to append.
     * @return tuple containing three headers.
     */
    operator fun <H3 : Any> plus(item3: Header<H3>): Headers3<H1, H2, H3> = Headers3(item1, item2, item3)

    override fun toList(): List<Header<*>> = listOf(item1, item2)
}

/** Header tuple with three elements. */
data class Headers3<H1 : Any, H2 : Any, H3 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>
) : Headers,
    AllOf3<Header<H1>, Header<H2>, Header<H3>> {
    /**
     * Appends a header to this tuple.
     *
     * @param item4 header to append.
     * @return tuple containing four headers.
     */
    operator fun <H4 : Any> plus(item4: Header<H4>): Headers4<H1, H2, H3, H4> = Headers4(item1, item2, item3, item4)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3)
}

/** Header tuple with four elements. */
data class Headers4<H1 : Any, H2 : Any, H3 : Any, H4 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>
) : Headers,
    AllOf4<Header<H1>, Header<H2>, Header<H3>, Header<H4>> {
    /**
     * Appends a header to this tuple.
     *
     * @param item5 header to append.
     * @return tuple containing five headers.
     */
    operator fun <H5 : Any> plus(item5: Header<H5>): Headers5<H1, H2, H3, H4, H5> =
        Headers5(item1, item2, item3, item4, item5)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4)
}

/** Header tuple with five elements. */
data class Headers5<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>
) : Headers,
    AllOf5<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>> {
    /**
     * Appends a header to this tuple.
     *
     * @param item6 header to append.
     * @return tuple containing six headers.
     */
    operator fun <H6 : Any> plus(item6: Header<H6>): Headers6<H1, H2, H3, H4, H5, H6> =
        Headers6(item1, item2, item3, item4, item5, item6)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5)
}

/** Header tuple with six elements. */
data class Headers6<H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any>(
    override val item1: Header<H1>,
    override val item2: Header<H2>,
    override val item3: Header<H3>,
    override val item4: Header<H4>,
    override val item5: Header<H5>,
    override val item6: Header<H6>
) : Headers,
    AllOf6<Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>> {
    /**
     * Appends a header to this tuple.
     *
     * @param item7 header to append.
     * @return tuple containing seven headers.
     */
    operator fun <H7 : Any> plus(item7: Header<H7>): Headers7<H1, H2, H3, H4, H5, H6, H7> =
        Headers7(item1, item2, item3, item4, item5, item6, item7)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6)
}

/** Header tuple with seven elements. */
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
    /**
     * Appends a header to this tuple.
     *
     * @param item8 header to append.
     * @return tuple containing eight headers.
     */
    operator fun <H8 : Any> plus(item8: Header<H8>): Headers8<H1, H2, H3, H4, H5, H6, H7, H8> =
        Headers8(item1, item2, item3, item4, item5, item6, item7, item8)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

/** Header tuple with eight elements. */
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
    /**
     * Appends a header to this tuple.
     *
     * @param item9 header to append.
     * @return tuple containing nine headers.
     */
    operator fun <H9 : Any> plus(item9: Header<H9>): Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9> =
        Headers9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

/** Header tuple with nine elements. */
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
    /**
     * Appends a header to this tuple.
     *
     * @param item10 header to append.
     * @return tuple containing ten headers.
     */
    operator fun <H10 : Any> plus(item10: Header<H10>): Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> =
        Headers10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override fun toList(): List<Header<*>> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

/** Header tuple with ten elements. */
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
