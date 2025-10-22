@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Adds the first input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers0.plus
 */
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

/**
 * Adds the second input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers1.plus
 */
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

/**
 * Adds the third input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers2.plus
 */
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

/**
 * Adds the 4th input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers3.plus
 */
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

/**
 * Adds the 5th input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers4.plus
 */
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

/**
 * Adds the 6th input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers5.plus
 */
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

/**
 * Adds the 7th input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers6.plus
 */
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

/**
 * Adds the 8th input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers7.plus
 */
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

/**
 * Adds the 9th input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers8.plus
 */
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

/**
 * Adds the 10th input header definition to the endpoint.
 *
 * @param header header definition to append to the current tuple.
 * @return endpoint instance updated with the additional header.
 * @see Headers9.plus
 */
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
