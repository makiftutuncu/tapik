package dev.akif.tapik.plugin.maven

import org.apache.maven.plugin.AbstractMojo
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

    @field:Parameter(property = "tapik.target", required = true)
    private lateinit var target: String

    @field:Parameter(
        property = "tapik.outputDirectory",
        defaultValue = "\${project.build.directory}/generated/tapik",
        required = true
    )
    private lateinit var outputDirectory: File

    @field:Parameter
    private var targetConfiguration: Map<String, String> = emptyMap()

    override fun execute() {
        try {
            val classpath = project.compileClasspathElements.map(Path::of)
            val generation =
                MavenGenerator().generate(
                    classpath = classpath,
                    targetId = target,
                    targetConfiguration = targetConfiguration,
                    outputDirectory = outputDirectory.toPath(),
                    parentClassLoader = requireNotNull(javaClass.classLoader)
                )
            if (generation.containsSources) {
                project.addCompileSourceRoot(outputDirectory.absolutePath)
            }
            generation.written.forEach { path -> log.info("Generated ${projectRelativePath(path)}") }
        } catch (cause: Exception) {
            throw MojoExecutionException("Tapik generation failed: ${cause.message}", cause)
        }
    }

    private fun projectRelativePath(path: Path): Path =
        runCatching { project.basedir.toPath().toAbsolutePath().normalize().relativize(path) }.getOrDefault(path)
}
