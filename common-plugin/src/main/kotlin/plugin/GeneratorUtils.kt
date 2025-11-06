package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.TypeMetadata

/**
 * Renders the receiver type as a Kotlin type literal, including generics and nullability information.
 *
 * @receiver Tapik type metadata instance.
 * @return Kotlin-compatible type literal.
 */
fun TypeMetadata.render(): String {
    val base = simpleName()
    val renderedArgs = arguments.joinToString(", ") { it.render() }
    val nullableSuffix = if (nullable == true) "?" else ""
    return if (renderedArgs.isEmpty()) {
        "$base$nullableSuffix"
    } else {
        "$base<$renderedArgs>$nullableSuffix"
    }
}

/**
 * Resolves the simple, unqualified name of the receiver type.
 *
 * @receiver Tapik type metadata instance.
 * @return simple type name without package information.
 */
fun TypeMetadata.simpleName(): String = name.substringAfterLast('.')

/**
 * Collects the simple names of the receiver type and all of its nested generic arguments.
 *
 * @receiver Tapik type metadata instance.
 * @return set containing the receiver's simple name plus the simple names of nested arguments.
 */
fun TypeMetadata.collectSimpleNames(): Set<String> =
    buildSet {
        add(simpleName())
        arguments.forEach { addAll(it.collectSimpleNames()) }
    }

/**
 * Determines a Kotlin type that represents the payload carried by Tapik body metadata.
 *
 * @receiver Tapik type metadata instance.
 * @return Kotlin type name representing the decoded body value.
 */
fun TypeMetadata.determineBodyValueType(): String =
    when (simpleName()) {
        "JsonBody" -> arguments.firstOrNull()?.render() ?: "Any"
        "StringBody" -> "String"
        "RawBody" -> "ByteArray"
        "EmptyBody" -> "Unit"
        else -> arguments.firstOrNull()?.render() ?: "Any"
    }

/**
 * Sanitises an arbitrary identifier so that it is safe to use as a Kotlin name.
 *
 * @param rawName original identifier or `null`.
 * @param fallback fallback value used when [rawName] cannot be salvaged.
 * @return valid Kotlin identifier matching the supplied intent.
 */
fun sanitizeIdentifier(rawName: String?, fallback: String): String {
    val primary = rawName?.trim().takeUnless { it.isNullOrEmpty() } ?: fallback
    normalizeIdentifier(primary)?.let { return it }
    normalizeIdentifier(fallback)?.let { return it }
    return "value"
}

/**
 * Generates a unique identifier by applying numeric suffixes until an unused variant is discovered.
 *
 * @param base preferred identifier.
 * @param used mutable set tracking previously allocated identifiers.
 * @return unique identifier safe to use within the current scope.
 */
fun uniqueName(base: String, used: MutableSet<String>): String {
    val normalizedBase = normalizeIdentifier(base) ?: "value"
    var candidate = normalizedBase
    var rendered = renderIdentifier(candidate)
    var index = 2
    while (rendered in used) {
        candidate = normalizedBase + index.toString()
        rendered = renderIdentifier(candidate)
        index++
    }
    used += rendered
    return rendered
}

/**
 * Quotes Kotlin keywords and other unsafe symbols to produce a valid identifier.
 *
 * @param name identifier to render.
 * @return safe Kotlin identifier, quoted if necessary.
 */
fun renderIdentifier(name: String): String {
    val candidate = normalizeIdentifier(name) ?: "value"
    return if (candidate in KOTLIN_KEYWORDS) {
        "`$candidate`"
    } else {
        candidate
    }
}

private val PRE_SANITIZED_PATTERN = Regex("[a-z][A-Za-z0-9]*")
private val NON_ALPHANUMERIC = Regex("[^A-Za-z0-9]+")

private fun normalizeIdentifier(input: String): String? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) {
        return null
    }
    if (PRE_SANITIZED_PATTERN.matches(trimmed)) {
        return trimmed
    }

    val parts =
        trimmed
            .split(NON_ALPHANUMERIC)
            .map { segment -> segment.filter { it.isLetterOrDigit() } }
            .filter { it.isNotEmpty() }

    if (parts.isEmpty()) {
        return null
    }

    val builder = StringBuilder()
    val first = parts.first().lowercase()
    if (first.isNotEmpty()) {
        builder.append(first)
    }

    parts
        .drop(1)
        .forEach { part ->
            val lower = part.lowercase()
            if (lower.isEmpty()) {
                return@forEach
            }
            if (lower[0].isLetter()) {
                builder.append(lower.replaceFirstChar { ch -> if (ch.isLetter()) ch.uppercaseChar() else ch })
            } else {
                builder.append(lower)
            }
        }

    var candidate = builder.toString().filter { it.isLetterOrDigit() }
    if (candidate.isEmpty()) {
        return null
    }

    if (!candidate.first().isLetter()) {
        candidate =
            "value" + candidate.replaceFirstChar { ch ->
                if (ch.isLetter()) {
                    ch.uppercaseChar()
                } else {
                    ch
                }
            }
    }

    return candidate
}

/**
 * Splits and trims multi-line documentation text into individual lines suitable for KDoc rendering.
 *
 * @param text raw documentation block.
 * @return trimmed documentation lines or an empty list when [text] is blank.
 */
fun linesForDocumentation(text: String?): List<String> =
    text
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.lines()
        ?.map { it.trim() }
        ?: emptyList()

/**
 * Escapes terminator sequences that would prematurely end a KDoc block.
 *
 * @receiver raw documentation line.
 * @return string safe for inclusion inside a KDoc comment.
 */
fun String.escapeForKdoc(): String = replace("*/", "* /")

/**
 * Escapes quotes and path separators so that the string can be safely embedded inside annotation arguments.
 *
 * @receiver raw annotation parameter value.
 * @return escaped value suitable for inclusion in source code.
 */
fun String.escapeForAnnotation(): String =
    replace("\\", "\\\\")
        .replace("\"", "\\\"")

private val KOTLIN_KEYWORDS = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
    "var", "when", "while"
)
