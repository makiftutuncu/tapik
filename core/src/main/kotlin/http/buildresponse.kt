package dev.akif.tapik.http

import dev.akif.tapik.types.*

fun <B> Outputs0.buildResponse(status: Status): EmptyResponse<B> = EmptyResponse(status)

fun <B1, OB1 : Body<B1>, H1 : Headers> Outputs1<OB1, H1>.buildResponse(status: Status, bytes: ByteArray): Response0<B1> =
    when (status) {
        item1.status -> item1.decode(bytes, { Response0(status, it) }) { it }
        else -> error("Unexpected response status $status and body: ${String(bytes)}")
    }

fun <B1, OB1 : Body<B1>, H1 : Headers, B2, OB2 : Body<B2>, H2 : Headers> Outputs2<OB1, H1, OB2, H2>.buildResponse(
    status: Status, bytes: ByteArray
): OneOf2<Response0<B1>, Response0<B2>> =
    when (status) {
        item1.status -> item1.decode(bytes, { Response0(status, it) }) { OneOf2.Value1(it) }
        item2.status -> item2.decode(bytes, { Response0(status, it) }) { OneOf2.Value2(it) }
        else -> error("Unexpected response status $status and body: ${String(bytes)}")
    }

private fun <B, OB : Body<B>, H : Headers, R: Response<B>, O> Output<OB, H>.decode(
    bytes: ByteArray,
    buildResponse: (B) -> R,
    construct: (R) -> O
): O =
    body.codec.decode(bytes).fold(
        { error("Cannot decode response for status $status as ${body.codec.sourceClass.simpleName}") },
        { body -> construct(buildResponse(body)) }
    )
