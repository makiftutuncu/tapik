package dev.akif.tapik.plugin.maven

import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.jetbrains.kotlin.maven.KotlinMavenPluginExtension
import org.jetbrains.kotlin.maven.PluginOption

/** Activates Tapik's host-neutral compiler plugin from Kotlin's Maven plugin. */
class TapikKotlinMavenPluginExtension : KotlinMavenPluginExtension {
    override fun isApplicable(
        project: MavenProject,
        execution: MojoExecution
    ): Boolean = true

    override fun getCompilerPluginId(): String = "dev.akif.tapik"

    override fun getPluginOptions(
        project: MavenProject,
        execution: MojoExecution
    ): List<PluginOption> = emptyList()
}
