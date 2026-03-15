@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Typed, fixed-arity tuple used throughout the DSL.
 *
 * Tapik uses concrete tuple arities instead of plain lists so each builder step can keep the exact
 * type of every header, parameter, or output slot while still offering [toList] for iteration.
 *
 * @param Super common super type shared by all tuple elements.
 */
sealed interface Tuple<out Super> : Listable<Super>

/** Fixed-arity tuple with no elements. */
data object Tuple0 : Tuple<Nothing>, AllOf0 {
    override fun toList(): List<Nothing> = emptyList()
}

/** Fixed-arity tuple with one typed element. */
@ConsistentCopyVisibility
data class Tuple1<Super, T1 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1
) : Tuple<Super>,
    AllOf1<T1> {
    override fun toList(): List<Super> = listOf(item1)
}

/** Fixed-arity tuple with two typed elements. */
@ConsistentCopyVisibility
data class Tuple2<Super, T1 : Super, T2 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2
) : Tuple<Super>,
    AllOf2<T1, T2> {
    override fun toList(): List<Super> = listOf(item1, item2)
}

/** Fixed-arity tuple with three typed elements. */
@ConsistentCopyVisibility
data class Tuple3<Super, T1 : Super, T2 : Super, T3 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3
) : Tuple<Super>,
    AllOf3<T1, T2, T3> {
    override fun toList(): List<Super> = listOf(item1, item2, item3)
}

/** Fixed-arity tuple with four typed elements. */
@ConsistentCopyVisibility
data class Tuple4<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3,
    /** Fourth tuple element. */
    override val item4: T4
) : Tuple<Super>,
    AllOf4<T1, T2, T3, T4> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4)
}

/** Fixed-arity tuple with five typed elements. */
@ConsistentCopyVisibility
data class Tuple5<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3,
    /** Fourth tuple element. */
    override val item4: T4,
    /** Fifth tuple element. */
    override val item5: T5
) : Tuple<Super>,
    AllOf5<T1, T2, T3, T4, T5> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5)
}

/** Fixed-arity tuple with six typed elements. */
@ConsistentCopyVisibility
data class Tuple6<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3,
    /** Fourth tuple element. */
    override val item4: T4,
    /** Fifth tuple element. */
    override val item5: T5,
    /** Sixth tuple element. */
    override val item6: T6
) : Tuple<Super>,
    AllOf6<T1, T2, T3, T4, T5, T6> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6)
}

/** Fixed-arity tuple with seven typed elements. */
@ConsistentCopyVisibility
data class Tuple7<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3,
    /** Fourth tuple element. */
    override val item4: T4,
    /** Fifth tuple element. */
    override val item5: T5,
    /** Sixth tuple element. */
    override val item6: T6,
    /** Seventh tuple element. */
    override val item7: T7
) : Tuple<Super>,
    AllOf7<T1, T2, T3, T4, T5, T6, T7> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

/** Fixed-arity tuple with eight typed elements. */
@ConsistentCopyVisibility
data class Tuple8<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3,
    /** Fourth tuple element. */
    override val item4: T4,
    /** Fifth tuple element. */
    override val item5: T5,
    /** Sixth tuple element. */
    override val item6: T6,
    /** Seventh tuple element. */
    override val item7: T7,
    /** Eighth tuple element. */
    override val item8: T8
) : Tuple<Super>,
    AllOf8<T1, T2, T3, T4, T5, T6, T7, T8> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7, item8)
}

/** Fixed-arity tuple with nine typed elements. */
@ConsistentCopyVisibility
data class Tuple9<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3,
    /** Fourth tuple element. */
    override val item4: T4,
    /** Fifth tuple element. */
    override val item5: T5,
    /** Sixth tuple element. */
    override val item6: T6,
    /** Seventh tuple element. */
    override val item7: T7,
    /** Eighth tuple element. */
    override val item8: T8,
    /** Ninth tuple element. */
    override val item9: T9
) : Tuple<Super>,
    AllOf9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

