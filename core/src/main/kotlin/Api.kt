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

    private val registeredEntries: MutableList<ApiEntry> = mutableListOf()

    /** Ready endpoints flattened recursively in declaration order. */
    val endpoints: List<Endpoint<*, *, *, *, *, Ready>>
        get() =
            registeredEntries.flatMap { entry ->
                when (entry) {
                    is EndpointEntry -> listOf(entry.endpoint)
                    is InclusionEntry -> entry.inclusion.api.endpoints
                }
            }.also(::requireUniqueEndpointIds)

    /** Direct API inclusions in inclusion declaration order. */
    val includedApis: List<ApiInclusion<*>>
        get() = registeredEntries.filterIsInstance<InclusionEntry>().map(InclusionEntry::inclusion)

    /** Starts a delegated inclusion of [api] at the property's declaration position. */
    protected fun <Included : Api> including(api: Included): ApiInclusion<Included> = ApiInclusion(api)

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
        require(endpoints.none { it.id == endpoint.id }) {
            "Endpoint ID '${endpoint.id}' is already registered"
        }
        registeredEntries.add(EndpointEntry(endpoint))
    }

    internal fun register(
        inclusion: ApiInclusion<*>,
        propertyName: String
    ) {
        val includedTree = inclusion.api.apiTree()
        require(includedTree.none { api -> api === this }) {
            "API '${id}' cannot include itself directly or transitively"
        }
        val currentTree = apiTree()
        val repeated = includedTree.firstOrNull { included -> currentTree.any { current -> current === included } }
        require(repeated == null) { "API '${requireNotNull(repeated).id}' is already included in API '$id'" }
        val currentIds = currentTree.mapTo(mutableSetOf(), Api::id)
        val duplicateId = includedTree.firstOrNull { included -> included.id in currentIds }
        require(duplicateId == null) {
            "API ID '${requireNotNull(duplicateId).id}' is already present in API '$id'"
        }
        require(includedApis.none { existing -> existing.propertyName == propertyName }) {
            "API inclusion property '$propertyName' is already registered"
        }
        inclusion.bind(this, propertyName)
        registeredEntries.add(InclusionEntry(inclusion))
    }

    private fun apiTree(): List<Api> =
        buildList {
            add(this@Api)
            includedApis.forEach { inclusion -> addAll(inclusion.api.apiTree()) }
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

private fun requireUniqueEndpointIds(endpoints: List<Endpoint<*, *, *, *, *, Ready>>) {
    val ids = mutableSetOf<String>()
    endpoints.forEach { endpoint ->
        require(ids.add(endpoint.id)) { "Endpoint ID '${endpoint.id}' is already registered" }
    }
}
