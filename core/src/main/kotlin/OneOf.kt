package dev.akif.tapik

sealed interface OneOf

sealed interface OneOf2<out T1, out T2> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf2<T, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf2<Nothing, T>

    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
        }
}

sealed interface OneOf3<out T1, out T2, out T3> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf3<T, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf3<Nothing, T, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf3<Nothing, Nothing, T>

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

sealed interface OneOf4<out T1, out T2, out T3, out T4> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf4<T, Nothing, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf4<Nothing, T, Nothing, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf4<Nothing, Nothing, T, Nothing>

    @JvmInline value class Option4<T>(
        val value: T
    ) : OneOf4<Nothing, Nothing, Nothing, T>

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

sealed interface OneOf5<out T1, out T2, out T3, out T4, out T5> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf5<T, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf5<Nothing, T, Nothing, Nothing, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf5<Nothing, Nothing, T, Nothing, Nothing>

    @JvmInline value class Option4<T>(
        val value: T
    ) : OneOf5<Nothing, Nothing, Nothing, T, Nothing>

    @JvmInline value class Option5<T>(
        val value: T
    ) : OneOf5<Nothing, Nothing, Nothing, Nothing, T>

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

sealed interface OneOf6<out T1, out T2, out T3, out T4, out T5, out T6> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf6<T, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf6<Nothing, T, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf6<Nothing, Nothing, T, Nothing, Nothing, Nothing>

    @JvmInline value class Option4<T>(
        val value: T
    ) : OneOf6<Nothing, Nothing, Nothing, T, Nothing, Nothing>

    @JvmInline value class Option5<T>(
        val value: T
    ) : OneOf6<Nothing, Nothing, Nothing, Nothing, T, Nothing>

    @JvmInline value class Option6<T>(
        val value: T
    ) : OneOf6<Nothing, Nothing, Nothing, Nothing, Nothing, T>

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

sealed interface OneOf7<out T1, out T2, out T3, out T4, out T5, out T6, out T7> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf7<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf7<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf7<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option4<T>(
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    @JvmInline value class Option5<T>(
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    @JvmInline value class Option6<T>(
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    @JvmInline value class Option7<T>(
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

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

sealed interface OneOf8<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf8<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf8<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf8<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option4<T>(
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option5<T>(
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    @JvmInline value class Option6<T>(
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    @JvmInline value class Option7<T>(
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    @JvmInline value class Option8<T>(
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

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

sealed interface OneOf9<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf9<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf9<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf9<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option4<T>(
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option5<T>(
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option6<T>(
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    @JvmInline value class Option7<T>(
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    @JvmInline value class Option8<T>(
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    @JvmInline value class Option9<T>(
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

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

sealed interface OneOf10<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10> : OneOf {
    @JvmInline value class Option1<T>(
        val value: T
    ) : OneOf10<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option2<T>(
        val value: T
    ) : OneOf10<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option3<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option4<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option5<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option6<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    @JvmInline value class Option7<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    @JvmInline value class Option8<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    @JvmInline value class Option9<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    @JvmInline value class Option10<T>(
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

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
