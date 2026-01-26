package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.*
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity

/**
 * Converts a Tapik [Response] produced by an endpoint with a single output into a Spring [ResponseEntity].
 *
 * @receiver Tapik endpoint that defines the available outputs.
 * @param response endpoint response to render.
 * @return Spring response entity containing the encoded payload, headers, and status.
 */
fun <P : Parameters, I : Input<*, *>, O : Outputs> HttpEndpoint<P, I, O>.toResponseEntity(
    response: Response
): ResponseEntity<Any?> {
    val output = outputs.toList().firstOrNull { output -> output.statusMatcher(response.status) }
    if (output == null) {
        error("Response didn't match to any known outputs")
    }
    val headers = buildHttpHeaders(output, response)
    val body = (response as? ResponseWithBody<*>)?.body
    return ResponseEntity(body, headers, response.status.toHttpStatus())
}

/**
 * Converts a Tapik `OneOf` response produced by an endpoint with multiple outputs into a Spring [ResponseEntity].
 *
 * @receiver Tapik endpoint that defines the available outputs.
 * @param oneOf union capturing which output branch was selected.
 * @return Spring response entity containing the encoded payload, headers, and status.
 */
fun <P : Parameters, I : Input<*, *>, O : Outputs> HttpEndpoint<P, I, O>.toResponseEntity(
    oneOf: OneOf
): ResponseEntity<Any?> = toResponseEntity(selectResponse(oneOf))

private fun selectResponse(oneOf: OneOf): Response =
    when (oneOf) {
        is OneOf2<*, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response }
            )

        is OneOf3<*, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response }
            )

        is OneOf4<*, *, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response },
                when4 = { it as Response }
            )

        is OneOf5<*, *, *, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response },
                when4 = { it as Response },
                when5 = { it as Response }
            )

        is OneOf6<*, *, *, *, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response },
                when4 = { it as Response },
                when5 = { it as Response },
                when6 = { it as Response }
            )

        is OneOf7<*, *, *, *, *, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response },
                when4 = { it as Response },
                when5 = { it as Response },
                when6 = { it as Response },
                when7 = { it as Response }
            )

        is OneOf8<*, *, *, *, *, *, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response },
                when4 = { it as Response },
                when5 = { it as Response },
                when6 = { it as Response },
                when7 = { it as Response },
                when8 = { it as Response }
            )

        is OneOf9<*, *, *, *, *, *, *, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response },
                when4 = { it as Response },
                when5 = { it as Response },
                when6 = { it as Response },
                when7 = { it as Response },
                when8 = { it as Response },
                when9 = { it as Response }
            )

        is OneOf10<*, *, *, *, *, *, *, *, *, *> ->
            oneOf.select(
                when1 = { it as Response },
                when2 = { it as Response },
                when3 = { it as Response },
                when4 = { it as Response },
                when5 = { it as Response },
                when6 = { it as Response },
                when7 = { it as Response },
                when8 = { it as Response },
                when9 = { it as Response },
                when10 = { it as Response }
            )
    }

@Suppress("UNCHECKED_CAST")
private fun buildHttpHeaders(
    output: Output<*, *>,
    response: Response
): HttpHeaders {
    val httpHeaders = HttpHeaders()
    val headerDefinitions = output.headers.toList()
    val headerValues = response.headers.toList()
    headerDefinitions.forEachIndexed { idx, header ->
        val values = headerValues.getOrNull(idx) ?: emptyList<Any?>()
        val codec = (header as Header<Any>).codec
        val encoded = values.filterNotNull().map { codec.encode(it) }
        if (encoded.isNotEmpty()) {
            httpHeaders[header.name] = encoded
        }
    }
    output
        .body
        .mediaType
        ?.toSpringMediaType()
        ?.also { httpHeaders.contentType = it }
    return httpHeaders
}
