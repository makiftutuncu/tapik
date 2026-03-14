package dev.akif.tapik

import java.net.URI

/**
 * Renders a concrete [URI] by applying encoded values to a path template and query definitions.
 *
 * Path parameter values must always be present and non-null. Query values are included when the
 * parameter is required or when an optional parameter has a non-null encoded value.
 *
 * @param segments path template segments.
 * @param parametersToValues encoded parameter values paired with their definitions.
 * @return rendered [URI].
 * @throws IllegalArgumentException when a path parameter value is missing/null or a required query value is null.
 */
fun renderURI(
    segments: List<String>,
    vararg parametersToValues: Pair<Parameter<*>, String?>
): URI {
    val pathValues =
        parametersToValues
            .asSequence()
            .filter { it.first.position == ParameterPosition.Path }
            .associate { (parameter, value) ->
                parameter.name to
                    requireNotNull(value) { "Path parameter '${parameter.name}' is required but was null" }
            }

    val path =
        segments.joinToString(separator = "/", prefix = "/") { segment ->
            val parameterName = segment.extractPathParameterName()
            if (parameterName == null) {
                segment
            } else {
                pathValues[parameterName]
                    ?: throw IllegalArgumentException("Missing value for path parameter '$parameterName'")
            }
        }

    val query =
        parametersToValues
            .asSequence()
            .filter { it.first.position == ParameterPosition.Query }
            .mapNotNull { (parameter, value) ->
                if (value == null) {
                    if (parameter.required) {
                        throw IllegalArgumentException("Query parameter '${parameter.name}' is required but was null")
                    }
                    null
                } else {
                    "${parameter.name}=$value"
                }
            }.joinToString(separator = "&")
            .takeIf { it.isNotEmpty() }

    return URI.create(listOfNotNull(path, query).joinToString("?"))
}

private fun String.extractPathParameterName(): String? =
    if (startsWith("{") && endsWith("}") && length > 2) {
        removePrefix("{").removeSuffix("}")
    } else {
        null
    }
