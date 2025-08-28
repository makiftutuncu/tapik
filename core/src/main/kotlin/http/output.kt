@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

data class Output<B : Body<*>, H : Headers>(
    val statusMatcher: StatusMatcher,
    val body: B,
    val headers: H
)

@JvmName("outputWithStatus0")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>> HttpEndpoint<P, Input, Outputs0>.output(
    status: Status = Status.OK,
    body: () -> B1
): HttpEndpoint<P, Input, Outputs1<B1, Headers0>> = output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus1")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>> HttpEndpoint<P, Input, Outputs1<B1, H1>>.output(
    status: Status = Status.OK,
    body: () -> B2
): HttpEndpoint<P, Input, Outputs2<B1, H1, B2, Headers0>> = output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus2")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>> HttpEndpoint<P, Input, Outputs2<B1, H1, B2, H2>>.output(
    status: Status = Status.OK,
    body: () -> B3
): HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, Headers0>> = output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus3")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>> HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    status: Status = Status.OK,
    body: () -> B4
): HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, Headers0>> = output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus4")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>> HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    status: Status = Status.OK,
    body: () -> B5
): HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, Headers0>> = output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus5")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>> HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    status: Status = Status.OK,
    body: () -> B6
): HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, Headers0>> =
    output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus6")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>> HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    status: Status = Status.OK,
    body: () -> B7
): HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, Headers0>> =
    output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus7")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>> HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    status: Status = Status.OK,
    body: () -> B8
): HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, Headers0>> =
    output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus8")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>> HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    status: Status = Status.OK,
    body: () -> B9
): HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, Headers0>> =
    output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("outputWithStatus9")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Headers, B10 : Body<*>> HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    status: Status = Status.OK,
    body: () -> B10
): HttpEndpoint<P, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, Headers0>> =
    output(StatusMatcher.Is(status), Headers0(), body)

@JvmName("output0")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>> HttpEndpoint<P, Input, Outputs0>.output(
    statusMatcher: StatusMatcher,
    body: () -> B1
): HttpEndpoint<P, Input, Outputs1<B1, Headers0>> = output(statusMatcher, Headers0(), body)

@JvmName("output1")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>> HttpEndpoint<P, Input, Outputs1<B1, H1>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B2
): HttpEndpoint<P, Input, Outputs2<B1, H1, B2, Headers0>> = output(statusMatcher, Headers0(), body)

@JvmName("output2")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>> HttpEndpoint<P, Input, Outputs2<B1, H1, B2, H2>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B3
): HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, Headers0>> = output(statusMatcher, Headers0(), body)

@JvmName("output3")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>> HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B4
): HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, Headers0>> = output(statusMatcher, Headers0(), body)

@JvmName("output4")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>> HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B5
): HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, Headers0>> = output(statusMatcher, Headers0(), body)

@JvmName("output5")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>> HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B6
): HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, Headers0>> =
    output(statusMatcher, Headers0(), body)

@JvmName("output6")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>> HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B7
): HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, Headers0>> =
    output(statusMatcher, Headers0(), body)

@JvmName("output7")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>> HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B8
): HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, Headers0>> =
    output(statusMatcher, Headers0(), body)

@JvmName("output8")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>> HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B9
): HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, Headers0>> =
    output(statusMatcher, Headers0(), body)

@JvmName("output9")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Headers, B10 : Body<*>> HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    statusMatcher: StatusMatcher,
    body: () -> B10
): HttpEndpoint<P, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, Headers0>> =
    output(statusMatcher, Headers0(), body)

@JvmName("outputWithStatusAndHeader0")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Any> HttpEndpoint<P, Input, Outputs0>.output(
    status: Status,
    header: Header<H1>,
    body: () -> B1
): HttpEndpoint<P, Input, Outputs1<B1, Headers1<H1>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader1")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Any> HttpEndpoint<P, Input, Outputs1<B1, H1>>.output(
    status: Status,
    header: Header<H2>,
    body: () -> B2
): HttpEndpoint<P, Input, Outputs2<B1, H1, B2, Headers1<H2>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader2")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Any> HttpEndpoint<P, Input, Outputs2<B1, H1, B2, H2>>.output(
    status: Status,
    header: Header<H3>,
    body: () -> B3
): HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, Headers1<H3>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader3")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Any> HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    status: Status,
    header: Header<H4>,
    body: () -> B4
): HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, Headers1<H4>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader4")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Any> HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    status: Status,
    header: Header<H5>,
    body: () -> B5
): HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, Headers1<H5>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader5")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Any> HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    status: Status,
    header: Header<H6>,
    body: () -> B6
): HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, Headers1<H6>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader6")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Any> HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    status: Status,
    header: Header<H7>,
    body: () -> B7
): HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, Headers1<H7>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader7")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Any> HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    status: Status,
    header: Header<H8>,
    body: () -> B8
): HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, Headers1<H8>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader8")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Any> HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    status: Status,
    header: Header<H9>,
    body: () -> B9
): HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, Headers1<H9>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithStatusAndHeader9")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Headers, B10 : Body<*>, H10 : Any> HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    status: Status,
    header: Header<H10>,
    body: () -> B10
): HttpEndpoint<P, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, Headers1<H10>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(StatusMatcher.Is(status), body(), Headers1(header))
    )

