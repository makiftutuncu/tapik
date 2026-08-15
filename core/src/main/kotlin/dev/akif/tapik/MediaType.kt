package dev.akif.tapik

/**
 * A complete HTTP media-type [value].
 *
 * @throws IllegalArgumentException when [value] is blank.
 */
@JvmInline
value class MediaType(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "Media type must not be blank" }
    }

    override fun toString(): String = value

    /** Common media types used by body builders. */
    companion object {
        /** `application/json`. */
        val Json: MediaType = MediaType("application/json")

        /** `application/xml`. */
        val Xml: MediaType = MediaType("application/xml")

        /** `text/plain`. */
        val PlainText: MediaType = MediaType("text/plain")

        /** `application/octet-stream`. */
        val OctetStream: MediaType = MediaType("application/octet-stream")
    }
}
