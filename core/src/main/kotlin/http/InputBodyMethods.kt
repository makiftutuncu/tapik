@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, OB : OutputBodies> HttpEndpoint<U, IH, EmptyBody, OH, OB>.inputBody(
    inputBody: () -> IB
): HttpEndpoint<U, IH, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = inputBody(),
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )
