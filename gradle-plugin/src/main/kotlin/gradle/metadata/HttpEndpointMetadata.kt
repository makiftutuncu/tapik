package dev.akif.tapik.gradle.metadata

data class TypeMetadata(
    val name: String,
    val nullable: Boolean? = false,
    val arguments: List<TypeMetadata> = emptyList()
) {
    override fun toString(): String =
        if (arguments.isEmpty()) {
            "$name${if (nullable == true) "?" else ""}"
        } else {
            arguments.joinToString(prefix = "$name<", separator = ", ", postfix = ">${if (nullable == true) "?" else ""}")
        }
}

sealed interface ParameterMetadata

data class PathVariableMetadata(
    val name: String,
    val type: TypeMetadata
) : ParameterMetadata

data class QueryParameterMetadata(
    val name: String,
    val type: TypeMetadata,
    val required: Boolean = false,
    val default: String? = null
) : ParameterMetadata

data class HeaderMetadata(
    val name: String,
    val type: TypeMetadata,
    val required: Boolean = true,
    val values: List<String> = emptyList()
)

data class BodyMetadata(
    val type: TypeMetadata,
    val name: String? = null,
    val mediaType: String? = null
)

data class OutputBodyMetadata(
    val description: String,
    val body: BodyMetadata
)

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
