package dev.akif.tapik.http

import dev.akif.tapik.tuple.Tuple

fun <PathVariables: Tuple, QueryParameters: Tuple, Headers: Tuple, Input: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, EmptyBody, Outputs0>.input(
    input: Input
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs0> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = input,
        outputs = this.outputs
    )
