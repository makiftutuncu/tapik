package dev.akif.tapik

internal data class ParsedMediaType(
    val type: String,
    val subtype: String,
    val parameters: Map<String, String>
) {
    fun render(): String = buildString {
        append(type).append('/').append(subtype)
        parameters.forEach { (name, value) ->
            append(';').append(name).append('=')
            if (value.isNotEmpty() && value.all(Char::isMediaTypeToken)) {
                append(value)
            } else {
                append('"')
                value.forEach { character ->
                    if (character == '"' || character == '\\') append('\\')
                    append(character)
                }
                append('"')
            }
        }
    }
}

internal fun parseMediaType(value: String): ParsedMediaType = MediaTypeParser(value).parse()

private class MediaTypeParser(private val source: String) {
    private var position = 0

    fun parse(): ParsedMediaType {
        skipWhitespace()
        val type = token("type").lowercase()
        requireCharacter('/', "expected '/' between type and subtype")
        val subtype = token("subtype").lowercase()
        check('*' !in type && '*' !in subtype, "wildcards are not concrete media types")
        val parameters = linkedMapOf<String, String>()
        while (true) {
            skipWhitespace()
            if (position == source.length) break
            requireCharacter(';', "expected ';' before a parameter")
            skipWhitespace()
            // HTTP parameters permit empty semicolon groups.
            if (position == source.length || source[position] == ';') continue
            val name = token("parameter name").lowercase()
            check(name !in parameters, "duplicate parameter '$name'")
            requireCharacter('=', "expected '=' immediately after parameter name")
            val value = if (position < source.length && source[position] == '"') quotedValue() else token("parameter value")
            parameters[name] = if (name == "charset") {
                check(value.isNotEmpty() && value.all(Char::isMediaTypeToken), "charset must be a non-empty token")
                value.lowercase()
            } else {
                value
            }
        }
        return ParsedMediaType(type, subtype, parameters.snapshotMap())
    }

    private fun token(description: String): String {
        val start = position
        while (position < source.length && source[position].isMediaTypeToken()) position++
        check(position > start, "expected $description token")
        return source.substring(start, position)
    }

    private fun quotedValue(): String = buildString {
        position++
        while (position < source.length) {
            val character = source[position++]
            when (character) {
                '"' -> return@buildString
                '\\' -> {
                    check(position < source.length, "unterminated quoted escape")
                    val escaped = source[position++]
                    check(escaped.isQuotedOctet(), "invalid escaped character")
                    append(escaped)
                }
                else -> {
                    check(character.isQuotedOctet(), "invalid quoted character")
                    append(character)
                }
            }
        }
        invalid("unterminated quoted value")
    }

    private fun skipWhitespace() {
        while (position < source.length && (source[position] == ' ' || source[position] == '\t')) position++
    }

    private fun requireCharacter(expected: Char, reason: String) {
        check(position < source.length && source[position] == expected, reason)
        position++
    }

    private fun check(valid: Boolean, reason: String) {
        if (!valid) invalid(reason)
    }

    private fun invalid(reason: String): Nothing =
        throw IllegalArgumentException("Media type is invalid at index $position: $reason")
}

private fun Char.isMediaTypeToken(): Boolean =
    this in 'a'..'z' || this in 'A'..'Z' || this in '0'..'9' || this in "!#$%&'*+-.^_`|~"

private fun Char.isQuotedOctet(): Boolean = this == '\t' || this in ' '..'~' || this in '\u0080'..'\u00ff'