/** Fixed-arity tuple with ten typed elements. */
@ConsistentCopyVisibility
data class Tuple10<Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super, T10 : Super> internal constructor(
    /** First tuple element. */
    override val item1: T1,
    /** Second tuple element. */
    override val item2: T2,
    /** Third tuple element. */
    override val item3: T3,
    /** Fourth tuple element. */
    override val item4: T4,
    /** Fifth tuple element. */
    override val item5: T5,
    /** Sixth tuple element. */
    override val item6: T6,
    /** Seventh tuple element. */
    override val item7: T7,
    /** Eighth tuple element. */
    override val item8: T8,
    /** Ninth tuple element. */
    override val item9: T9,
    /** Tenth tuple element. */
    override val item10: T10
) : Tuple<Super>,
    AllOf10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
    override fun toList(): List<Super> = listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
}

/** Appends the first typed element to an empty tuple. */
operator fun <Super, T1 : Super> Tuple0.plus(item1: T1): Tuple1<Super, T1> = Tuple1(item1)

/** Appends another element while preserving the existing tuple slots. */
operator fun <Super, T1 : Super, T2 : Super> Tuple1<*, T1>.plus(item2: T2): Tuple2<Super, T1, T2> = Tuple2(item1, item2)

/** Appends a third typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super> Tuple2<*, T1, T2>.plus(item3: T3): Tuple3<Super, T1, T2, T3> =
    Tuple3(item1, item2, item3)

/** Appends a fourth typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super> Tuple3<*, T1, T2, T3>.plus(
    item4: T4
): Tuple4<Super, T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

/** Appends a fifth typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super> Tuple4<*, T1, T2, T3, T4>.plus(
    item5: T5
): Tuple5<Super, T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

/** Appends a sixth typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super> Tuple5<*, T1, T2, T3, T4, T5>.plus(
    item6: T6
): Tuple6<Super, T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

/** Appends a seventh typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super> Tuple6<*, T1, T2, T3, T4, T5, T6>.plus(
    item7: T7
): Tuple7<Super, T1, T2, T3, T4, T5, T6, T7> = Tuple7(item1, item2, item3, item4, item5, item6, item7)

/** Appends an eighth typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super> Tuple7<*, T1, T2, T3, T4, T5, T6, T7>.plus(
    item8: T8
): Tuple8<Super, T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

/** Appends a ninth typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super> Tuple8<*, T1, T2, T3, T4, T5, T6, T7, T8>.plus(
    item9: T9
): Tuple9<Super, T1, T2, T3, T4, T5, T6, T7, T8, T9> =
    Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

/** Appends a tenth typed element to the tuple. */
operator fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super, T8 : Super, T9 : Super, T10 : Super> Tuple9<*, T1, T2, T3, T4, T5, T6, T7, T8, T9>.plus(
    item10: T10
): Tuple10<Super, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
    Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

/** Returns the empty tuple singleton. */
fun emptyTuple(): Tuple0 = Tuple0

/** Creates a one-element tuple. */
fun <Super, T1 : Super> tupleOf(item1: T1): Tuple1<Super, T1> = Tuple1(item1)

/** Creates a two-element tuple. */
fun <Super, T1 : Super, T2 : Super> tupleOf(
    item1: T1,
    item2: T2
): Tuple2<Super, T1, T2> = Tuple2(item1, item2)

/** Creates a three-element tuple. */
fun <Super, T1 : Super, T2 : Super, T3 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3
): Tuple3<Super, T1, T2, T3> = Tuple3(item1, item2, item3)

/** Creates a four-element tuple. */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4
): Tuple4<Super, T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

/** Creates a five-element tuple. */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4,
    item5: T5
): Tuple5<Super, T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

/** Creates a six-element tuple. */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4,
    item5: T5,
    item6: T6
): Tuple6<Super, T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

/** Creates a seven-element tuple. */
fun <Super, T1 : Super, T2 : Super, T3 : Super, T4 : Super, T5 : Super, T6 : Super, T7 : Super> tupleOf(
    item1: T1,
    item2: T2,
    item3: T3,
    item4: T4,
    item5: T5,
    item6: T6,
    item7: T7
): Tuple7<Super, T1, T2, T3, T4, T5, T6, T7> = Tuple7(item1, item2, item3, item4, item5, item6, item7)

/** Creates an eight-element tuple. */
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

/** Creates a nine-element tuple. */
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

/** Creates a ten-element tuple. */
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
