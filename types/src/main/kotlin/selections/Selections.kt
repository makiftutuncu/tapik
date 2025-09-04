// This file is auto-generated. Do not edit manually as your changes will be lost.
package dev.akif.tapik.selections

sealed interface Selection

sealed interface Selection1<out T1>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection1<T>

    fun <R> select(
        when1: (T1) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
        }
}

sealed interface Selection2<out T1, out T2>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection2<T, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection2<Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
        }
}

sealed interface Selection3<out T1, out T2, out T3>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection3<T, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection3<Nothing, T, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection3<Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
        }
}

sealed interface Selection4<out T1, out T2, out T3, out T4>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection4<T, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection4<Nothing, T, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection4<Nothing, Nothing, T, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection4<Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
        }
}

sealed interface Selection5<out T1, out T2, out T3, out T4, out T5>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection5<T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection5<Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection5<Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection5<Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection5<Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
        }
}

sealed interface Selection6<out T1, out T2, out T3, out T4, out T5, out T6>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection6<T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection6<Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection6<Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection6<Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection6<Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection6<Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
        }
}

sealed interface Selection7<out T1, out T2, out T3, out T4, out T5, out T6, out T7>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection7<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection7<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection7<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection7<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection7<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection7<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection7<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
        }
}

sealed interface Selection8<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection8<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection8<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection8<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection8<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection8<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection8<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
        }
}

sealed interface Selection9<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection9<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection9<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection9<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection9<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection9<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection9<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
        }
}

sealed interface Selection10<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection10<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection10<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection10<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection10<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection10<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection10<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option10<T>(val value: T): Selection10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R,
        when10: (T10) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
            is Option10<T10> -> when10(value)
        }
}

sealed interface Selection11<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10, out T11>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection11<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection11<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection11<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection11<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection11<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection11<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection11<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection11<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection11<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option10<T>(val value: T): Selection11<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option11<T>(val value: T): Selection11<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R,
        when10: (T10) -> R,
        when11: (T11) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
            is Option10<T10> -> when10(value)
            is Option11<T11> -> when11(value)
        }
}

sealed interface Selection12<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10, out T11, out T12>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection12<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection12<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection12<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection12<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option10<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option11<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option12<T>(val value: T): Selection12<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R,
        when10: (T10) -> R,
        when11: (T11) -> R,
        when12: (T12) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
            is Option10<T10> -> when10(value)
            is Option11<T11> -> when11(value)
            is Option12<T12> -> when12(value)
        }
}

sealed interface Selection13<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10, out T11, out T12, out T13>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection13<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection13<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection13<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection13<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option10<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option11<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option12<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option13<T>(val value: T): Selection13<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R,
        when10: (T10) -> R,
        when11: (T11) -> R,
        when12: (T12) -> R,
        when13: (T13) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
            is Option10<T10> -> when10(value)
            is Option11<T11> -> when11(value)
            is Option12<T12> -> when12(value)
            is Option13<T13> -> when13(value)
        }
}

sealed interface Selection14<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10, out T11, out T12, out T13, out T14>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection14<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection14<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection14<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection14<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option10<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option11<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option12<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option13<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option14<T>(val value: T): Selection14<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R,
        when10: (T10) -> R,
        when11: (T11) -> R,
        when12: (T12) -> R,
        when13: (T13) -> R,
        when14: (T14) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
            is Option10<T10> -> when10(value)
            is Option11<T11> -> when11(value)
            is Option12<T12> -> when12(value)
            is Option13<T13> -> when13(value)
            is Option14<T14> -> when14(value)
        }
}

sealed interface Selection15<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10, out T11, out T12, out T13, out T14, out T15>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection15<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection15<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection15<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection15<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option10<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option11<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option12<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option13<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option14<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option15<T>(val value: T): Selection15<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R,
        when10: (T10) -> R,
        when11: (T11) -> R,
        when12: (T12) -> R,
        when13: (T13) -> R,
        when14: (T14) -> R,
        when15: (T15) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
            is Option10<T10> -> when10(value)
            is Option11<T11> -> when11(value)
            is Option12<T12> -> when12(value)
            is Option13<T13> -> when13(value)
            is Option14<T14> -> when14(value)
            is Option15<T15> -> when15(value)
        }
}

sealed interface Selection16<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10, out T11, out T12, out T13, out T14, out T15, out T16>: Selection {
    @JvmInline value class Option1<T>(val value: T): Selection16<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option2<T>(val value: T): Selection16<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option3<T>(val value: T): Selection16<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option4<T>(val value: T): Selection16<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option5<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option6<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option7<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option8<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option9<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option10<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option11<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option12<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Option13<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Option14<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Option15<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Option16<T>(val value: T): Selection16<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R,
        when3: (T3) -> R,
        when4: (T4) -> R,
        when5: (T5) -> R,
        when6: (T6) -> R,
        when7: (T7) -> R,
        when8: (T8) -> R,
        when9: (T9) -> R,
        when10: (T10) -> R,
        when11: (T11) -> R,
        when12: (T12) -> R,
        when13: (T13) -> R,
        when14: (T14) -> R,
        when15: (T15) -> R,
        when16: (T16) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
            is Option3<T3> -> when3(value)
            is Option4<T4> -> when4(value)
            is Option5<T5> -> when5(value)
            is Option6<T6> -> when6(value)
            is Option7<T7> -> when7(value)
            is Option8<T8> -> when8(value)
            is Option9<T9> -> when9(value)
            is Option10<T10> -> when10(value)
            is Option11<T11> -> when11(value)
            is Option12<T12> -> when12(value)
            is Option13<T13> -> when13(value)
            is Option14<T14> -> when14(value)
            is Option15<T15> -> when15(value)
            is Option16<T16> -> when16(value)
        }
}
