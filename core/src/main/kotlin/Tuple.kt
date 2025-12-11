@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Marker hierarchy for reusable heterogeneous tuples backed by a shared super type.
 *
 * @param Super common super type for all tuple elements.
 */
sealed interface Tuple<out Super> : Listable<Super>

/** Zero-arity tuple with no elements. */
data object Tuple0 : Tuple<Nothing>, AllOf0 {
    override fun toList(): List<Nothing> = emptyList()
}

/**
 * Tuple carrying a single element [item1].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @property item1 the first element.
 */
@ConsistentCopyVisibility
data class Tuple1<Super, T1 : Super> internal constructor(
    override val item1: T1
) : Tuple<Super>,
    AllOf1<T1> {
    override fun toList(): List<Super> = listOf(item1)
}

/**
 * Tuple carrying elements [item1] and [item2].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @property item1 the first element.
 * @property item2 the second element.
 */
@ConsistentCopyVisibility
data class Tuple2<Super, T1 : Super, T2 : Super> internal constructor(
    override val item1: T1,
    override val item2: T2
) : Tuple<Super>,
    AllOf2<T1, T2> {
    override fun toList(): List<Super> = listOf(item1, item2)
}

/**
 * Tuple carrying elements [item1], [item2], and [item3].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 */
@ConsistentCopyVisibility
data class Tuple3<Super, T1 : Super, T2 : Super, T3 : Super> internal constructor(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3
) : Tuple<Super>,
    AllOf3<T1, T2, T3> {
    override fun toList(): List<Super> = listOf(item1, item2, item3)
}

/**
 * Tuple carrying elements [item1] through [item4].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 * @property item4 the fourth element.
 */
@ConsistentCopyVisibility
data class Tuple4<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super> internal constructor(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4
) : Tuple<Super>,
    AllOf4<T1, T2, T3, T4> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4)
}

/**
 * Tuple carrying elements [item1] through [item5].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 * @property item4 the fourth element.
 * @property item5 the fifth element.
 */
@ConsistentCopyVisibility
data class Tuple5<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super> internal constructor(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5
) : Tuple<Super>,
    AllOf5<T1, T2, T3, T4, T5> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5)
}

/**
 * Tuple carrying elements [item1] through [item6].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 * @property item4 the fourth element.
 * @property item5 the fifth element.
 * @property item6 the sixth element.
 */
