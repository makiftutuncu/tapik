package dev.akif.tapik

/**
 * Marker hierarchy for discriminated unions with a known upper bound on the number of cases.
 *
 * Generators rely on this sealed interface family to model `oneOf` shapes that carry typed payloads
 * while still enabling exhaustive handling through helpers such as [OneOf2.select].
 */
sealed interface OneOf

/** Two-way discriminated union. */
sealed interface OneOf2<out T1, out T2> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf2<T, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf2<Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     */
    fun <R> select(
        when1: (T1) -> R,
        when2: (T2) -> R
    ): R =
        when (this) {
            is Option1<T1> -> when1(value)
            is Option2<T2> -> when2(value)
        }
}

/** Three-way discriminated union. */
sealed interface OneOf3<out T1, out T2, out T3> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf3<T, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf3<Nothing, T, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf3<Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     */
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

/** Four-way discriminated union. */
sealed interface OneOf4<out T1, out T2, out T3, out T4> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf4<T, Nothing, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf4<Nothing, T, Nothing, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf4<Nothing, Nothing, T, Nothing>

    /** Represents the fourth alternative of the union. @property value payload chosen for the fourth branch. */
    @JvmInline value class Option4<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf4<Nothing, Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @param when4 handler invoked when the receiver is an [Option4].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     * @see Option4
     */
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

/** Five-way discriminated union. */
sealed interface OneOf5<out T1, out T2, out T3, out T4, out T5> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf5<T, Nothing, Nothing, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf5<Nothing, T, Nothing, Nothing, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf5<Nothing, Nothing, T, Nothing, Nothing>

    /** Represents the fourth alternative of the union. @property value payload chosen for the fourth branch. */
    @JvmInline value class Option4<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf5<Nothing, Nothing, Nothing, T, Nothing>

    /** Represents the fifth alternative of the union. @property value payload chosen for the fifth branch. */
    @JvmInline value class Option5<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf5<Nothing, Nothing, Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @param when4 handler invoked when the receiver is an [Option4].
     * @param when5 handler invoked when the receiver is an [Option5].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     * @see Option4
     * @see Option5
     */
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

/** Six-way discriminated union. */
sealed interface OneOf6<out T1, out T2, out T3, out T4, out T5, out T6> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf6<T, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf6<Nothing, T, Nothing, Nothing, Nothing, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf6<Nothing, Nothing, T, Nothing, Nothing, Nothing>

    /** Represents the fourth alternative of the union. @property value payload chosen for the fourth branch. */
    @JvmInline value class Option4<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf6<Nothing, Nothing, Nothing, T, Nothing, Nothing>

    /** Represents the fifth alternative of the union. @property value payload chosen for the fifth branch. */
    @JvmInline value class Option5<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf6<Nothing, Nothing, Nothing, Nothing, T, Nothing>

    /** Represents the sixth alternative of the union. @property value payload chosen for the sixth branch. */
    @JvmInline value class Option6<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf6<Nothing, Nothing, Nothing, Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @param when4 handler invoked when the receiver is an [Option4].
     * @param when5 handler invoked when the receiver is an [Option5].
     * @param when6 handler invoked when the receiver is an [Option6].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     * @see Option4
     * @see Option5
     * @see Option6
     */
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

/** Seven-way discriminated union. */
sealed interface OneOf7<out T1, out T2, out T3, out T4, out T5, out T6, out T7> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf7<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf7<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf7<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    /** Represents the fourth alternative of the union. @property value payload chosen for the fourth branch. */
    @JvmInline value class Option4<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    /** Represents the fifth alternative of the union. @property value payload chosen for the fifth branch. */
    @JvmInline value class Option5<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    /** Represents the sixth alternative of the union. @property value payload chosen for the sixth branch. */
    @JvmInline value class Option6<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    /** Represents the seventh alternative of the union. @property value payload chosen for the seventh branch. */
    @JvmInline value class Option7<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf7<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @param when4 handler invoked when the receiver is an [Option4].
     * @param when5 handler invoked when the receiver is an [Option5].
     * @param when6 handler invoked when the receiver is an [Option6].
     * @param when7 handler invoked when the receiver is an [Option7].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     * @see Option4
     * @see Option5
     * @see Option6
     * @see Option7
     */
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

