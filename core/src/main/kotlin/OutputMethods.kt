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

/**
 * Adds a new output that is selected when the response status equals [status].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares no outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the existing configuration plus the newly added output.
 */
@JvmName("outputWithStatus0")
fun <P : Parameters, I : Input<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs0>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs1<Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Adds a new output that uses [status] together with [headers] and [body].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares no outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the existing configuration plus the newly added output.
 */
@JvmName("outputWithStatusHeaders0")
fun <P : Parameters, I : Input<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs0>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs1<Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Adds a new output that is selected when [statusMatcher] accepts the response and uses [body].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares no outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the existing configuration plus the newly added output.
 */
@JvmName("outputMatcher0")
fun <P : Parameters, I : Input<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs0>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs1<Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Adds a new output that is selected when [statusMatcher] accepts the response and uses [headers] and [body].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares no outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the existing configuration plus the newly added output.
 */
@JvmName("outputMatcherHeaders0")
fun <P : Parameters, I : Input<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs0>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs1<Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving the existing output [O1].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares a single output.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original output and the newly added one.
 */
@JvmName("outputWithStatus1")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs1<O1>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs2<O1, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving the existing output [O1].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares a single output.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original output and the newly added one.
 */
@JvmName("outputWithStatusHeaders1")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs1<O1>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs2<O1, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving the existing output [O1].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares a single output.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original output and the newly added one.
 */
@JvmName("outputMatcher1")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs1<O1>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs2<O1, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving the existing output [O1].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares a single output.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original output and the newly added one.
 */
@JvmName("outputMatcherHeaders1")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs1<O1>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs2<O1, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1] and [O2].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares two outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus2")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs2<O1, O2>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs3<O1, O2, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1] and [O2].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares two outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders2")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs2<O1, O2>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs3<O1, O2, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1] and [O2].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares two outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher2")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs2<O1, O2>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs3<O1, O2, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1] and [O2].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares two outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders2")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs2<O1, O2>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs3<O1, O2, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1], [O2], and [O3].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares three outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus3")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs3<O1, O2, O3>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs4<O1, O2, O3, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1], [O2], and [O3].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares three outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders3")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs3<O1, O2, O3>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs4<O1, O2, O3, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1], [O2], and [O3].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares three outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher3")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs3<O1, O2, O3>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs4<O1, O2, O3, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1], [O2], and [O3].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares three outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders3")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs3<O1, O2, O3>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs4<O1, O2, O3, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1], [O2], [O3], and [O4].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares four outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus4")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs4<O1, O2, O3, O4>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1], [O2], [O3], and [O4].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares four outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders4")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs4<O1, O2, O3, O4>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1], [O2], [O3], and [O4].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares four outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher4")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs4<O1, O2, O3, O4>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1], [O2], [O3], and [O4].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares four outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders4")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs4<O1, O2, O3, O4>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1], [O2], [O3], [O4], and [O5].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares five outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus5")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, O5>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], and [O5].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares five outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders5")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, O5>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1], [O2], [O3], [O4], and [O5].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares five outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher5")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, O5>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], and [O5].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares five outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders5")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs5<O1, O2, O3, O4, O5>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], and [O6].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares six outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus6")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], and [O6].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares six outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders6")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], and [O6].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares six outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher6")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], and [O6].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares six outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders6")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs6<O1, O2, O3, O4, O5, O6>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], and [O7].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares seven outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus7")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], and [O7].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares seven outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders7")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], and [O7].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares seven outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher7")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], and [O7].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares seven outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders7")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs7<O1, O2, O3, O4, O5, O6, O7>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], and [O8].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares eight outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus8")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], and [O8].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares eight outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders8")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], and [O8].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares eight outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher8")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], and [O8].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares eight outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders8")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs8<O1, O2, O3, O4, O5, O6, O7, O8>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}

/**
 * Appends a new output matched by [status] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], [O8], and [O9].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param O9 type of the ninth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares nine outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatus9")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    status: Status = Status.OK,
    body: () -> B
): HttpEndpoint<P, I, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<Headers0, B>>> =
    output(
        StatusMatcher.Is(status),
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [status] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], [O8], and [O9].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param O9 type of the ninth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares nine outputs.
 * @param status HTTP status used to select the new output; defaults to [Status.OK].
 * @param headers factory producing the response headers emitted when [status] matches.
 * @param body factory producing the response body definition when [status] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputWithStatusHeaders9")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    status: Status = Status.OK,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<H, B>>> =
    output(
        StatusMatcher.Is(status),
        headers,
        body
    )

/**
 * Appends a new output matched by [statusMatcher] and using [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], [O8], and [O9].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param O9 type of the ninth existing output that remains unchanged.
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares nine outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcher9")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, B : Body<*>> HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B
): HttpEndpoint<P, I, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<Headers0, B>>> =
    output(
        statusMatcher,
        { Headers0 },
        body
    )

/**
 * Appends a new output matched by [statusMatcher] using [headers] and [body] while preserving [O1], [O2], [O3], [O4], [O5], [O6], [O7], [O8], and [O9].
 *
 * @param U type of the endpoint URI definition.
 * @param I type of the request input definition.
 * @param O1 type of the first existing output that remains unchanged.
 * @param O2 type of the second existing output that remains unchanged.
 * @param O3 type of the third existing output that remains unchanged.
 * @param O4 type of the fourth existing output that remains unchanged.
 * @param O5 type of the fifth existing output that remains unchanged.
 * @param O6 type of the sixth existing output that remains unchanged.
 * @param O7 type of the seventh existing output that remains unchanged.
 * @param O8 type of the eighth existing output that remains unchanged.
 * @param O9 type of the ninth existing output that remains unchanged.
 * @param H type of the response header definition created by [headers].
 * @param B type of the response body definition created by [body].
 * @receiver Endpoint that currently declares nine outputs.
 * @param statusMatcher matcher describing which statuses select the new output.
 * @param headers factory producing the response headers emitted when [statusMatcher] matches.
 * @param body factory producing the response body definition when [statusMatcher] matches.
 * @return endpoint containing the original outputs together with the newly added one.
 */
@JvmName("outputMatcherHeaders9")
fun <P : Parameters, I : Input<*, *>, O1 : Output<*, *>, O2 : Output<*, *>, O3 : Output<*, *>, O4 : Output<*, *>, O5 : Output<*, *>, O6 : Output<*, *>, O7 : Output<*, *>, O8 : Output<*, *>, O9 : Output<*, *>, H : Headers, B : Body<*>> HttpEndpoint<P, I, Outputs9<O1, O2, O3, O4, O5, O6, O7, O8, O9>>.output(
    statusMatcher: StatusMatcher,
    headers: () -> H,
    body: () -> B
): HttpEndpoint<P, I, Outputs10<O1, O2, O3, O4, O5, O6, O7, O8, O9, Output<H, B>>> {
    val headerParameters = headers()
    val bodyDefinition = body()
    return HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        path = this.path,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, headerParameters, bodyDefinition)
    )
}
