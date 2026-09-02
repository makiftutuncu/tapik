package dev.akif.tapik.target.openapi

import dev.akif.tapik.*

/** Programmatic OpenAPI 3.2 interpreter for compiled tapik API values. */
object OpenApi {
    /**
     * Interprets [api] using explicit document [info].
     *
     * @param api compiled tapik API value to interpret.
     * @param info identifying information for the generated document.
     * @param componentNaming policy for converting schema names to component names.
     * @return an OpenAPI document preserving endpoint declaration order.
     * @throws OpenApiGenerationException when the API cannot be represented faithfully.
     */
    fun from(
        api: Api,
        info: OpenApiInfo,
        componentNaming: OpenApiComponentNaming = OpenApiComponentNaming.Simple
    ): OpenApiDocument = Interpreter(info, componentNaming).interpret(api)

    /**
     * Interprets [api], using its ID as the title and [version] as the document version.
     *
     * @param api compiled tapik API value to interpret.
     * @param version version of the API document.
     * @param title human-readable API title, defaulting to [Api.id].
     * @param componentNaming policy for converting schema names to component names.
     * @return an OpenAPI document preserving endpoint declaration order.
     * @throws IllegalArgumentException when [title] or [version] is blank.
     * @throws OpenApiGenerationException when the API cannot be represented faithfully.
     */
    fun from(
        api: Api,
        version: String,
        title: String = api.id,
        componentNaming: OpenApiComponentNaming = OpenApiComponentNaming.Simple
    ): OpenApiDocument =
        from(
            api = api,
            info = OpenApiInfo(title = title, version = version),
            componentNaming = componentNaming
        )
}

private class Interpreter(
    private val info: OpenApiInfo,
    componentNaming: OpenApiComponentNaming
) {
    private val schemas = SchemaRegistry(componentNaming)

    fun interpret(api: Api): OpenApiDocument {
        val paths = linkedMapOf<String, MutableMap<Method, OpenApiOperation>>()
        val templatedPaths = mutableMapOf<String, String>()

        api.endpoints.forEach { endpoint ->
            endpoint.uri.paths.values.filterIsInstance<RemainingPath>().firstOrNull()?.let { remaining ->
                throw OpenApiGenerationException(
                    "${endpoint.id} uses remaining path '${remaining.name}', which OpenAPI cannot represent faithfully"
                )
            }
            val path = endpoint.pathTemplate()
            val shape = endpoint.pathShape()
            val existingPath = templatedPaths.putIfAbsent(shape, path)
            if (existingPath != null && existingPath != path) {
                throw OpenApiGenerationException(
                    "OpenAPI paths '$existingPath' and '$path' have the same templated hierarchy"
                )
            }
            val operations = paths.getOrPut(path, ::linkedMapOf)
            if (endpoint.method in operations) {
                throw OpenApiGenerationException(
                    "OpenAPI path '$path' already contains a ${endpoint.method} operation"
                )
            }
            operations[endpoint.method] = operation(endpoint)
        }

        schemas.requireResolvedReferences()

        return OpenApiDocument(
            info = info,
            paths = paths.mapValues { OpenApiPathItem(it.value.toMap()) },
            components = OpenApiComponents(schemas.components.toMap())
        )
    }

    private fun operation(endpoint: Endpoint<*, *, *, *, *, Ready>): OpenApiOperation =
        OpenApiOperation(
            operationId = endpoint.id,
            tags = endpoint.tags.sorted(),
            summary = endpoint.documentation.summary,
            description = endpoint.documentation.description,
            parameters = parameters(endpoint),
            requestBody = requestBody(endpoint.input),
            responses = responses(endpoint.id, endpoint.outputs)
        )

    private fun parameters(endpoint: Endpoint<*, *, *, *, *, Ready>): List<OpenApiParameter> =
        buildList {
            endpoint.uri.paths.values.forEach { variable ->
                add(
                    OpenApiParameter(
                        name = variable.name,
                        location = OpenApiParameterLocation.PATH,
                        description = variable.documentation.description,
                        required = true,
                        deprecated = variable.documentation.deprecated,
                        schema = schemas.schema(variable.format.schema)
                    )
                )
            }

            endpoint.uri.queries.values.forEach { query -> add(query.parameter()) }
            endpoint.headers.values.forEach { header -> add(header.parameter()) }
        }

    private fun Query.parameter(): OpenApiParameter =
        when (this) {
            is QueryParameter<*, *> -> parameter()
            is RepeatedQueryParameter<*, *> -> parameter()
        }

    private fun <Value : Any> QueryParameter<Value, *>.parameter(): OpenApiParameter =
        OpenApiParameter(
            name = name,
            location = OpenApiParameterLocation.QUERY,
            description = documentation.description,
            required = presence.required,
            deprecated = documentation.deprecated,
            schema = schemas.schema(format.schema).withPresence(presence, format),
            style = null,
            explode = null
        )

    private fun <Value : Any> RepeatedQueryParameter<Value, *>.parameter(): OpenApiParameter =
        OpenApiParameter(
            name = name,
            location = OpenApiParameterLocation.QUERY,
            description = documentation.description,
            required = presence.required,
            deprecated = documentation.deprecated,
            schema = schemas.schema(format.schema).withPresence(presence, format),
            style = "form",
            explode = true
        )

    private fun <Value : Any> Header<Value, *>.parameter(): OpenApiParameter =
        OpenApiParameter(
            name = name,
            location = OpenApiParameterLocation.HEADER,
            description = documentation.description,
            required = presence.required,
            deprecated = documentation.deprecated,
            schema = schemas.schema(format.schema).withPresence(presence, format),
            style = null,
            explode = null
        )

    private fun requestBody(input: Input): OpenApiRequestBody? =
        when (input) {
            NoInput -> null
            is BodyInput<*> -> {
                val content = input.bodies.content()
                if (content.isEmpty()) {
                    throw OpenApiGenerationException(
                        "An OpenAPI request body must contain at least one media type"
                    )
                }
                OpenApiRequestBody(
                    description = input.documentation.description,
                    required = input.bodies.values.none { it === NoBody },
                    content = content
                )
            }
        }

    private fun responses(
        endpointId: String,
        outputs: Tuple<OutputAlternative>
    ): Map<String, OpenApiResponse> =
        buildMap {
            outputs.values.forEach { alternative ->
                val output = alternative as? Output<*, *, *>
                    ?: throw OpenApiGenerationException(
                        "Unsupported endpoint output '${alternative::class.qualifiedName}'"
                    )
                output.matcher.openApiStatuses(endpointId).forEach { status ->
                    put(
                        status.key,
                        OpenApiResponse(
                            description = output.documentation.description ?: status.description,
                            headers = output.headers.values.associate { it.name to it.responseHeader() },
                            content = output.bodies.content()
                        )
                    )
                }
            }
        }

    private fun <Value : Any> Header<Value, *>.responseHeader(): OpenApiHeader =
        OpenApiHeader(
            description = documentation.description,
            required = presence.required,
            deprecated = documentation.deprecated,
            schema = schemas.schema(format.schema).withPresence(presence, format)
        )

    private fun Bodies.content(): Map<String, OpenApiMediaType> =
        values
            .filterIsInstance<Body<*>>()
            .associate { body ->
                body.mediaType.value to OpenApiMediaType(schemas.schema(body.format.schema))
            }
}

