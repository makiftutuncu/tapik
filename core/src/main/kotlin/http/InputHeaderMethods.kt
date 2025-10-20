@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

@JvmName("inputHeader0")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any> HttpEndpoint<U, Headers0, IB, OH, OB>.inputHeader(
    header: Header<H1>
): HttpEndpoint<U, Headers1<H1>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader1")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any> HttpEndpoint<U, Headers1<H1>, IB, OH, OB>.inputHeader(
    header: Header<H2>
): HttpEndpoint<U, Headers2<H1, H2>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader2")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any> HttpEndpoint<U, Headers2<H1, H2>, IB, OH, OB>.inputHeader(
    header: Header<H3>
): HttpEndpoint<U, Headers3<H1, H2, H3>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader3")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any> HttpEndpoint<U, Headers3<H1, H2, H3>, IB, OH, OB>.inputHeader(
    header: Header<H4>
): HttpEndpoint<U, Headers4<H1, H2, H3, H4>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader4")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> HttpEndpoint<U, Headers4<H1, H2, H3, H4>, IB, OH, OB>.inputHeader(
    header: Header<H5>
): HttpEndpoint<U, Headers5<H1, H2, H3, H4, H5>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader5")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> HttpEndpoint<U, Headers5<H1, H2, H3, H4, H5>, IB, OH, OB>.inputHeader(
    header: Header<H6>
): HttpEndpoint<U, Headers6<H1, H2, H3, H4, H5, H6>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader6")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> HttpEndpoint<U, Headers6<H1, H2, H3, H4, H5, H6>, IB, OH, OB>.inputHeader(
    header: Header<H7>
): HttpEndpoint<U, Headers7<H1, H2, H3, H4, H5, H6, H7>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader7")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> HttpEndpoint<U, Headers7<H1, H2, H3, H4, H5, H6, H7>, IB, OH, OB>.inputHeader(
    header: Header<H8>
): HttpEndpoint<U, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader8")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> HttpEndpoint<U, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, IB, OH, OB>.inputHeader(
    header: Header<H9>
): HttpEndpoint<U, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )

@JvmName("inputHeader9")
fun <U : URIWithParameters, IB : Body<*>, OH : Headers, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> HttpEndpoint<U, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, IB, OH, OB>.inputHeader(
    header: Header<H10>
): HttpEndpoint<U, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, IB, OH, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders + header,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies
    )
