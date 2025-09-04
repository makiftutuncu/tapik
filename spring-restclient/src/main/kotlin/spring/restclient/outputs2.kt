@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.spring.restclient

import dev.akif.tapik.http.*
import dev.akif.tapik.types.*

fun <B1, OB1 : Body<B1>, OH1 : Headers, B2, OB2 : Body<B2>, OH2 : Headers> RestClientInterpreter.sendWithRestClient(
    endpoint: HttpEndpoint<Parameters0, EmptyBody, Outputs2<OB1, OH1, OB2, OH2>>
): OneOf2<Response0<B1>, Response0<B2>> {
    val response =
        send(
            method = endpoint.method,
            uri = endpoint.buildURI(),
            headers = endpoint.buildHeaders(),
            contentType = endpoint.input.mediaType,
            body = null,
            outputs = endpoint.outputs.toList()
        )

    return endpoint.outputs.buildResponse(
        status = Status.of(response.statusCode.value()),
        bytes = response.body ?: ByteArray(0)
    )
}

fun <P1 : Any, B1, OB1 : Body<B1>, OH1 : Headers, B2, OB2 : Body<B2>, OH2 : Headers> RestClientInterpreter.sendWithRestClient(
    endpoint: HttpEndpoint<Parameters1<P1>, EmptyBody, Outputs2<OB1, OH1, OB2, OH2>>,
    p1: P1
): OneOf2<Response0<B1>, Response0<B2>> {
    val response =
        send(
            method = endpoint.method,
            uri = endpoint.buildURI(p1),
            headers = endpoint.buildHeaders(p1),
            contentType = endpoint.input.mediaType,
            body = null,
            outputs = endpoint.outputs.toList()
        )

    return endpoint.outputs.buildResponse(
        status = Status.of(response.statusCode.value()),
        bytes = response.body ?: ByteArray(0)
    )
}

@JvmName("sendWith2Parameters")
fun <P1 : Any, P2 : Any, B1, OB1 : Body<B1>, OH1 : Headers, B2, OB2 : Body<B2>, OH2 : Headers> RestClientInterpreter.send(
    endpoint: HttpEndpoint<Parameters2<P1, P2>, EmptyBody, Outputs2<OB1, OH1, OB2, OH2>>,
    p1: P1,
    p2: P2
): OneOf2<Response0<B1>, Response0<B2>> {
    val response =
        send(
            method = endpoint.method,
            uri = endpoint.buildURI(p1, p2),
            headers = endpoint.buildHeaders(p1, p2),
            contentType = endpoint.input.mediaType,
            body = null,
            outputs = endpoint.outputs.toList()
        )

    return endpoint.outputs.buildResponse(
        status = Status.of(response.statusCode.value()),
        bytes = response.body ?: ByteArray(0)
    )
}

@JvmName("sendWith1ParameterAndInput")
context(interpreter: RestClientInterpreter)
fun <P1 : Any, B, IB : Body<B>, B1, OB1 : Body<B1>, OH1 : Headers, B2, OB2 : Body<B2>, OH2 : Headers> HttpEndpoint<Parameters1<P1>, IB, Outputs2<OB1, OH1, OB2, OH2>>.send(
    p1: P1,
    input: B
): OneOf2<Response0<B1>, Response0<B2>> {
    val response =
        interpreter.send(
            method = this.method,
            uri = this.buildURI(p1),
            headers = this.buildHeaders(p1),
            contentType = this.input.mediaType,
            body = this.input.codec.encode(input),
            outputs = this.outputs.toList()
        )

    return this.outputs.buildResponse(
        status = Status.of(response.statusCode.value()),
        bytes = response.body ?: ByteArray(0)
    )
}

// TODO: Complete to 10