/** Eight-way discriminated union. */
sealed interface OneOf8<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the fourth alternative of the union. @property value payload chosen for the fourth branch. */
    @JvmInline value class Option4<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    /** Represents the fifth alternative of the union. @property value payload chosen for the fifth branch. */
    @JvmInline value class Option5<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    /** Represents the sixth alternative of the union. @property value payload chosen for the sixth branch. */
    @JvmInline value class Option6<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    /** Represents the seventh alternative of the union. @property value payload chosen for the seventh branch. */
    @JvmInline value class Option7<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    /** Represents the eighth alternative of the union. @property value payload chosen for the eighth branch. */
    @JvmInline value class Option8<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf8<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @param when4 handler invoked when the receiver is an [Option4].
     * @param when5 handler invoked when the receiver is an [Option5].
     * @param when6 handler invoked when the receiver is an [Option6].
     * @param when7 handler invoked when the receiver is an [Option7].
     * @param when8 handler invoked when the receiver is an [Option8].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     * @see Option4
     * @see Option5
     * @see Option6
     * @see Option7
     * @see Option8
     */
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

/** Nine-way discriminated union. */
sealed interface OneOf9<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the fourth alternative of the union. @property value payload chosen for the fourth branch. */
    @JvmInline value class Option4<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the fifth alternative of the union. @property value payload chosen for the fifth branch. */
    @JvmInline value class Option5<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    /** Represents the sixth alternative of the union. @property value payload chosen for the sixth branch. */
    @JvmInline value class Option6<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    /** Represents the seventh alternative of the union. @property value payload chosen for the seventh branch. */
    @JvmInline value class Option7<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    /** Represents the eighth alternative of the union. @property value payload chosen for the eighth branch. */
    @JvmInline value class Option8<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    /** Represents the ninth alternative of the union. @property value payload chosen for the ninth branch. */
    @JvmInline value class Option9<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf9<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @param when4 handler invoked when the receiver is an [Option4].
     * @param when5 handler invoked when the receiver is an [Option5].
     * @param when6 handler invoked when the receiver is an [Option6].
     * @param when7 handler invoked when the receiver is an [Option7].
     * @param when8 handler invoked when the receiver is an [Option8].
     * @param when9 handler invoked when the receiver is an [Option9].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     * @see Option4
     * @see Option5
     * @see Option6
     * @see Option7
     * @see Option8
     * @see Option9
     */
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

/** Ten-way discriminated union. */
sealed interface OneOf10<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9, out T10> : OneOf {
    /** Represents the first alternative of the union. @property value payload chosen for the first branch. */
    @JvmInline value class Option1<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the second alternative of the union. @property value payload chosen for the second branch. */
    @JvmInline value class Option2<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the third alternative of the union. @property value payload chosen for the third branch. */
    @JvmInline value class Option3<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the fourth alternative of the union. @property value payload chosen for the fourth branch. */
    @JvmInline value class Option4<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the fifth alternative of the union. @property value payload chosen for the fifth branch. */
    @JvmInline value class Option5<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing, Nothing>

    /** Represents the sixth alternative of the union. @property value payload chosen for the sixth branch. */
    @JvmInline value class Option6<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing, Nothing>

    /** Represents the seventh alternative of the union. @property value payload chosen for the seventh branch. */
    @JvmInline value class Option7<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing, Nothing>

    /** Represents the eighth alternative of the union. @property value payload chosen for the eighth branch. */
    @JvmInline value class Option8<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing, Nothing>

    /** Represents the ninth alternative of the union. @property value payload chosen for the ninth branch. */
    @JvmInline value class Option9<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T, Nothing>

    /** Represents the tenth alternative of the union. @property value payload chosen for the tenth branch. */
    @JvmInline value class Option10<T>(
        /** Payload carried when this option is used. */
        val value: T
    ) : OneOf10<Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, T>

    /**
     * Exhaustively handles the stored value by delegating to the matching lambda.
     *
     * @param when1 handler invoked when the receiver is an [Option1].
     * @param when2 handler invoked when the receiver is an [Option2].
     * @param when3 handler invoked when the receiver is an [Option3].
     * @param when4 handler invoked when the receiver is an [Option4].
     * @param when5 handler invoked when the receiver is an [Option5].
     * @param when6 handler invoked when the receiver is an [Option6].
     * @param when7 handler invoked when the receiver is an [Option7].
     * @param when8 handler invoked when the receiver is an [Option8].
     * @param when9 handler invoked when the receiver is an [Option9].
     * @param when10 handler invoked when the receiver is an [Option10].
     * @return the result of the handler that matches the concrete option.
     * @see Option1
     * @see Option2
     * @see Option3
     * @see Option4
     * @see Option5
     * @see Option6
     * @see Option7
     * @see Option8
     * @see Option9
     * @see Option10
     */
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
