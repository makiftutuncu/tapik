package dev.akif.tapik

/**
 * Marker hierarchy representing heterogeneous tuples backed by strongly-typed fields.
 *
 * The DSL constructs endpoint inputs and outputs through these tuples so that generators can
 * easily reason about arity and element types.
 */
sealed interface Tuple

/**
 * Flattens the tuple into a list containing values assignable to [T].
 *
 * This helper performs runtime checks to guard against extracting the wrong tuple arity.
 *
 * @param T expected element type when expanding the tuple.
 * @return a list containing all items that are assignable to [T] in their original order.
 * @throws AssertionError when the number of extracted elements does not match the tuple arity.
 * @see Tuple
 */
inline fun <reified T> Tuple.toList(): List<T> =
    when (this) {
        Tuple0 -> emptyList()
        is Tuple1<*> ->
            this.toList<T>().also {
                assert(it.size == 1) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple1" }
            }
        is Tuple2<*, *> ->
            this.toList<T>().also {
                assert(it.size == 2) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple2" }
            }
        is Tuple3<*, *, *> ->
            this.toList<T>().also {
                assert(it.size == 3) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple3" }
            }
        is Tuple4<*, *, *, *> ->
            this.toList<T>().also {
                assert(it.size == 4) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple4" }
            }
        is Tuple5<*, *, *, *, *> ->
            this.toList<T>().also {
                assert(it.size == 5) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple5" }
            }
        is Tuple6<*, *, *, *, *, *> ->
            this.toList<T>().also {
                assert(it.size == 6) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple6" }
            }
        is Tuple7<*, *, *, *, *, *, *> ->
            this.toList<T>().also {
                assert(it.size == 7) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple7" }
            }
        is Tuple8<*, *, *, *, *, *, *, *> ->
            this.toList<T>().also {
                assert(it.size == 8) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple8" }
            }
        is Tuple9<*, *, *, *, *, *, *, *, *> ->
            this.toList<T>().also {
                assert(it.size == 9) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple9" }
            }
        is Tuple10<*, *, *, *, *, *, *, *, *, *> ->
            this.toList<T>().also {
                assert(it.size == 10) { "Found ${it.size} items of type ${T::class.qualifiedName} in a Tuple10" }
            }
    }

/** Zero-arity tuple. */
data object Tuple0 : Tuple, AllOf0 {
    /**
     * Creates a [Tuple1] by appending [item1] to the empty tuple.
     *
     * @param item1 value to become the first element.
     * @return a tuple containing a single value.
     * @see Tuple1
     */
    operator fun <T1> plus(item1: T1): Tuple1<T1> = Tuple1(item1)
}

/** Tuple containing a single [item1]. */
data class Tuple1<T1>(
    override val item1: T1
) : Tuple,
    AllOf1<T1> {
    /**
     * Creates a [Tuple2] by appending [item2].
     *
     * @param item2 value to become the second element.
     * @return a tuple containing two values.
     * @see Tuple2
     */
    operator fun <T2> plus(item2: T2): Tuple2<T1, T2> = Tuple2(item1, item2)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return the single element of the tuple if it is assignable to [T].
     */
    inline fun <reified T> toList(): List<T> = listOf(item1).filterIsInstance<T>()
}

/** Tuple containing [item1] and [item2]. */
data class Tuple2<T1, T2>(
    override val item1: T1,
    override val item2: T2
) : Tuple,
    AllOf2<T1, T2> {
    /**
     * Creates a [Tuple3] by appending [item3].
     *
     * @param item3 value to become the third element.
     * @return a tuple containing three values.
     * @see Tuple3
     */
    operator fun <T3> plus(item3: T3): Tuple3<T1, T2, T3> = Tuple3(item1, item2, item3)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> = listOf(item1, item2).filterIsInstance<T>()
}

