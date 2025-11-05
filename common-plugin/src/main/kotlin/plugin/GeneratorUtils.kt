package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.TypeMetadata

internal fun TypeMetadata.render(): String {
    val base = simpleName()
    val renderedArgs = arguments.joinToString(", ") { it.render() }
    val nullableSuffix = if (nullable == true) "?" else ""
    return if (renderedArgs.isEmpty()) {
        "$base$nullableSuffix"
    } else {
        "$base<$renderedArgs>$nullableSuffix"
    }
}

internal fun TypeMetadata.simpleName(): String = name.substringAfterLast('.')

internal fun TypeMetadata.collectSimpleNames(): Set<String> =
    buildSet {
        add(simpleName())
        arguments.forEach { addAll(it.collectSimpleNames()) }
    }

internal fun TypeMetadata.determineBodyValueType(): String =
    when (simpleName()) {
        "JsonBody" -> arguments.firstOrNull()?.render() ?: "Any"
        "StringBody" -> "String"
        "RawBody" -> "ByteArray"
        "EmptyBody" -> "Unit"
        else -> arguments.firstOrNull()?.render() ?: "Any"
    }

internal fun sanitizeIdentifier(rawName: String?, fallback: String): String {
    val source = rawName?.trim().takeUnless { it.isNullOrEmpty() } ?: fallback
    var cleaned = buildString {
        for (ch in source) {
            when {
                ch.isLetterOrDigit() -> append(ch)
                ch == '_' -> append('_')
                else -> append('_')
            }
        }
    }.replace(Regex("_+"), "_").trim('_')

    if (cleaned.isEmpty()) {
        cleaned = fallback
    }

    if (cleaned.firstOrNull()?.let { it.isLetter() || it == '_' } != true) {
        cleaned = "_$cleaned"
    }

    return cleaned
}

internal fun uniqueName(base: String, used: MutableSet<String>): String {
    var candidate = base
    var index = 2
    while (renderIdentifier(candidate) in used) {
        candidate = "${base}_$index"
        index++
    }
    val rendered = renderIdentifier(candidate)
    used += rendered
    return rendered
}

internal fun renderIdentifier(name: String): String {
    val keyword = name in KOTLIN_KEYWORDS
    val identifierPattern = Regex("[A-Za-z_][A-Za-z0-9_]*")
    return if (!keyword && identifierPattern.matches(name)) {
        name
    } else {
        "`$name`"
    }
}

internal fun linesForDocumentation(text: String?): List<String> =
    text
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.lines()
        ?.map { it.trim() }
        ?: emptyList()

internal fun String.escapeForKdoc(): String = replace("*/", "* /")

internal fun String.escapeForAnnotation(): String =
    replace("\\", "\\\\")
        .replace("\"", "\\\"")

private val KOTLIN_KEYWORDS = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
    "var", "when", "while"
)
