package dev.akif.tapik.http

import dev.akif.tapik.Endpoint
import dev.akif.tapik.types.*
import kotlin.properties.ReadOnlyProperty

val path: PathVariable.Companion = PathVariable.Companion

val query: QueryParameter.Companion = QueryParameter.Companion

val header: Header.Companion = Header.Companion

fun anyStatus(first: Status, vararg rest: Status): StatusMatcher =
    StatusMatcher.AnyOf(setOf(first, *rest))

fun matchStatus(description: String, predicate: (Status) -> Boolean): StatusMatcher =
    StatusMatcher.Predicate(description, predicate)

val unmatchedStatus: StatusMatcher =
    StatusMatcher.Unmatched

fun <T, P : Parameters, I : Body<*>, O : Tuple<Output<*, *>>> http(description: String? = null, details: String? = null, builder: HttpEndpointWithoutMethod.() -> HttpEndpoint<P, I, O>): ReadOnlyProperty<T, HttpEndpoint<P, I, O>> =
    ReadOnlyProperty { _, property ->
        HttpEndpointWithoutMethod(id = property.name, description = description, details = details).builder()
    }

data class HttpEndpointWithoutMethod(
    override val id: String,
    override val description: String?,
    override val details: String?
) : Endpoint<Parameters0, EmptyBody, Outputs0>() {
    override val parameters: Parameters0 = Parameters0()
    override val input: EmptyBody = EmptyBody
    override val outputs: Outputs0 = Outputs0()

    val get: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.GET)
    val head: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.HEAD)
    val post: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.POST)
    val put: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.PUT)
    val patch: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.PATCH)
    val delete: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.DELETE)
    val options: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.OPTIONS)
    val trace: HttpEndpointWithoutURI = HttpEndpointWithoutURI(id, description, details, Method.TRACE)
}

data class HttpEndpointWithoutURI(
    override val id: String,
    override val description: String?,
    override val details: String?,
    private val method: Method
) : Endpoint<Parameters0, EmptyBody, Outputs0>() {
    override val parameters: Parameters0 = Parameters0()
    override val input: EmptyBody = EmptyBody
    override val outputs: Outputs0 = Outputs0()

    fun uri(segment: String): HttpEndpoint<Parameters0, EmptyBody, Outputs0> {
        return HttpEndpoint(
            id = this.id,
            description = this.description,
            details = this.details,
            method = this.method,
            uri = listOf(segment),
            parameters = this.parameters,
            input = EmptyBody,
            outputs = Outputs0()
        )
    }

    fun <P : Any> uri(parameter: Parameter<P>): HttpEndpoint<Parameters1<P>, EmptyBody, Outputs0> {
        return HttpEndpoint(
            id = this.id,
            description = this.description,
            details = this.details,
            method = this.method,
            uri = emptyList(),
            parameters = Parameters1(parameter),
            input = EmptyBody,
            outputs = Outputs0()
        )
    }

    fun <P : Parameters> uri(uriWithParameters: URIWithParameters<P>): HttpEndpoint<P, EmptyBody, Outputs0> {
        val (uri, parameters) = uriWithParameters
        return HttpEndpoint(
            id = this.id,
            description = this.description,
            details = this.details,
            method = this.method,
            uri = uri,
            parameters = parameters,
            input = EmptyBody,
            outputs = Outputs0()
        )
    }
}

data class HttpEndpoint<out P : Parameters, out I : Body<*>, out O : Tuple<Output<*, *>>>(
    public override val id: String,
    public override val description: String?,
    public override val details: String?,
    val method: Method,
    val uri: List<String>,
    public override val parameters: P,
    public override val input: I,
    public override val outputs: O
) : Endpoint<P, I, O>()
