@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

@JvmName("outputHeader0")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any> HttpEndpoint<P, IH, IB, Headers0, OB>.outputHeader(
    header: Header<H1>
): HttpEndpoint<P, IH, IB, Headers1<H1>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader1")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any> HttpEndpoint<P, IH, IB, Headers1<H1>, OB>.outputHeader(
    header: Header<H2>
): HttpEndpoint<P, IH, IB, Headers2<H1, H2>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader2")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any> HttpEndpoint<P, IH, IB, Headers2<H1, H2>, OB>.outputHeader(
    header: Header<H3>
): HttpEndpoint<P, IH, IB, Headers3<H1, H2, H3>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader3")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any> HttpEndpoint<P, IH, IB, Headers3<H1, H2, H3>, OB>.outputHeader(
    header: Header<H4>
): HttpEndpoint<P, IH, IB, Headers4<H1, H2, H3, H4>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader4")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any> HttpEndpoint<P, IH, IB, Headers4<H1, H2, H3, H4>, OB>.outputHeader(
    header: Header<H5>
): HttpEndpoint<P, IH, IB, Headers5<H1, H2, H3, H4, H5>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader5")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any> HttpEndpoint<P, IH, IB, Headers5<H1, H2, H3, H4, H5>, OB>.outputHeader(
    header: Header<H6>
): HttpEndpoint<P, IH, IB, Headers6<H1, H2, H3, H4, H5, H6>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader6")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any> HttpEndpoint<P, IH, IB, Headers6<H1, H2, H3, H4, H5, H6>, OB>.outputHeader(
    header: Header<H7>
): HttpEndpoint<P, IH, IB, Headers7<H1, H2, H3, H4, H5, H6, H7>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader7")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any> HttpEndpoint<P, IH, IB, Headers7<H1, H2, H3, H4, H5, H6, H7>, OB>.outputHeader(
    header: Header<H8>
): HttpEndpoint<P, IH, IB, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader8")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any> HttpEndpoint<P, IH, IB, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, OB>.outputHeader(
    header: Header<H9>
): HttpEndpoint<P, IH, IB, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

@JvmName("outputHeader9")
fun <P: Parameters, IH:Headers, IB: Body<*>, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any, H10: Any> HttpEndpoint<P, IH, IB, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, OB>.outputHeader(
    header: Header<H10>
): HttpEndpoint<P, IH, IB, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )
