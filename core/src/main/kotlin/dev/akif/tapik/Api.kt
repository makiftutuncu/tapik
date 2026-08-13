package dev.akif.tapik

/**
 * A named, ordered collection of delegated endpoint definitions.
 *
 * @property id API identifier used to qualify endpoint property names.
 * @throws IllegalArgumentException when [id] is blank.
 */
abstract class Api(
    val id: String
) {
    init {
        require(id.isNotBlank()) { "API ID must not be blank" }
    }

    /** Ready endpoints in property declaration order. */
    val endpoints: List<Endpoint<*, *, *, *, *, Ready>>
        field: MutableList<Endpoint<*, *, *, *, *, Ready>> = mutableListOf()

    /** Starts a draft `GET` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> get(uri: Uri<P, Q>) = endpoint(Method.GET, uri)

    /** Starts a draft `HEAD` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> head(uri: Uri<P, Q>) = endpoint(Method.HEAD, uri)

    /** Starts a draft `POST` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> post(uri: Uri<P, Q>) = endpoint(Method.POST, uri)

    /** Starts a draft `PUT` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> put(uri: Uri<P, Q>) = endpoint(Method.PUT, uri)

    /** Starts a draft `PATCH` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> patch(uri: Uri<P, Q>) = endpoint(Method.PATCH, uri)

    /** Starts a draft `DELETE` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> delete(uri: Uri<P, Q>) = endpoint(Method.DELETE, uri)

    /** Starts a draft `CONNECT` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> connect(uri: Uri<P, Q>) = endpoint(Method.CONNECT, uri)

    /** Starts a draft `OPTIONS` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> options(uri: Uri<P, Q>) = endpoint(Method.OPTIONS, uri)

    /** Starts a draft `TRACE` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> trace(uri: Uri<P, Q>) = endpoint(Method.TRACE, uri)

    /** Starts a draft `QUERY` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> query(uri: Uri<P, Q>) = endpoint(Method.QUERY, uri)

    internal fun register(endpoint: Endpoint<*, *, *, *, *, Ready>) {
        require(endpoints.none { it.id == endpoint.id }) {
            "Endpoint ID '${endpoint.id}' is already registered"
        }
        endpoints.add(endpoint)
    }

    private fun <P : Paths, Q : Queries> endpoint(
        method: Method,
        uri: Uri<P, Q>
    ): Endpoint<P, Q, Headers0, NoInput, DefaultOutputs, Draft> =
        Endpoint(
            method = method,
            uri = uri,
            headers = noHeaders,
            input = noInput,
            outputs = DefaultOutputs,
            state = Draft
        )
}
