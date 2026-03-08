package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import java.io.File

/**
 * Contract implemented by Tapik code generators loaded at runtime.
 */
interface TapikGenerator {
    /**
     * Unique identifier reported to Tapik when registering this generator.
     *
     * @return generator identifier.
     */
    val id: String

    /**
     * Generates artefacts for the provided endpoints.
     *
     * @param endpoints endpoints discovered during bytecode scanning.
     * @param context contextual information and utilities required by generators.
     * @return generation result including generated Kotlin source files.
     */
    fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): TapikGenerationResult
}

/**
 * Result of a [TapikGenerator] invocation.
 *
 * @property generatedSourceFiles Kotlin source files generated during this invocation.
 */
data class TapikGenerationResult(
    val generatedSourceFiles: Set<File> = emptySet()
)

/**
 * Carries directories and logging utilities that Tapik generators rely on.
 *
 * @property outputDirectory directory used for non-source outputs (reports, documentation).
 * @property generatedSourcesDirectory directory where generated Kotlin sources should be written.
 * @property log info-level logger.
 * @property logDebug debug-level logger.
 * @property logWarn warning-level logger that accepts an optional [Throwable].
 * @property generatorConfiguration configuration for the current generator invocation.
 */
data class TapikGeneratorContext(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val log: (String) -> Unit,
    val logDebug: (String) -> Unit,
    val logWarn: (String, Throwable?) -> Unit,
    val generatorConfiguration: GeneratorConfiguration
)
