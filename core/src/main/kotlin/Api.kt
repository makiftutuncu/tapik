package dev.akif.tapik

/**
 * A named, ordered collection of delegated endpoint definitions.
 *
 * @param id optional API identifier used to qualify endpoint property names. The concrete type's simple name is used
 * when omitted.
 * @property id resolved API identifier.
 * @throws IllegalArgumentException when [id] is blank or the concrete type has no simple name.
 */
abstract class Api(
    id: String? = null
) {
    val id: String = id ?: requireNotNull(this::class.simpleName) { "API type must have a simple name" }

    init {
        require(this.id.isNotBlank()) { "API ID must not be blank" }
    }

    private val registeredEndpoints: MutableList<Endpoint<*, *, *, *, *, Ready>> = mutableListOf()

    /** Ready endpoints in property declaration order. */
    val endpoints: List<Endpoint<*, *, *, *, *, Ready>>
        get() = registeredEndpoints.toList()

    /** Starts a draft `GET` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> get(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.GET, uri, summary, description, tags)

    /** Starts a draft `HEAD` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> head(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.HEAD, uri, summary, description, tags)

    /** Starts a draft `POST` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> post(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.POST, uri, summary, description, tags)

    /** Starts a draft `PUT` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> put(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.PUT, uri, summary, description, tags)

    /** Starts a draft `PATCH` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> patch(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.PATCH, uri, summary, description, tags)

    /** Starts a draft `DELETE` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> delete(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.DELETE, uri, summary, description, tags)

    /** Starts a draft `CONNECT` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> connect(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.CONNECT, uri, summary, description, tags)

    /** Starts a draft `OPTIONS` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> options(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.OPTIONS, uri, summary, description, tags)

    /** Starts a draft `TRACE` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> trace(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.TRACE, uri, summary, description, tags)

    /** Starts a draft `QUERY` endpoint for [uri]. */
    protected fun <P : Paths, Q : Queries> query(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.QUERY, uri, summary, description, tags)

    internal fun register(endpoint: Endpoint<*, *, *, *, *, Ready>) {
        require(registeredEndpoints.none { it.id == endpoint.id }) {
            "Endpoint ID '${endpoint.id}' is already registered"
        }
        registeredEndpoints.add(endpoint)
    }

    private fun <P : Paths, Q : Queries> endpoint(
        method: Method,
        uri: Uri<P, Q>,
        summary: String?,
        description: String?,
        tags: Set<String>
    ): Endpoint<P, Q, Headers0, NoInput, DefaultOutput, Draft> =
        Endpoint(
            method = method,
            uri = uri,
            headers = noHeaders,
            input = noInput,
            outputs = DefaultOutput,
            documentation = EndpointDocumentation(summary, description),
            tags = validatedTags(tags),
            state = Draft
        )
}