@JvmName("outputWithHeader0")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Any> HttpEndpoint<P, Input, Outputs0>.output(
    statusMatcher: StatusMatcher,
    header: Header<H1>,
    body: () -> B1
): HttpEndpoint<P, Input, Outputs1<B1, Headers1<H1>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader1")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Any> HttpEndpoint<P, Input, Outputs1<B1, H1>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H2>,
    body: () -> B2
): HttpEndpoint<P, Input, Outputs2<B1, H1, B2, Headers1<H2>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader2")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Any> HttpEndpoint<P, Input, Outputs2<B1, H1, B2, H2>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H3>,
    body: () -> B3
): HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, Headers1<H3>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader3")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Any> HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H4>,
    body: () -> B4
): HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, Headers1<H4>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader4")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Any> HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H5>,
    body: () -> B5
): HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, Headers1<H5>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader5")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Any> HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H6>,
    body: () -> B6
): HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, Headers1<H6>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader6")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Any> HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H7>,
    body: () -> B7
): HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, Headers1<H7>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader7")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Any> HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H8>,
    body: () -> B8
): HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, Headers1<H8>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader8")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Any> HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H9>,
    body: () -> B9
): HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, Headers1<H9>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeader9")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Headers, B10 : Body<*>, H10 : Any> HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    statusMatcher: StatusMatcher,
    header: Header<H10>,
    body: () -> B10
): HttpEndpoint<P, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, Headers1<H10>>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), Headers1(header))
    )

@JvmName("outputWithHeaders0")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers> HttpEndpoint<P, Input, Outputs0>.output(
    statusMatcher: StatusMatcher,
    headers: H1,
    body: () -> B1
): HttpEndpoint<P, Input, Outputs1<B1, H1>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders1")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers> HttpEndpoint<P, Input, Outputs1<B1, H1>>.output(
    statusMatcher: StatusMatcher,
    headers: H2,
    body: () -> B2
): HttpEndpoint<P, Input, Outputs2<B1, H1, B2, H2>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders2")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers> HttpEndpoint<P, Input, Outputs2<B1, H1, B2, H2>>.output(
    statusMatcher: StatusMatcher,
    headers: H3,
    body: () -> B3
): HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, H3>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders3")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers> HttpEndpoint<P, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    statusMatcher: StatusMatcher,
    headers: H4,
    body: () -> B4
): HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders4")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers> HttpEndpoint<P, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    statusMatcher: StatusMatcher,
    headers: H5,
    body: () -> B5
): HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders5")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers> HttpEndpoint<P, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    statusMatcher: StatusMatcher,
    headers: H6,
    body: () -> B6
): HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders6")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers> HttpEndpoint<P, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    statusMatcher: StatusMatcher,
    headers: H7,
    body: () -> B7
): HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders7")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers> HttpEndpoint<P, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    statusMatcher: StatusMatcher,
    headers: H8,
    body: () -> B8
): HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders8")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Headers> HttpEndpoint<P, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    statusMatcher: StatusMatcher,
    headers: H9,
    body: () -> B9
): HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )

@JvmName("outputWithHeaders9")
fun <P : Parameters, Input : Body<*>, B1 : Body<*>, H1 : Headers, B2 : Body<*>, H2 : Headers, B3 : Body<*>, H3 : Headers, B4 : Body<*>, H4 : Headers, B5 : Body<*>, H5 : Headers, B6 : Body<*>, H6 : Headers, B7 : Body<*>, H7 : Headers, B8 : Body<*>, H8 : Headers, B9 : Body<*>, H9 : Headers, B10 : Body<*>, H10 : Headers> HttpEndpoint<P, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    statusMatcher: StatusMatcher,
    headers: H10,
    body: () -> B10
): HttpEndpoint<P, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, H10>> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uri = this.uri,
        parameters = this.parameters,
        input = this.input,
        outputs = this.outputs + Output(statusMatcher, body(), headers)
    )
