package dev.akif.tapik.target.openapi

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
class OpenApiOperation(
    val operationId: String,
    tags: List<String>,
    val summary: String?,
    val description: String?,
    parameters: List<OpenApiParameter>,
    val requestBody: OpenApiRequestBody?,
    responses: Map<String, OpenApiResponse>
) {
    val tags: List<String> = tags.snapshotList()

    val parameters: List<OpenApiParameter> = parameters.snapshotList()

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

    override fun toString(): String =
        "OpenApiOperation(operationId=$operationId, tags=$tags, summary=$summary, description=$description, parameters=$parameters, requestBody=$requestBody, responses=$responses)"
}
