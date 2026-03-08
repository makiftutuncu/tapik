package dev.akif.tapik.plugin

import java.io.File

/**
 * Optimizes imports in generated Kotlin source files by shortening fully-qualified references where safe.
 */
object KotlinGeneratedSourceImportOptimizer {
    private val packageRoots =
        setOf(
            "dev",
            "org",
            "java",
            "javax",
            "kotlin",
            "arrow",
            "com",
            "io",
            "net"
        )

    /**
     * Optimizes imports in specific [files].
     *
     * @param files Kotlin source files to optimize.
     * @param logDebug optional debug logger.
     * @param logWarn optional warning logger.
     */
    fun optimizeFiles(
        files: Iterable<File>,
        logDebug: (String) -> Unit = {},
        logWarn: (String, Throwable?) -> Unit = { _, _ -> }
    ) {
        files.forEach { file ->
            runCatching {
                optimizeFile(file)
            }.onSuccess { changed ->
                if (changed) {
                    logDebug("Optimized imports in ${file.absolutePath}")
                }
            }.onFailure { error ->
                logWarn("Failed to optimize imports in ${file.absolutePath}", error)
            }
        }
    }

    private fun optimizeFile(file: File): Boolean {
        val original = file.readText().replace("\r\n", "\n")
        val optimized = optimizeContent(original)
        if (optimized == original) {
            return false
        }
        file.writeText(optimized)
        return true
    }

    internal fun optimizeContent(content: String): String {
        val sections = KotlinFileSections.parse(content) ?: return content
        val qualifiedOccurrences = collectQualifiedOccurrences(sections.body)

        val simpleNameToPaths = mutableMapOf<String, MutableSet<String>>()
        sections.imports
            .filter { it.alias == null }
            .forEach { importSpec ->
                simpleNameToPaths.getOrPut(importSpec.simpleName()) { linkedSetOf() } += importSpec.path
            }
        qualifiedOccurrences.forEach { occurrence ->
            simpleNameToPaths.getOrPut(occurrence.importSimpleName) { linkedSetOf() } += occurrence.importPath
        }

        val collidingPaths =
            simpleNameToPaths
                .filterValues { it.size > 1 }
                .values
                .flatten()
                .toSet()

        val applicableOccurrences =
            qualifiedOccurrences.filterNot { it.importPath in collidingPaths }

        val rewrittenBody =
            applicableOccurrences
                .sortedByDescending { it.start }
                .fold(sections.body) { current, occurrence ->
                    current.replaceRange(occurrence.start, occurrence.endExclusive, occurrence.replacement)
                }

        val bodyIdentifiers = collectIdentifiers(rewrittenBody)

        val keptExistingImports =
            sections.imports.filter { importSpec ->
                when {
                    !importSpec.shouldBeImportedForPackage(sections.packageName) -> false
                    importSpec.path.endsWith(".*") -> true
                    importSpec.alias != null -> importSpec.alias in bodyIdentifiers
                    else -> importSpec.simpleName() in bodyIdentifiers
                }
            }

        val newImports =
            applicableOccurrences
                .asSequence()
                .filter { it.requiresImport }
                .map { ImportSpec(path = it.importPath) }
                .filter { it.shouldBeImportedForPackage(sections.packageName) }
                .toSet()

        val mergedImports =
            (keptExistingImports + newImports)
                .distinctBy { it.render() }
                .sortedBy { it.render() }

        return sections.render(rewrittenBody, mergedImports)
    }

