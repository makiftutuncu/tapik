package dev.akif.tapik.http

import dev.akif.tapik.selections.*

fun <B> Outputs0.buildResponse(status: Status): EmptyResponse<B> = EmptyResponse(status)

fun <B1, OB1 : Body<B1>, H1 : Headers> Outputs1<OB1, H1>.buildResponse(
    status: Status,
    bytes: ByteArray
): Response0<B1> =
    when {
        item1.statusMatcher(status) -> item1.decode(bytes, status, { Response0(status, it) }) { it }
        else -> error("Unexpected response status $status and body: ${String(bytes)}")
    }

fun <B1, OB1 : Body<B1>, H1 : Headers, B2, OB2 : Body<B2>, H2 : Headers> Outputs2<OB1, H1, OB2, H2>.buildResponse(
    status: Status,
    bytes: ByteArray
): Selection2<Response0<B1>, Response0<B2>> =
    when {
        item1.statusMatcher(status) -> item1.decode(bytes, status, { Response0(status, it) }) { Selection2.Option1(it) }
        item2.statusMatcher(status) -> item2.decode(bytes, status, { Response0(status, it) }) { Selection2.Option2(it) }
        else -> error("Unexpected response status $status and body: ${String(bytes)}")
    }

fun <B1, OB1 : Body<B1>, H1 : Headers, B2, OB2 : Body<B2>, H2 : Headers, B3, OB3 : Body<B3>, H3 : Headers> Outputs3<OB1, H1, OB2, H2, OB3, H3>.buildResponse(
    status: Status,
    bytes: ByteArray
): Selection3<Response0<B1>, Response0<B2>, Response0<B3>> =
    when {
        item1.statusMatcher(status) -> item1.decode(bytes, status, { Response0(status, it) }) { Selection3.Option1(it) }
        item2.statusMatcher(status) -> item2.decode(bytes, status, { Response0(status, it) }) { Selection3.Option2(it) }
        item3.statusMatcher(status) -> item3.decode(bytes, status, { Response0(status, it) }) { Selection3.Option3(it) }
        else -> error("Unexpected response status $status and body: ${String(bytes)}")
    }

// TODO: Complete to 10

private fun <B, OB : Body<B>, H : Headers, R : Response<B>, O> Output<OB, H>.decode(
    bytes: ByteArray,
    status: Status,
    buildResponse: (B) -> R,
    construct: (R) -> O
): O =
    body.codec.decode(bytes).fold(
        { error("Cannot decode response for status $status as ${body.codec.sourceClass.simpleName}") },
        { body -> construct(buildResponse(body)) }
    )