@ConsistentCopyVisibility
data class Tuple6<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super> internal constructor(
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

/**
 * Tuple carrying elements [item1] through [item7].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 * @property item4 the fourth element.
 * @property item5 the fifth element.
 * @property item6 the sixth element.
 * @property item7 the seventh element.
 */
@ConsistentCopyVisibility
data class Tuple7<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super> internal constructor(
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

/**
 * Tuple carrying elements [item1] through [item8].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @param T8 type of the eighth element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 * @property item4 the fourth element.
 * @property item5 the fifth element.
 * @property item6 the sixth element.
 * @property item7 the seventh element.
 * @property item8 the eighth element.
 */
@ConsistentCopyVisibility
data class Tuple8<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super> internal constructor(
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

/**
 * Tuple carrying elements [item1] through [item9].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @param T8 type of the eighth element.
 * @param T9 type of the ninth element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 * @property item4 the fourth element.
 * @property item5 the fifth element.
 * @property item6 the sixth element.
 * @property item7 the seventh element.
 * @property item8 the eighth element.
 * @property item9 the ninth element.
 */
@ConsistentCopyVisibility
data class Tuple9<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super> internal constructor(
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

/**
 * Tuple carrying elements [item1] through [item10].
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @param T8 type of the eighth element.
 * @param T9 type of the ninth element.
 * @param T10 type of the tenth element.
 * @property item1 the first element.
 * @property item2 the second element.
 * @property item3 the third element.
 * @property item4 the fourth element.
 * @property item5 the fifth element.
 * @property item6 the sixth element.
 * @property item7 the seventh element.
 * @property item8 the eighth element.
 * @property item9 the ninth element.
 * @property item10 the tenth element.
 */
@ConsistentCopyVisibility
data class Tuple10<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super, T10 : Super> internal constructor(
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

/**
 * Returns an empty tuple.
 *
 * @return an empty [Tuple0].
 */
fun emptyTuple(): Tuple0 = Tuple0

/**
 * Returns a tuple with one element.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param item1 the first element.
 * @return a [Tuple1] containing [item1].
 */
fun <Super, T1 : Super> tupleOf(item1: T1): Tuple1<Super, T1> = Tuple1(item1)

/**
 * Returns a tuple with two elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @return a [Tuple2] containing [item1] and [item2].
 */
fun <Super, T1 : Super, T2 : Super> tupleOf(
    item1: T1,
    item2: T2
): Tuple2<Super, T1, T2> = Tuple2(item1, item2)

/**
 * Returns a tuple with three elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @return a [Tuple3] containing [item1], [item2], and [item3].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3
): Tuple3<Super, T1, T2, T3> = Tuple3(item1, item2, item3)

/**
 * Returns a tuple with four elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @param item4 the fourth element.
 * @return a [Tuple4] containing [item1] through [item4].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4
): Tuple4<Super, T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

/**
 * Returns a tuple with five elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @param item4 the fourth element.
 * @param item5 the fifth element.
 * @return a [Tuple5] containing [item1] through [item5].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4,
    item5: T5
): Tuple5<Super, T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

/**
 * Returns a tuple with six elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @param item4 the fourth element.
 * @param item5 the fifth element.
 * @param item6 the sixth element.
 * @return a [Tuple6] containing [item1] through [item6].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4,
    item5: T5,
    item6: T6
): Tuple6<Super, T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

/**
 * Returns a tuple with seven elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @param item4 the fourth element.
 * @param item5 the fifth element.
 * @param item6 the sixth element.
 * @param item7 the seventh element.
 * @return a [Tuple7] containing [item1] through [item7].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4,
    item5: T5,
    item6: T6,
    item7: T7
): Tuple7<Super, T1, T2, T3, T4, T5, T6, T7> = Tuple7(item1, item2, item3, item4, item5, item6, item7)

/**
 * Returns a tuple with eight elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @param T8 type of the eighth element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @param item4 the fourth element.
 * @param item5 the fifth element.
 * @param item6 the sixth element.
 * @param item7 the seventh element.
 * @param item8 the eighth element.
 * @return a [Tuple8] containing [item1] through [item8].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4,
    item5: T5,
    item6: T6,
    item7: T7,
    item8: T8
): Tuple8<Super, T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

/**
 * Returns a tuple with nine elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @param T8 type of the eighth element.
 * @param T9 type of the ninth element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @param item4 the fourth element.
 * @param item5 the fifth element.
 * @param item6 the sixth element.
 * @param item7 the seventh element.
 * @param item8 the eighth element.
 * @param item9 the ninth element.
 * @return a [Tuple9] containing [item1] through [item9].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super> tupleOf(
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
 * Returns a tuple with ten elements.
 *
 * @param Super common super type for all tuple elements.
 * @param T1 type of the first element.
 * @param T2 type of the second element.
 * @param T3 type of the third element.
 * @param T4 type of the fourth element.
 * @param T5 type of the fifth element.
 * @param T6 type of the sixth element.
 * @param T7 type of the seventh element.
 * @param T8 type of the eighth element.
 * @param T9 type of the ninth element.
 * @param T10 type of the tenth element.
 * @param item1 the first element.
 * @param item2 the second element.
 * @param item3 the third element.
 * @param item4 the fourth element.
 * @param item5 the fifth element.
 * @param item6 the sixth element.
 * @param item7 the seventh element.
 * @param item8 the eighth element.
 * @param item9 the ninth element.
 * @param item10 the tenth element.
 * @return a [Tuple10] containing [item1] through [item10].
 */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super, T10 : Super> tupleOf(
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
    Tuple10(
        item1,
        item2,
        item3,
        item4,
        item5,
        item6,
        item7,
        item8,
        item9,
        item10
    )
