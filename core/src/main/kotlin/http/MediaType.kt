package dev.akif.tapik.http

/**
 * Represents an HTTP media type with helpers for common Tapik defaults.
 *
 * Concrete implementations either map to well-known types or a [Custom] instance based on the
 * provided major/minor pair.
 *
 * @property major primary media type token (e.g. `"application"`).
 * @property minor secondary media type token (e.g. `"json"`).
 */
sealed class MediaType(
    open val major: String,
    open val minor: String
) {
    /** Factory helpers for [MediaType] instances. */
    companion object {
        /**
         * Builds a [MediaType] for the given [major]/[minor] pair, reusing predefined instances when possible.
         *
         * @param major primary media type token.
         * @param minor secondary media type token.
         * @return a predefined [PlainText] or [Json] instance when recognised, otherwise [Custom].
         * @see Custom
         */
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

    /**
     * Produces the canonical string representation in the form `major/minor`.
     *
     * @return serialized media type.
     */
    override fun toString(): String = "$major/$minor"

    /** Plain text media type alias for `text/plain`. */
    data object PlainText : MediaType("text", "plain") {
        /**
         * Returns the canonical `text/plain` representation.
         *
         * @return literal `text/plain`.
         */
        override fun toString(): String = super.toString()
    }

    /** JSON media type alias for `application/json`. */
    data object Json : MediaType("application", "json") {
        /**
         * Returns the canonical `application/json` representation.
         *
         * @return literal `application/json`.
         */
        override fun toString(): String = super.toString()
    }

    /** Arbitrary user-supplied media type.
     *
     * @property major primary media type token.
     * @property minor secondary media type token.
     */
    data class Custom(
        override val major: String,
        override val minor: String
    ) : MediaType(major, minor) {
        /**
         * Returns the canonical custom media type representation.
         *
         * @return literal `major/minor`.
         */
        override fun toString(): String = super.toString()
    }
}
