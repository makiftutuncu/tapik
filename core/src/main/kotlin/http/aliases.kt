@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import dev.akif.tapik.tuples.*

typealias Headers = Tuple<Header<*>>
typealias Headers0 = Tuple0<Header<*>>
typealias Headers1<T1> = Tuple1<Header<*>, Header<T1>>
typealias Headers2<T1, T2> = Tuple2<Header<*>, Header<T1>, Header<T2>>
typealias Headers3<T1, T2, T3> = Tuple3<Header<*>, Header<T1>, Header<T2>, Header<T3>>
typealias Headers4<T1, T2, T3, T4> = Tuple4<Header<*>, Header<T1>, Header<T2>, Header<T3>, Header<T4>>
typealias Headers5<T1, T2, T3, T4, T5> = Tuple5<Header<*>, Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>>
typealias Headers6<T1, T2, T3, T4, T5, T6> = Tuple6<Header<*>, Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>>
typealias Headers7<T1, T2, T3, T4, T5, T6, T7> = Tuple7<Header<*>, Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>>
typealias Headers8<T1, T2, T3, T4, T5, T6, T7, T8> = Tuple8<Header<*>, Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>, Header<T8>>
typealias Headers9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = Tuple9<Header<*>, Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>, Header<T8>, Header<T9>>
typealias Headers10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = Tuple10<Header<*>, Header<T1>, Header<T2>, Header<T3>, Header<T4>, Header<T5>, Header<T6>, Header<T7>, Header<T8>, Header<T9>, Header<T10>>

typealias Outputs = Tuple<Output<*, *>>
typealias Outputs0 = Tuple0<Output<*, *>>
typealias Outputs1<B1, H1> = Tuple1<Output<*, *>, Output<B1, H1>>
typealias Outputs2<B1, H1, B2, H2> = Tuple2<Output<*, *>, Output<B1, H1>, Output<B2, H2>>
typealias Outputs3<B1, H1, B2, H2, B3, H3> = Tuple3<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>>
typealias Outputs4<B1, H1, B2, H2, B3, H3, B4, H4> = Tuple4<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>>
typealias Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5> = Tuple5<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>>
typealias Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6> = Tuple6<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>>
typealias Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7> = Tuple7<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>>
typealias Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8> = Tuple8<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>, Output<B8, H8>>
typealias Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9> = Tuple9<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>, Output<B8, H8>, Output<B9, H9>>
typealias Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, H10> = Tuple10<Output<*, *>, Output<B1, H1>, Output<B2, H2>, Output<B3, H3>, Output<B4, H4>, Output<B5, H5>, Output<B6, H6>, Output<B7, H7>, Output<B8, H8>, Output<B9, H9>, Output<B10, H10>>

typealias Parameters = Tuple<Parameter<*>>
typealias Parameters0 = Tuple0<Parameter<*>>
typealias Parameters1<P1> = Tuple1<Parameter<*>, Parameter<P1>>
typealias Parameters2<P1, P2> = Tuple2<Parameter<*>, Parameter<P1>, Parameter<P2>>
typealias Parameters3<P1, P2, P3> = Tuple3<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>>
typealias Parameters4<P1, P2, P3, P4> = Tuple4<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>>
typealias Parameters5<P1, P2, P3, P4, P5> = Tuple5<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>>
typealias Parameters6<P1, P2, P3, P4, P5, P6> = Tuple6<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>>
typealias Parameters7<P1, P2, P3, P4, P5, P6, P7> = Tuple7<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>>
typealias Parameters8<P1, P2, P3, P4, P5, P6, P7, P8> = Tuple8<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>>
typealias Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> = Tuple9<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>>
typealias Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> = Tuple10<Parameter<*>, Parameter<P1>, Parameter<P2>, Parameter<P3>, Parameter<P4>, Parameter<P5>, Parameter<P6>, Parameter<P7>, Parameter<P8>, Parameter<P9>, Parameter<P10>>

typealias URIWithParameters<P> = Pair<List<String>, P>
typealias URIWithParameters0 = URIWithParameters<Parameters0>
typealias URIWithParameters1<P1> = URIWithParameters<Parameters1<P1>>
typealias URIWithParameters2<P1, P2> = URIWithParameters<Parameters2<P1, P2>>
typealias URIWithParameters3<P1, P2, P3> = URIWithParameters<Parameters3<P1, P2, P3>>
typealias URIWithParameters4<P1, P2, P3, P4> = URIWithParameters<Parameters4<P1, P2, P3, P4>>
typealias URIWithParameters5<P1, P2, P3, P4, P5> = URIWithParameters<Parameters5<P1, P2, P3, P4, P5>>
typealias URIWithParameters6<P1, P2, P3, P4, P5, P6> = URIWithParameters<Parameters6<P1, P2, P3, P4, P5, P6>>
typealias URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> = URIWithParameters<Parameters7<P1, P2, P3, P4, P5, P6, P7>>
typealias URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> = URIWithParameters<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>>
typealias URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> = URIWithParameters<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>>
typealias URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> = URIWithParameters<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>>

typealias AnyHttpEndpoint = HttpEndpoint<Parameters, Body<*>, Outputs>
