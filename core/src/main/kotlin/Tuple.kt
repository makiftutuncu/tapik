package dev.akif.tapik

sealed interface Tuple

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

data object Tuple0 : Tuple, AllOf0 {
    operator fun <T1> plus(item1: T1): Tuple1<T1> = Tuple1(item1)
}

data class Tuple1<T1>(
    override val item1: T1
) : Tuple,
    AllOf1<T1> {
    operator fun <T2> plus(item2: T2): Tuple2<T1, T2> = Tuple2(item1, item2)

    inline fun <reified T> toList(): List<T> = listOf(item1).filterIsInstance<T>()
}

data class Tuple2<T1, T2>(
    override val item1: T1,
    override val item2: T2
) : Tuple,
    AllOf2<T1, T2> {
    operator fun <T3> plus(item3: T3): Tuple3<T1, T2, T3> = Tuple3(item1, item2, item3)

    inline fun <reified T> toList(): List<T> = listOf(item1, item2).filterIsInstance<T>()
}

data class Tuple3<T1, T2, T3>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3
) : Tuple,
    AllOf3<T1, T2, T3> {
    operator fun <T4> plus(item4: T4): Tuple4<T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3).filterIsInstance<T>()
}

data class Tuple4<T1, T2, T3, T4>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4
) : Tuple,
    AllOf4<T1, T2, T3, T4> {
    operator fun <T5> plus(item5: T5): Tuple5<T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3, item4).filterIsInstance<T>()
}

data class Tuple5<T1, T2, T3, T4, T5>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5
) : Tuple,
    AllOf5<T1, T2, T3, T4, T5> {
    operator fun <T6> plus(item6: T6): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3, item4, item5).filterIsInstance<T>()
}

data class Tuple6<T1, T2, T3, T4, T5, T6>(
    override val item1: T1,
    override val item2: T2,
    override val item3: T3,
    override val item4: T4,
    override val item5: T5,
    override val item6: T6
) : Tuple,
    AllOf6<T1, T2, T3, T4, T5, T6> {
    operator fun <T7> plus(item7: T7): Tuple7<T1, T2, T3, T4, T5, T6, T7> =
        Tuple7(item1, item2, item3, item4, item5, item6, item7)

    inline fun <reified T> toList(): List<T> = listOf(item1, item2, item3, item4, item5, item6).filterIsInstance<T>()
}

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
    operator fun <T8> plus(item8: T8): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> =
        Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7).filterIsInstance<T>()
}

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
    operator fun <T9> plus(item9: T9): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> =
        Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8).filterIsInstance<T>()
}

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
    operator fun <T10> plus(item10: T10): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
        Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9).filterIsInstance<T>()
}

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
    inline fun <reified T> toList(): List<T> =
        listOf(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10).filterIsInstance<T>()
}
