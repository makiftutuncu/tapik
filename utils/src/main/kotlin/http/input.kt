package dev.akif.tapik.http

import dev.akif.tapik.tuple.TupleLike

fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, EmptyBody, Outputs0>.input(
    input: Input
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs0> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = input,
        outputs = this.outputs
    )
