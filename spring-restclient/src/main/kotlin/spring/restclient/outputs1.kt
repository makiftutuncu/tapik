package dev.akif.tapik.spring.restclient

//import dev.akif.tapik.http.*
//
//fun <B1, OB1 : Body<B1>, OH1 : Headers> RestClientInterpreter.sendWithRestClient(
//    endpoint: HttpEndpoint<Parameters0, EmptyBody, Outputs1<OB1, OH1>>
//): Response0<B1> {
//    val response =
//        send(
//            method = endpoint.method,
//            uri = endpoint.buildURI(),
//            headers = endpoint.buildHeaders(),
//            contentType = endpoint.input.mediaType,
//            body = null,
//            outputs = endpoint.outputs.toList()
//        )
//
//    return endpoint.outputs.buildResponse(
//        status = Status.of(response.statusCode.value()),
//        bytes = response.body ?: ByteArray(0)
//    )
//}
//
//fun <P1 : Any, B1, OB1 : Body<B1>, OH1 : Headers> RestClientInterpreter.sendWithRestClient(
//    endpoint: HttpEndpoint<Parameters1<P1>, EmptyBody, Outputs1<OB1, OH1>>,
//    p1: P1
//): Response0<B1> {
//    val response =
//        send(
//            method = endpoint.method,
//            uri = endpoint.buildURI(p1),
//            headers = endpoint.buildHeaders(p1),
//            contentType = endpoint.input.mediaType,
//            body = null,
//            outputs = endpoint.outputs.toList()
//        )
//
//    return endpoint.outputs.buildResponse(
//        status = Status.of(response.statusCode.value()),
//        bytes = response.body ?: ByteArray(0)
//    )
//}
//
//fun <P1 : Any, P2 : Any, B1, OB1 : Body<B1>, OH1 : Headers> RestClientInterpreter.sendWithRestClient(
//    endpoint: HttpEndpoint<Parameters2<P1, P2>, EmptyBody, Outputs1<OB1, OH1>>,
//    p1: P1,
//    p2: P2
//): Response0<B1> {
//    val response =
//        send(
//            method = endpoint.method,
//            uri = endpoint.buildURI(p1, p2),
//            headers = endpoint.buildHeaders(p1, p2),
//            contentType = endpoint.input.mediaType,
//            body = null,
//            outputs = endpoint.outputs.toList()
//        )
//
//    return endpoint.outputs.buildResponse(
//        status = Status.of(response.statusCode.value()),
//        bytes = response.body ?: ByteArray(0)
//    )
//}
