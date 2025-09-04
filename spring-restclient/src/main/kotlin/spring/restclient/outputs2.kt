@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.spring.restclient

//import dev.akif.tapik.http.*
//import dev.akif.tapik.selections.*
//
//context(interpreter: RestClientInterpreter)
//fun <P1 : Any, B, IB : Body<B>, B1, OB1 : Body<B1>, OH1 : Headers, B2, OB2 : Body<B2>, OH2 : Headers> HttpEndpoint<Parameters1<P1>, IB, Outputs2<OB1, OH1, OB2, OH2>>.send(
//    p1: P1,
//    input: B
//): Selection2<ResponseWithHeaders0<B1>, ResponseWithHeaders0<B2>> {
//    val response =
//        interpreter.send(
//            method = this.method,
//            uri = this.buildURI(p1),
//            headers = this.buildHeaders(p1),
//            contentType = this.input.mediaType,
//            body = this.input.codec.encode(input),
//            outputs = this.outputs.toList()
//        )
//
//    return this.outputs.buildResponse(
//        status = Status.of(response.statusCode.value()),
//        bytes = response.body ?: ByteArray(0)
//    )
//}
