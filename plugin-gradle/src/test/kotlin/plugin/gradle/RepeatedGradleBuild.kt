package dev.akif.tapik.plugin.gradle

import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult

internal data class RepeatedGradleBuild(
    val first: BuildResult,
    val reused: BuildResult
)

internal fun repeatWithConfigurationCache(build: () -> BuildResult): RepeatedGradleBuild {
    val first = build()
    val reused = build()
    reused.output shouldContain "Reusing configuration cache."
    return RepeatedGradleBuild(first, reused)
}
