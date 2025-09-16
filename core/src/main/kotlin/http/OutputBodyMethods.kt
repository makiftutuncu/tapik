package dev.akif.tapik.http

data class OutputBody<B : Body<*>>(
    val statusMatcher: StatusMatcher,
    val body: B
)

@JvmName("outputWithStatus0")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies0>.outputBody(
    status: Status = Status.OK,
    body: () -> B1
): HttpEndpoint<P, IH, IB, OH, OutputBodies1<B1>> = outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus1")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies1<B1>>.outputBody(
    status: Status = Status.OK,
    body: () -> B2
): HttpEndpoint<P, IH, IB, OH, OutputBodies2<B1, B2>> = outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus2")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies2<B1, B2>>.outputBody(
    status: Status = Status.OK,
    body: () -> B3
): HttpEndpoint<P, IH, IB, OH, OutputBodies3<B1, B2, B3>> = outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus3")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies3<B1, B2, B3>>.outputBody(
    status: Status = Status.OK,
    body: () -> B4
): HttpEndpoint<P, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>> = outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus4")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>>.outputBody(
    status: Status = Status.OK,
    body: () -> B5
): HttpEndpoint<P, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>> = outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus5")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>>.outputBody(
    status: Status = Status.OK,
    body: () -> B6
): HttpEndpoint<P, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>> = outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus6")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>>.outputBody(
    status: Status = Status.OK,
    body: () -> B7
): HttpEndpoint<P, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>> = outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus7")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>>.outputBody(
    status: Status = Status.OK,
    body: () -> B8
): HttpEndpoint<P, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>> =
    outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus8")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>>.outputBody(
    status: Status = Status.OK,
    body: () -> B9
): HttpEndpoint<P, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>> =
    outputBody(StatusMatcher.Is(status), body)

@JvmName("outputWithStatus9")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>, B10 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>>.outputBody(
    status: Status = Status.OK,
    body: () -> B10
): HttpEndpoint<P, IH, IB, OH, OutputBodies10<B1, B2, B3, B4, B5, B6, B7, B8, B9, B10>> =
    outputBody(StatusMatcher.Is(status), body)

@JvmName("output0")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies0>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B1
): HttpEndpoint<P, IH, IB, OH, OutputBodies1<B1>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output1")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies1<B1>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B2
): HttpEndpoint<P, IH, IB, OH, OutputBodies2<B1, B2>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output2")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies2<B1, B2>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B3
): HttpEndpoint<P, IH, IB, OH, OutputBodies3<B1, B2, B3>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output3")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies3<B1, B2, B3>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B4
): HttpEndpoint<P, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output4")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies4<B1, B2, B3, B4>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B5
): HttpEndpoint<P, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output5")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies5<B1, B2, B3, B4, B5>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B6
): HttpEndpoint<P, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output6")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies6<B1, B2, B3, B4, B5, B6>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B7
): HttpEndpoint<P, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output7")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies7<B1, B2, B3, B4, B5, B6, B7>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B8
): HttpEndpoint<P, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output8")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies8<B1, B2, B3, B4, B5, B6, B7, B8>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B9
): HttpEndpoint<P, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )

@JvmName("output9")
fun <P : Parameters, IH : Headers, IB : Body<*>, OH : Headers, B1 : Body<*>, B2 : Body<*>, B3 : Body<*>, B4 : Body<*>, B5 : Body<*>, B6 : Body<*>, B7 : Body<*>, B8 : Body<*>, B9 : Body<*>, B10 : Body<*>> HttpEndpoint<P, IH, IB, OH, OutputBodies9<B1, B2, B3, B4, B5, B6, B7, B8, B9>>.outputBody(
    statusMatcher: StatusMatcher,
    body: () -> B10
): HttpEndpoint<P, IH, IB, OH, OutputBodies10<B1, B2, B3, B4, B5, B6, B7, B8, B9, B10>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputHeaders = this.outputHeaders,
        outputBodies = this.outputBodies + OutputBody(statusMatcher, body())
    )
