package dev.akif.tapik.plugin.maven

import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.jetbrains.kotlin.maven.KotlinMavenPluginExtension
import org.jetbrains.kotlin.maven.PluginOption

/** Activates tapik's host-neutral compiler plugin from Kotlin's Maven plugin. */
class TapikKotlinMavenPluginExtension : KotlinMavenPluginExtension {
    /** Applies the tapik compiler extension to every Kotlin Maven execution. */
    override fun isApplicable(
        project: MavenProject,
        execution: MojoExecution
    ): Boolean = true

    /** Returns the compiler plug-in ID recognized by Kotlin. */
    override fun getCompilerPluginId(): String = "dev.akif.tapik"

    /** Returns no options because registry generation uses compiler defaults. */
    override fun getPluginOptions(
        project: MavenProject,
        execution: MojoExecution
    ): List<PluginOption> = emptyList()
}
