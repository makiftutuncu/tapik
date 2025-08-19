package dev.akif.tapik.http

import dev.akif.tapik.Endpoint
import dev.akif.tapik.tuple.Tuple

val root: URIBuilder<PathVariables0, QueryParameters0> = URIBuilder(emptyList(), PathVariables0.instance(), QueryParameters0.instance())

val path: PathVariable.Companion = PathVariable.Companion

val query: QueryParameter.Companion = QueryParameter.Companion

val header: Header.Companion = Header.Companion

fun http(id: String, description: String? = null, details: String? = null): HttpEndpointWithoutMethod =
    HttpEndpointWithoutMethod(id, description, details)

internal fun undefined(what: String): Nothing = throw NotImplementedError("Undefined $what")

data class HttpEndpointWithoutMethod(
    override val id: String,
    override val description: String?,
    override val details: String?,
): Endpoint<Nothing, Nothing, Nothing, Nothing, Nothing>() {
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

    val get: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.GET, description, details)
    val head: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.HEAD, description, details)
    val post: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.POST, description, details)
    val put: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.PUT, description, details)
    val patch: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.PATCH, description, details)
    val delete: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.DELETE, description, details)
    val options: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.OPTIONS, description, details)
    val trace: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, Method.TRACE, description, details)
}

data class HttpEndpointWithoutURI(
    override val id: String,
    override val method: Method,
    override val description: String?,
    override val details: String?,
) : Endpoint<Method, Nothing, Nothing, Nothing, Nothing>() {
    override val uri: Nothing
        get() = undefined("uri")

    override val headers: Nothing
        get() = undefined("headers")

    override val input: Nothing
        get() = undefined("input")

    override val outputs: Nothing
        get() = undefined("outputs")

    fun <PathVariables : Tuple<PathVariable<*>>, QueryParameters : Tuple<QueryParameter<*>>> uri(
        uri: URIBuilder<PathVariables, QueryParameters>
    ): HttpEndpoint<PathVariables, QueryParameters, Headers0, EmptyBody, Outputs0> =
        HttpEndpoint(
            id = this.id,
            method = this.method,
            uri = uri,
            headers = Headers0.instance(),
            input = EmptyBody,
            outputs = Outputs0.instance(),
            details = this.details,
            description = this.description,
        )
}

data class HttpEndpoint<PathVariables : Tuple<PathVariable<*>>, QueryParameters : Tuple<QueryParameter<*>>, Headers: Tuple<Header<*>>, Input: Body<*>, Outputs: Tuple<Output<*, *>>>(
    override val id: String,
    override val method: Method,
    override val uri: URIBuilder<PathVariables, QueryParameters>,
    override val headers: Headers,
    override val input: Input,
    override val outputs: Outputs,
    override val description: String?,
    override val details: String?
) : Endpoint<Method, URIBuilder<PathVariables, QueryParameters>, Headers, Input, Outputs>()
