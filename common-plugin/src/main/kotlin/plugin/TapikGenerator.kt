package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import java.io.File

/**
 * Marker contract implemented by Tapik generators loaded at runtime.
 */
interface TapikGenerator {
    /**
     * Unique identifier reported to Tapik when registering this generator.
     *
     * @return generator identifier.
     */
    val id: String
}

/**
 * Contract implemented by generators that directly emit artefacts during invocation.
 */
interface TapikDirectGenerator : TapikGenerator {
    /**
     * Generates artefacts for the provided endpoints.
     *
     * @param endpoints endpoints discovered during bytecode scanning.
     * @param context contextual information and utilities required by generators.
     * @return files generated during this invocation.
     */
    fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): Set<File>
}

/**
 * Carries directories and logging utilities that Tapik generators rely on.
 *
 * @property outputDirectory directory used for non-source outputs (reports, documentation).
 * @property generatedSourcesDirectory directory where generated Kotlin sources should be written.
 * @property generatedPackageName package segment appended to source packages for generated Kotlin sources.
 * @property endpointsSuffix suffix appended to the source-level enclosing endpoints interface.
 * @property logger logger used for generation progress and diagnostics.
 * @property generatorConfiguration configuration for the current generator invocation.
 */
data class TapikGeneratorContext(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val generatedPackageName: String = "generated",
    val endpointsSuffix: String = "Endpoints",
    val logger: TapikLogger,
    val generatorConfiguration: GeneratorConfiguration
)
