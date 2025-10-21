package dev.akif.tapik

/**
 * Marker hierarchy for structural product types that carry strongly-typed items.
 *
 * The DSL composes endpoint pieces using these interfaces so that code generators can
 * reason about the shape of header and parameter tuples without reflection.
 *
 * @see OneOf
 */
sealed interface AllOf

/**
 * Zero-arity product type marker.
 *
 * @see AllOf
 */
interface AllOf0 : AllOf

/**
 * Product of a single item [T1].
 *
 * @param T1 type of the first item.
 * @property item1 first value contained in the product.
 */
interface AllOf1<T1> : AllOf {
    val item1: T1
}

/**
 * Product of items [T1] and [T2].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @property item1 first value.
 * @property item2 second value.
 */
interface AllOf2<T1, T2> : AllOf {
    val item1: T1
    val item2: T2
}

/**
 * Product of items [T1], [T2], and [T3].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 */
interface AllOf3<T1, T2, T3> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
}

/**
 * Product of items [T1], [T2], [T3], and [T4].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @param T4 type carried in the fourth position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 * @property item4 fourth value.
 */
interface AllOf4<T1, T2, T3, T4> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
}

/**
 * Product of items [T1] through [T5].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @param T4 type carried in the fourth position.
 * @param T5 type carried in the fifth position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 * @property item4 fourth value.
 * @property item5 fifth value.
 */
interface AllOf5<T1, T2, T3, T4, T5> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
}

/**
 * Product of items [T1] through [T6].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @param T4 type carried in the fourth position.
 * @param T5 type carried in the fifth position.
 * @param T6 type carried in the sixth position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 * @property item4 fourth value.
 * @property item5 fifth value.
 * @property item6 sixth value.
 */
interface AllOf6<T1, T2, T3, T4, T5, T6> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
    val item6: T6
}

/**
 * Product of items [T1] through [T7].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @param T4 type carried in the fourth position.
 * @param T5 type carried in the fifth position.
 * @param T6 type carried in the sixth position.
 * @param T7 type carried in the seventh position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 * @property item4 fourth value.
 * @property item5 fifth value.
 * @property item6 sixth value.
 * @property item7 seventh value.
 */
interface AllOf7<T1, T2, T3, T4, T5, T6, T7> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
    val item6: T6
    val item7: T7
}

/**
 * Product of items [T1] through [T8].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @param T4 type carried in the fourth position.
 * @param T5 type carried in the fifth position.
 * @param T6 type carried in the sixth position.
 * @param T7 type carried in the seventh position.
 * @param T8 type carried in the eighth position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 * @property item4 fourth value.
 * @property item5 fifth value.
 * @property item6 sixth value.
 * @property item7 seventh value.
 * @property item8 eighth value.
 */
interface AllOf8<T1, T2, T3, T4, T5, T6, T7, T8> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
    val item6: T6
    val item7: T7
    val item8: T8
}

/**
 * Product of items [T1] through [T9].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @param T4 type carried in the fourth position.
 * @param T5 type carried in the fifth position.
 * @param T6 type carried in the sixth position.
 * @param T7 type carried in the seventh position.
 * @param T8 type carried in the eighth position.
 * @param T9 type carried in the ninth position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 * @property item4 fourth value.
 * @property item5 fifth value.
 * @property item6 sixth value.
 * @property item7 seventh value.
 * @property item8 eighth value.
 * @property item9 ninth value.
 */
interface AllOf9<T1, T2, T3, T4, T5, T6, T7, T8, T9> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
    val item6: T6
    val item7: T7
    val item8: T8
    val item9: T9
}

/**
 * Product of items [T1] through [T10].
 *
 * @param T1 type carried in the first position.
 * @param T2 type carried in the second position.
 * @param T3 type carried in the third position.
 * @param T4 type carried in the fourth position.
 * @param T5 type carried in the fifth position.
 * @param T6 type carried in the sixth position.
 * @param T7 type carried in the seventh position.
 * @param T8 type carried in the eighth position.
 * @param T9 type carried in the ninth position.
 * @param T10 type carried in the tenth position.
 * @property item1 first value.
 * @property item2 second value.
 * @property item3 third value.
 * @property item4 fourth value.
 * @property item5 fifth value.
 * @property item6 sixth value.
 * @property item7 seventh value.
 * @property item8 eighth value.
 * @property item9 ninth value.
 * @property item10 tenth value.
 */
interface AllOf10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
    val item6: T6
    val item7: T7
    val item8: T8
    val item9: T9
    val item10: T10
}
