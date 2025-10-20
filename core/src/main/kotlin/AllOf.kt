package dev.akif.tapik

sealed interface AllOf

interface AllOf0 : AllOf

interface AllOf1<T1> : AllOf {
    val item1: T1
}

interface AllOf2<T1, T2> : AllOf {
    val item1: T1
    val item2: T2
}

interface AllOf3<T1, T2, T3> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
}

interface AllOf4<T1, T2, T3, T4> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
}

interface AllOf5<T1, T2, T3, T4, T5> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
}

interface AllOf6<T1, T2, T3, T4, T5, T6> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
    val item6: T6
}

interface AllOf7<T1, T2, T3, T4, T5, T6, T7> : AllOf {
    val item1: T1
    val item2: T2
    val item3: T3
    val item4: T4
    val item5: T5
    val item6: T6
    val item7: T7
}

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
