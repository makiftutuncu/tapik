package dev.akif.tapik.common.plugin

internal fun String.kotlinCodeMask(): BooleanArray {
    val code = BooleanArray(length)
    var index = 0
    while (index < length) {
        when {
            startsWith("//", index) -> index = lineCommentEnd(index + 2)
            startsWith("/*", index) -> index = blockCommentEnd(index + 2)
            startsWith("\"\"\"", index) -> index = rawStringEnd(index + 3)
            this[index] == '"' -> index = quotedEnd(index + 1, '"')
            this[index] == '\'' -> index = quotedEnd(index + 1, '\'')
            else -> {
                code[index] = true
                index++
            }
        }
    }
    return code
}

private fun String.lineCommentEnd(start: Int): Int {
    val newline = indexOf('\n', start)
    return if (newline < 0) length else newline
}

private fun String.blockCommentEnd(start: Int): Int {
    var depth = 1
    var index = start
    while (index < length && depth > 0) {
        when {
            startsWith("/*", index) -> {
                depth++
                index += 2
            }
            startsWith("*/", index) -> {
                depth--
                index += 2
            }
            else -> index++
        }
    }
    return index
}

private fun String.rawStringEnd(start: Int): Int {
    val end = indexOf("\"\"\"", start)
    return if (end < 0) length else end + 3
}

private fun String.quotedEnd(
    start: Int,
    delimiter: Char
): Int {
    var escaped = false
    var index = start
    while (index < length) {
        val character = this[index++]
        when {
            escaped -> escaped = false
            character == '\\' -> escaped = true
            character == delimiter -> return index
        }
    }
    return index
}

internal fun BooleanArray.containsOnlyCode(range: IntRange): Boolean =
    range.all { index -> this[index] }
