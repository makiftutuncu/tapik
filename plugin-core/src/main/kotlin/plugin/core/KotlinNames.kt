package dev.akif.tapik.plugin.core

/** Returns whether this string is an unescaped Kotlin identifier. */
fun String.isKotlinIdentifier(): Boolean =
    isNotEmpty() &&
        first().let { character -> character == '_' || character.isLetter() } &&
        drop(1).all { character -> character == '_' || character.isLetterOrDigit() } &&
        this !in KOTLIN_KEYWORDS

/**
 * Sanitizes this string as a Kotlin declaration identifier.
 *
 * @param fallback identifier used when this string has no alphanumeric words.
 */
fun String.kotlinIdentifier(fallback: String): String {
    if (isKotlinIdentifier()) return this
    val words = split(Regex("[^A-Za-z0-9]+")).filter(String::isNotEmpty)
    val candidate =
        words
            .mapIndexed { index, word ->
                if (index == 0) word.replaceFirstChar(Char::lowercaseChar)
                else word.replaceFirstChar(Char::uppercaseChar)
            }.joinToString("")
            .ifEmpty { fallback }
            .let { value -> if (value.first().isDigit()) "_$value" else value }
    return if (candidate in KOTLIN_KEYWORDS) "`$candidate`" else candidate
}

/** Escapes this string with backticks when it is not an unescaped Kotlin identifier. */
fun String.kotlinReferenceIdentifier(): String =
    if (isKotlinIdentifier()) this else "`$this`"

/** Returns a sanitized upper-camel Kotlin name. */
fun String.upperCamel(): String =
    kotlinIdentifier("Value")
        .removeSurrounding("`")
        .replaceFirstChar(Char::uppercaseChar)

/**
 * Returns a sanitized lower-camel Kotlin name.
 *
 * @param fallback identifier used when this string has no alphanumeric words.
 */
fun String.lowerCamel(fallback: String): String =
    kotlinIdentifier(fallback)
        .removeSurrounding("`")
        .replaceFirstChar(Char::lowercaseChar)
        .let { value -> if (value in KOTLIN_KEYWORDS) "`$value`" else value }

/**
 * Allocates [requested] in [used], adding the first available numeric suffix when necessary.
 *
 * @param requested preferred Kotlin name.
 * @param used names allocated in the current namespace.
 * @return the allocated unique name.
 */
fun uniqueKotlinName(
    requested: String,
    used: MutableSet<String>
): String {
    if (used.add(requested)) return requested
    val raw = requested.removeSurrounding("`")
    var suffix = 2
    while (!used.add(raw + suffix)) suffix++
    return raw + suffix
}

private val KOTLIN_KEYWORDS: Set<String> =
    setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface",
        "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias",
        "typeof", "val", "var", "when", "while"
    )
