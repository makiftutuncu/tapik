package dev.akif.tapik.http

fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies> HttpEndpoint<P, IH, EmptyBody, OH, OB>.inputBody(
    inputBody: () -> IB
): HttpEndpoint<P, IH, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = inputBody(),
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )
