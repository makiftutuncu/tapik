package dev.akif.tapik.plugin

import java.io.File

/**
 * Configuration for the [GenerateTask].
 *
 * @param outputDirectory The directory to which the report is written.
 * @param generatedSourcesDirectory The directory to which the generated sources are written.
 * @param endpointPackages The packages to scan for endpoint definitions.
 * @param compiledClassesDirectory The directory containing the compiled classes of the project.
 * @param additionalClasspathDirectories Additional directories to add to the classpath when scanning for endpoints.
 */
data class GenerateTaskConfiguration(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val endpointPackages: List<String>,
    val compiledClassesDirectory: File,
    val additionalClasspathDirectories: List<File>
)
