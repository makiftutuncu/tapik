package dev.akif.tapik.http

import dev.akif.tapik.codec.*

inline fun <reified H : Any> header(name: String, codec: StringCodec<H>): Header<H> = HeaderInput(name, codec)

@JvmName("headers0")
fun <H : Any> HttpEndpoint<Parameters0, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters1<H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers1")
fun <P1 : Any, H : Any> HttpEndpoint<Parameters1<P1>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters2<P1, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers2")
fun <P1 : Any, P2 : Any, H : Any> HttpEndpoint<Parameters2<P1, P2>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters3<P1, P2, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers3")
fun <P1 : Any, P2 : Any, P3 : Any, H : Any> HttpEndpoint<Parameters3<P1, P2, P3>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters4<P1, P2, P3, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers4")
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, H : Any> HttpEndpoint<Parameters4<P1, P2, P3, P4>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters5<P1, P2, P3, P4, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers5")
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, H : Any> HttpEndpoint<Parameters5<P1, P2, P3, P4, P5>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters6<P1, P2, P3, P4, P5, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers6")
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, H : Any> HttpEndpoint<Parameters6<P1, P2, P3, P4, P5, P6>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters7<P1, P2, P3, P4, P5, P6, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers7")
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, H : Any> HttpEndpoint<Parameters7<P1, P2, P3, P4, P5, P6, P7>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters8<P1, P2, P3, P4, P5, P6, P7, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers8")
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, H : Any> HttpEndpoint<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )

@JvmName("headers9")
fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, H : Any> HttpEndpoint<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>, EmptyBody, Outputs0>.headers(header: Header<H>): HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, H>, EmptyBody, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters + header,
        input = this.input,
        outputs = this.outputs
    )
