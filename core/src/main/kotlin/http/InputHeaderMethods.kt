@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

@JvmName("inputHeader0")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any> HttpEndpoint<P, Headers0, IB, OH, OB>.inputHeader(
    header: Header<H1>
): HttpEndpoint<P, Headers1<H1>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader1")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any> HttpEndpoint<P, Headers1<H1>, IB, OH, OB>.inputHeader(
    header: Header<H2>
): HttpEndpoint<P, Headers2<H1, H2>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader2")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any> HttpEndpoint<P, Headers2<H1, H2>, IB, OH, OB>.inputHeader(
    header: Header<H3>
): HttpEndpoint<P, Headers3<H1, H2, H3>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader3")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any> HttpEndpoint<P, Headers3<H1, H2, H3>, IB, OH, OB>.inputHeader(
    header: Header<H4>
): HttpEndpoint<P, Headers4<H1, H2, H3, H4>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader4")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any> HttpEndpoint<P, Headers4<H1, H2, H3, H4>, IB, OH, OB>.inputHeader(
    header: Header<H5>
): HttpEndpoint<P, Headers5<H1, H2, H3, H4, H5>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader5")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any> HttpEndpoint<P, Headers5<H1, H2, H3, H4, H5>, IB, OH, OB>.inputHeader(
    header: Header<H6>
): HttpEndpoint<P, Headers6<H1, H2, H3, H4, H5, H6>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader6")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any> HttpEndpoint<P, Headers6<H1, H2, H3, H4, H5, H6>, IB, OH, OB>.inputHeader(
    header: Header<H7>
): HttpEndpoint<P, Headers7<H1, H2, H3, H4, H5, H6, H7>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader7")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any> HttpEndpoint<P, Headers7<H1, H2, H3, H4, H5, H6, H7>, IB, OH, OB>.inputHeader(
    header: Header<H8>
): HttpEndpoint<P, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader8")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any> HttpEndpoint<P, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, IB, OH, OB>.inputHeader(
    header: Header<H9>
): HttpEndpoint<P, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader9")
fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, H1: Any, H2: Any, H3: Any, H4: Any, H5: Any, H6: Any, H7: Any, H8: Any, H9: Any, H10: Any> HttpEndpoint<P, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, IB, OH, OB>.inputHeader(
    header: Header<H10>
): HttpEndpoint<P, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )
