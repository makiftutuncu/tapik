package dev.akif.tapik

/** Marker shared by typed query parameters with decoded [Value] stored in a [Uri]. */
sealed interface QueryParameter<Value : Any>

/** An ordered, heterogeneous tuple of query parameters. */
typealias Queries = Tuple<QueryParameter<*>>

/** A query-parameter tuple with no values. */
typealias Queries0 = Tuple0

/** A query-parameter tuple with one value. */
typealias Queries1<Value1> = Tuple1<QueryParameter<*>, QueryParameter<Value1>>

/** A query-parameter tuple with two values. */
typealias Queries2<Value1, Value2> = Tuple2<QueryParameter<*>, QueryParameter<Value1>, QueryParameter<Value2>>

/** A query-parameter tuple with three values. */
typealias Queries3<Value1, Value2, Value3> =
    Tuple3<QueryParameter<*>, QueryParameter<Value1>, QueryParameter<Value2>, QueryParameter<Value3>>

/** A query-parameter tuple with four values. */
typealias Queries4<Value1, Value2, Value3, Value4> =
    Tuple4<
        QueryParameter<*>,
        QueryParameter<Value1>,
        QueryParameter<Value2>,
        QueryParameter<Value3>,
        QueryParameter<Value4>
    >

/** A query-parameter tuple with five values. */
typealias Queries5<Value1, Value2, Value3, Value4, Value5> =
    Tuple5<
        QueryParameter<*>,
        QueryParameter<Value1>,
        QueryParameter<Value2>,
        QueryParameter<Value3>,
        QueryParameter<Value4>,
        QueryParameter<Value5>
    >

/** A query-parameter tuple with six values. */
typealias Queries6<Value1, Value2, Value3, Value4, Value5, Value6> =
    Tuple6<
        QueryParameter<*>,
        QueryParameter<Value1>,
        QueryParameter<Value2>,
        QueryParameter<Value3>,
        QueryParameter<Value4>,
        QueryParameter<Value5>,
        QueryParameter<Value6>
    >

/** A query-parameter tuple with seven values. */
typealias Queries7<Value1, Value2, Value3, Value4, Value5, Value6, Value7> =
    Tuple7<
        QueryParameter<*>,
        QueryParameter<Value1>,
        QueryParameter<Value2>,
        QueryParameter<Value3>,
        QueryParameter<Value4>,
        QueryParameter<Value5>,
        QueryParameter<Value6>,
        QueryParameter<Value7>
    >

/** A query-parameter tuple with eight values. */
typealias Queries8<Value1, Value2, Value3, Value4, Value5, Value6, Value7, Value8> =
    Tuple8<
        QueryParameter<*>,
        QueryParameter<Value1>,
        QueryParameter<Value2>,
        QueryParameter<Value3>,
        QueryParameter<Value4>,
        QueryParameter<Value5>,
        QueryParameter<Value6>,
        QueryParameter<Value7>,
        QueryParameter<Value8>
    >
