package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.Method
import dev.akif.tapik.common.spring.toHttpMethod
import dev.akif.tapik.common.spring.toMediaType
import dev.akif.tapik.common.spring.toSpringMediaType
import dev.akif.tapik.common.spring.toStatus
import dev.akif.tapik.common.spring.toTapikHeaders
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.util.UriBuilder
import java.net.URI

/**
 * Executes encoded Tapik requests with Spring [RestClient].
 *
 * @property client underlying Spring client.
 */
class RestClientTransport(
    val client: RestClient
) {
    /**
     * Exchanges one encoded request without interpreting endpoint outputs.
     *
     * @param method HTTP method to send.
     * @param uri resolves the request URI using the client's configured URI builder.
     * @param headers encoded request headers.
     * @param body optional encoded request body.
     * @return raw response status, headers, media type, and bytes.
     * @throws RestClientException when Spring cannot complete the exchange.
     */
    fun exchange(
        method: Method,
        uri: (UriBuilder) -> URI,
        headers: Map<String, List<String>> = emptyMap(),
        body: RestClientRequestBody? = null
    ): RestClientResponse {
        val request =
            client
                .method(method.toHttpMethod())
                .uri(uri)
                .headers { springHeaders ->
                    headers.forEach { (name, values) -> springHeaders.addAll(name, values) }
                }

        if (body != null) {
            request
                .contentType(body.mediaType.toSpringMediaType())
                .body(body.bytes)
        }

        return request.exchangeForRequiredValue { _, response ->
            RestClientResponse(
                status = response.statusCode.toStatus(),
                headers = response.headers.toTapikHeaders(),
                mediaType = response.headers.contentType?.toMediaType(),
                body = response.body.readAllBytes()
            )
        }
    }
}
