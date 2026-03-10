package dev.akif.tapik.plugin

import java.io.File

/**
 * Configuration for the [GenerateTask].
 *
 * @param outputDirectory The directory to which the report is written.
 * @param generatedSourcesDirectory The directory to which the generated sources are written.
 * @param generatedPackageName package segment appended to source packages for generated Kotlin sources.
 * @param endpointsSuffix suffix appended to the source-level enclosing endpoints interface.
 * @param basePackage The base package to scan for endpoint definitions.
 * @param compiledClassesDirectory The directory containing the compiled classes of the project.
 * @param additionalClasspathDirectories Additional directories to add to the classpath when scanning for endpoints.
 * @param generatorConfigurations Map of generator id to its generation configuration.
 * If a generator is enabled, it must be present in this map.
 */
data class GenerateTaskConfiguration(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val generatedPackageName: String = "generated",
    val endpointsSuffix: String = "Endpoints",
    val basePackage: String,
    val compiledClassesDirectory: File,
    val additionalClasspathDirectories: List<File>,
    val generatorConfigurations: Map<String, GeneratorConfiguration>
)

/**
 * Generator-specific configuration for [GenerateTask].
 *
 * @param clientSuffix suffix appended to aggregate client interface names and nested client contracts.
 * @param serverSuffix suffix appended to aggregate server interface names and nested server contracts.
 */
data class GeneratorConfiguration(
    val clientSuffix: String = "Client",
    val serverSuffix: String = "Server"
)
