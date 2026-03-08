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
 * @param generatorConfigurations Map of generator id to its generation configuration.
 * If a generator is enabled, it must be present in this map.
 */
data class GenerateTaskConfiguration(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val basePackage: String,
    val compiledClassesDirectory: File,
    val additionalClasspathDirectories: List<File>,
    val generatorConfigurations: Map<String, GeneratorConfiguration>
)

/**
 * Generator-specific configuration for [GenerateTask].
 *
 * @param optimizeImports Whether generated Kotlin sources for this generator should be post-processed
 * to optimize imports.
 * @param namePrefix Optional prefix for generated type names.
 * @param nameSuffix Optional suffix for generated type names.
 */
data class GeneratorConfiguration(
    val optimizeImports: Boolean,
    val namePrefix: String?,
    val nameSuffix: String?
)
