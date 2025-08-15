package dev.akif.tapik.http

import dev.akif.tapik.tuple.TupleLike

data class URIBuilder<PathVariables: TupleLike, QueryParameters: TupleLike>(
    val segments: List<String>,
    val pathVariables: PathVariables,
    val queryParameters: QueryParameters
) {
    operator fun div(path: String): URIBuilder<PathVariables, QueryParameters> =
        copy(segments = segments + path)
}
