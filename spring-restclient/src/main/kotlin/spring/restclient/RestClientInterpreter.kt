package dev.akif.tapik.spring.restclient

import dev.akif.tapik.http.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpMethod as SpringMethod
import org.springframework.http.MediaType as SpringMediaType
import org.springframework.web.client.RestClient
import java.net.URI

data class RestClientInterpreter(val client: RestClient) {
    internal fun send(
        method: Method,
        uri: URI,
        headers: Map<String, List<String>>,
        contentType: MediaType?,
        body: ByteArray?
    ): ResponseEntity<ByteArray> =
        client
            .method(SpringMethod.valueOf(method.name))
            .uri(uri)
            .headers {
                headers.forEach { (name, values) -> it[name] = values }
            }
            .apply {
                if (contentType != null) {
                    contentType(SpringMediaType(contentType.major, contentType.minor))
                }
                if (body != null) {
                    body { it.write(body) }
                }
            }
            .retrieve()
            .toEntity(ByteArray::class.java)
}

