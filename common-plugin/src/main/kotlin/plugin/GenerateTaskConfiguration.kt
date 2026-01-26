package dev.akif.tapik.plugin

import java.io.File

/**
 * Configuration for the [GenerateTask].
 *
 * @param outputDirectory The directory to which the report is written.
 * @param generatedSourcesDirectory The directory to which the generated sources are written.
 * @param basePackage The base package to scan for endpoint definitions.
 * @param compiledClassesDirectory The directory containing the compiled classes of the project.
 * @param additionalClasspathDirectories Additional directories to add to the classpath when scanning for endpoints.
 * @param enabledGeneratorIds Identifiers of code generators that should run.
 */
data class GenerateTaskConfiguration(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val basePackage: String,
    val compiledClassesDirectory: File,
    val additionalClasspathDirectories: List<File>,
    val enabledGeneratorIds: Set<String>
)
