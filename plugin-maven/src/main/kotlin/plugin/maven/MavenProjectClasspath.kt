package dev.akif.tapik.plugin.maven

import org.apache.maven.project.MavenProject
import java.nio.file.Path

internal fun MavenProject.generationClasspath(): List<Path> =
    buildList {
        addAll(compileClasspathElements)
        addAll(runtimeClasspathElements)
    }.map { element -> Path.of(element).toAbsolutePath().normalize() }
        .distinct()
