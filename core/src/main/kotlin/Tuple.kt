package dev.akif.tapik

/** An ordered, heterogeneous collection whose element types are retained in its concrete subtype. */
sealed interface Tuple<out T> {
    /** Elements in declaration order, widened to their shared [T] type. */
    val values: List<T>
}

/** The empty tuple. */
data object Tuple0 : Tuple<Nothing> {
    override val values: List<Nothing> = emptyList()
}

/** A tuple containing one value. */
data class Tuple1<T, T1 : T>(
    val _1: T1
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1)
}

/** A tuple containing two values. */
data class Tuple2<T, T1 : T, T2 : T>(
    val _1: T1,
    val _2: T2
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1, _2)
}

/** A tuple containing three values. */
data class Tuple3<T, T1 : T, T2 : T, T3 : T>(
    val _1: T1,
    val _2: T2,
    val _3: T3
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1, _2, _3)
}

/** A tuple containing four values. */
data class Tuple4<T, T1 : T, T2 : T, T3 : T, T4 : T>(
    val _1: T1,
    val _2: T2,
    val _3: T3,
    val _4: T4
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1, _2, _3, _4)
}

/** A tuple containing five values. */
data class Tuple5<T, T1 : T, T2 : T, T3 : T, T4 : T, T5 : T>(
    val _1: T1,
    val _2: T2,
    val _3: T3,
    val _4: T4,
    val _5: T5
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1, _2, _3, _4, _5)
}

/** A tuple containing six values. */
data class Tuple6<T, T1 : T, T2 : T, T3 : T, T4 : T, T5 : T, T6 : T>(
    val _1: T1,
    val _2: T2,
    val _3: T3,
    val _4: T4,
    val _5: T5,
    val _6: T6
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1, _2, _3, _4, _5, _6)
}

/** A tuple containing seven values. */
data class Tuple7<
    T,
    T1 : T,
    T2 : T,
    T3 : T,
    T4 : T,
    T5 : T,
    T6 : T,
    T7 : T
>(
    val _1: T1,
    val _2: T2,
    val _3: T3,
    val _4: T4,
    val _5: T5,
    val _6: T6,
    val _7: T7
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1, _2, _3, _4, _5, _6, _7)
}

/** A tuple containing eight values, the maximum supported arity. */
data class Tuple8<
    T,
    T1 : T,
    T2 : T,
    T3 : T,
    T4 : T,
    T5 : T,
    T6 : T,
    T7 : T,
    T8 : T
>(
    val _1: T1,
    val _2: T2,
    val _3: T3,
    val _4: T4,
    val _5: T5,
    val _6: T6,
    val _7: T7,
    val _8: T8
) : Tuple<T> {
    override val values: List<T>
        get() = listOf(_1, _2, _3, _4, _5, _6, _7, _8)
}

/** Starts a tuple with [_1]. */
infix fun <T, T1 : T> Tuple0.and(_1: T1): Tuple1<T, T1> = Tuple1(_1)

/** Appends [_2] while retaining the first element's concrete type. */
infix fun <T, T1 : T, T2 : T> Tuple1<*, T1>.and(_2: T2): Tuple2<T, T1, T2> = Tuple2(_1, _2)

/** Appends [_3] while retaining the existing elements' concrete types. */
infix fun <T, T1 : T, T2 : T, T3 : T> Tuple2<*, T1, T2>.and(_3: T3): Tuple3<T, T1, T2, T3> =
    Tuple3(_1, _2, _3)

/** Appends [_4] while retaining the existing elements' concrete types. */
infix fun <T, T1 : T, T2 : T, T3 : T, T4 : T> Tuple3<*, T1, T2, T3>.and(
    _4: T4
): Tuple4<T, T1, T2, T3, T4> = Tuple4(_1, _2, _3, _4)

/** Appends [_5] while retaining the existing elements' concrete types. */
infix fun <T, T1 : T, T2 : T, T3 : T, T4 : T, T5 : T> Tuple4<*, T1, T2, T3, T4>.and(
    _5: T5
): Tuple5<T, T1, T2, T3, T4, T5> = Tuple5(_1, _2, _3, _4, _5)

/** Appends [_6] while retaining the existing elements' concrete types. */
infix fun <T, T1 : T, T2 : T, T3 : T, T4 : T, T5 : T, T6 : T> Tuple5<*, T1, T2, T3, T4, T5>.and(
    _6: T6
): Tuple6<T, T1, T2, T3, T4, T5, T6> = Tuple6(_1, _2, _3, _4, _5, _6)

/** Appends [_7] while retaining the existing elements' concrete types. */
infix fun <T, T1 : T, T2 : T, T3 : T, T4 : T, T5 : T, T6 : T, T7 : T>
    Tuple6<*, T1, T2, T3, T4, T5, T6>.and(_7: T7): Tuple7<T, T1, T2, T3, T4, T5, T6, T7> =
        Tuple7(_1, _2, _3, _4, _5, _6, _7)

/** Appends [_8] while retaining the existing elements' concrete types. */
infix fun <T, T1 : T, T2 : T, T3 : T, T4 : T, T5 : T, T6 : T, T7 : T, T8 : T>
    Tuple7<*, T1, T2, T3, T4, T5, T6, T7>.and(_8: T8): Tuple8<T, T1, T2, T3, T4, T5, T6, T7, T8> =
        Tuple8(_1, _2, _3, _4, _5, _6, _7, _8)
