package dev.akif.tapik.plugin.maven

import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetCatalog
import java.util.ServiceConfigurationError

internal object MavenTargets {
    fun requireTarget(
        targetId: String,
        pluginClassLoader: ClassLoader,
        projectClassLoader: ClassLoader
    ): List<GenerationTarget> {
        val pluginTargets = load(pluginClassLoader, "Maven plugin")
        if (pluginTargets.any { target -> target.id == targetId }) {
            return pluginTargets
        }

        val projectTargets = load(projectClassLoader, "project")
        require(projectTargets.none { target -> target.id == targetId }) {
            "Generation target '$targetId' was found on the project classpath, but generation targets must be " +
                "declared as tapik-plugin-maven plugin dependencies."
        }

        val available = pluginTargets.joinToString { target -> target.id }.ifEmpty { "none" }
        throw IllegalArgumentException(
            "Unknown generation target '$targetId'. Available targets: $available. " +
                "Custom generation targets must be declared as tapik-plugin-maven plugin dependencies."
        )
    }

    private fun load(
        classLoader: ClassLoader,
        classpathSide: String
    ): List<GenerationTarget> =
        try {
            GenerationTargetCatalog.load(classLoader).targets
        } catch (cause: ServiceConfigurationError) {
            throw loadingFailure(classpathSide, cause)
        } catch (cause: LinkageError) {
            throw loadingFailure(classpathSide, cause)
        }

    private fun loadingFailure(
        classpathSide: String,
        cause: Throwable
    ): IllegalStateException =
        IllegalStateException(
            "Failed to load generation targets from the $classpathSide classpath. " +
                "Ensure custom targets are placed in Maven plugin dependencies and use compatible tapik versions.",
            cause
        )
}
