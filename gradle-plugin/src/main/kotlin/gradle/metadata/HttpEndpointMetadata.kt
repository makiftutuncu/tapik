package dev.akif.tapik.gradle.metadata

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
            arguments.joinToString(prefix = "$name<", separator = ", ", postfix = ">${if (nullable == true) "?" else ""}")
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
    val required: Boolean = false,
    val default: String? = null
) : ParameterMetadata

/**
 * Metadata describing a Tapik header definition.
 *
 * @property name header name.
 * @property type Kotlin type metadata for the header value.
 * @property required whether the header must be present on the wire.
 * @property values optional list of preconfigured header values.
 */
data class HeaderMetadata(
    val name: String,
    val type: TypeMetadata,
    val required: Boolean = true,
    val values: List<String> = emptyList()
)

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
 * Metadata describing a single output body alternative.
 *
 * @property description human-readable summary of the response branch.
 * @property body type and serialization metadata for the payload.
 */
data class OutputBodyMetadata(
    val description: String,
    val body: BodyMetadata
)

/**
 * Aggregated metadata describing a Tapik HTTP endpoint.
 *
 * @property id stable identifier for the endpoint.
 * @property propertyName backing property or function used to expose the endpoint.
 * @property description optional short summary of the endpoint.
 * @property details optional long-form documentation.
 * @property method HTTP method handled by the endpoint.
 * @property uri ordered list of uri segments and placeholders.
 * @property parameters parameters captured by the endpoint.
 * @property inputHeaders headers expected from the caller.
 * @property inputBody optional body consumed from the caller.
 * @property outputHeaders headers returned to the caller.
 * @property outputBodies possible response bodies together with their descriptions.
 * @property packageName package containing the endpoint declaration.
 * @property sourceFile Kotlin file hosting the endpoint declaration.
 * @property imports fully-qualified imports required when generating clients.
 * @property rawType string representation of the original endpoint type.
 */
data class HttpEndpointMetadata(
    val id: String,
    val propertyName: String,
    val description: String?,
    val details: String?,
    val method: String,
    val uri: List<String>,
    val parameters: List<ParameterMetadata>,
    val inputHeaders: List<HeaderMetadata>,
    val inputBody: BodyMetadata?,
    val outputHeaders: List<HeaderMetadata>,
    val outputBodies: List<OutputBodyMetadata>,
    val packageName: String,
    val sourceFile: String,
    val imports: List<String>,
    val rawType: String
)
