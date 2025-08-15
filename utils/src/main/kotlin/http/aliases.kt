package dev.akif.tapik.http

import dev.akif.tapik.tuple.*

typealias PathVariables0 = Tuple0
typealias PathVariables1<T1> = Tuple1<PathVariable<T1>>
typealias PathVariables2<T1, T2> = Tuple2<PathVariable<T1>, PathVariable<T2>>
typealias PathVariables3<T1, T2, T3> = Tuple3<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>>
typealias PathVariables4<T1, T2, T3, T4> = Tuple4<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>, PathVariable<T4>>
typealias PathVariables5<T1, T2, T3, T4, T5> = Tuple5<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>, PathVariable<T4>, PathVariable<T5>>
typealias PathVariables6<T1, T2, T3, T4, T5, T6> = Tuple6<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>, PathVariable<T4>, PathVariable<T5>, PathVariable<T6>>
typealias PathVariables7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>, PathVariable<T4>, PathVariable<T5>, PathVariable<T6>, PathVariable<T7>>
typealias PathVariables8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>, PathVariable<T4>, PathVariable<T5>, PathVariable<T6>, PathVariable<T7>, PathVariable<T8>>
typealias PathVariables9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>, PathVariable<T4>, PathVariable<T5>, PathVariable<T6>, PathVariable<T7>, PathVariable<T8>, PathVariable<T9>>
typealias PathVariables10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<PathVariable<T1>, PathVariable<T2>, PathVariable<T3>, PathVariable<T4>, PathVariable<T5>, PathVariable<T6>, PathVariable<T7>, PathVariable<T8>, PathVariable<T9>, PathVariable<T10>>

typealias QueryParameters0 = Tuple0
typealias QueryParameters1<T1> = Tuple1<QueryParameter<T1>>
typealias QueryParameters2<T1, T2> = Tuple2<QueryParameter<T1>, QueryParameter<T2>>
typealias QueryParameters3<T1, T2, T3> = Tuple3<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>>
typealias QueryParameters4<T1, T2, T3, T4> = Tuple4<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>, QueryParameter<T4>>
typealias QueryParameters5<T1, T2, T3, T4, T5> = Tuple5<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>, QueryParameter<T4>, QueryParameter<T5>>
typealias QueryParameters6<T1, T2, T3, T4, T5, T6> = Tuple6<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>, QueryParameter<T4>, QueryParameter<T5>, QueryParameter<T6>>
typealias QueryParameters7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>, QueryParameter<T4>, QueryParameter<T5>, QueryParameter<T6>, QueryParameter<T7>>
typealias QueryParameters8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>, QueryParameter<T4>, QueryParameter<T5>, QueryParameter<T6>, QueryParameter<T7>, QueryParameter<T8>>
typealias QueryParameters9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>, QueryParameter<T4>, QueryParameter<T5>, QueryParameter<T6>, QueryParameter<T7>, QueryParameter<T8>, QueryParameter<T9>>
typealias QueryParameters10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<QueryParameter<T1>, QueryParameter<T2>, QueryParameter<T3>, QueryParameter<T4>, QueryParameter<T5>, QueryParameter<T6>, QueryParameter<T7>, QueryParameter<T8>, QueryParameter<T9>, QueryParameter<T10>>

typealias Headers0 = Tuple0
typealias Headers1<T1> = Tuple1<Header<T1>>
typealias Headers2<T1, T2> = Tuple2<Header<T1>, Header<T2>>
typealias Headers3<T1, T2, T3> = Tuple3<Header<T1>, Header<T2>, Header<T3>>
typealias Headers4<T1, T2, T3, T4> = Tuple4<Header<T1>, Header<T2>, Header<T3>, Header<T4>>
typealias Headers5<T1, T2, T3, T4, T5> = Tuple5<Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>>
typealias Headers6<T1, T2, T3, T4, T5, T6> = Tuple6<Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>>
typealias Headers7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>>
typealias Headers8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>, Header<T8>>
typealias Headers9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>, Header<T8>, Header<T9>>
typealias Headers10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>, Header<T8>, Header<T9>, Header<T10>>

typealias Outputs0 = Tuple0
typealias Outputs1<B1, H1> = Tuple1<Output<B1, H1>>
typealias Outputs2<B1, H1, B2, H2> = Tuple2<Output<B1, H1>, Output<B2, H2>>
typealias Outputs3<B1, H1, B2, H2, B3, H3> = Tuple3<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>>
typealias Outputs4<B1, H1, B2, H2, B3, H3, B4, H4> = Tuple4<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>>
typealias Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5> = Tuple5<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>>
typealias Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6> = Tuple6<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>>
typealias Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7> = Tuple7<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>>
typealias Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8> = Tuple8<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>, Output<B8, H8>>
typealias Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9> = Tuple9<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>, Output<B8, H8>, Output<B9, H9>>
typealias Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, H10> = Tuple10<Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>, Output<B8, H8>, Output<B9, H9>, Output<B10, H10>>
