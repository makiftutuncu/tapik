package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.ParameterMetadata
import dev.akif.tapik.plugin.metadata.PathVariableMetadata
import dev.akif.tapik.plugin.metadata.QueryParameterMetadata

internal data class GeneratedEndpointUriParameters(
    val requiredEntries: List<Entry>,
    val optionalEntries: List<Entry>
) {
    internal data class Entry(
        val name: String,
        val accessor: String,
        val renderedType: String,
        val nonNullableType: String?,
        val hasDefault: Boolean,
        val hasNonNullDefault: Boolean
    ) {
        fun declaration(): String {
            if (!hasDefault) {
                return "$name: $renderedType"
            }

            val queryType = checkNotNull(nonNullableType)
            val defaultValue =
                if (hasNonNullDefault) {
                    "$accessor.asQueryParameter<$queryType>().getDefaultOrFail()"
                } else {
                    "$accessor.asQueryParameter<$queryType>().default.getOrNull()"
                }

            return "$name: $renderedType = $defaultValue"
        }

        fun renderArgument(): String {
            val encodedValue =
                if (!hasDefault || hasNonNullDefault) {
                    "$accessor.codec.encode($name)"
                } else {
                    "$name?.let { $accessor.codec.encode(it) }"
                }

            return "$accessor to $encodedValue"
        }
    }

    val entriesInMethodOrder: List<Entry> = requiredEntries + optionalEntries

    fun declarations(): List<String> = entriesInMethodOrder.map { it.declaration() }

    fun callArguments(): String = entriesInMethodOrder.joinToString(", ") { it.name }

    fun renderArguments(): List<String> = entriesInMethodOrder.map { it.renderArgument() }
}

internal fun buildGeneratedEndpointUriParameters(
    parameters: List<ParameterMetadata>,
    endpointReference: String
): GeneratedEndpointUriParameters {
    val usedNames = mutableSetOf<String>()
    val entries =
        parameters.mapIndexed { index, parameter ->
            val fallback = "parameter${index + 1}"
            val baseName =
                when (parameter) {
                    is PathVariableMetadata -> sanitizeIdentifier(parameter.name, fallback)
                    is QueryParameterMetadata -> sanitizeIdentifier(parameter.name, fallback)
                }
            val name = uniqueName(baseName, usedNames)
            val accessor = "$endpointReference.parameters.item${index + 1}"

            when (parameter) {
                is PathVariableMetadata ->
                    GeneratedEndpointUriParameters.Entry(
                        name = name,
                        accessor = accessor,
                        renderedType = parameter.type.render(),
                        nonNullableType = null,
                        hasDefault = false,
                        hasNonNullDefault = false
                    )

                is QueryParameterMetadata -> {
                    val typeInfo = parameter.toGeneratedTypeInfo()
                    GeneratedEndpointUriParameters.Entry(
                        name = name,
                        accessor = accessor,
                        renderedType = typeInfo.renderedType,
                        nonNullableType = typeInfo.nonNullableType,
                        hasDefault = typeInfo.hasDefault,
                        hasNonNullDefault = typeInfo.hasNonNullDefault
                    )
                }
            }
        }

    return GeneratedEndpointUriParameters(
        requiredEntries = entries.filterNot { it.hasDefault },
        optionalEntries = entries.filter { it.hasDefault }
    )
}
