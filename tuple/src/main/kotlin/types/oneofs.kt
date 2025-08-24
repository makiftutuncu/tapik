package dev.akif.tapik.types

sealed interface OneOf

sealed interface OneOf2<out T1, out T2> : OneOf {
    @JvmInline
    value class Value1<T>(val value: T) : OneOf2<T, Nothing>

    @JvmInline
    value class Value2<T>(val value: T) : OneOf2<Nothing, T>

    fun <R> handle(if1: (T1) -> R, if2: (T2) -> R): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
        }
}

sealed interface OneOf3<out T1, out T2, out T3> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf3<T, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf3<Nothing, T, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf3<Nothing, Nothing, T>

    fun <R> handle(if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
        }
}

sealed interface OneOf4<out T1, out T2, out T3, out T4> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf4<T, Nothing, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf4<Nothing, T, Nothing, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf4<Nothing, Nothing, T, Nothing>
    @JvmInline value class Value4<T>(val value: T) : OneOf4<Nothing, Nothing, Nothing, T>

    fun <R> handle(if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R, if4: (T4) -> R): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
            is Value4<T4> -> if4(value)
        }
}

sealed interface OneOf5<out T1, out T2, out T3, out T4, out T5> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf5<T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf5<Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf5<Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Value4<T>(val value: T) : OneOf5<Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Value5<T>(val value: T) : OneOf5<Nothing, Nothing, Nothing, Nothing, T>

    fun <R> handle(
        if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R,
        if4: (T4) -> R, if5: (T5) -> R
    ): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
            is Value4<T4> -> if4(value)
            is Value5<T5> -> if5(value)
        }
}

sealed interface OneOf6<out T1, out T2, out T3, out T4, out T5, out T6> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf6<T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf6<Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf6<Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Value4<T>(val value: T) : OneOf6<Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Value5<T>(val value: T) : OneOf6<Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Value6<T>(val value: T) : OneOf6<Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> handle(
        if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R,
        if4: (T4) -> R, if5: (T5) -> R, if6: (T6) -> R
    ): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
            is Value4<T4> -> if4(value)
            is Value5<T5> -> if5(value)
            is Value6<T6> -> if6(value)
        }
}

sealed interface OneOf7<out T1, out T2, out T3, out T4, out T5, out T6, out T7> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf7<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf7<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf7<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value4<T>(val value: T) : OneOf7<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Value5<T>(val value: T) : OneOf7<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Value6<T>(val value: T) : OneOf7<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Value7<T>(val value: T) : OneOf7<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> handle(
        if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R,
        if4: (T4) -> R, if5: (T5) -> R, if6: (T6) -> R, if7: (T7) -> R
    ): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
            is Value4<T4> -> if4(value)
            is Value5<T5> -> if5(value)
            is Value6<T6> -> if6(value)
            is Value7<T7> -> if7(value)
        }
}

sealed interface OneOf8<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf8<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf8<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf8<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value4<T>(val value: T) : OneOf8<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value5<T>(val value: T) : OneOf8<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Value6<T>(val value: T) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Value7<T>(val value: T) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Value8<T>(val value: T) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> handle(
        if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R, if4: (T4) -> R,
        if5: (T5) -> R, if6: (T6) -> R, if7: (T7) -> R, if8: (T8) -> R
    ): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
            is Value4<T4> -> if4(value)
            is Value5<T5> -> if5(value)
            is Value6<T6> -> if6(value)
            is Value7<T7> -> if7(value)
            is Value8<T8> -> if8(value)
        }
}

sealed interface OneOf9<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf9<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf9<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf9<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value4<T>(val value: T) : OneOf9<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value5<T>(val value: T) : OneOf9<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value6<T>(val value: T) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Value7<T>(val value: T) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Value8<T>(val value: T) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Value9<T>(val value: T) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> handle(
        if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R, if4: (T4) -> R,
        if5: (T5) -> R, if6: (T6) -> R, if7: (T7) -> R, if8: (T8) -> R, if9: (T9) -> R
    ): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
            is Value4<T4> -> if4(value)
            is Value5<T5> -> if5(value)
            is Value6<T6> -> if6(value)
            is Value7<T7> -> if7(value)
            is Value8<T8> -> if8(value)
            is Value9<T9> -> if9(value)
        }
}

sealed interface OneOf10<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10> : OneOf {
    @JvmInline value class Value1<T>(val value: T) : OneOf10<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value2<T>(val value: T) : OneOf10<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value3<T>(val value: T) : OneOf10<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value4<T>(val value: T) : OneOf10<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value5<T>(val value: T) : OneOf10<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value6<T>(val value: T) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>
    @JvmInline value class Value7<T>(val value: T) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>
    @JvmInline value class Value8<T>(val value: T) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>
    @JvmInline value class Value9<T>(val value: T) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>
    @JvmInline value class Value10<T>(val value: T) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    fun <R> handle(
        if1: (T1) -> R, if2: (T2) -> R, if3: (T3) -> R, if4: (T4) -> R, if5: (T5) -> R,
        if6: (T6) -> R, if7: (T7) -> R, if8: (T8) -> R, if9: (T9) -> R, if10: (T10) -> R
    ): R =
        when (this) {
            is Value1<T1> -> if1(value)
            is Value2<T2> -> if2(value)
            is Value3<T3> -> if3(value)
            is Value4<T4> -> if4(value)
            is Value5<T5> -> if5(value)
            is Value6<T6> -> if6(value)
            is Value7<T7> -> if7(value)
            is Value8<T8> -> if8(value)
            is Value9<T9> -> if9(value)
            is Value10<T10> -> if10(value)
        }
}
