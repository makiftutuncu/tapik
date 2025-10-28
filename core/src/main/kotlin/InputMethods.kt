@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Replaces the request body while keeping the existing empty header tuple.
 *
 * @param body supplier producing the new body definition.
 */
@JvmName("inputBody")
fun <U : URIWithParameters, O : Outputs, B : Body<*>> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    body: () -> B
): HttpEndpoint<U, Input<Headers0, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(this.input.headers, body()),
        outputs = this.outputs
    )

/**
 * Adds a single request header definition while keeping the empty body.
 *
 * @param h1 header definition to include.
 */
@JvmName("inputHeaders1")
fun <U : URIWithParameters, O : Outputs, H1 : Any> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>
): HttpEndpoint<U, Input<Headers1<H1>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers1(h1), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds two request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 */
@JvmName("inputHeaders2")
fun <U : URIWithParameters, O : Outputs, H1 : Any, H2 : Any> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>
): HttpEndpoint<U, Input<Headers2<H1, H2>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers2(h1, h2), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds three request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 */
@JvmName("inputHeaders3")
fun <U : URIWithParameters, O : Outputs, H1 : Any, H2 : Any, H3 : Any> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>
): HttpEndpoint<U, Input<Headers3<H1, H2, H3>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers3(h1, h2, h3), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds four request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 */
@JvmName("inputHeaders4")
fun <U : URIWithParameters, O : Outputs, H1 : Any, H2 : Any, H3 : Any, H4 : Any> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>
): HttpEndpoint<U, Input<Headers4<H1, H2, H3, H4>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers4(h1, h2, h3, h4), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds five request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 */
@JvmName("inputHeaders5")
fun <U : URIWithParameters, O : Outputs, H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>
): HttpEndpoint<U, Input<Headers5<H1, H2, H3, H4, H5>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers5(h1, h2, h3, h4, h5), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds six request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 */
@JvmName("inputHeaders6")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>
): HttpEndpoint<U, Input<Headers6<H1, H2, H3, H4, H5, H6>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers6(h1, h2, h3, h4, h5, h6), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds seven request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 */
@JvmName("inputHeaders7")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>
): HttpEndpoint<U, Input<Headers7<H1, H2, H3, H4, H5, H6, H7>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers7(h1, h2, h3, h4, h5, h6, h7), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds eight request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 */
@JvmName("inputHeaders8")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    H8 : Any
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>
): HttpEndpoint<U, Input<Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers8(h1, h2, h3, h4, h5, h6, h7, h8), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds nine request header definitions while keeping the empty body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @param h9 ninth header definition.
 */
@JvmName("inputHeaders9")
fun <
    U : URIWithParameters,
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
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>,
    h9: Header<H9>
): HttpEndpoint<U, Input<Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers9(h1, h2, h3, h4, h5, h6, h7, h8, h9), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds ten request header definitions while keeping the empty body.
 *
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
 */
@JvmName("inputHeaders10")
fun <
    U : URIWithParameters,
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
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
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
): HttpEndpoint<U, Input<Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, EmptyBody>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers10(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10), this.input.body),
        outputs = this.outputs
    )

/**
 * Adds a single header definition and replaces the body.
 *
 * @param h1 header definition to include.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody1")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    B : Body<*>
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    body: () -> B
): HttpEndpoint<U, Input<Headers1<H1>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers1(h1), body()),
        outputs = this.outputs
    )

/**
 * Adds a header definition and replaces the body.
 *
 * @param h1 header definition to include.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody2")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    B : Body<*>
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    body: () -> B
): HttpEndpoint<U, Input<Headers2<H1, H2>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers2(h1, h2), body()),
        outputs = this.outputs
    )

/**
 * Adds two header definitions and replaces the body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody3")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    B : Body<*>
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    body: () -> B
): HttpEndpoint<U, Input<Headers3<H1, H2, H3>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers3(h1, h2, h3), body()),
        outputs = this.outputs
    )

/**
 * Adds three header definitions and replaces the body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody4")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    B : Body<*>
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    body: () -> B
): HttpEndpoint<U, Input<Headers4<H1, H2, H3, H4>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers4(h1, h2, h3, h4), body()),
        outputs = this.outputs
    )

/**
 * Adds four header definitions and replaces the body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody5")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    B : Body<*>
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    body: () -> B
): HttpEndpoint<U, Input<Headers5<H1, H2, H3, H4, H5>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers5(h1, h2, h3, h4, h5), body()),
        outputs = this.outputs
    )

/**
 * Adds five header definitions and replaces the body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody6")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    B : Body<*>
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    body: () -> B
): HttpEndpoint<U, Input<Headers6<H1, H2, H3, H4, H5, H6>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers6(h1, h2, h3, h4, h5, h6), body()),
        outputs = this.outputs
    )

/**
 * Adds six header definitions and replaces the body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody7")
fun <
    U : URIWithParameters,
    O : Outputs,
    H1 : Any,
    H2 : Any,
    H3 : Any,
    H4 : Any,
    H5 : Any,
    H6 : Any,
    H7 : Any,
    B : Body<*>
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    body: () -> B
): HttpEndpoint<U, Input<Headers7<H1, H2, H3, H4, H5, H6, H7>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers7(h1, h2, h3, h4, h5, h6, h7), body()),
        outputs = this.outputs
    )

/**
 * Adds seven header definitions and replaces the body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody8")
fun <
    U : URIWithParameters,
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
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
    h1: Header<H1>,
    h2: Header<H2>,
    h3: Header<H3>,
    h4: Header<H4>,
    h5: Header<H5>,
    h6: Header<H6>,
    h7: Header<H7>,
    h8: Header<H8>,
    body: () -> B
): HttpEndpoint<U, Input<Headers8<H1, H2, H3, H4, H5, H6, H7, H8>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers8(h1, h2, h3, h4, h5, h6, h7, h8), body()),
        outputs = this.outputs
    )

/**
 * Adds eight header definitions and replaces the body.
 *
 * @param h1 first header definition.
 * @param h2 second header definition.
 * @param h3 third header definition.
 * @param h4 fourth header definition.
 * @param h5 fifth header definition.
 * @param h6 sixth header definition.
 * @param h7 seventh header definition.
 * @param h8 eighth header definition.
 * @param body supplier producing the new body definition.
 */
@JvmName("inputHeadersBody9")
fun <
    U : URIWithParameters,
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
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
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
): HttpEndpoint<U, Input<Headers9<H1, H2, H3, H4, H5, H6, H7, H8, H9>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers9(h1, h2, h3, h4, h5, h6, h7, h8, h9), body()),
        outputs = this.outputs
    )

/**
 * Adds nine header definitions and replaces the body.
 *
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
 */
@JvmName("inputHeadersBody10")
fun <
    U : URIWithParameters,
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
> HttpEndpoint<U, Input<Headers0, EmptyBody>, O>.input(
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
): HttpEndpoint<U, Input<Headers10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>, B>, O> =
    HttpEndpoint(
        id = this.id,
        description = this.description,
        details = this.details,
        method = this.method,
        uriWithParameters = this.uriWithParameters,
        input = Input(Headers10(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10), body()),
        outputs = this.outputs
    )
