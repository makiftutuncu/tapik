package dev.akif.tapik.http

sealed class MediaType(open val major: String, open val minor: String) {
    data object PlainText : MediaType("text", "plain")

    data object Json : MediaType("application", "json")

    data class Custom(
        override val major: String,
        override val minor: String
    ) : MediaType(major, minor)
}
