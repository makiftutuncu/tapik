package dev.akif.tapik.tuple

sealed interface Tuple<out T> {
    fun toList(): List<T>
}

class Tuple0<T> : Tuple<T> {
    companion object {
        fun <T> instance(): Tuple0<T> = Tuple0()
    }

    operator fun <T, T1: T> plus(item1: T1): Tuple1<T, T1> = Tuple1(item1)

    override fun toList(): List<Nothing> = emptyList()
}

data class Tuple1<T, T1: T>(val item1: T1) : Tuple<T> {
    operator fun <T2: T> plus(item2: T2): Tuple2<T, T1, T2> = Tuple2(item1, item2)

    override fun toList(): List<T> = listOf(item1)
}

data class Tuple2<T, T1: T, T2: T>(val item1: T1, val item2: T2) : Tuple<T> {
    operator fun <T3: T> plus(item3: T3): Tuple3<T, T1, T2, T3> = Tuple3(item1, item2, item3)

    override fun toList(): List<T> = listOf(item1, item2)
}

data class Tuple3<T, T1: T, T2: T, T3: T>(val item1: T1, val item2: T2, val item3: T3) : Tuple<T> {
    operator fun <T4: T> plus(item4: T4): Tuple4<T, T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)

    override fun toList(): List<T> = listOf(item1, item2, item3)
}

data class Tuple4<T, T1: T, T2: T, T3: T, T4: T>(val item1: T1, val item2: T2, val item3: T3, val item4: T4) : Tuple<T> {
    operator fun <T5: T> plus(item5: T5): Tuple5<T, T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)

    override fun toList(): List<T> = listOf(item1, item2, item3, item4)
}

data class Tuple5<T, T1: T, T2: T, T3: T, T4: T, T5: T>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5) : Tuple<T> {
    operator fun <T6: T> plus(item6: T6): Tuple6<T, T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)

    override fun toList(): List<T> = listOf(item1, item2, item3, item4, item5)
}

data class Tuple6<T, T1: T, T2: T, T3: T, T4: T, T5: T, T6: T>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6) : Tuple<T> {
    operator fun <T7: T> plus(item7: T7): Tuple7<T, T1, T2, T3, T4, T5, T6, T7> = Tuple7(item1, item2, item3, item4, item5, item6, item7)

    override fun toList(): List<T> = listOf(item1, item2, item3, item4, item5, item6)
}

data class Tuple7<T, T1: T, T2: T, T3: T, T4: T, T5: T, T6: T, T7: T>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7) : Tuple<T> {
    operator fun <T8: T> plus(item8: T8): Tuple8<T, T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)

    override fun toList(): List<T> = listOf(item1, item2, item3, item4, item5, item6, item7)
}

data class Tuple8<T, T1: T, T2: T, T3: T, T4: T, T5: T, T6: T, T7: T, T8: T>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7, val item8: T8) : Tuple<T> {
    operator fun <T9: T> plus(item9: T9): Tuple9<T, T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)

    override fun toList(): List<T> = listOf(item1, item2, item3, item4, item5, item6, item8)
}

data class Tuple9<T, T1: T, T2: T, T3: T, T4: T, T5: T, T6: T, T7: T, T8: T, T9: T>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7, val item8: T8, val item9: T9) : Tuple<T> {
    operator fun <T10: T> plus(item10: T10): Tuple10<T, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)

    override fun toList(): List<T> = listOf(item1, item2, item3, item4, item5, item6, item8, item9)
}

data class Tuple10<T, T1: T, T2: T, T3: T, T4: T, T5: T, T6: T, T7: T, T8: T, T9: T, T10: T>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7, val item8: T8, val item9: T9, val item10: T10) : Tuple<T> {
    override fun toList(): List<T> = listOf(item1, item2, item3, item4, item5, item6, item8, item9, item10)
}
