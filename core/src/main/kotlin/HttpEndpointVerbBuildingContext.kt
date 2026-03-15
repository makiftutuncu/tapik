package dev.akif.tapik

/**
 * First stage of the endpoint DSL, exposed before a method has been chosen.
 *
 * Only verb-selection helpers live here. That restriction exists so every endpoint chooses its
 * HTTP method and URI template before request and response details are added.
 */
class HttpEndpointVerbBuildingContext internal constructor(
    private val id: String,
    private val description: String?,
    private val details: String?
) {
    /** Starts a GET endpoint from a precomposed URI template. */
    fun <P1 : Parameters> get(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.GET, uriWithParameters)

    /** Starts a GET endpoint from a slash-separated literal path. */
    fun get(path: String) = method(Method.GET, stringToUri(path))

    /** Starts a GET endpoint whose URI is built from a single parameter. */
    fun <P : Any> get(parameter: Parameter<P>) = method(Method.GET, parameterToUri(parameter))

    /** Starts a HEAD endpoint from a precomposed URI template. */
    fun <P1 : Parameters> head(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.HEAD, uriWithParameters)

    /** Starts a HEAD endpoint from a slash-separated literal path. */
    fun head(path: String) = method(Method.HEAD, stringToUri(path))

    /** Starts a HEAD endpoint whose URI is built from a single parameter. */
    fun <P : Any> head(parameter: Parameter<P>) = method(Method.HEAD, parameterToUri(parameter))

    /** Starts a POST endpoint from a precomposed URI template. */
    fun <P1 : Parameters> post(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.POST, uriWithParameters)

    /** Starts a POST endpoint from a slash-separated literal path. */
    fun post(path: String) = method(Method.POST, stringToUri(path))

    /** Starts a POST endpoint whose URI is built from a single parameter. */
    fun <P : Any> post(parameter: Parameter<P>) = method(Method.POST, parameterToUri(parameter))

    /** Starts a PUT endpoint from a precomposed URI template. */
    fun <P1 : Parameters> put(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.PUT, uriWithParameters)

    /** Starts a PUT endpoint from a slash-separated literal path. */
    fun put(path: String) = method(Method.PUT, stringToUri(path))

    /** Starts a PUT endpoint whose URI is built from a single parameter. */
    fun <P : Any> put(parameter: Parameter<P>) = method(Method.PUT, parameterToUri(parameter))

    /** Starts a PATCH endpoint from a precomposed URI template. */
    fun <P1 : Parameters> patch(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.PATCH, uriWithParameters)

    /** Starts a PATCH endpoint from a slash-separated literal path. */
    fun patch(path: String) = method(Method.PATCH, stringToUri(path))

    /** Starts a PATCH endpoint whose URI is built from a single parameter. */
    fun <P : Any> patch(parameter: Parameter<P>) = method(Method.PATCH, parameterToUri(parameter))

    /** Starts a DELETE endpoint from a precomposed URI template. */
    fun <P1 : Parameters> delete(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.DELETE, uriWithParameters)

    /** Starts a DELETE endpoint from a slash-separated literal path. */
    fun delete(path: String) = method(Method.DELETE, stringToUri(path))

    /** Starts a DELETE endpoint whose URI is built from a single parameter. */
    fun <P : Any> delete(parameter: Parameter<P>) = method(Method.DELETE, parameterToUri(parameter))

    /** Starts an OPTIONS endpoint from a precomposed URI template. */
    fun <P1 : Parameters> options(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.OPTIONS, uriWithParameters)

    /** Starts an OPTIONS endpoint from a slash-separated literal path. */
    fun options(path: String) = method(Method.OPTIONS, stringToUri(path))

    /** Starts an OPTIONS endpoint whose URI is built from a single parameter. */
    fun <P : Any> options(parameter: Parameter<P>) = method(Method.OPTIONS, parameterToUri(parameter))

    /** Starts a TRACE endpoint from a precomposed URI template. */
    fun <P1 : Parameters> trace(
        uriWithParameters: URIWithParameters<P1>
    ): HttpEndpointBuildingContext<P1, Input<Headers0, EmptyBody>, Outputs0> = method(Method.TRACE, uriWithParameters)

    /** Starts a TRACE endpoint from a slash-separated literal path. */
    fun trace(path: String) = method(Method.TRACE, stringToUri(path))

    /** Starts a TRACE endpoint whose URI is built from a single parameter. */
    fun <P : Any> trace(parameter: Parameter<P>) = method(Method.TRACE, parameterToUri(parameter))

    /**
     * Seeds the next builder stage with the chosen HTTP [method] and URI template.
     *
     * The resulting context always starts with an empty request contract and no outputs so later DSL
     * steps can refine those parts explicitly.
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
