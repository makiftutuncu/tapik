package dev.akif.tapik.spring.restclient

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status

/**
 * An encoded RestClient request body.
 *
 * @property mediaType content type of [bytes].
 * @property bytes encoded request bytes.
 */
data class RestClientRequestBody(
    val mediaType: MediaType,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean =
        this === other ||
            other is RestClientRequestBody &&
            mediaType == other.mediaType &&
            bytes.contentEquals(other.bytes)

    override fun hashCode(): Int = 31 * mediaType.hashCode() + bytes.contentHashCode()
}

/**
 * A raw response received by [RestClientTransport].
 *
 * @property status received HTTP status.
 * @property headers all received header values.
 * @property mediaType received content type, when present.
 * @property body complete response bytes.
 */
data class RestClientResponse(
    val status: Status,
    val headers: Map<String, List<String>>,
    val mediaType: MediaType?,
    val body: ByteArray
) {
    override fun equals(other: Any?): Boolean =
        this === other ||
            other is RestClientResponse &&
            status == other.status &&
            headers == other.headers &&
            mediaType == other.mediaType &&
            body.contentEquals(other.body)

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + (mediaType?.hashCode() ?: 0)
        result = 31 * result + body.contentHashCode()
        return result
    }
}
