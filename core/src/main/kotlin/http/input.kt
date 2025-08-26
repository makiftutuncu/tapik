package dev.akif.tapik.http

fun <P : Parameters, Input : Body<*>> HttpEndpoint<P, EmptyBody, Outputs0>.input(
    input: Input
): HttpEndpoint<P, Input, Outputs0> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = input,
        outputs = this.outputs
    )
