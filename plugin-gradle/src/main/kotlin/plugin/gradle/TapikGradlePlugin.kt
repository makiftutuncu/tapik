package dev.akif.tapik.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/** Entry point for tapik's Gradle integration. */
class TapikGradlePlugin : Plugin<Project> {
    /** Applies tapik to [target]. */
    override fun apply(target: Project) = Unit
}
