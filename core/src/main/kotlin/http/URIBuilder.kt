package dev.akif.tapik.http

import dev.akif.tapik.tuple.Tuple

data class URIBuilder<PathVariables: Tuple, QueryParameters: Tuple>(
    val segments: List<String>,
    val pathVariables: PathVariables,
    val queryParameters: QueryParameters
) {
    operator fun div(path: String): URIBuilder<PathVariables, QueryParameters> =
        copy(segments = segments + path)
}
