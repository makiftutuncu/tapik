package dev.akif.tapik.plugin.gradle

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/** Entry point for tapik's Gradle integration. */
class TapikGradlePlugin : KotlinCompilerPluginSupportPlugin {
    /** Applies tapik to [target]. */
    override fun apply(target: Project) = Unit

    /** Returns whether the tapik compiler plugin supports [kotlinCompilation]. */
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.platformType == KotlinPlatformType.jvm

    /** Attaches tapik to [kotlinCompilation] without user-facing compiler options. */
    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ) = kotlinCompilation.target.project.provider<List<SubpluginOption>> { emptyList() }

    /** Kotlin compiler plugin ID used by tapik. */
    override fun getCompilerPluginId(): String = "dev.akif.tapik"

    /** Compiler plugin artifact matching this Gradle plugin's version. */
    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "dev.akif",
            artifactId = "tapik-plugin-compiler",
            version = tapikVersion
        )
}
