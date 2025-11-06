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
     */
    fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    )
}

/**
 * Carries directories and logging utilities that Tapik generators rely on.
 *
 * @param outputDirectory directory used for non-source outputs (reports, documentation).
 * @param generatedSourcesDirectory directory where generated Kotlin sources should be written.
 * @param log info-level logger.
 * @param logDebug debug-level logger.
 * @param logWarn warning-level logger that accepts an optional [Throwable].
 *
 * @property outputDirectory directory used for non-source outputs (reports, documentation).
 * @property generatedSourcesDirectory directory where generated Kotlin sources should be written.
 * @property log info-level logger.
 * @property logDebug debug-level logger.
 * @property logWarn warning-level logger that accepts an optional [Throwable].
 */
data class TapikGeneratorContext(
    val outputDirectory: File,
    val generatedSourcesDirectory: File,
    val log: (String) -> Unit,
    val logDebug: (String) -> Unit,
    val logWarn: (String, Throwable?) -> Unit
)
