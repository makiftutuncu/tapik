package dev.akif.tapik.common.plugin

/**
 * Returns this generated Kotlin source with unambiguous qualified references shortened and imports ordered.
 *
 * Existing explicit imports are retained. References in [packageName] and Kotlin/JVM default-import packages do not
 * produce imports. Conflicting references remain qualified, and literals and comments are preserved verbatim.
 */
fun String.optimizeKotlinImports(packageName: String): String {
    require(packageName.isNotBlank()) { "Generated Kotlin package name must not be blank" }
    val existingImports = IMPORT_LINE.findAll(this).map(MatchResult::kotlinImport).toList()
    val sourceWithoutImports = IMPORT_LINE.replace(this, "")
    val code = sourceWithoutImports.kotlinCodeMask()
    val declaredNames = sourceWithoutImports.declaredNames(code)
    val references = sourceWithoutImports.qualifiedReferences(code)
    val winners = importWinners(references, existingImports, declaredNames, packageName)
    val newImports =
        winners.values
            .filterNotNull()
            .filterNot { declaration -> declaration.isDefaultImported() || declaration.isInPackage(packageName) }
            .filterNot { declaration -> existingImports.any { imported -> imported.qualifiedName == declaration } }
            .map(::KotlinImport)
    val imports = (existingImports + newImports).distinct().sortedBy(KotlinImport::render)
    val optimized =
        QUALIFIED_REFERENCE.replace(sourceWithoutImports) { match ->
            if (!code.containsOnlyCode(match.range)) return@replace match.value
            val reference = match.qualifiedReference() ?: return@replace match.value
            if (winners[reference.simpleName] != reference.declaration) {
                match.value
            } else {
                existingImports
                    .singleOrNull { imported -> imported.qualifiedName == reference.declaration }
                    ?.alias
                    ?.plus(reference.suffix)
                    ?: reference.shortened
            }
        }
    return optimized.withImports(imports)
}

private data class KotlinImport(
    val qualifiedName: String,
    val alias: String? = null
) {
    val exposedName: String?
        get() = alias ?: qualifiedName.substringAfterLast('.').takeUnless { name -> name == "*" }

    fun render(): String =
        if (alias == null) qualifiedName else "$qualifiedName as $alias"
}

private data class QualifiedReference(
    val declaration: String,
    val simpleName: String,
    val suffix: String
) {
    val shortened: String
        get() = simpleName + suffix
}

private fun MatchResult.kotlinImport(): KotlinImport =
    KotlinImport(
        qualifiedName = groupValues[1],
        alias = groupValues[2].ifEmpty { null }
    )

private fun MatchResult.qualifiedReference(): QualifiedReference? {
    val parts = value.split('.')
    val declarationIndex = parts.indexOfFirst { part -> part.firstOrNull()?.isUpperCase() == true }
        .takeIf { index -> index >= 0 }
        ?: parts.lastIndex.takeIf { value.startsWith("kotlin.") }
        ?: return null
    if (declarationIndex == 0) return null
    val declaration = parts.take(declarationIndex + 1).joinToString(".")
    val suffix = parts.drop(declarationIndex + 1).joinToString(separator = ".", prefix = ".").takeUnless {
        it == "."
    }.orEmpty()
    return QualifiedReference(
        declaration = declaration,
        simpleName = parts[declarationIndex],
        suffix = suffix
    )
}

private fun String.declaredNames(code: BooleanArray): Set<String> =
    sequenceOf(TYPE_DECLARATION, FUNCTION_DECLARATION)
        .flatMap { pattern -> pattern.findAll(this) }
        .filter { match -> code.containsOnlyCode(match.range) }
        .map { match -> match.groupValues[1].removeSurrounding("`") }
        .toSet()

private fun String.qualifiedReferences(code: BooleanArray): List<QualifiedReference> =
    QUALIFIED_REFERENCE.findAll(this)
        .filter { match -> code.containsOnlyCode(match.range) }
        .mapNotNull(MatchResult::qualifiedReference)
        .toList()

private fun importWinners(
    references: List<QualifiedReference>,
    existingImports: List<KotlinImport>,
    declaredNames: Set<String>,
    packageName: String
): Map<String, String?> =
    references.groupBy(QualifiedReference::simpleName).mapValues { (simpleName, namedReferences) ->
        val declarations = namedReferences.map(QualifiedReference::declaration).distinct()
        val importsWithName = existingImports.filter { imported -> imported.exposedName == simpleName }
        val samePackage = declarations.filter { declaration -> declaration.isInPackage(packageName) }
        val explicitlyImported = declarations.filter { declaration ->
            importsWithName.any { imported -> imported.qualifiedName == declaration }
        }
        val defaultImported = declarations.filter(String::isDefaultImported)
        when {
            simpleName in declaredNames -> samePackage.singleOrNull()
            importsWithName.isNotEmpty() -> explicitlyImported.singleOrNull()
            samePackage.size == 1 -> samePackage.single()
            defaultImported.size == 1 -> defaultImported.single()
            declarations.size == 1 -> declarations.single()
            else -> null
        }
    }

private fun String.isInPackage(packageName: String): Boolean = substringBeforeLast('.', "") == packageName

private fun String.isDefaultImported(): Boolean =
    substringBeforeLast('.', "") in DEFAULT_IMPORT_PACKAGES

private fun String.withImports(imports: List<KotlinImport>): String {
    val packageDirective = PACKAGE_DIRECTIVE.find(this)
        ?: throw IllegalArgumentException("Generated Kotlin source must contain a package directive")
    val prefix = substring(0, packageDirective.range.last + 1).trimEnd()
    val body = substring(packageDirective.range.last + 1).trimStart('\r', '\n')
    return buildString {
        appendLine(prefix)
        appendLine()
        imports.forEach { imported -> appendLine("import ${imported.render()}") }
        if (imports.isNotEmpty()) appendLine()
        append(body)
    }
}

private val IMPORT_LINE: Regex =
    Regex("(?m)^[ \\t]*import[ \\t]+([^ \\t\\r\\n]+)(?:[ \\t]+as[ \\t]+([^ \\t\\r\\n]+))?[ \\t]*(?:\\r?\\n|$)")

private val PACKAGE_DIRECTIVE: Regex = Regex("(?m)^[ \\t]*package[ \\t]+[^\\r\\n]+")

private val TYPE_DECLARATION: Regex =
    Regex("\\b(?:class|interface|object|typealias)[ \\t]+(`?[A-Za-z_][A-Za-z0-9_]*`?)")

private val FUNCTION_DECLARATION: Regex =
    Regex("\\bfun[ \\t]+(?:<[^>\\r\\n]+>[ \\t]+)?(`?[A-Za-z_][A-Za-z0-9_]*`?)")

private val QUALIFIED_REFERENCE: Regex =
    Regex(
        "(?<![A-Za-z0-9_])(?:" +
            "(?:[a-z_][A-Za-z0-9_]*\\.)+[A-Z][A-Za-z0-9_]*(?:\\.[A-Za-z_][A-Za-z0-9_]*)*" +
            "|kotlin(?:\\.(?:annotation|collections|comparisons|io|ranges|sequences|text|jvm))?\\.[a-z_][A-Za-z0-9_]*" +
            ")"
    )

private val DEFAULT_IMPORT_PACKAGES: Set<String> =
    setOf(
        "java.lang",
        "kotlin",
        "kotlin.annotation",
        "kotlin.collections",
        "kotlin.comparisons",
        "kotlin.io",
        "kotlin.jvm",
        "kotlin.ranges",
        "kotlin.sequences",
        "kotlin.text"
    )
