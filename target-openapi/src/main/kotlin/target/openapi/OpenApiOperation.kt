package dev.akif.tapik.target.openapi

/**
 * One OpenAPI operation.
 *
 * @property operationId unique identifier derived from the delegated endpoint property.
 * @property summary optional short summary.
 * @property description optional detailed description.
 * @property requestBody optional request body.
 */
class OpenApiOperation(
    /** Unique identifier derived from the delegated endpoint property. */
    val operationId: String,
    tags: List<String>,
    /** Optional short summary. */
    val summary: String?,
    /** Optional detailed description. */
    val description: String?,
    parameters: List<OpenApiParameter>,
    /** Optional request body. */
    val requestBody: OpenApiRequestBody?,
    responses: Map<String, OpenApiResponse>
) {
    /** Unmodifiable snapshot of endpoint tags. */
    val tags: List<String> = tags.snapshotList()

    /** Unmodifiable snapshot of ordered path, query, and request-header parameters. */
    val parameters: List<OpenApiParameter> = parameters.snapshotList()

    /** Unmodifiable snapshot of responses keyed by OpenAPI response code or status class, such as `4XX`. */
    val responses: Map<String, OpenApiResponse> = responses.snapshotMap()

    /** Returns [operationId] for destructuring. */
    operator fun component1(): String = operationId

    /** Returns [tags] for destructuring. */
    operator fun component2(): List<String> = tags

    /** Returns [summary] for destructuring. */
    operator fun component3(): String? = summary

    /** Returns [description] for destructuring. */
    operator fun component4(): String? = description

    /** Returns [parameters] for destructuring. */
    operator fun component5(): List<OpenApiParameter> = parameters

    /** Returns [requestBody] for destructuring. */
    operator fun component6(): OpenApiRequestBody? = requestBody

    /** Returns [responses] for destructuring. */
    operator fun component7(): Map<String, OpenApiResponse> = responses

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        operationId: String = this.operationId,
        tags: List<String> = this.tags,
        summary: String? = this.summary,
        description: String? = this.description,
        parameters: List<OpenApiParameter> = this.parameters,
        requestBody: OpenApiRequestBody? = this.requestBody,
        responses: Map<String, OpenApiResponse> = this.responses
    ): OpenApiOperation = OpenApiOperation(operationId, tags, summary, description, parameters, requestBody, responses)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiOperation &&
                operationId == other.operationId &&
                tags == other.tags &&
                summary == other.summary &&
                description == other.description &&
                parameters == other.parameters &&
                requestBody == other.requestBody &&
                responses == other.responses)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int {
        var result = operationId.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + summary.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + parameters.hashCode()
        result = 31 * result + requestBody.hashCode()
        result = 31 * result + responses.hashCode()
        return result
    }

    /** Returns a string representation of this value. */
    override fun toString(): String =
        "OpenApiOperation(operationId=$operationId, tags=$tags, summary=$summary, description=$description, parameters=$parameters, requestBody=$requestBody, responses=$responses)"
}
