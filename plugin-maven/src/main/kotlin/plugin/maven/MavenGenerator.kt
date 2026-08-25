package dev.akif.tapik.plugin.maven

import dev.akif.tapik.common.plugin.ArtifactWriter
import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GenerationEngine
import dev.akif.tapik.common.plugin.ScalarConfigurationValue
import dev.akif.tapik.common.plugin.TargetConfiguration
import java.nio.file.Path

internal class MavenGenerator {
    fun generate(
        classpath: List<Path>,
        targetId: String,
        targetConfiguration: Map<String, String>,
        outputDirectory: Path,
        executionId: String,
        parentClassLoader: ClassLoader,
        pluginVersion: String,
        projectTapikVersions: Set<String>
    ): MavenGeneration {
        TapikVersionCompatibility.requireCompatible(pluginVersion, projectTapikVersions)
        return ProjectApis.use(classpath, parentClassLoader) { apis, projectClassLoader ->
            val configuration =
                TargetConfiguration(
                    targetConfiguration.mapValues { (_, value) -> ScalarConfigurationValue(value) }
                )
            val targets = MavenTargets.requireTarget(targetId, parentClassLoader, projectClassLoader)
            val result = GenerationEngine(targets).generate(targetId, apis, configuration)
            MavenGeneration(
                written = ArtifactWriter.write(result, outputDirectory, owner = executionId),
                containsSources = result.artifacts.any { artifact -> artifact.kind == ArtifactKind.SOURCE }
            )
        }
    }
}

internal data class MavenGeneration(
    val written: List<Path>,
    val containsSources: Boolean
)
