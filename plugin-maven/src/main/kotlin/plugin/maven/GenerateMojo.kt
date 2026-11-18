package dev.akif.tapik.plugin.maven

import dev.akif.tapik.common.plugin.reportGeneratedPaths
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

/**
 * Runs one configured tapik generation execution against compiled API registries.
 *
 * The goal defaults to `process-classes`, after APIs declared by the current project have compiled. Bind it to
 * `generate-sources` when generating from an already compiled API dependency so generated Kotlin can be used by
 * the current project's main sources. The goal does not scan packages or execute API declarations.
 */
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

    /** Generates and materializes the artifacts for the configured target execution. */
    override fun execute() {
        try {
            val mode = MavenGenerationMode.from(mojoExecution)
            if (mode == MavenGenerationMode.DIRECT) {
                log.info(
                    "Direct tapik generation reuses compiled output and does not run user compilation or preceding lifecycle phases. " +
                        "Compiled output may be stale; compile changed endpoint definitions first."
                )
            }
            mode.requireCompiledOutput(Path.of(project.build.outputDirectory))
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
                            "tapik Maven plugin version is unavailable"
                        },
                    projectTapikVersions = project.tapikDependencyVersions(),
                    mode = mode
                )
            if (mode.compileGeneratedSources) {
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
            reportGeneratedPaths(generation.written, project.basedir.toPath()) { path ->
                log.info("Generated $path")
            }
        } catch (cause: Exception) {
            throw MojoExecutionException("tapik generation failed: ${cause.message}", cause)
        }
    }

    private fun generatedModuleName(): String =
        "tapik-${project.artifactId}-${mojoExecution.executionId}"
            .replace(Regex("[^A-Za-z0-9_.-]"), "_")
}
