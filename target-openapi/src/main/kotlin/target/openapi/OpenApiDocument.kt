package dev.akif.tapik.target.openapi

import dev.akif.tapik.Method

/**
 * A complete OpenAPI document.
 *
 * @property specificationVersion OpenAPI specification version used by the document.
 * @property info identifying information about the API document.
 * @property paths operations grouped by URI path template in declaration order.
 * @property components reusable components referenced by operations.
 */
data class OpenApiDocument(
    val specificationVersion: String = "3.2.0",
    val info: OpenApiInfo,
    val paths: Map<String, OpenApiPathItem>,
    val components: OpenApiComponents = OpenApiComponents()
)

/**
 * Required identifying information for an OpenAPI document.
 *
 * @property title human-readable API title.
 * @property version version of the API document, independent of [OpenApiDocument.specificationVersion].
 * @property summary optional short summary.
 * @property description optional detailed description.
 * @throws IllegalArgumentException when [title] or [version] is blank.
 */
data class OpenApiInfo(
    val title: String,
    val version: String,
    val summary: String? = null,
    val description: String? = null
) {
    init {
        require(title.isNotBlank()) { "OpenAPI title must not be blank" }
        require(version.isNotBlank()) { "OpenAPI document version must not be blank" }
    }
}

/**
 * Operations sharing one URI path template.
 *
 * @property operations operations keyed by HTTP method in declaration order.
 */
data class OpenApiPathItem(
    val operations: Map<Method, OpenApiOperation>
)

/**
 * One OpenAPI operation.
 *
 * @property operationId unique identifier derived from the delegated endpoint property.
 * @property tags endpoint tags.
 * @property summary optional short summary.
 * @property description optional detailed description.
 * @property parameters ordered path, query, and request-header parameters.
 * @property requestBody optional request body.
 * @property responses responses keyed by status matcher representation.
 */
data class OpenApiOperation(
    val operationId: String,
    val tags: List<String>,
    val summary: String?,
    val description: String?,
    val parameters: List<OpenApiParameter>,
    val requestBody: OpenApiRequestBody?,
    val responses: Map<String, OpenApiResponse>
)

/**
 * Supported OpenAPI parameter locations.
 *
 * @property value wire value used by OpenAPI.
 */
enum class OpenApiParameterLocation(
    val value: String
) {
    /** URI path-template parameter. */
    PATH("path"),

    /** URI query parameter. */
    QUERY("query"),

    /** HTTP request-header parameter. */
    HEADER("header")
}

/**
 * A path, query, or request-header parameter.
 *
 * @property name parameter name.
 * @property location parameter location.
 * @property required whether callers must provide the parameter.
 * @property deprecated whether consumers should avoid the parameter.
 * @property schema parameter value schema.
 * @property style optional OpenAPI serialization style.
 * @property explode optional OpenAPI serialization expansion behavior.
 */
data class OpenApiParameter(
    val name: String,
    val location: OpenApiParameterLocation,
    val required: Boolean,
    val deprecated: Boolean,
    val schema: OpenApiSchema,
    val style: String? = null,
    val explode: Boolean? = null
)

/**
 * A request body and its media representations.
 *
 * @property required whether callers must provide a body.
 * @property content representations keyed by media type in declaration order.
 */
data class OpenApiRequestBody(
    val required: Boolean,
    val content: Map<String, OpenApiMediaType>
)

/**
 * A response with headers and media representations.
 *
 * @property description required response description.
 * @property headers response headers keyed by name in declaration order.
 * @property content representations keyed by media type in declaration order.
 */
data class OpenApiResponse(
    val description: String,
    val headers: Map<String, OpenApiHeader>,
    val content: Map<String, OpenApiMediaType>
)

/**
 * A response header.
 *
 * @property required whether the response always contains the header.
 * @property deprecated whether consumers should avoid the header.
 * @property schema header value schema.
 */
data class OpenApiHeader(
    val required: Boolean,
    val deprecated: Boolean,
    val schema: OpenApiSchema
)

/**
 * The schema applying to a complete media representation.
 *
 * @property schema schema of the complete representation.
 */
data class OpenApiMediaType(
    val schema: OpenApiSchema
)

/**
 * Reusable OpenAPI components.
 *
 * @property schemas schemas keyed by component name in first-use order.
 */
data class OpenApiComponents(
    val schemas: Map<String, OpenApiSchema> = emptyMap()
)
