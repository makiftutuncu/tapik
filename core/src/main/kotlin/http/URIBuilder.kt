package dev.akif.tapik.http

import dev.akif.tapik.codec.Codec
import dev.akif.tapik.tuple.Tuple

data class URIBuilder<PathVariables : Tuple<PathVariable<*>>, QueryParameters : Tuple<QueryParameter<*>>>(
    val segments: List<String>,
    val pathVariables: PathVariables,
    val queryParameters: QueryParameters
) {
    operator fun div(path: String): URIBuilder<PathVariables, QueryParameters> =
        copy(segments = segments + path)

    override fun toString(): String {
        val getType: (Codec<*, *>) -> String? = { it.sourceClass.simpleName }
        val pathVariablesToTypes = pathVariables.toList().associate { it.name to getType(it.codec) }
        val queryParametersToTypes = queryParameters.toList().associate { it.name to getType(it.codec) }

        val path = segments.joinToString(separator = "/", prefix = "/") { segment ->
            if (segment.startsWith("{") && segment.endsWith("}")) {
                val name = segment.removePrefix("{").removeSuffix("}")
                pathVariablesToTypes[name]?.let { type -> "{$name: $type}" } ?: "{$name}"
            } else {
                segment
            }
        }

        val query = queryParameters.toList().joinToString(separator = "&") {
            queryParametersToTypes[it.name]?.let { type -> "{${it.name}: $type}" } ?: "{${it.name}}"
        }

        return "$path${if (query.isEmpty()) "" else "?$query"}"
    }
}
