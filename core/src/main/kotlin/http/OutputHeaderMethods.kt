@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

/**
 * Adds the first output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers0.plus
 */
@JvmName("outputHeader0")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any> HttpEndpoint<U, IH, IB, Headers0, OB>.outputHeader(
    header: Header<H1>
): HttpEndpoint<U, IH, IB, Headers1<H1>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the second output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers1.plus
 */
@JvmName("outputHeader1")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any> HttpEndpoint<U, IH, IB, Headers1<H1>, OB>.outputHeader(
    header: Header<H2>
): HttpEndpoint<U, IH, IB, Headers2<H1, H2>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the third output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers2.plus
 */
@JvmName("outputHeader2")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any> HttpEndpoint<U, IH, IB, Headers2<H1, H2>, OB>.outputHeader(
    header: Header<H3>
): HttpEndpoint<U, IH, IB, Headers3<H1, H2, H3>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the 4th output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers3.plus
 */
@JvmName("outputHeader3")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any> HttpEndpoint<U, IH, IB, Headers3<H1, H2, H3>, OB>.outputHeader(
    header: Header<H4>
): HttpEndpoint<U, IH, IB, Headers4<H1, H2, H3, H4>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the 5th output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers4.plus
 */
@JvmName("outputHeader4")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> HttpEndpoint<U, IH, IB, Headers4<H1, H2, H3, H4>, OB>.outputHeader(
    header: Header<H5>
): HttpEndpoint<U, IH, IB, Headers5<H1, H2, H3, H4, H5>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the 6th output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers5.plus
 */
@JvmName("outputHeader5")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> HttpEndpoint<U, IH, IB, Headers5<H1, H2, H3, H4, H5>, OB>.outputHeader(
    header: Header<H6>
): HttpEndpoint<U, IH, IB, Headers6<H1, H2, H3, H4, H5, H6>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the 7th output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers6.plus
 */
@JvmName("outputHeader6")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> HttpEndpoint<U, IH, IB, Headers6<H1, H2, H3, H4, H5, H6>, OB>.outputHeader(
    header: Header<H7>
): HttpEndpoint<U, IH, IB, Headers7<H1, H2, H3, H4, H5, H6, H7>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the 8th output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers7.plus
 */
@JvmName("outputHeader7")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> HttpEndpoint<U, IH, IB, Headers7<H1, H2, H3, H4, H5, H6, H7>, OB>.outputHeader(
    header: Header<H8>
): HttpEndpoint<U, IH, IB, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the 9th output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers8.plus
 */
@JvmName("outputHeader8")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> HttpEndpoint<U, IH, IB, Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, OB>.outputHeader(
    header: Header<H9>
): HttpEndpoint<U, IH, IB, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )

/**
 * Adds the 10th output header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers9.plus
 */
@JvmName("outputHeader9")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OB : OutputBodies, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> HttpEndpoint<U, IH, IB, Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, OB>.outputHeader(
    header: Header<H10>
): HttpEndpoint<U, IH, IB, Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, OB> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders + header,
        outputBodies = this.outputBodies
    )
