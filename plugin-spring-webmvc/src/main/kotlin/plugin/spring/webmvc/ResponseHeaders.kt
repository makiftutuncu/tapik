package dev.akif.tapik.plugin.spring.webmvc

import dev.akif.tapik.Headers

internal fun validateResponseHeaders(
    endpointId: String,
    outputNumber: Int,
    hasBody: Boolean,
    headers: Headers
) {
    headers.values.forEach { header ->
        require(!header.name.equals("Content-Length", ignoreCase = true)) {
            "$endpointId output $outputNumber declares Content-Length, which is managed by Spring WebMVC"
        }
        require(!hasBody || !header.name.equals("Content-Type", ignoreCase = true)) {
            "$endpointId output $outputNumber declares Content-Type, which is derived from its selected body representation"
        }
    }
}
