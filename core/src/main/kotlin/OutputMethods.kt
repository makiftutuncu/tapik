@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/** Associates a [StatusMatcher] with an output [Body] definition and optional response headers. */
data class Output<H : Headers, B : Body<*>>(
    /** Status matching logic that selects this output definition. */
    val statusMatcher: StatusMatcher,
    /** Headers emitted when [statusMatcher] is satisfied. */
    val headers: H,
    /** Body definition emitted when [statusMatcher] is satisfied. */
    val body: B
)

@JvmName("outputWithStatus0")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs0>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs1<Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders0")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs0>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs1<Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher0")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs0>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs1<Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders0")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs0>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs1<Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus1")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs1<O1>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs2<O1, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders1")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs1<O1>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs2<O1, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher1")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs1<O1>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs2<O1, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders1")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs1<O1>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs2<O1, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus2")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs2<O1, O2>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs3<O1, O2, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders2")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs2<O1, O2>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs3<O1, O2, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher2")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs2<O1, O2>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs3<O1, O2, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders2")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs2<O1, O2>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs3<O1, O2, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus3")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs3<O1, O2, O3>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders3")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs3<O1, O2, O3>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher3")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs3<O1, O2, O3>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders3")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs3<O1, O2, O3>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus4")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, O4>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders4")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, O4>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher4")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, O4>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders4")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs4<O1, O2, O3, O4>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus5")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, O5>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders5")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, O5>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher5")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, O5>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders5")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs5<O1, O2, O3, O4, O5>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus6")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders6")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher6")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders6")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus7")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders7")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher7")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders7")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus8")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders8")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher8")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders8")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}

@JvmName("outputWithStatus9")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

@JvmName("outputWithStatusHeaders9")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

@JvmName("outputMatcher9")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

@JvmName("outputMatcherHeaders9")
fun <U : URIWithParameters, IH : Headers, IB : Body<*>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<U, IH, IB, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<U, IH, IB, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<H, B>>> {
    val headerTuple = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        inputHeaders = this.inputHeaders,
        inputBody = this.inputBody,
        outputs = this.outputs + Output(statusMatcher, headerTuple, bodyDefinition)
    )
}
