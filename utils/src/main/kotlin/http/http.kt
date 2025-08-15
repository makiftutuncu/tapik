package dev.akif.tapik.http

import dev.akif.tapik.Endpoint
import dev.akif.tapik.tuple.TupleLike

val root: URIBuilder<PathVariables0, QueryParameters0> = URIBuilder(emptyList(), PathVariables0, QueryParameters0)

val http: HttpEndpointWithoutMethod = HttpEndpointWithoutMethod

internal fun undefined(what: String): Nothing = throw NotImplementedError("Undefined $what")

data object HttpEndpointWithoutMethod: Endpoint<Nothing, Nothing, Nothing, Nothing, Nothing>() {
    override val method: Nothing
        get() = undefined("method")

    override val uri: Nothing
        get() = undefined("uri")

    override val headers: Nothing
        get() = undefined("headers")

    override val input: Nothing
        get() = undefined("input")

    override val outputs: Nothing
        get() = undefined("outputs")

    val get: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.GET)
    val head: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.HEAD)
    val post: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.POST)
    val put: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.PUT)
    val patch: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.PATCH)
    val delete: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.DELETE)
    val options: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.OPTIONS)
    val trace: HttpEndpointWithoutURI = HttpEndpointWithoutURI(Method.TRACE)
}

data class HttpEndpointWithoutURI(override val method: Method) : Endpoint<Method, Nothing, Nothing, Nothing, Nothing>() {
    override val uri: Nothing
        get() = undefined("uri")

    override val headers: Nothing
        get() = undefined("headers")

    override val input: Nothing
        get() = undefined("input")

    override val outputs: Nothing
        get() = undefined("outputs")

    fun <PathVariables : TupleLike, QueryParameters: TupleLike> uri(
        uri: URIBuilder<PathVariables, QueryParameters>
    ): HttpEndpoint<PathVariables, QueryParameters, Headers0, EmptyBody, Outputs0> =
        HttpEndpoint(
            method = this.method,
            uri = uri,
            headers = Headers0,
            input = EmptyBody,
            outputs = Outputs0
        )
}

data class HttpEndpoint<PathVariables : TupleLike, QueryParameters : TupleLike, Headers: TupleLike, Input: Body<*>, Outputs: TupleLike>(
    override val method: Method,
    override val uri: URIBuilder<PathVariables, QueryParameters>,
    override val headers: Headers,
    override val input: Input,
    override val outputs: Outputs,
) : Endpoint<Method, URIBuilder<PathVariables, QueryParameters>, Headers, Input, Outputs>()
