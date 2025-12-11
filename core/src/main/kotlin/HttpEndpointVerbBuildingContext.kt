package dev.akif.tapik

/**
 * Interim building context exposed inside `endpoint { }` blocks before an HTTP verb is selected.
 *
 * The DSL implementation only exposes HTTP verb helpers here so callers cannot mutate other
 * metadata until they choose the method. Each helper returns a fully configured
 * [HttpEndpointBuildingContext] that the rest of the builder can keep mutating.
 */
class HttpEndpointVerbBuildingContext internal constructor(
    private val id: String,
    private val description: String?,
    private val details: String?
) {
    /**
     * Creates a GET endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> get(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.GET, uriWithParameters)

    /**
     * Creates a GET endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun get(path: String) = method(Method.GET, stringToUri(path))

    /**
     * Creates a GET endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> get(parameter: Parameter<P>) = method(Method.GET, parameterToUri(parameter))

    /**
     * Creates a HEAD endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> head(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.HEAD, uriWithParameters)

    /**
     * Creates a HEAD endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun head(path: String) = method(Method.HEAD, stringToUri(path))

    /**
     * Creates a HEAD endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> head(parameter: Parameter<P>) = method(Method.HEAD, parameterToUri(parameter))

    /**
     * Creates a POST endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> post(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.POST, uriWithParameters)

    /**
     * Creates a POST endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun post(path: String) = method(Method.POST, stringToUri(path))

    /**
     * Creates a POST endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> post(parameter: Parameter<P>) = method(Method.POST, parameterToUri(parameter))

    /**
     * Creates a PUT endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> put(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.PUT, uriWithParameters)

    /**
     * Creates a PUT endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun put(path: String) = method(Method.PUT, stringToUri(path))

    /**
     * Creates a PUT endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> put(parameter: Parameter<P>) = method(Method.PUT, parameterToUri(parameter))

    /**
     * Creates a PATCH endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> patch(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.PATCH, uriWithParameters)

    /**
     * Creates a PATCH endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun patch(path: String) = method(Method.PATCH, stringToUri(path))

    /**
     * Creates a PATCH endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> patch(parameter: Parameter<P>) = method(Method.PATCH, parameterToUri(parameter))

    /**
     * Creates a DELETE endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> delete(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.DELETE, uriWithParameters)

    /**
     * Creates a DELETE endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun delete(path: String) = method(Method.DELETE, stringToUri(path))

    /**
     * Creates a DELETE endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> delete(parameter: Parameter<P>) = method(Method.DELETE, parameterToUri(parameter))

    /**
     * Creates an OPTIONS endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> options(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.OPTIONS, uriWithParameters)

    /**
     * Creates an OPTIONS endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun options(path: String) = method(Method.OPTIONS, stringToUri(path))

    /**
     * Creates an OPTIONS endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> options(parameter: Parameter<P>) = method(Method.OPTIONS, parameterToUri(parameter))

    /**
     * Creates a TRACE endpoint for the provided [uriWithParameters].
     *
     * @param uriWithParameters URI template matched by the endpoint.
     */
    fun <P1 : Parameters> trace(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.TRACE, uriWithParameters)

    /**
     * Creates a TRACE endpoint by supplying a literal path.
     *
     * @param path slash-separated segments describing the URI template.
     */
    fun trace(path: String) = method(Method.TRACE, stringToUri(path))

    /**
     * Creates a TRACE endpoint that uses a single parameter placeholder (path or query).
     *
     * @param parameter parameter whose name and codec derive the template segment.
     */
    fun <P : Any> trace(parameter: Parameter<P>) = method(Method.TRACE, parameterToUri(parameter))

    /**
     * Configures the HTTP verb and URI for the endpoint being declared.
     *
     * @param P1 tuple capturing referenced path and query parameters.
     * @param method HTTP verb associated with the endpoint.
     * @param uriWithParameters URI template matched by the endpoint.
     * @return fresh [HttpEndpointBuildingContext] pre-populated with the chosen verb and URI.
     */
    fun <P1 : Parameters> method(
        method: Method,
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> =
        HttpEndpointBuildingContext(
            id = id,
            description = description,
            details = details,
            method = method,
            path = uriWithParameters.first,
            parameters = uriWithParameters.second,
            input = Input(emptyHeaders(), EmptyBody),
            outputs = emptyOutputs()
        )

    private fun <P : Any> parameterToUri(parameter: Parameter<P>): URIWithParameters1<P> =
        when (parameter) {
            is PathVariable<P> ->
                listOf("{${parameter.name}}") to parametersOf(parameter)

            is QueryParameter<P> ->
                emptyList<String>() to parametersOf(parameter)
        }

    private fun stringToUri(path: String): URIWithParameters0 {
        val trimmed = path.trim('/')
        val segments =
            trimmed
                .takeIf { it.isNotBlank() }
                ?.split('/')
                ?.filter { it.isNotBlank() }
                ?: emptyList()

        return segments to emptyParameters()
    }
}