/** Tuple containing [item1], [item2], and [item3]. */
data class Tuple3<T1, T2, T3>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3
) : Tuple,
    AllOf3<T1, T2, T3> {
    /**
     * Creates a [Tuple4] by appending [item4].
     *
     * @param item4 value to become the fourth element.
     * @return a tuple containing four values.
     * @see Tuple4
     */
    operator fun <T4> plus(item4: T4): Tuple4<T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3).filterIsInstance<T>()
}

/** Tuple containing [item1] through [item4]. */
data class Tuple4<T1, T2, T3, T4>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4
) : Tuple,
    AllOf4<T1, T2, T3, T4> {
    /**
     * Creates a [Tuple5] by appending [item5].
     *
     * @param item5 value to become the fifth element.
     * @return a tuple containing five values.
     * @see Tuple5
     */
    operator fun <T5> plus(item5: T5): Tuple5<T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3, item4).filterIsInstance<T>()
}

/** Tuple containing [item1] through [item5]. */
data class Tuple5<T1, T2, T3, T4, T5>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5
) : Tuple,
    AllOf5<T1, T2, T3, T4, T5> {
    /**
     * Creates a [Tuple6] by appending [item6].
     *
     * @param item6 value to become the sixth element.
     * @return a tuple containing six values.
     * @see Tuple6
     */
    operator fun <T6> plus(item6: T6): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3, item4, item5).filterIsInstance<T>()
}

/** Tuple containing [item1] through [item6]. */
data class Tuple6<T1, T2, T3, T4, T5, T6>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6
) : Tuple,
    AllOf6<T1, T2, T3, T4, T5, T6> {
    /**
     * Creates a [Tuple7] by appending [item7].
     *
     * @param item7 value to become the seventh element.
     * @return a tuple containing seven values.
     * @see Tuple7
     */
    operator fun <T7> plus(item7: T7): Tuple7<T1, T2, T3, T4, T5, T6, T7> =
        Tuple7(item1, item2, item3, item4, item5, item6, item7)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3, item4, item5, item6).filterIsInstance<T>()
}

/** Tuple containing [item1] through [item7]. */
data class Tuple7<T1, T2, T3, T4, T5, T6, T7>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6,
    override val item7: T7
) : Tuple,
    AllOf7<T1, T2, T3, T4, T5, T6, T7> {
    /**
     * Creates a [Tuple8] by appending [item8].
     *
     * @param item8 value to become the eighth element.
     * @return a tuple containing eight values.
     * @see Tuple8
     */
    operator fun <T8> plus(item8: T8): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> =
        Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7).filterIsInstance<T>()
}

/** Tuple containing [item1] through [item8]. */
data class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6,
    override val item7: T7,
    override val item8: T8
) : Tuple,
    AllOf8<T1, T2, T3, T4, T5, T6, T7, T8> {
    /**
     * Creates a [Tuple9] by appending [item9].
     *
     * @param item9 value to become the ninth element.
     * @return a tuple containing nine values.
     * @see Tuple9
     */
    operator fun <T9> plus(item9: T9): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> =
        Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8).filterIsInstance<T>()
}

/** Tuple containing [item1] through [item9]. */
data class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6,
    override val item7: T7,
    override val item8: T8,
    override val item9: T9
) : Tuple,
    AllOf9<T1, T2, T3, T4, T5, T6, T7, T8, T9> {
    /**
     * Creates a [Tuple10] by appending [item10].
     *
     * @param item10 value to become the tenth element.
     * @return a tuple containing ten values.
     * @see Tuple10
     */
    operator fun <T10> plus(item10: T10): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
        Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9).filterIsInstance<T>()
}

/** Tuple containing [item1] through [item10]. */
data class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(
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
) : Tuple,
    AllOf10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> {
    /**
     * Lists the items that are assignable to [T].
     *
     * @param T expected element type when expanding the tuple.
     * @return elements assignable to [T].
     */
    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10).filterIsInstance<T>()
}
