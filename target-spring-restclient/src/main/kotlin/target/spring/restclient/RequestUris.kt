package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.PathSegment
import dev.akif.tapik.PathVariable
import dev.akif.tapik.RemainingPath
import dev.akif.tapik.Uri
import org.springframework.web.util.UriBuilder
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriUtils
import java.net.URI
import java.nio.charset.StandardCharsets.UTF_8

/**
 * Resolves a request URI while preserving already-encoded contract literals.
 *
 * @param builder the client's configured builder, used to resolve the base URI.
 * @param uri endpoint URI definition, including ordered literal and variable segments.
 * @param pathValues raw format output by path-variable name; remaining paths use slash-separated segments.
 * @param queryValues raw format output by query name, with absent optional queries omitted.
 * @return the base URI with the endpoint path and queries appended, encoding parameter data once with UTF-8.
 * @throws NoSuchElementException when a path value is missing.
 * @throws IllegalArgumentException when an already-encoded component is invalid.
 */
fun restClientUri(
    builder: UriBuilder,
    uri: Uri<*, *>,
    pathValues: Map<String, String> = emptyMap(),
    queryValues: Map<String, List<String>> = emptyMap()
): URI {
    val segments = uri.segments.flatMap { segment ->
        when (segment) {
            is PathSegment.Literal -> listOf(segment.value)
            is PathVariable<*> -> listOf(UriUtils.encode(pathValues.getValue(segment.name), UTF_8))
            is RemainingPath -> pathValues.getValue(segment.name).split('/').map { UriUtils.encode(it, UTF_8) }
        }
    }
    val resolved = UriComponentsBuilder.fromUri(builder.build())
        .path(segments.joinToString(separator = "/", prefix = "/"))
    uri.queries.values.forEach { query ->
        queryValues[query.name]?.let { values ->
            resolved.queryParam(
                UriUtils.encode(query.name, UTF_8),
                *values.map { UriUtils.encode(it, UTF_8) }.toTypedArray()
            )
        }
    }
    return resolved.build(true).toUri()
}
