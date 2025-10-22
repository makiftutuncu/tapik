@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

/** Associates a [StatusMatcher] with an output [Body] definition. */
data class OutputBody<B : Body<*>>(
    /** Status matching logic that selects this output body. */
    val statusMatcher: StatusMatcher,
    /** Body definition emitted when [statusMatcher] is satisfied. */
    val body: B
)

/**
 * Adds an output body that matches [status] for the endpoint.
 *
 * @param status HTTP status that will be matched against the response.
 * @param body builder that produces the output body definition.
 * @return a new endpoint with the appended body configuration.
 * @see StatusMatcher.Is
 */
@JvmName("outputWithStatus0")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies0>.outputBody(
    status: Status = Status.OK,
    body: () -> B1
): HttpEndpoint<U, IH, IB, OH, OutputBodies1<B1>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus1")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies1<B1>>.outputBody(
    status: Status = Status.OK,
    body: () -> B2
): HttpEndpoint<U, IH, IB, OH, OutputBodies2<B1, B2>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus2")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies2<B1, B2>>.outputBody(
    status: Status = Status.OK,
    body: () -> B3
): HttpEndpoint<U, IH, IB, OH, OutputBodies3<B1, B2, B3>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus3")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies3<B1, B2, B3>>.outputBody(
    status: Status = Status.OK,
    body: () -> B4
): HttpEndpoint<U, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus4")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>>.outputBody(
    status: Status = Status.OK,
    body: () -> B5
): HttpEndpoint<U, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus5")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>>.outputBody(
    status: Status = Status.OK,
    body: () -> B6
): HttpEndpoint<U, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus6")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>>.outputBody(
    status: Status = Status.OK,
    body: () -> B7
): HttpEndpoint<U, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus7")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>>.outputBody(
    status: Status = Status.OK,
    body: () -> B8
): HttpEndpoint<U, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus8")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>>.outputBody(
    status: Status = Status.OK,
    body: () -> B9
): HttpEndpoint<U, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/** @see outputBody */
@JvmName("outputWithStatus9")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>, B10 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>>.outputBody(
    status: Status = Status.OK,
    body: () -> B10
): HttpEndpoint<U, IH, IB, OH, OutputBodies10<B1, B2, B3, B4, B5, B6, B7, B8, B9, B10>> =
    outputBody(
        StatusMatcher.Is(status),
        body
    )

/**
 * Adds an output body guarded by a custom [statusMatcher].
 *
 * @param statusMatcher predicate that declares when the accompanying body should be used.
 * @param body builder that produces the output body definition.
 * @return a new endpoint with the appended body configuration.
 * @see StatusMatcher
 */
@JvmName("output0")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies0>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B1
): HttpEndpoint<U, IH, IB, OH, OutputBodies1<B1>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output1")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies1<B1>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B2
): HttpEndpoint<U, IH, IB, OH, OutputBodies2<B1, B2>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output2")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies2<B1, B2>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B3
): HttpEndpoint<U, IH, IB, OH, OutputBodies3<B1, B2, B3>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output3")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies3<B1, B2, B3>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B4
): HttpEndpoint<U, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output4")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B5
): HttpEndpoint<U, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output5")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B6
): HttpEndpoint<U, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output6")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B7
): HttpEndpoint<U, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output7")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B8
): HttpEndpoint<U, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output8")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B9
): HttpEndpoint<U, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

/** @see outputBody */
@JvmName("output9")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>, B10 : Body<*>> HttpEndpoint<U, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B10
): HttpEndpoint<U, IH, IB, OH, OutputBodies10<B1, B2, B3, B4, B5, B6, B7, B8, B9, B10>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )
