package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.MediaType
import dev.akif.tapik.Status
import java.util.Collections

/**
 * An encoded RestClient request body.
 *
 * @property mediaType content type of [bytes].
 */
class RestClientRequestBody(
    val mediaType: MediaType,
    bytes: ByteArray
) {
    private val byteSnapshot: ByteArray = bytes.copyOf()

    /** Encoded request bytes, returned as a fresh copy on each access. */
    val bytes: ByteArray
        get() = byteSnapshot.copyOf()

    /** Returns [mediaType] for destructuring. */
    operator fun component1(): MediaType = mediaType

    /** Returns the encoded [bytes] for destructuring. */
    operator fun component2(): ByteArray = bytes

    /** Returns a copy, snapshotting [bytes]. */
    fun copy(
        mediaType: MediaType = this.mediaType,
        bytes: ByteArray = this.bytes
    ): RestClientRequestBody = RestClientRequestBody(mediaType, bytes)

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is RestClientRequestBody &&
            mediaType == other.mediaType &&
            byteSnapshot.contentEquals(other.byteSnapshot)

    override fun hashCode(): Int = 31 * mediaType.hashCode() + byteSnapshot.contentHashCode()

    override fun toString(): String = "RestClientRequestBody(mediaType=$mediaType, bytes=${byteSnapshot.contentToString()})"
}

/**
 * A raw response received by [RestClientTransport].
 *
 * @property status received HTTP status.
 * @property mediaType received content type, when present.
 */
class RestClientResponse(
    val status: Status,
    headers: Map<String, List<String>>,
    val mediaType: MediaType?,
    body: ByteArray
) {
    /** Received header values, copied into an immutable map. */
    val headers: Map<String, List<String>> = headers.snapshotHeaders()

    private val bodySnapshot: ByteArray = body.copyOf()

    /** Complete response bytes, returned as a fresh copy on each access. */
    val body: ByteArray
        get() = bodySnapshot.copyOf()

    /** Returns [status] for destructuring. */
    operator fun component1(): Status = status

    /** Returns [headers] for destructuring. */
    operator fun component2(): Map<String, List<String>> = headers

    /** Returns [mediaType] for destructuring. */
    operator fun component3(): MediaType? = mediaType

    /** Returns a copy of [body] for destructuring. */
    operator fun component4(): ByteArray = body

    /** Returns a copy, snapshotting [headers] and [body]. */
    fun copy(
        status: Status = this.status,
        headers: Map<String, List<String>> = this.headers,
        mediaType: MediaType? = this.mediaType,
        body: ByteArray = this.body
    ): RestClientResponse = RestClientResponse(status, headers, mediaType, body)

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is RestClientResponse &&
            status == other.status &&
            headers == other.headers &&
            mediaType == other.mediaType &&
            bodySnapshot.contentEquals(other.bodySnapshot)

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + bodySnapshot.contentHashCode()
        return result
    }

    override fun toString(): String =
        "RestClientResponse(status=$status, headers=$headers, mediaType=$mediaType, body=${bodySnapshot.contentToString()})"
}

private fun Map<String, List<String>>.snapshotHeaders(): Map<String, List<String>> =
    entries.associateTo(LinkedHashMap()) { (name, values) ->
        name to Collections.unmodifiableList(values.toList())
    }.let(Collections::unmodifiableMap)
