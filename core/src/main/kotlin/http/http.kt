package dev.akif.tapik.http

import dev.akif.tapik.Endpoint
import dev.akif.tapik.codec.StringCodec
import kotlin.properties.ReadOnlyProperty

fun String.toURIWithParameters(): URIWithParameters<Parameters0> =
    listOf(this) to Parameters0()

inline fun <reified P : Any> path(
    name: String,
    codec: StringCodec<P>
): PathVariable<P> = PathVariable(name, codec)

val path: PathVariable.Companion = PathVariable.Companion

fun <Q : Any> query(
    name: String,
    codec: StringCodec<Q>,
    required: Boolean,
    default: Q?
): QueryParameter<Q> = QueryParameter(name, codec, required, default)

val query: QueryParameter.Companion = QueryParameter.Companion

inline fun <reified H: Any> header(
    name: String,
    codec: StringCodec<H>
): Header<H> = HeaderInput(name, codec)

val header: Header.Companion = Header.Companion

fun anyStatus(first: Status, vararg rest: Status): StatusMatcher =
    StatusMatcher.AnyOf(setOf(first, *rest))

fun matchStatus(description: String, predicate: (Status) -> Boolean): StatusMatcher =
    StatusMatcher.Predicate(description, predicate)

val unmatchedStatus: StatusMatcher =
    StatusMatcher.Unmatched

fun <T, P: Parameters, IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies> http(description: String? = null, details: String? = null, builder: HttpEndpointWithoutMethod.() -> HttpEndpoint<P, IH, IB, OH, OB>): ReadOnlyProperty<T, HttpEndpoint<P, IH, IB, OH, OB>> =
    ReadOnlyProperty { _, property ->
        HttpEndpointWithoutMethod(id = property.name, description = description, details = details).builder()
    }

data class HttpEndpointWithoutMethod(
    override val id: String,
    override val description: String?,
    override val details: String?
) : Endpoint<Parameters0, Headers0, EmptyBody, Headers0, OutputBodies0>() {
    override val parameters: Parameters0 = Parameters0()
    override val inputHeaders: Headers0 = Headers0()
    override val inputBody: EmptyBody = EmptyBody
    override val outputHeaders: Headers0 = Headers0()
    override val outputBodies: OutputBodies0 = OutputBodies0()

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
) : Endpoint<Parameters0, Headers0, EmptyBody, Headers0, OutputBodies0>() {
    override val parameters: Parameters0 = Parameters0()
    override val inputHeaders: Headers0 = Headers0()
    override val inputBody: EmptyBody = EmptyBody
    override val outputHeaders: Headers0 = Headers0()
    override val outputBodies: OutputBodies0 = OutputBodies0()

    fun uri(segment: String): HttpEndpoint<Parameters0, Headers0, EmptyBody, Headers0, OutputBodies0> {
        return HttpEndpoint(
            id = this.id,
            description = this.description,
            details = this.details,
            method = this.method,
            uri = listOf(segment),
            parameters = this.parameters,
            inputHeaders = this.inputHeaders,
            inputBody = EmptyBody,
            outputHeaders = this.outputHeaders,
            outputBodies = OutputBodies0()
        )
    }

    fun <P : Any> uri(parameter: Parameter<P>): HttpEndpoint<Parameters1<P>, Headers0, EmptyBody, Headers0, OutputBodies0> {
        return HttpEndpoint(
            id = this.id,
            description = this.description,
            details = this.details,
            method = this.method,
            uri = emptyList(),
            parameters = Parameters1(parameter),
            inputHeaders = this.inputHeaders,
            inputBody = EmptyBody,
            outputHeaders = this.outputHeaders,
            outputBodies = OutputBodies0()
        )
    }

    fun <P : Parameters> uri(uriWithParameters: URIWithParameters<P>): HttpEndpoint<P, Headers0, EmptyBody, Headers0, OutputBodies0> {
        val (uri, parameters) = uriWithParameters
        return HttpEndpoint(
            id = this.id,
            description = this.description,
            details = this.details,
            method = this.method,
            uri = uri,
            parameters = parameters,
            inputHeaders = this.inputHeaders,
            inputBody = EmptyBody,
            outputHeaders = this.outputHeaders,
            outputBodies = OutputBodies0()
        )
    }
}

data class HttpEndpoint<out P: Parameters, out IH: Headers, out IB: Body<*>, out OH: Headers, out OB: OutputBodies>(
    public override val id: String,
    public override val description: String?,
    public override val details: String?,
    val method: Method,
    val uri: List<String>,
    public override val parameters: P,
    public override val inputHeaders: IH,
    public override val inputBody: IB,
    public override val outputHeaders: OH,
    public override val outputBodies: OB
) : Endpoint<P, IH, IB, OH, OB>()
