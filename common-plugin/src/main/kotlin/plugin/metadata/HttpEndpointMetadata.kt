package dev.akif.tapik.plugin.metadata

import arrow.core.Option

/**
 * Describes a Kotlin type used while rendering generated clients.
 *
 * @property name simple name of the type (without package qualification).
 * @property nullable whether the type is nullable; `null` indicates unknown/nullability.
 * @property arguments generic type arguments that decorate the type.
 */
data class TypeMetadata(
    val name: String,
    val nullable: Boolean? = false,
    val arguments: List<TypeMetadata> = emptyList()
) {
    /**
     * Renders the type as a Kotlin type literal, including nullability and generic arguments.
     *
     * @return Kotlin-compatible type literal.
     */
    override fun toString(): String =
        if (arguments.isEmpty()) {
            "$name${if (nullable == true) "?" else ""}"
        } else {
            arguments.joinToString(
                prefix = "$name<",
                separator = ", ",
                postfix = ">${if (nullable == true) "?" else ""}"
            )
        }
}

/** Marker interface implemented by endpoint parameter metadata types. */
sealed interface ParameterMetadata

/**
 * Metadata describing a Tapik path variable parameter.
 *
 * @property name name of the variable inside the URI template.
 * @property type Kotlin type metadata for the variable.
 */
data class PathVariableMetadata(
    val name: String,
    val type: TypeMetadata
) : ParameterMetadata

/**
 * Metadata describing a Tapik query parameter.
 *
 * @property name query parameter name.
 * @property type Kotlin type metadata for the parameter.
 * @property required whether the parameter must be supplied by callers.
 * @property default optional default value applied when the parameter is omitted.
 */
data class QueryParameterMetadata(
    val name: String,
    val type: TypeMetadata,
    val required: Boolean,
    val default: Option<String?>
) : ParameterMetadata

/**
 * Metadata describing a Tapik header definition.
 *
 * @property name header name.
 * @property type Kotlin type metadata for the header value.
 * @property required whether the header must be present on the wire.
 * @property values optional list of preconfigured header values.
 * @property cardinality whether generated code should expose the header as one value or many.
 */
data class HeaderMetadata(
    val name: String,
    val type: TypeMetadata,
    val required: Boolean = true,
    val values: List<String> = emptyList(),
    val cardinality: HeaderCardinality = HeaderCardinality.Single
)

/**
 * Describes whether a generated header field should expose one value or many.
 */
enum class HeaderCardinality {
    /** The header is modeled as a single scalar value. */
    Single,

    /** The header is modeled as a list because multiple values are expected. */
    Multiple
}

/**
 * Metadata describing an HTTP body definition.
 *
 * @property type Kotlin type metadata for the payload.
 * @property name optional symbolic name associated with the body.
 * @property mediaType optional media type advertised for the payload.
 */
data class BodyMetadata(
    val type: TypeMetadata,
    val name: String? = null,
    val mediaType: String? = null
)

/**
 * Metadata describing a single output alternative including headers and body.
 *
 * @property description human-readable summary of the response branch.
 * @property match machine-readable status matcher for the response branch.
 * @property headers headers returned when this branch is selected.
 * @property body type and serialization metadata for the payload.
 */
data class OutputMetadata(
    val match: OutputMatchMetadata,
    val description: String,
    val headers: List<HeaderMetadata>,
    val body: BodyMetadata
) {
    constructor(
        description: String,
        headers: List<HeaderMetadata>,
        body: BodyMetadata
    ) : this(
        match = OutputMatchMetadata.Described(description),
        description = description,
        headers = headers,
        body = body
    )
}

/**
 * Machine-readable view of the status matcher backing an endpoint output.
 */
sealed interface OutputMatchMetadata {
    /**
     * Matches a single exact HTTP status.
     *
     * @property status exact status.
     */
    data class Exact(
        val status: dev.akif.tapik.Status
    ) : OutputMatchMetadata

    /**
     * Matches any status from a fixed set.
     *
     * @property statuses accepted statuses in declaration order.
     */
    data class AnyOf(
        val statuses: List<dev.akif.tapik.Status>
    ) : OutputMatchMetadata

    /**
     * Matches statuses described by a free-form predicate.
     *
     * @property description human-readable predicate description.
     */
    data class Described(
        val description: String
    ) : OutputMatchMetadata

    /** Matches the fallback unmatched-status branch. */
    data object Unmatched : OutputMatchMetadata
}

/**
 * Aggregated metadata describing a Tapik HTTP endpoint.
 *
 * @property id stable identifier for the endpoint.
 * @property propertyName backing property or function used to expose the endpoint.
 * @property description optional short summary of the endpoint.
 * @property details optional long-form documentation.
 * @property method HTTP method handled by the endpoint.
 * @property path ordered list of path segments and placeholders.
 * @property parameters parameters captured by the endpoint.
 * @property input input definition describing headers and body expected from the caller.
 * @property outputs possible response alternatives together with their headers and bodies.
 * @property packageName package containing the endpoint declaration.
 * @property sourceFile Kotlin file hosting the endpoint declaration.
 */
data class HttpEndpointMetadata(
    val id: String,
    val propertyName: String,
    val description: String?,
    val details: String?,
    val method: String,
    val path: List<String>,
    val parameters: List<ParameterMetadata>,
    val input: InputMetadata,
    val outputs: List<OutputMetadata>,
    val packageName: String,
    val sourceFile: String
)

/**
 * Aggregated metadata describing HTTP input expectations.
 *
 * @property headers header definitions expected from the caller.
 * @property body optional request body definition.
 */
data class InputMetadata(
    val headers: List<HeaderMetadata>,
    val body: BodyMetadata?
)
