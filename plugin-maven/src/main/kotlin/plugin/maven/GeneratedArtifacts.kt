package dev.akif.tapik.plugin.maven

import org.apache.maven.model.Resource
import org.apache.maven.project.MavenProject
import java.nio.file.Path

internal fun MavenProject.registerGeneratedArtifacts(
    outputDirectory: Path,
    generation: MavenGeneration
) {
    val directory = outputDirectory.toAbsolutePath().normalize().toString()
    if (generation.containsSources) addCompileSourceRoot(directory)
    if (generation.resourcePaths.isNotEmpty()) {
        addResource(
            Resource().apply {
                this.directory = directory
                includes = generation.resourcePaths
            }
        )
    }
}
