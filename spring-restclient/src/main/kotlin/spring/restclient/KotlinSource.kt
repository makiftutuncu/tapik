package dev.akif.tapik.spring.restclient

import dev.akif.tapik.plugin.core.*

internal fun KotlinType.argument(index: Int, location: String): KotlinType {
    val projection =
        arguments.getOrNull(index)
            ?: throw IllegalArgumentException("$location is missing type argument $index")
    return (projection as? KotlinTypedProjection)?.type
        ?: throw IllegalArgumentException("$location type argument $index must not be star-projected")
}

internal fun KotlinType.tupleElements(location: String): List<KotlinType> {
    val name = (classifier as? KotlinClassClassifier)?.name
    if (name == "dev.akif.tapik.Tuple0") return emptyList()
    require(name != null && TUPLE_CLASSIFIER.matches(name)) {
        "$location must be a Tapik tuple, but was '$classifier'"
    }
    return arguments.drop(1).mapIndexed { index, projection ->
        (projection as? KotlinTypedProjection)?.type
            ?: throw IllegalArgumentException("$location element ${index + 1} must not be star-projected")
    }
}

internal fun String.isKotlinIdentifier(): Boolean =
    isNotEmpty() &&
        first().let { character -> character == '_' || character.isLetter() } &&
        drop(1).all { character -> character == '_' || character.isLetterOrDigit() } &&
        this !in KOTLIN_KEYWORDS

internal fun String.kotlinIdentifier(fallback: String): String {
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

internal fun String.kotlinReferenceIdentifier(): String =
    if (isKotlinIdentifier()) this else "`$this`"

internal fun String.upperCamel(): String =
    kotlinIdentifier("Value")
        .removeSurrounding("`")
        .replaceFirstChar(Char::uppercaseChar)

internal fun String.lowerCamel(fallback: String): String =
    kotlinIdentifier(fallback)
        .removeSurrounding("`")
        .replaceFirstChar(Char::lowercaseChar)
        .let { value -> if (value in KOTLIN_KEYWORDS) "`$value`" else value }

internal fun String.kotlinString(): String =
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

private val TUPLE_CLASSIFIER: Regex = Regex("dev\\.akif\\.tapik\\.Tuple[1-8]")

private val KOTLIN_KEYWORDS: Set<String> =
    setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface",
        "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias",
        "typeof", "val", "var", "when", "while"
    )
