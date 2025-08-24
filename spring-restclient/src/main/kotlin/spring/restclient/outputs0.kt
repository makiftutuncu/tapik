package dev.akif.tapik.spring.restclient

import dev.akif.tapik.http.*

fun RestClientInterpreter.sendWithRestClient(endpoint: HttpEndpoint<Parameters0, EmptyBody, Outputs0>): EmptyResponse<Nothing> {
    val response = send(
        method = endpoint.method,
        uri = endpoint.buildURI(),
        headers = endpoint.buildHeaders(),
        contentType = null,
        body = null
    )
    return endpoint.outputs.buildResponse(Status.of(response.statusCode.value()))
}

fun <P1: Any> RestClientInterpreter.sendWithRestClient(endpoint: HttpEndpoint<Parameters1<P1>, EmptyBody, Outputs0>, p1: P1): EmptyResponse<Nothing> {
    val response = send(
        method = endpoint.method,
        uri = endpoint.buildURI(p1),
        headers = endpoint.buildHeaders(p1),
        contentType = null,
        body = null
    )
    return endpoint.outputs.buildResponse(Status.of(response.statusCode.value()))
}

fun <P1: Any, P2: Any> RestClientInterpreter.sendWithRestClient(endpoint: HttpEndpoint<Parameters2<P1, P2>, EmptyBody, Outputs0>, p1: P1, p2: P2): EmptyResponse<Nothing> {
    val response = send(
        method = endpoint.method,
        uri = endpoint.buildURI(p1, p2),
        headers = endpoint.buildHeaders(p1, p2),
        contentType = null,
        body = null
    )
    return endpoint.outputs.buildResponse(Status.of(response.statusCode.value()))
}

// TODO: Complete to 10
