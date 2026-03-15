package dev.akif.tapik

/**
 * Marker hierarchy for fixed-arity product types.
 *
 * Tuple implementations inherit these interfaces so generators can talk about positional items in a
 * type-safe way without resorting to reflection.
 */
sealed interface AllOf

/** Product type with no items. */
interface AllOf0 : AllOf

/** Product type with one typed slot. */
interface AllOf1<T1> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1
}

/** Product type with two typed slots. */
interface AllOf2<T1, T2> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2
}

/** Product type with three typed slots. */
interface AllOf3<T1, T2, T3> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3
}

/** Product type with four typed slots. */
interface AllOf4<T1, T2, T3, T4> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3

    /** Value stored in the fourth slot. */
    val item4: T4
}

/** Product type with five typed slots. */
interface AllOf5<T1, T2, T3, T4, T5> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3

    /** Value stored in the fourth slot. */
    val item4: T4

    /** Value stored in the fifth slot. */
    val item5: T5
}

/** Product type with six typed slots. */
interface AllOf6<T1, T2, T3, T4, T5, T6> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3

    /** Value stored in the fourth slot. */
    val item4: T4

    /** Value stored in the fifth slot. */
    val item5: T5

    /** Value stored in the sixth slot. */
    val item6: T6
}

/** Product type with seven typed slots. */
interface AllOf7<T1, T2, T3, T4, T5, T6, T7> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3

    /** Value stored in the fourth slot. */
    val item4: T4

    /** Value stored in the fifth slot. */
    val item5: T5

    /** Value stored in the sixth slot. */
    val item6: T6

    /** Value stored in the seventh slot. */
    val item7: T7
}

/** Product type with eight typed slots. */
interface AllOf8<T1, T2, T3, T4, T5, T6, T7, T8> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3

    /** Value stored in the fourth slot. */
    val item4: T4

    /** Value stored in the fifth slot. */
    val item5: T5

    /** Value stored in the sixth slot. */
    val item6: T6

    /** Value stored in the seventh slot. */
    val item7: T7

    /** Value stored in the eighth slot. */
    val item8: T8
}

/** Product type with nine typed slots. */
interface AllOf9<T1, T2, T3, T4, T5, T6, T7, T8, T9> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3

    /** Value stored in the fourth slot. */
    val item4: T4

    /** Value stored in the fifth slot. */
    val item5: T5

    /** Value stored in the sixth slot. */
    val item6: T6

    /** Value stored in the seventh slot. */
    val item7: T7

    /** Value stored in the eighth slot. */
    val item8: T8

    /** Value stored in the ninth slot. */
    val item9: T9
}

/** Product type with ten typed slots. */
interface AllOf10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> : AllOf {
    /** Value stored in the first slot. */
    val item1: T1

    /** Value stored in the second slot. */
    val item2: T2

    /** Value stored in the third slot. */
    val item3: T3

    /** Value stored in the fourth slot. */
    val item4: T4

    /** Value stored in the fifth slot. */
    val item5: T5

    /** Value stored in the sixth slot. */
    val item6: T6

    /** Value stored in the seventh slot. */
    val item7: T7

    /** Value stored in the eighth slot. */
    val item8: T8

    /** Value stored in the ninth slot. */
    val item9: T9

    /** Value stored in the tenth slot. */
    val item10: T10
}
