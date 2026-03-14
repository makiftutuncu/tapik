package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.BodyMetadata
import dev.akif.tapik.plugin.metadata.QueryParameterMetadata
import dev.akif.tapik.plugin.metadata.TypeMetadata

/**
 * Renders the receiver type as a Kotlin type literal, including generics and nullability information.
 *
 * @receiver Tapik type metadata instance.
 * @return Kotlin-compatible type literal.
 */
fun TypeMetadata.render(): String {
    val base = name
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
fun TypeMetadata.simpleName(): String = computeSimpleName(name)

/**
 * Returns the Kotlin value type exposed by generated code for the receiver body metadata.
 *
 * `JsonBody<T>` resolves to `T`, `StringBody` to `kotlin.String`, `RawBody` to `kotlin.ByteArray`,
 * and unknown body wrappers fall back to their first generic argument or `kotlin.Any`.
 *
 * @receiver body metadata to inspect.
 * @return generated Kotlin value type.
 */
fun BodyMetadata.renderValueType(): String =
    when (type.simpleName()) {
        "JsonBody" -> type.arguments.firstOrNull()?.render() ?: "kotlin.Any"
        "StringBody" -> "kotlin.String"
        "RawBody" -> "kotlin.ByteArray"
        else -> type.arguments.firstOrNull()?.render() ?: "kotlin.Any"
    }

/**
 * Shared type information used when generators expose query parameters as Kotlin method arguments.
 *
 * @property nonNullableType rendered type without nullable marker.
 * @property renderedType rendered Kotlin argument type after applying Tapik default/nullability rules.
 * @property hasDefault whether the generated argument should declare a default expression.
 * @property hasNonNullDefault whether the endpoint metadata guarantees a non-null default value.
 */
data class GeneratedQueryParameterTypeInfo(
    val nonNullableType: String,
    val renderedType: String,
    val hasDefault: Boolean,
    val hasNonNullDefault: Boolean
)

/**
 * Resolves the Kotlin argument type information used by generated clients/controllers for a query parameter.
 *
 * Required parameters and optional parameters with non-null defaults stay non-nullable. Optional
 * parameters without such defaults are rendered nullable and receive a nullable default expression.
 *
 * @receiver query parameter metadata to inspect.
 * @return rendered type information shared by generators.
 */
fun QueryParameterMetadata.toGeneratedTypeInfo(): GeneratedQueryParameterTypeInfo {
    val hasDefault = !required
    val hasNonNullDefault = default.fold({ false }) { it != null }
    val nonNullableType = type.copy(nullable = false).render()
    val renderedType =
        if (required || hasNonNullDefault) {
            nonNullableType
        } else {
            type.copy(nullable = true).render()
        }

    return GeneratedQueryParameterTypeInfo(
        nonNullableType = nonNullableType,
        renderedType = renderedType,
        hasDefault = hasDefault,
        hasNonNullDefault = hasNonNullDefault
    )
}

/**
 * Sanitises an arbitrary identifier so that it is safe to use as a Kotlin name.
 *
 * @param rawName original identifier or `null`.
 * @param fallback fallback value used when [rawName] cannot be salvaged.
 * @return valid Kotlin identifier matching the supplied intent.
 */
fun sanitizeIdentifier(
    rawName: String?,
    fallback: String
): String {
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
fun uniqueName(
    base: String,
    used: MutableSet<String>
): String {
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
internal fun renderIdentifier(name: String): String {
    val candidate = normalizeIdentifier(name) ?: "value"
    return if (candidate in KOTLIN_KEYWORDS) {
        "`$candidate`"
    } else {
        candidate
    }
}

private val PRE_SANITIZED_PATTERN = Regex("[a-z][A-Za-z0-9]*")
private val NON_ALPHANUMERIC = Regex("[^A-Za-z0-9]+")

/**
 * Derives a simple name from a fully-qualified type name, preserving nested class qualifiers.
 *
 * Examples:
 * - "com.example.Book.Isbn" -> "Book.Isbn"
 * - "java.util.Map" -> "Map"
 *
 * @param fqcn fully-qualified class name, possibly including nested types.
 * @return tail name starting at the first segment that begins with an uppercase letter.
 */
internal fun computeSimpleName(fqcn: String): String {
    val parts = fqcn.split('.')
    val indexOfType = parts.indexOfFirst { it.firstOrNull()?.isUpperCase() == true }
    return if (indexOfType == -1) {
        parts.last()
    } else {
        parts.drop(indexOfType).joinToString(".")
    }
}

/**
 * Appends [generatedPackageName] to the receiver package unless the receiver is blank.
 *
 * @receiver source package name.
 * @param generatedPackageName generated package segment.
 * @return resolved target package name.
 */
fun String.toGeneratedPackageName(generatedPackageName: String): String =
    listOfNotNull(
        takeIf(String::isNotBlank),
        generatedPackageName.trim().takeIf(String::isNotBlank)
    ).joinToString(".")

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
            "value" +
            candidate.replaceFirstChar { ch ->
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
internal fun linesForDocumentation(text: String?): List<String> =
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

private val KOTLIN_KEYWORDS =
    setOf(
        "as",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "interface",
        "is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "true",
        "try",
        "typealias",
        "typeof",
        "val",
        "var",
        "when",
        "while"
    )
