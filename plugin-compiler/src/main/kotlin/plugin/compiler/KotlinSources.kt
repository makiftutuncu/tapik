package dev.akif.tapik.plugin.compiler

import java.nio.file.Files
import java.nio.file.Path

internal fun activeKotlinSources(sourceRoots: List<Path>): Set<Path> =
    sourceRoots.flatMapTo(linkedSetOf()) { root ->
        val normalized = root.toAbsolutePath().normalize()
        when {
            Files.isRegularFile(normalized) && normalized.isKotlinSource() -> listOf(normalized)
            Files.isDirectory(normalized) ->
                Files.walk(normalized).use { paths ->
                    paths.filter(Files::isRegularFile).filter(Path::isKotlinSource).toList()
                }
            else -> emptyList()
        }
    }

private fun Path.isKotlinSource(): Boolean =
    fileName.toString().let { name -> name.endsWith(".kt") || name.endsWith(".kts") }
