package dev.akif.tapik

/**
 * A validated, concrete HTTP media type.
 *
 * Type/subtype and parameter names are case-insensitive, as are charset values. Other parameter values are
 * case-sensitive. Parameter order and equivalent quoting do not affect equality or hashing.
 *
 * @param value complete HTTP media-type syntax, not an `Accept` range.
 * @property value normalized wire syntax, retaining parameter declaration order.
 * @throws IllegalArgumentException when syntax is invalid, parameter names repeat, or type/subtype contains a wildcard.
 */
class MediaType(
    value: String
) {
    private val parsed: ParsedMediaType = parseMediaType(value)

    val value: String = parsed.render()

    override fun equals(other: Any?): Boolean =
        this === other || other is MediaType && parsed == other.parsed

    override fun hashCode(): Int = parsed.hashCode()

    override fun toString(): String = value

    /** Common media types used by body builders. */
    companion object {
        /** `application/atom+xml`. */
        val AtomXml: MediaType = MediaType("application/atom+xml")

        /** `application/cbor`. */
        val Cbor: MediaType = MediaType("application/cbor")

        /** `application/x-www-form-urlencoded`. */
        val FormUrlEncoded: MediaType = MediaType("application/x-www-form-urlencoded")

        /** `application/graphql-response+json`. */
        val GraphQlResponse: MediaType = MediaType("application/graphql-response+json")

        /** `application/json`. */
        val Json: MediaType = MediaType("application/json")

        /** `application/x-ndjson`. */
        val NdJson: MediaType = MediaType("application/x-ndjson")

        /** `application/octet-stream`. */
        val OctetStream: MediaType = MediaType("application/octet-stream")

        /** `application/pdf`. */
        val Pdf: MediaType = MediaType("application/pdf")

        /** `application/problem+json`. */
        val ProblemJson: MediaType = MediaType("application/problem+json")

        /** `application/problem+xml`. */
        val ProblemXml: MediaType = MediaType("application/problem+xml")

        /** `application/x-protobuf`. */
        val Protobuf: MediaType = MediaType("application/x-protobuf")

        /** `application/rss+xml`. */
        val RssXml: MediaType = MediaType("application/rss+xml")

        /** `application/xhtml+xml`. */
        val XhtmlXml: MediaType = MediaType("application/xhtml+xml")

        /** `application/xml`. */
        val Xml: MediaType = MediaType("application/xml")

        /** `application/yaml`. */
        val Yaml: MediaType = MediaType("application/yaml")

        /** `image/gif`. */
        val Gif: MediaType = MediaType("image/gif")

        /** `image/jpeg`. */
        val Jpeg: MediaType = MediaType("image/jpeg")

        /** `image/png`. */
        val Png: MediaType = MediaType("image/png")

        /** `multipart/form-data`. */
        val MultipartFormData: MediaType = MediaType("multipart/form-data")

        /** `multipart/mixed`. */
        val MultipartMixed: MediaType = MediaType("multipart/mixed")

        /** `multipart/related`. */
        val MultipartRelated: MediaType = MediaType("multipart/related")

        /** `text/event-stream`. */
        val EventStream: MediaType = MediaType("text/event-stream")

        /** `text/html`. */
        val Html: MediaType = MediaType("text/html")

        /** `text/markdown`. */
        val Markdown: MediaType = MediaType("text/markdown")

        /** `text/plain`. */
        val PlainText: MediaType = MediaType("text/plain")

        /** `text/xml`. */
        val TextXml: MediaType = MediaType("text/xml")
    }
}
