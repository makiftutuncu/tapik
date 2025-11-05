@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Marker hierarchy for reusable heterogeneous tuples backed by a shared super type.
 *
 * @param Super common super type for all tuple elements.
 */
sealed interface Tuple<out Super> : Listable<Super> {
    /**
     * Factory helpers for constructing tuples with arities from zero to ten.
     */
    companion object {
        /**
         * Creates an empty tuple.
         *
         * @return zero-arity tuple instance.
         */
        operator fun invoke(): Tuple0 = Tuple0

        /**
         * Creates a tuple with one element.
         *
         * @param item1 first element.
         * @return tuple containing the supplied element.
         */
        operator fun <Super, T1 : Super> invoke(item1: T1): Tuple1<Super, T1> = Tuple1(item1)

        /**
         * Creates a tuple with two elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super> invoke(
            item1: T1,
            item2: T2
        ): Tuple2<Super, T1, T2> = Tuple2(item1, item2)

        /**
         * Creates a tuple with three elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super, T3 : Super> invoke(
            item1: T1,
            item2: T2,
            item3: T3
        ): Tuple3<Super, T1, T2, T3> = Tuple3(item1, item2, item3)

        /**
         * Creates a tuple with four elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @param item4 fourth element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super> invoke(
            item1: T1,
            item2: T2,
            item3: T3,
            item4: T4
        ): Tuple4<Super, T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

        /**
         * Creates a tuple with five elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @param item4 fourth element.
         * @param item5 fifth element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super> invoke(
            item1: T1,
            item2: T2,
            item3: T3,
            item4: T4,
            item5: T5
        ): Tuple5<Super, T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

        /**
         * Creates a tuple with six elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @param item4 fourth element.
         * @param item5 fifth element.
         * @param item6 sixth element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super> invoke(
            item1: T1,
            item2: T2,
            item3: T3,
            item4: T4,
            item5: T5,
            item6: T6
        ): Tuple6<Super, T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

        /**
         * Creates a tuple with seven elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @param item4 fourth element.
         * @param item5 fifth element.
         * @param item6 sixth element.
         * @param item7 seventh element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super> invoke(
            item1: T1,
            item2: T2,
            item3: T3,
            item4: T4,
            item5: T5,
            item6: T6,
            item7: T7
        ): Tuple7<Super, T1, T2, T3, T4, T5, T6, T7> = Tuple7(item1, item2, item3, item4, item5, item6, item7)

        /**
         * Creates a tuple with eight elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @param item4 fourth element.
         * @param item5 fifth element.
         * @param item6 sixth element.
         * @param item7 seventh element.
         * @param item8 eighth element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super> invoke(
            item1: T1,
            item2: T2,
            item3: T3,
            item4: T4,
            item5: T5,
            item6: T6,
            item7: T7,
            item8: T8
        ): Tuple8<Super, T1, T2, T3, T4, T5, T6, T7, T8> =
            Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

        /**
         * Creates a tuple with nine elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @param item4 fourth element.
         * @param item5 fifth element.
         * @param item6 sixth element.
         * @param item7 seventh element.
         * @param item8 eighth element.
         * @param item9 ninth element.
         * @return tuple containing the supplied elements.
         */
        operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super> invoke(
            item1: T1,
            item2: T2,
            item3: T3,
            item4: T4,
            item5: T5,
            item6: T6,
            item7: T7,
            item8: T8,
            item9: T9
        ): Tuple9<Super, T1, T2, T3, T4, T5, T6, T7, T8, T9> =
            Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

        /**
         * Creates a tuple with ten elements.
         *
         * @param item1 first element.
         * @param item2 second element.
         * @param item3 third element.
         * @param item4 fourth element.
         * @param item5 fifth element.
         * @param item6 sixth element.
         * @param item7 seventh element.
         * @param item8 eighth element.
         * @param item9 ninth element.
         * @param item10 tenth element.
         * @return tuple containing the supplied elements.
         */
        operator fun <
            Super,
            T1 : Super,
            T2 : Super,
            T3 : Super,
            T4 : Super,
            T5 : Super,
            T6 : Super,
            T7 : Super,
            T8 : Super,
            T9 : Super,
            T10 : Super
        > invoke(
            item1: T1,
            item2: T2,
            item3: T3,
            item4: T4,
            item5: T5,
            item6: T6,
            item7: T7,
            item8: T8,
            item9: T9,
            item10: T10
        ): Tuple10<Super, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
            Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
    }
}

/** Zero-arity tuple with no elements. */
data object Tuple0 : Tuple<Nothing>, AllOf0 {
    override fun toList(): List<Nothing> = emptyList()
}

/** Tuple carrying a single element [item1]. */
data class Tuple1<Super, T1 : Super>(
    override val item1: T1
) : Tuple<Super>,
    AllOf1<T1> {
    override fun toList(): List<Super> = listOf(item1)
}

