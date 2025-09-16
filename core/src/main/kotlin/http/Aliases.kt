// This file is auto-generated. Do not edit manually as your changes will be lost.
@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import dev.akif.tapik.tuples.*

typealias AnyHttpEndpoint = HttpEndpoint<Parameters, Headers, Body<*>, Headers, OutputBodies>

typealias Headers = Tuple<Header<*>>
typealias Headers0 = Tuple0<Header<*>>
typealias Headers1<H1> = Tuple1<Header<*>, Header<H1>>
typealias Headers2<H1, H2> = Tuple2<Header<*>, Header<H1>, Header<H2>>
typealias Headers3<H1, H2, H3> = Tuple3<Header<*>, Header<H1>, Header<H2>, Header<H3>>
typealias Headers4<H1, H2, H3, H4> = Tuple4<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>>
typealias Headers5<H1, H2, H3, H4, H5> = Tuple5<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>>
typealias Headers6<H1, H2, H3, H4, H5, H6> = Tuple6<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>>
typealias Headers7<H1, H2, H3, H4, H5, H6, H7> = Tuple7<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>>
typealias Headers8<H1, H2, H3, H4, H5, H6, H7, H8> = Tuple8<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>>
typealias Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9> = Tuple9<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>>
typealias Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10> = Tuple10<Header<*>, Header<H1>, Header<H2>, Header<H3>, Header<H4>, Header<H5>, Header<H6>, Header<H7>, Header<H8>, Header<H9>, Header<H10>>

typealias OutputBodies = Tuple<OutputBody<*>>
typealias OutputBodies0 = Tuple0<OutputBody<*>>
typealias OutputBodies1<B1> = Tuple1<OutputBody<*>, OutputBody<B1>>
typealias OutputBodies2<B1, B2> = Tuple2<OutputBody<*>, OutputBody<B1>, OutputBody<B2>>
typealias OutputBodies3<B1, B2, B3> = Tuple3<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>>
typealias OutputBodies4<B1, B2, B3, B4> = Tuple4<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>, OutputBody<B4>>
typealias OutputBodies5<B1, B2, B3, B4, B5> = Tuple5<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>, OutputBody<B4>, OutputBody<B5>>
typealias OutputBodies6<B1, B2, B3, B4, B5, B6> = Tuple6<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>, OutputBody<B4>, OutputBody<B5>, OutputBody<B6>>
typealias OutputBodies7<B1, B2, B3, B4, B5, B6, B7> = Tuple7<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>, OutputBody<B4>, OutputBody<B5>, OutputBody<B6>, OutputBody<B7>>
typealias OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8> = Tuple8<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>, OutputBody<B4>, OutputBody<B5>, OutputBody<B6>, OutputBody<B7>, OutputBody<B8>>
typealias OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9> = Tuple9<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>, OutputBody<B4>, OutputBody<B5>, OutputBody<B6>, OutputBody<B7>, OutputBody<B8>, OutputBody<B9>>
typealias OutputBodies10<B1, B2, B3, B4, B5, B6, B7, B8, B9, B10> = Tuple10<OutputBody<*>, OutputBody<B1>, OutputBody<B2>, OutputBody<B3>, OutputBody<B4>, OutputBody<B5>, OutputBody<B6>, OutputBody<B7>, OutputBody<B8>, OutputBody<B9>, OutputBody<B10>>

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
