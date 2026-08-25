package dev.akif.tapik.common.plugin

/** Returns this string as an escaped Kotlin string literal. */
fun String.kotlinString(): String =
    buildString {
        append('"')
        this@kotlinString.forEach { character ->
            append(
                when (character) {
                    '\\' -> "\\\\"
                    '"' -> "\\\""
                    '\n' -> "\\n"
                    '\r' -> "\\r"
                    '\t' -> "\\t"
                    '$' -> "\\$"
                    else -> character
                }
            )
        }
        append('"')
    }
