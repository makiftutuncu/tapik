package dev.akif.tapik.plugin.maven

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecution
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import java.io.File
import java.nio.file.Path

/** Generates artifacts from the Tapik APIs compiled by the current Maven project. */
@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.PROCESS_CLASSES,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    threadSafe = true
)
class GenerateMojo : AbstractMojo() {
    @field:Parameter(defaultValue = "\${project}", readonly = true, required = true)
    private lateinit var project: MavenProject

    @field:Parameter(defaultValue = "\${mojoExecution}", readonly = true, required = true)
    private lateinit var mojoExecution: MojoExecution

    @field:Parameter(property = "tapik.target", required = true)
    private lateinit var target: String

    @field:Parameter(
        property = "tapik.outputDirectory",
        defaultValue = "\${project.build.directory}/generated/tapik",
        required = true
    )
    private lateinit var outputDirectory: File

    @field:Parameter
    private var includeApis: Set<String> = emptySet()

    @field:Parameter
    private var excludeApis: Set<String> = emptySet()

    @field:Parameter
    private var targetConfiguration: Map<String, String> = emptyMap()

    override fun execute() {
        try {
            val generation =
                MavenGenerator().generate(
                    classpath = project.generationClasspath(),
                    targetId = target,
                    targetConfiguration = targetConfiguration,
                    includeApis = includeApis,
                    excludeApis = excludeApis,
                    outputDirectory = outputDirectory.toPath(),
                    executionId = mojoExecution.executionId,
                    parentClassLoader = requireNotNull(javaClass.classLoader),
                    pluginVersion =
                        requireNotNull(mojoExecution.mojoDescriptor.pluginDescriptor.version) {
                            "Tapik Maven plugin version is unavailable"
                        },
                    projectTapikVersions = project.tapikDependencyVersions()
                )
            if (mojoExecution.lifecyclePhase != "generate-sources") {
                val classesDirectory = Path.of(project.build.outputDirectory)
                val buildDirectory = Path.of(project.build.directory)
                val stagingDirectory = buildDirectory.resolve("tapik-generated-classes").resolve(generatedModuleName())
                GeneratedKotlinCompiler.compile(
                    sources = generation.sourcePaths,
                    classpath = project.generationClasspath(),
                    outputDirectory = stagingDirectory,
                    moduleName = generatedModuleName()
                )
                copyGeneratedResources(outputDirectory.toPath(), generation.resourcePaths, stagingDirectory)
                GeneratedOutputSynchronizer.synchronize(
                    stagingDirectory = stagingDirectory,
                    outputDirectory = classesDirectory,
                    stateDirectory = buildDirectory.resolve("tapik-state"),
                    owner = mojoExecution.executionId
                )
            }
            project.registerGeneratedArtifacts(outputDirectory.toPath(), generation)
            generation.written.forEach { path -> log.info("Generated ${projectRelativePath(path)}") }
        } catch (cause: Exception) {
            throw MojoExecutionException("Tapik generation failed: ${cause.message}", cause)
        }
    }

    private fun projectRelativePath(path: Path): Path =
        runCatching { project.basedir.toPath().toAbsolutePath().normalize().relativize(path) }.getOrDefault(path)

    private fun generatedModuleName(): String =
        "tapik-${project.artifactId}-${mojoExecution.executionId}"
            .replace(Regex("[^A-Za-z0-9_.-]"), "_")
}
