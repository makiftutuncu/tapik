package dev.akif.tapik.plugin.maven

import dev.akif.tapik.common.plugin.ArtifactWriter
import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.ApiSelection
import dev.akif.tapik.common.plugin.GenerationEngine
import dev.akif.tapik.common.plugin.ScalarConfigurationValue
import dev.akif.tapik.common.plugin.TargetConfiguration
import java.nio.file.Path

internal class MavenGenerator {
    fun generate(
        classpath: List<Path>,
        targetId: String,
        targetConfiguration: Map<String, String>,
        includeApis: Set<String> = emptySet(),
        excludeApis: Set<String> = emptySet(),
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
            val result =
                GenerationEngine(targets).generate(
                    targetId = targetId,
                    apis = apis,
                    configuration = configuration,
                    selection = ApiSelection(includes = includeApis, excludes = excludeApis)
                )
            val written = ArtifactWriter.write(result, outputDirectory, owner = executionId)
            MavenGeneration(
                written = written,
                sourcePaths =
                    result.artifacts
                        .filter { artifact -> artifact.kind == ArtifactKind.SOURCE }
                        .map { artifact -> outputDirectory.resolve(artifact.relativePath).toAbsolutePath().normalize() },
                resourcePaths =
                    result.artifacts
                        .filter { artifact -> artifact.kind == ArtifactKind.RESOURCE }
                        .map { artifact -> artifact.relativePath }
            )
        }
    }
}

internal data class MavenGeneration(
    val written: List<Path>,
    val sourcePaths: List<Path>,
    val resourcePaths: List<String>
) {
    val containsSources: Boolean
        get() = sourcePaths.isNotEmpty()
}
