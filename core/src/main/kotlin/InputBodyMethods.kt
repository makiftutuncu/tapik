@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/** Replaces the empty input body with a concrete [inputBody] definition. */
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : Outputs> HttpEndpoint<U, IH, EmptyBody, OB>.inputBody(
    inputBody: () -> IB
): HttpEndpoint<U, IH, IB, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = inputBody(),
        outputs = this.outputs
    )