    private fun collectQualifiedOccurrences(content: String): List<QualifiedOccurrence> {
        val occurrences = mutableListOf<QualifiedOccurrence>()
        scanCode(content) { index, text ->
            if (index > 0) {
                val previousChar = text[index - 1]
                if (previousChar == '.' || isIdentifierPart(previousChar)) {
                    return@scanCode
                }
            }

            parseQualifiedReferenceAt(text, index)?.let { parsed ->
                if (parsed.segments.size < 2 || parsed.segments.first() !in packageRoots) {
                    return@let
                }

                val previousSegment = parsed.segments.getOrNull(parsed.segments.lastIndex - 1)
                val isConstantMember =
                    parsed.segments.last().looksLikeConstantMember() &&
                    previousSegment?.firstOrNull()?.isUpperCase() == true
                val importPath =
                    if (isConstantMember) {
                        parsed.segments.dropLast(1).joinToString(".")
                    } else {
                        parsed.segments.joinToString(".")
                    }
                val importSimpleName = importPath.substringAfterLast('.')
                val replacement =
                    if (isConstantMember) {
                        "$importSimpleName.${parsed.segments.last()}"
                    } else {
                        importSimpleName
                    }
                val requiresImport = !importPath.startsWith("kotlin.") && !importPath.startsWith("java.lang.")

                occurrences +=
                    QualifiedOccurrence(
                        start = parsed.start,
                        endExclusive = parsed.endExclusive,
                        importPath = importPath,
                        importSimpleName = importSimpleName,
                        replacement = replacement,
                        requiresImport = requiresImport
                    )
            }
        }
        return occurrences
    }

    private fun collectIdentifiers(content: String): Set<String> {
        val identifiers = linkedSetOf<String>()
        scanCode(content) { index, text ->
            parseIdentifierAt(text, index)?.let { parsed ->
                identifiers += parsed.value
            }
        }
        return identifiers
    }

    private fun scanCode(
        content: String,
        onCodeIndex: (Int, String) -> Unit
    ) {
        var index = 0
        var state = LexState.Code
        while (index < content.length) {
            when (state) {
                LexState.Code -> {
                    when {
                        content.startsWith("//", index) -> {
                            state = LexState.LineComment
                            index += 2
                        }
                        content.startsWith("/*", index) -> {
                            state = LexState.BlockComment
                            index += 2
                        }
                        content.startsWith("\"\"\"", index) -> {
                            state = LexState.TripleQuotedString
                            index += 3
                        }
                        content[index] == '"' -> {
                            state = LexState.String
                            index++
                        }
                        content[index] == '\'' -> {
                            state = LexState.Char
                            index++
                        }
                        isIdentifierStart(content[index]) -> {
                            onCodeIndex(index, content)
                            val parsed = parseIdentifierAt(content, index)
                            index = parsed?.endExclusive ?: (index + 1)
                        }
                        else -> {
                            index++
                        }
                    }
                }
                LexState.LineComment -> {
                    if (content[index] == '\n') {
                        state = LexState.Code
                    }
                    index++
                }
                LexState.BlockComment -> {
                    if (content.startsWith("*/", index)) {
                        state = LexState.Code
                        index += 2
                    } else {
                        index++
                    }
                }
                LexState.String -> {
                    if (content[index] == '\\') {
                        index += 2
                    } else {
                        if (content[index] == '"') {
                            state = LexState.Code
                        }
                        index++
                    }
                }
                LexState.TripleQuotedString -> {
                    if (content.startsWith("\"\"\"", index)) {
                        state = LexState.Code
                        index += 3
                    } else {
                        index++
                    }
                }
                LexState.Char -> {
                    if (content[index] == '\\') {
                        index += 2
                    } else {
                        if (content[index] == '\'') {
                            state = LexState.Code
                        }
                        index++
                    }
                }
            }
        }
    }

    private fun parseQualifiedReferenceAt(
        content: String,
        startIndex: Int
    ): ParsedQualifiedReference? {
        var index = startIndex
        val first = parseIdentifierAt(content, index) ?: return null
        val segments = mutableListOf(first.value)
        index = first.endExclusive

        while (index < content.length && content[index] == '.') {
            val next = parseIdentifierAt(content, index + 1) ?: break
            segments += next.value
            index = next.endExclusive
        }

        if (segments.size < 2) {
            return null
        }

        return ParsedQualifiedReference(
            start = startIndex,
            endExclusive = index,
            segments = segments
        )
    }

    private fun parseIdentifierAt(
        content: String,
        startIndex: Int
    ): ParsedIdentifier? {
        if (startIndex !in content.indices || !isIdentifierStart(content[startIndex])) {
            return null
        }

        var index = startIndex + 1
        while (index < content.length && isIdentifierPart(content[index])) {
            index++
        }

        return ParsedIdentifier(
            value = content.substring(startIndex, index),
            endExclusive = index
        )
    }

