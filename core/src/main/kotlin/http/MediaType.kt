package dev.akif.tapik.http

sealed class MediaType(open val major: String, open val minor: String) {
    companion object {
        fun of(major: String, minor: String): MediaType =
            when (major) {
                "text" -> when (minor) {
                    "plain" -> PlainText
                    else -> Custom(major, minor)
                }

                "application" -> when (minor) {
                    "json" -> Json
                    else -> Custom(major, minor)
                }

                else -> Custom(major, minor)
            }
    }

    data object PlainText : MediaType("text", "plain")

    data object Json : MediaType("application", "json")

    data class Custom(
        override val major: String,
        override val minor: String
    ) : MediaType(major, minor)
}
