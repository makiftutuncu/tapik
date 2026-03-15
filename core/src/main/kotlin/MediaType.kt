package dev.akif.tapik

/**
 * HTTP media type value used by body definitions and header codecs.
 *
 * Tapik interns its built-in defaults so common values such as plain text and JSON share stable
 * singleton instances, while still allowing arbitrary custom types.
 *
 * @property major primary media type token, such as `"application"`.
 * @property minor secondary token, such as `"json"`.
 */
sealed class MediaType(
    open val major: String,
    open val minor: String
) {
    /** Factory helpers for [MediaType] instances. */
    companion object {
        /** Returns a known singleton when possible, or falls back to [Custom]. */
        fun of(
            major: String,
            minor: String
        ): MediaType =
            when (major) {
                "text" ->
                    when (minor) {
                        "plain" -> PlainText
                        else -> Custom(major, minor)
                    }

                "application" ->
                    when (minor) {
                        "json" -> Json
                        else -> Custom(major, minor)
                    }

                else -> Custom(major, minor)
            }
    }

    /** Produces the canonical `major/minor` representation used on the wire. */
    override fun toString(): String = "$major/$minor"

    /** Built-in media type for `text/plain`. */
    data object PlainText : MediaType("text", "plain") {
        override fun toString(): String = super.toString()
    }

    /** Built-in media type for `application/json`. */
    data object Json : MediaType("application", "json") {
        override fun toString(): String = super.toString()
    }

    /** Arbitrary media type not covered by Tapik's built-in singletons. */
    data class Custom(
        override val major: String,
        override val minor: String
    ) : MediaType(major, minor) {
        override fun toString(): String = super.toString()
    }
}
