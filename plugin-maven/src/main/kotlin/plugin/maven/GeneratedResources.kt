package dev.akif.tapik.plugin.maven

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

internal fun copyGeneratedResources(
    generationDirectory: Path,
    resourcePaths: List<String>,
    outputDirectory: Path
) {
    resourcePaths.forEach { relativePath ->
        val source = generationDirectory.resolve(relativePath)
        val destination = outputDirectory.resolve(relativePath)
        destination.parent?.let(Files::createDirectories)
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING)
    }
}