/** Tuple carrying elements [item1] and [item2]. */
data class Tuple2<Super, T1 : Super, T2 : Super>(
    override val item1: T1,
    override val item2: T2
) : Tuple<Super>,
    AllOf2<T1, T2> {
    override fun toList(): List<Super> = listOf(item1, item2)
}

/** Tuple carrying elements [item1], [item2], and [item3]. */
data class Tuple3<Super, T1 : Super, T2 : Super, T3 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3
) : Tuple<Super>,
    AllOf3<T1, T2, T3> {
    override fun toList(): List<Super> = listOf(item1, item2, item3)
}

/** Tuple carrying elements [item1] through [item4]. */
data class Tuple4<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4
) : Tuple<Super>,
    AllOf4<T1, T2, T3, T4> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4)
}

/** Tuple carrying elements [item1] through [item5]. */
data class Tuple5<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5
) : Tuple<Super>,
    AllOf5<T1, T2, T3, T4, T5> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5)
}

/** Tuple carrying elements [item1] through [item6]. */
data class Tuple6<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6
) : Tuple<Super>,
    AllOf6<T1, T2, T3, T4, T5, T6> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6)
}

/** Tuple carrying elements [item1] through [item7]. */
data class Tuple7<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6,
    override val item7: T7
) : Tuple<Super>,
    AllOf7<T1, T2, T3, T4, T5, T6, T7> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

/** Tuple carrying elements [item1] through [item8]. */
data class Tuple8<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6,
    override val item7: T7,
    override val item8: T8
) : Tuple<Super>,
    AllOf8<T1, T2, T3, T4, T5, T6, T7, T8> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

/** Tuple carrying elements [item1] through [item9]. */
data class Tuple9<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6,
    override val item7: T7,
    override val item8: T8,
    override val item9: T9
) : Tuple<Super>,
    AllOf9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

/** Tuple carrying elements [item1] through [item10]. */
data class Tuple10<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super, T10 : Super>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6,
    override val item7: T7,
    override val item8: T8,
    override val item9: T9,
    override val item10: T10
) : Tuple<Super>,
    AllOf10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
}

/**
 * Appends [item1] to this tuple.
 *
 * @param item1 element to append.
 * @return tuple containing one element.
 */
operator fun <Super, T1 : Super> Tuple0.plus(item1: T1): Tuple1<Super, T1> = Tuple1(item1)

/**
 * Appends [item2] to this tuple.
 *
 * @param item2 element to append.
 * @return tuple containing two elements.
 */
operator fun <Super, T1 : Super, T2 : Super> Tuple1<*, T1>.plus(item2: T2): Tuple2<Super, T1, T2> = Tuple2(item1, item2)

/**
 * Appends [item3] to this tuple.
 *
 * @param item3 element to append.
 * @return tuple containing three elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super> Tuple2<*, T1, T2>.plus(item3: T3): Tuple3<Super, T1, T2, T3> =
    Tuple3(item1, item2, item3)

/**
 * Appends [item4] to this tuple.
 *
 * @param item4 element to append.
 * @return tuple containing four elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super> Tuple3<*, T1, T2, T3>.plus(
    item4: T4
): Tuple4<Super, T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

/**
 * Appends [item5] to this tuple.
 *
 * @param item5 element to append.
 * @return tuple containing five elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super> Tuple4<*, T1, T2, T3, T4>.plus(
    item5: T5
): Tuple5<Super, T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

/**
 * Appends [item6] to this tuple.
 *
 * @param item6 element to append.
 * @return tuple containing six elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super> Tuple5<*, T1, T2, T3, T4, T5>.plus(
    item6: T6
): Tuple6<Super, T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

/**
 * Appends [item7] to this tuple.
 *
 * @param item7 element to append.
 * @return tuple containing seven elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super> Tuple6<*, T1, T2, T3, T4, T5, T6>.plus(
    item7: T7
): Tuple7<Super, T1, T2, T3, T4, T5, T6, T7> = Tuple7(item1, item2, item3, item4, item5, item6, item7)

/**
 * Appends [item8] to this tuple.
 *
 * @param item8 element to append.
 * @return tuple containing eight elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super> Tuple7<*, T1, T2, T3, T4, T5, T6, T7>.plus(
    item8: T8
): Tuple8<Super, T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

/**
 * Appends [item9] to this tuple.
 *
 * @param item9 element to append.
 * @return tuple containing nine elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super> Tuple8<*, T1, T2, T3, T4, T5, T6, T7, T8>.plus(
    item9: T9
): Tuple9<Super, T1, T2, T3, T4, T5, T6, T7, T8, T9> =
    Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

/**
 * Appends [item10] to this tuple.
 *
 * @param item10 element to append.
 * @return tuple containing ten elements.
 */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super, T10 : Super> Tuple9<*, T1, T2, T3, T4, T5, T6, T7, T8, T9>.plus(
    item10: T10
): Tuple10<Super, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
    Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
