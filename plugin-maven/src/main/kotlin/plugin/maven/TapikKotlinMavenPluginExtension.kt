package dev.akif.tapik.plugin.maven

import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.jetbrains.kotlin.maven.KotlinMavenPluginExtension
import org.jetbrains.kotlin.maven.PluginOption

/**
 * Activates tapik's compiler plugin through Kotlin's Maven plugin.
 *
 * The extension uses the compiler plugin's default options to register APIs from the current project.
 */
class TapikKotlinMavenPluginExtension : KotlinMavenPluginExtension {
    /**
     * Applies the tapik compiler extension to every Kotlin Maven execution.
     *
     * @param project Maven project being compiled.
     * @param execution Kotlin Maven execution being configured.
     * @return always `true`.
     */
    override fun isApplicable(
        project: MavenProject,
        execution: MojoExecution
    ): Boolean = true

    /** Returns `dev.akif.tapik`, the compiler plugin ID recognized by Kotlin. */
    override fun getCompilerPluginId(): String = "dev.akif.tapik"

    /**
     * Returns no options because registry generation uses compiler defaults.
     *
     * @param project Maven project being compiled.
     * @param execution Kotlin Maven execution being configured.
     * @return an empty option list.
     */
    override fun getPluginOptions(
        project: MavenProject,
        execution: MojoExecution
    ): List<PluginOption> = emptyList()
}
