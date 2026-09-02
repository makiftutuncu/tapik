package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.MediaType
import dev.akif.tapik.common.spring.toSpringMediaType
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

/** Builds a Spring response from encoded tapik status, headers, and optional body values. */
fun webMvcResponse(
    status: Int,
    headers: Map<String, List<String>>,
    body: Pair<MediaType, ByteArray>?
): ResponseEntity<ByteArray> {
    val springHeaders = HttpHeaders()
    headers.forEach { (name, values) -> springHeaders.addAll(name, values) }
    body?.let { (mediaType, _) -> springHeaders.contentType = mediaType.toSpringMediaType() }
    return ResponseEntity(body?.second, springHeaders, status)
}
