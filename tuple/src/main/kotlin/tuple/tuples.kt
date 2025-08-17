package dev.akif.tapik.tuple

sealed interface Tuple

data object Tuple0 : Tuple {
    operator fun <T1> plus(item1: T1): Tuple1<T1> = Tuple1(item1)
}

data class Tuple1<T1>(val item1: T1) : Tuple {
    operator fun <T2> plus(item2: T2): Tuple2<T1, T2> = Tuple2(item1, item2)
}

data class Tuple2<T1, T2>(val item1: T1, val item2: T2) : Tuple {
    operator fun <T3> plus(item3: T3): Tuple3<T1, T2, T3> = Tuple3(item1, item2, item3)
}

data class Tuple3<T1, T2, T3>(val item1: T1, val item2: T2, val item3: T3) : Tuple {
    operator fun <T4> plus(item4: T4): Tuple4<T1, T2, T3, T4> = Tuple4(item1, item2, item3, item4)
}

data class Tuple4<T1, T2, T3, T4>(val item1: T1, val item2: T2, val item3: T3, val item4: T4) : Tuple {
    operator fun <T5> plus(item5: T5): Tuple5<T1, T2, T3, T4, T5> = Tuple5(item1, item2, item3, item4, item5)
}

data class Tuple5<T1, T2, T3, T4, T5>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5) : Tuple {
    operator fun <T6> plus(item6: T6): Tuple6<T1, T2, T3, T4, T5, T6> = Tuple6(item1, item2, item3, item4, item5, item6)
}

data class Tuple6<T1, T2, T3, T4, T5, T6>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6) : Tuple {
    operator fun <T7> plus(item7: T7): Tuple7<T1, T2, T3, T4, T5, T6, T7> = Tuple7(item1, item2, item3, item4, item5, item6, item7)
}

data class Tuple7<T1, T2, T3, T4, T5, T6, T7>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7) : Tuple {
    operator fun <T8> plus(item8: T8): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8(item1, item2, item3, item4, item5, item6, item7, item8)
}

data class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7, val item8: T8) : Tuple {
    operator fun <T9> plus(item9: T9): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9(item1, item2, item3, item4, item5, item6, item7, item8, item9)
}

data class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7, val item8: T8, val item9: T9) : Tuple {
    operator fun <T10> plus(item10: T10): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10)
}

data class Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>(val item1: T1, val item2: T2, val item3: T3, val item4: T4, val item5: T5, val item6: T6, val item7: T7, val item8: T8, val item9: T9, val item10: T10) : Tuple
