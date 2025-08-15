package dev.akif.tapik.http

import dev.akif.tapik.tuple.*

data class Output<B: Body<*>, H: TupleLike>(val status: Status, val body: B, val headers: H)

@JvmName("output0")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs0>.output(
    status: Status = Status.OK,
    body: () -> B1
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs1<B1, Headers0>> =
    output(status, Headers0, body)

@JvmName("output1")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs1<B1, H1>>.output(
    status: Status = Status.OK,
    body: () -> B2
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs2<B1, H1, B2, Headers0>> =
    output(status, Headers0, body)

@JvmName("output2")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs2<B1, H1, B2, H2>>.output(
    status: Status = Status.OK,
    body: () -> B3
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs3<B1, H1, B2, H2, B3, Headers0>> =
    output(status, Headers0, body)

@JvmName("output3")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    status: Status = Status.OK,
    body: () -> B4
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, Headers0>> =
    output(status, Headers0, body)

@JvmName("output4")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    status: Status = Status.OK,
    body: () -> B5
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, Headers0>> =
    output(status, Headers0, body)

@JvmName("output5")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    status: Status = Status.OK,
    body: () -> B6
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, Headers0>> =
    output(status, Headers0, body)

@JvmName("output6")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    status: Status = Status.OK,
    body: () -> B7
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, Headers0>> =
    output(status, Headers0, body)

@JvmName("output7")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    status: Status = Status.OK,
    body: () -> B8
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, Headers0>> =
    output(status, Headers0, body)

@JvmName("output8")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8: TupleLike, B9: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    status: Status = Status.OK,
    body: () -> B9
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, Headers0>> =
    output(status, Headers0, body)

@JvmName("output9")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8: TupleLike, B9: Body<*>, H9: TupleLike, B10: Body<*>> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    status: Status = Status.OK,
    body: () -> B10
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, Headers0>> =
    output(status, Headers0, body)

@JvmName("outputWithHeader0")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs0>.output(
    status: Status,
    header: Header<H1>,
    body: () -> B1
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs1<B1, Headers1<H1>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader1")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs1<B1, H1>>.output(
    status: Status,
    header: Header<H2>,
    body: () -> B2
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs2<B1, H1, B2, Headers1<H2>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader2")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs2<B1, H1, B2, H2>>.output(
    status: Status,
    header: Header<H3>,
    body: () -> B3
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs3<B1, H1, B2, H2, B3, Headers1<H3>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader3")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    status: Status,
    header: Header<H4>,
    body: () -> B4
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, Headers1<H4>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader4")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    status: Status,
    header: Header<H5>,
    body: () -> B5
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, Headers1<H5>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader5")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    status: Status,
    header: Header<H6>,
    body: () -> B6
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, Headers1<H6>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader6")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    status: Status,
    header: Header<H7>,
    body: () -> B7
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, Headers1<H7>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader7")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    status: Status,
    header: Header<H8>,
    body: () -> B8
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, Headers1<H8>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader8")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8: TupleLike, B9: Body<*>, H9> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    status: Status,
    header: Header<H9>,
    body: () -> B9
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, Headers1<H9>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeader9")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8: TupleLike, B9: Body<*>, H9: TupleLike, B10: Body<*>, H10> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    status: Status,
    header: Header<H10>,
    body: () -> B10
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, Headers1<H10>>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), Headers1(header))
    )

@JvmName("outputWithHeaders0")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs0>.output(
    status: Status,
    headers: H1,
    body: () -> B1
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs1<B1, H1>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders1")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs1<B1, H1>>.output(
    status: Status,
    headers: H2,
    body: () -> B2
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs2<B1, H1, B2, H2>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders2")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs2<B1, H1, B2, H2>>.output(
    status: Status,
    headers: H3,
    body: () -> B3
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs3<B1, H1, B2, H2, B3, H3>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders3")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs3<B1, H1, B2, H2, B3, H3>>.output(
    status: Status,
    headers: H4,
    body: () -> B4
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders4")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs4<B1, H1, B2, H2, B3, H3, B4, H4>>.output(
    status: Status,
    headers: H5,
    body: () -> B5
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders5")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs5<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5>>.output(
    status: Status,
    headers: H6,
    body: () -> B6
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders6")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs6<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6>>.output(
    status: Status,
    headers: H7,
    body: () -> B7
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders7")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs7<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7>>.output(
    status: Status,
    headers: H8,
    body: () -> B8
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders8")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8: TupleLike, B9: Body<*>, H9: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs8<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8>>.output(
    status: Status,
    headers: H9,
    body: () -> B9
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )

@JvmName("outputWithHeaders9")
fun <PathVariables: TupleLike, QueryParameters: TupleLike, Headers: TupleLike, Input: Body<*>, B1: Body<*>, H1: TupleLike, B2: Body<*>, H2: TupleLike, B3: Body<*>, H3: TupleLike, B4: Body<*>, H4: TupleLike, B5: Body<*>, H5: TupleLike, B6: Body<*>, H6: TupleLike, B7: Body<*>, H7: TupleLike, B8: Body<*>, H8: TupleLike, B9: Body<*>, H9: TupleLike, B10: Body<*>, H10: TupleLike> HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs9<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9>>.output(
    status: Status,
    headers: H10,
    body: () -> B10
): HttpEndpoint<PathVariables, QueryParameters, Headers, Input, Outputs10<B1, H1, B2, H2, B3, H3, B4, H4, B5, H5, B6, H6, B7, H7, B8, H8, B9, H9, B10, H10>> =
    HttpEndpoint(
        method = this.method,
        uri = this.uri,
        headers = this.headers,
        input = this.input,
        outputs = this.outputs + Output(status, body(), headers)
    )
