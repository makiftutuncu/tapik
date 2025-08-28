package dev.akif.tapik.spring.restclient

import dev.akif.tapik.http.*
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import java.net.URI
import org.springframework.http.HttpMethod as SpringMethod
import org.springframework.http.MediaType as SpringMediaType

data class RestClientInterpreter(
    val client: RestClient
) {
    internal fun send(
        method: Method,
        uri: URI,
        headers: Map<String, List<String>>,
        contentType: MediaType?,
        body: ByteArray?,
        outputs: List<Output<*, *>>
    ): ResponseEntity<ByteArray> =
        client
            .method(SpringMethod.valueOf(method.name))
            .uri { it.pathSegment(*uri.path.split("/").toTypedArray()).query(uri.query).build() }
            .headers {
                headers.forEach { (name, values) -> it[name] = values }
            }.apply {
                if (contentType != null) {
                    contentType(SpringMediaType(contentType.major, contentType.minor))
                }
                if (body != null) {
                    body { it.write(body) }
                }
            }
            .retrieve()
            .apply {
                outputs.fold(this) { spec, output ->
                    spec.onStatus(
                        { output.statusMatcher(Status.of(it.value())) },
                        statusHandler(output, method, uri)
                    )
                }
            }
            .onStatus(unmatchedStatusHandler(outputs))
            .toEntity(ByteArray::class.java)

    internal fun statusHandler(output: Output<*, *>, method: Method, uri: URI): RestClient.ResponseSpec.ErrorHandler =
        RestClient.ResponseSpec.ErrorHandler { _, response ->
            val status = Status.of(response.statusCode.value())
            val mediaType = output.body.mediaType
            val responseContentType = response.headers.contentType
            if (mediaType != null && responseContentType != null && mediaType.toString() !in responseContentType.toString()) {
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

    internal fun unmatchedStatusHandler(outputs: List<Output<*, *>>): ResponseErrorHandler = object: ResponseErrorHandler {
        override fun hasError(response: ClientHttpResponse): Boolean =
            true

        override fun handleError(
            url: URI,
            method: SpringMethod,
            response: ClientHttpResponse
        ) {
            val status = Status.of(response.statusCode.value())
            val expected = outputs.flatMap {
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
