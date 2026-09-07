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