    private fun isIdentifierStart(char: Char): Boolean = char == '_' || char.isLetter()

    private fun isIdentifierPart(char: Char): Boolean = isIdentifierStart(char) || char.isDigit()

    private fun String.looksLikeConstantMember(): Boolean = any(Char::isLetter) && all { it.isUpperCase() || it.isDigit() || it == '_' }

    private data class QualifiedOccurrence(
        val start: Int,
        val endExclusive: Int,
        val importPath: String,
        val importSimpleName: String,
        val replacement: String,
        val requiresImport: Boolean
    )

    private data class ParsedIdentifier(
        val value: String,
        val endExclusive: Int
    )

    private data class ParsedQualifiedReference(
        val start: Int,
        val endExclusive: Int,
        val segments: List<String>
    )

    private enum class LexState {
        Code,
        LineComment,
        BlockComment,
        String,
        TripleQuotedString,
        Char
    }

    private data class ImportSpec(
        val path: String,
        val alias: String? = null
    ) {
        fun simpleName(): String = path.substringAfterLast('.')

        fun shouldBeImportedForPackage(packageName: String): Boolean {
            if (packageName.isBlank()) {
                return true
            }

            val importPackage =
                if (path.endsWith(".*")) {
                    path.removeSuffix(".*")
                } else {
                    path.substringBeforeLast('.', "")
                }

            return importPackage != packageName
        }

        fun render(): String =
            if (alias == null) {
                "import $path"
            } else {
                "import $path as $alias"
            }

        companion object {
            fun parse(line: String): ImportSpec? {
                val trimmed = line.trim()
                if (!trimmed.startsWith("import ")) {
                    return null
                }

                val payload = trimmed.removePrefix("import ").trim()
                val aliasDelimiter = payload.indexOf(" as ")

                return if (aliasDelimiter == -1) {
                    ImportSpec(path = payload)
                } else {
                    val path = payload.substring(0, aliasDelimiter).trim()
                    val alias = payload.substring(aliasDelimiter + 4).trim()
                    if (path.isEmpty() || alias.isEmpty()) {
                        null
                    } else {
                        ImportSpec(path = path, alias = alias)
                    }
                }
            }
        }
    }

    private data class KotlinFileSections(
        val headerLines: List<String>,
        val packageLine: String,
        val packageName: String,
        val imports: List<ImportSpec>,
        val body: String
    ) {
        fun render(
            rewrittenBody: String,
            rewrittenImports: List<ImportSpec>
        ): String =
            buildString {
                headerLines.forEach { appendLine(it) }
                appendLine(packageLine)
                appendLine()
                rewrittenImports.forEach { appendLine(it.render()) }
                if (rewrittenImports.isNotEmpty()) {
                    appendLine()
                }
                append(rewrittenBody.trimEnd('\n'))
                appendLine()
            }

        companion object {
            fun parse(content: String): KotlinFileSections? {
                val lines = content.split('\n')
                val packageIndex = lines.indexOfFirst { it.trimStart().startsWith("package ") }
                if (packageIndex == -1) {
                    return null
                }

                val headerLines = lines.take(packageIndex)
                val packageLine = lines[packageIndex].trimEnd()
                val packageName = packageLine.removePrefix("package ").trim()

                var index = packageIndex + 1
                while (index < lines.size && lines[index].isBlank()) {
                    index++
                }

                val imports = mutableListOf<ImportSpec>()
                while (index < lines.size && lines[index].trimStart().startsWith("import ")) {
                    ImportSpec.parse(lines[index])?.let { imports += it }
                    index++
                }

                while (index < lines.size && lines[index].isBlank()) {
                    index++
                }

                val body = lines.drop(index).joinToString("\n")

                return KotlinFileSections(
                    headerLines = headerLines.map { it.trimEnd() },
                    packageLine = packageLine,
                    packageName = packageName,
                    imports = imports,
                    body = body
                )
            }
        }
    }
}
