package dev.akif.tapik.plugin.maven

import dev.akif.tapik.plugin.core.ArtifactWriter
import dev.akif.tapik.plugin.core.GenerationEngine
import dev.akif.tapik.plugin.core.GenerationTargetCatalog
import dev.akif.tapik.plugin.core.ScalarConfigurationValue
import dev.akif.tapik.plugin.core.TargetConfiguration
import java.nio.file.Path

internal class MavenGenerator {
    fun generate(
        classpath: List<Path>,
        targetId: String,
        targetConfiguration: Map<String, String>,
        outputDirectory: Path,
        parentClassLoader: ClassLoader
    ): List<Path> =
        ProjectApis.use(classpath, parentClassLoader) { apis ->
            val configuration =
                TargetConfiguration(
                    targetConfiguration.mapValues { (_, value) -> ScalarConfigurationValue(value) }
                )
            val targets = GenerationTargetCatalog.load(parentClassLoader).targets
            val result = GenerationEngine(targets).generate(targetId, apis, configuration)
            ArtifactWriter.write(result, outputDirectory)
        }
}
