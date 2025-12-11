@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Replaces the request body while keeping the existing empty header tuple.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param body supplier producing the new body definition.
 * @return a new building context with the specified body.
 */
@JvmName("inputBody")
fun <P : Parameters, O : Outputs, B : Body<*>> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers0, B>, O> =
    modifiedAs(
        input = Input(this.input.headers, body()),
        outputs = this.outputs
    )

/**
 * Adds a single request header definition while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 header definition to include.
 * @return a new building context including the specified header.
 */
@JvmName("inputHeaders1")
fun <P : Parameters, O : Outputs, H1 : Any> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>
): HttpEndpointBuildingContext<P, Input<Headers1<H1>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds two request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders2")
fun <P : Parameters, O : Outputs, H1 : Any, H2 : Any> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>
): HttpEndpointBuildingContext<P, Input<Headers2<H1, H2>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds three request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders3")
fun <P : Parameters, O : Outputs, H1 : Any, H2 : Any, H3 : Any> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>
): HttpEndpointBuildingContext<P, Input<Headers3<H1, H2, H3>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds four request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders4")
fun <P : Parameters, O : Outputs, H1 : Any, H2 : Any, H3 : Any, H4 : Any> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>
): HttpEndpointBuildingContext<P, Input<Headers4<H1, H2, H3, H4>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds five request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders5")
fun <P : Parameters, O : Outputs, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>
): HttpEndpointBuildingContext<P, Input<Headers5<H1, H2, H3, H4, H5>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds six request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders6")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>
): HttpEndpointBuildingContext<P, Input<Headers6<H1, H2, H3, H4, H5, H6>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds seven request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders7")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>
): HttpEndpointBuildingContext<P, Input<Headers7<H1, H2, H3, H4, H5, H6, H7>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds eight request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @param H8 type of the eighth header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders8")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    H8 : Any
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>
): HttpEndpointBuildingContext<P, Input<Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7, h8), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds nine request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @param H8 type of the eighth header to add.
 * @param H9 type of the ninth header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @param h9 ninth header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders9")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    H8 : Any,
    H9 : Any
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>,
    h9: Header<H9>
): HttpEndpointBuildingContext<P, Input<Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7, h8, h9), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds ten request header definitions while keeping the empty body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @param H8 type of the eighth header to add.
 * @param H9 type of the ninth header to add.
 * @param H10 type of the tenth header to add.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @param h9 ninth header definition.
 * @param h10 tenth header definition.
 * @return a new building context including the specified headers.
 */
@JvmName("inputHeaders10")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    H8 : Any,
    H9 : Any,
    H10 : Any
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>,
    h9: Header<H9>,
    h10: Header<H10>
): HttpEndpointBuildingContext<P, Input<Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, EmptyBody>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds a single header definition and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 header definition to include.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified header and body.
 */
@JvmName("inputHeadersBody1")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers1<H1>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1), body()),
        outputs = this.outputs
    )

/**
 * Adds two header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody2")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers2<H1, H2>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2), body()),
        outputs = this.outputs
    )

/**
 * Adds three header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody3")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers3<H1, H2, H3>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3), body()),
        outputs = this.outputs
    )

/**
 * Adds four header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody4")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers4<H1, H2, H3, H4>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4), body()),
        outputs = this.outputs
    )

/**
 * Adds five header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody5")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers5<H1, H2, H3, H4, H5>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5), body()),
        outputs = this.outputs
    )

/**
 * Adds six header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody6")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers6<H1, H2, H3, H4, H5, H6>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6), body()),
        outputs = this.outputs
    )

/**
 * Adds seven header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody7")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers7<H1, H2, H3, H4, H5, H6, H7>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7), body()),
        outputs = this.outputs
    )

/**
 * Adds eight header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @param H8 type of the eighth header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody8")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    H8 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7, h8), body()),
        outputs = this.outputs
    )

/**
 * Adds nine header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @param H8 type of the eighth header to add.
 * @param H9 type of the ninth header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @param h9 ninth header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody9")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    H8 : Any,
    H9 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>,
    h9: Header<H9>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7, h8, h9), body()),
        outputs = this.outputs
    )

/**
 * Adds ten header definitions and replaces the body.
 *
 * @param P tuple capturing referenced path and query parameters.
 * @param O outbound response definitions.
 * @param H1 type of the first header to add.
 * @param H2 type of the second header to add.
 * @param H3 type of the third header to add.
 * @param H4 type of the fourth header to add.
 * @param H5 type of the fifth header to add.
 * @param H6 type of the sixth header to add.
 * @param H7 type of the seventh header to add.
 * @param H8 type of the eighth header to add.
 * @param H9 type of the ninth header to add.
 * @param H10 type of the tenth header to add.
 * @param B type of the new request body definition.
 * @receiver Endpoint with no body defined.
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @param h9 ninth header definition.
 * @param h10 tenth header definition.
 * @param body supplier producing the new body definition.
 * @return a new building context including the specified headers and body.
 */
@JvmName("inputHeadersBody10")
fun <
    P : Parameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    H8 : Any,
    H9 : Any,
    H10 : Any,
    B : Body<*>
> HttpEndpointBuildingContext<P, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>,
    h9: Header<H9>,
    h10: Header<H10>,
    body: () -> B
): HttpEndpointBuildingContext<P, Input<Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, B>, O> =
    modifiedAs(
        input = Input(headersOf(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10), body()),
        outputs = this.outputs
    )