private data class OpenApiStatus(
    val key: String,
    val description: String
)

private fun StatusMatcher.openApiStatuses(endpointId: String): List<OpenApiStatus> =
    when (this) {
        is ExactStatus -> listOf(status.openApiStatus())
        is StatusSet -> statuses.map(Status::openApiStatus)
        is StatusRange -> {
            if (range.first % 100 == 0 && range.last == range.first + 99) {
                listOf(OpenApiStatus("${range.first / 100}XX", "HTTP ${range.first / 100}XX responses"))
            } else {
                range.map { code -> Status(code).openApiStatus() }
            }
        }
        is CustomStatus ->
            throw OpenApiGenerationException(
                "$endpointId has an unsupported OpenAPI custom status matcher '$description'"
            )
    }

private fun Status.openApiStatus(): OpenApiStatus = OpenApiStatus(code.toString(), description)

private val Presence<*>.required: Boolean
    get() = this === Required || this is Fixed<*>

private fun Endpoint<*, *, *, *, *, Ready>.pathTemplate(): String {
    if (uri.segments.isEmpty()) return "/"
    return uri.segments.joinToString(separator = "/", prefix = "/") { segment ->
        when (segment) {
            is PathSegment.Literal -> segment.value
            is PathVariable<*> -> "{${segment.name}}"
            is RemainingPath -> "{*${segment.name}}"
        }
    }
}

private fun Endpoint<*, *, *, *, *, Ready>.pathShape(): String {
    if (uri.segments.isEmpty()) return "/"
    return uri.segments.joinToString(separator = "/", prefix = "/") { segment ->
        when (segment) {
            is PathSegment.Literal -> segment.value
            is PathVariable<*> -> "{}"
            is RemainingPath -> "{*}"
        }
    }
}

private val Status.description: String
    get() =
        when (code) {
            200 -> "OK"
            201 -> "Created"
            204 -> "No Content"
            400 -> "Bad Request"
            404 -> "Not Found"
            409 -> "Conflict"
            500 -> "Internal Server Error"
            else -> "HTTP $code response"
        }
