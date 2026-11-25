package dev.akif.tapik

/**
 * Defines endpoints as properties of an API.
 *
 * Each endpoint property name is combined with [apiId] to form its ID. Included APIs contribute their endpoints where
 * their inclusion property is declared, so [endpoints] follows property declaration order recursively.
 *
 * @param apiId optional API identifier used to qualify endpoint property names. The concrete type's simple name is
 * used when omitted.
 * @throws IllegalArgumentException when [apiId] is blank or the concrete type has no simple name.
 */
abstract class Api(
    apiId: String? = null
) {
    /** Resolved API identifier, used to qualify endpoint property names. IDs must be unique within a composition. */
    val apiId: String = apiId ?: requireNotNull(this::class.simpleName) { "API type must have a simple name" }

    init {
        require(this.apiId.isNotBlank()) { "API ID must not be blank" }
    }

    private val registeredEntries: MutableList<ApiEntry> = mutableListOf()

    /**
     * A fresh list of ready endpoints in property declaration order. Endpoints from an included API appear where its
     * inclusion property is declared.
     */
    val endpoints: List<Endpoint<*, *, *, *, *, Ready>>
        get() =
            registeredEntries.flatMap { entry ->
                when (entry) {
                    is EndpointEntry -> listOf(entry.endpoint)
                    is InclusionEntry -> entry.inclusion.api.endpoints
                }
            }.also(::requireUniqueEndpointIds)

    /** A fresh list of APIs included directly by this API, in property declaration order. */
    val includedApis: List<ApiInclusion<*>>
        get() = registeredEntries.filterIsInstance<InclusionEntry>().map(InclusionEntry::inclusion)

    /**
     * Starts including [api] as a property of this API.
     *
     * @param api API value to include.
     * @return an inclusion registered when assigned to an API property.
     */
    protected fun <Included : Api> including(api: Included): ApiInclusion<Included> = ApiInclusion(api)

    /**
     * Starts a draft `GET` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `GET`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> get(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.GET, uri, summary, description, tags)

    /**
     * Starts a draft `HEAD` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `HEAD`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> head(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.HEAD, uri, summary, description, tags)

    /**
     * Starts a draft `POST` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `POST`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> post(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.POST, uri, summary, description, tags)

    /**
     * Starts a draft `PUT` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `PUT`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> put(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.PUT, uri, summary, description, tags)

    /**
     * Starts a draft `PATCH` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `PATCH`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> patch(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.PATCH, uri, summary, description, tags)

    /**
     * Starts a draft `DELETE` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `DELETE`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> delete(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.DELETE, uri, summary, description, tags)

    /**
     * Starts a draft `CONNECT` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `CONNECT`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> connect(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.CONNECT, uri, summary, description, tags)

    /**
     * Starts a draft `OPTIONS` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `OPTIONS`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> options(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.OPTIONS, uri, summary, description, tags)

    /**
     * Starts a draft `TRACE` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `TRACE`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
    protected fun <P : Paths, Q : Queries> trace(
        uri: Uri<P, Q>,
        summary: String? = null,
        description: String? = null,
        tags: Set<String> = noTags
    ) = endpoint(Method.TRACE, uri, summary, description, tags)

    /**
     * Starts a draft `QUERY` endpoint at [uri].
     *
     * @param uri path and query definition for the endpoint.
     * @param summary optional short summary for generated documentation.
     * @param description optional detailed description for generated documentation.
     * @param tags endpoint tags for generated documentation.
     * @return a draft endpoint using `QUERY`.
     * @throws IllegalArgumentException when a supplied summary, description, or tag is blank.
     */
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
            "API '$apiId' cannot include itself directly or transitively"
        }
        val currentTree = apiTree()
        val repeated = includedTree.firstOrNull { included -> currentTree.any { current -> current === included } }
        require(repeated == null) {
            "API '${requireNotNull(repeated).apiId}' is already included in API '$apiId'"
        }
        val currentIds = currentTree.mapTo(mutableSetOf(), Api::apiId)
        val duplicateId = includedTree.firstOrNull { included -> included.apiId in currentIds }
        require(duplicateId == null) {
            "API ID '${requireNotNull(duplicateId).apiId}' is already present in API '$apiId'"
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
