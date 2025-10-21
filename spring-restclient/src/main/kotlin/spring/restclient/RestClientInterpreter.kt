package dev.akif.tapik.spring.restclient

import dev.akif.tapik.http.MediaType
import dev.akif.tapik.http.Method
import dev.akif.tapik.http.OutputBody
import dev.akif.tapik.http.StatusMatcher
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import java.net.URI
import org.springframework.http.HttpMethod as SpringMethod
import org.springframework.http.MediaType as SpringMediaType

/**
 * Executes Tapik endpoints using a Spring [RestClient] while enforcing status and content-type checks.
 *
 * @property client underlying [RestClient] instance.
 */
data class RestClientInterpreter(
    val client: RestClient
) {
    /**
     * Sends a request to the configured [client] and validates the response against the expected [outputBodies].
     *
     * @param method HTTP method to invoke.
     * @param uri fully resolved request URI.
     * @param inputHeaders header values to send with the request.
     * @param inputBodyContentType optional request body media type.
     * @param inputBody optional request body payload.
     * @param outputBodies collection of expected output body definitions for status matching.
     * @return the received [ResponseEntity].
     * @throws RestClientResponseException when the response does not satisfy status or content-type expectations.
     * @see RestClient
     */
    fun send(
        method: Method,
        uri: URI,
        inputHeaders: Map<String, List<String>>,
        inputBodyContentType: MediaType?,
        inputBody: ByteArray?,
        outputBodies: List<OutputBody<*>>
    ): ResponseEntity<ByteArray> =
        client
            .method(SpringMethod.valueOf(method.name))
            .uri { it.pathSegment(*uri.path.split("/").toTypedArray()).query(uri.query).build() }
            .headers {
                inputHeaders.forEach { (name, values) -> it[name] = values }
            }.apply {
                if (inputBodyContentType != null) {
                    contentType(SpringMediaType(inputBodyContentType.major, inputBodyContentType.minor))
                }
                if (inputBody != null) {
                    body { it.write(inputBody) }
                }
            }.retrieve()
            .apply {
                outputBodies.fold(this) { spec, output ->
                    spec.onStatus(
                        { output.statusMatcher(it.toStatus()) },
                        statusHandler(output, method, uri)
                    )
                }
            }.onStatus(unmatchedStatusHandler(outputBodies))
            .toEntity(ByteArray::class.java)

    /**
     * Produces an error handler that verifies the response content type for [outputBody].
     *
     * @param outputBody body definition that produced the handler.
     * @param method HTTP method used for the request.
     * @param uri request URI.
     * @return an error handler that raises [RestClientResponseException] with detailed diagnostics.
     */
    fun statusHandler(
        outputBody: OutputBody<*>,
        method: Method,
        uri: URI
    ): RestClient.ResponseSpec.ErrorHandler =
        RestClient.ResponseSpec.ErrorHandler { _, response ->
            val status = response.statusCode.toStatus()
            val mediaType = outputBody.body.mediaType
            val responseContentType = response.headers.contentType
            if (mediaType != null &&
                responseContentType != null &&
                mediaType.toString() !in responseContentType.toString()
            ) {
                throw RestClientResponseException(
                    "Unexpected content type '$responseContentType' for request '${"$method $uri"}' and status $status, expecting '$mediaType'",
                    status.code,
                    status.name,
                    response.headers,
                    response.body.readAllBytes(),
                    responseContentType.charset
                )
            }
        }

    /**
     * Produces a [ResponseErrorHandler] for statuses not matched by any declared [outputBodies].
     *
     * @param outputBodies collection of expected output body definitions.
     * @return handler that throws [RestClientResponseException] for unmatched responses.
     */
    fun unmatchedStatusHandler(outputBodies: List<OutputBody<*>>): ResponseErrorHandler =
        object : ResponseErrorHandler {
            override fun hasError(response: ClientHttpResponse): Boolean = true

            override fun handleError(
                url: URI,
                method: SpringMethod,
                response: ClientHttpResponse
            ) {
                val status = response.statusCode.toStatus()
                val expected =
                    outputBodies.flatMap {
                        when (val matcher = it.statusMatcher) {
                            is StatusMatcher.Is -> listOf(matcher.status.toString())
                            is StatusMatcher.AnyOf -> matcher.statuses.map { s -> s.toString() }
                            is StatusMatcher.Predicate -> listOf(matcher.description)
                            StatusMatcher.Unmatched -> emptyList()
                        }
                    }
                throw RestClientResponseException(
                    "Unexpected status code '${status.code}' for request '${"$method $url"}', expecting $expected",
                    status.code,
                    status.name,
                    response.headers,
                    response.body.readAllBytes(),
                    response.headers.contentType?.charset
                )
            }
        }
}
