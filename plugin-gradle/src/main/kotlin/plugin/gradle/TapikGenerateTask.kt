package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactWriter
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.nio.file.Files
import javax.inject.Inject

/** Aggregates configured tapik generation executions behind one Gradle output owner. */
@CacheableTask
abstract class TapikGenerateTask @Inject constructor(
    private val workerExecutor: WorkerExecutor
) : DefaultTask() {
    /** Explicit source of compiled API contracts. */
    @get:Optional
    @get:Input
    abstract val mode: Property<Mode>

    /** Version of the applied tapik Gradle plugin. */
    @get:Input
    abstract val pluginVersion: Property<String>

    /** Generation executions in declaration order. */
    @get:Nested
    abstract val executions: ListProperty<TapikExecution>

    /** Resolved contract artifacts and their runtime dependencies. */
    @get:Classpath
    abstract val contractClasspath: ConfigurableFileCollection

    /** Resolved generation targets and their runtime dependencies. */
    @get:Classpath
    abstract val targetClasspath: ConfigurableFileCollection

    /** Root directory owned by this task. */
    @get:OutputDirectory
    abstract val outputRoot: DirectoryProperty

    /** Project directory used to report generated paths. */
    @get:Internal
    abstract val projectDirectory: DirectoryProperty

    /** Generates all configured target executions and materializes one merged result. */
    @TaskAction
    internal fun generate() {
        validateConfiguration()

        val configuredExecutions = executions.get()
        val resultDirectory = temporaryDir.toPath().resolve("results")
        Files.createDirectories(resultDirectory)
        val queue =
            workerExecutor.classLoaderIsolation { isolation ->
                isolation.classpath.from(targetClasspath)
            }
        val resultFiles =
            configuredExecutions.mapIndexed { index, execution ->
                val resultFile = resultDirectory.resolve("$index.bin")
                Files.deleteIfExists(resultFile)
                queue.submit(GradleGenerationWorkAction::class.java) { parameters ->
                    parameters.contractClasspath.from(contractClasspath)
                    parameters.executionName.set(execution.name)
                    parameters.targetId.set(execution.targetId)
                    parameters.configuration.set(execution.generationConfiguration())
                    parameters.includeApis.set(execution.includeApis)
                    parameters.excludeApis.set(execution.excludeApis)
                    parameters.resultFile.fileValue(resultFile.toFile())
                }
                resultFile
            }
        queue.await()

        val aggregate =
            aggregateGradleGenerationResults(
                configuredExecutions.zip(resultFiles).map { (execution, resultFile) ->
                    GradleExecutionGeneration(execution.name, GradleGenerationResultCodec.read(resultFile))
                }
            )
        val written =
            ArtifactWriter.write(
                result = aggregate,
                outputDirectory = outputRoot.get().asFile.toPath(),
                owner = "gradle:tapikGenerate"
            )
        val projectPath = projectDirectory.get().asFile.toPath().toAbsolutePath().normalize()
        written.forEach { path ->
            val relative = projectPath.relativize(path.toAbsolutePath().normalize()).toString().replace('\\', '/')
            logger.lifecycle("Generated $relative")
        }
    }

    internal fun validateConfiguration() {
        try {
            require(executions.get().isNotEmpty()) { "tapik generation requires at least one execution" }
            require(mode.isPresent) {
                "tapik generation requires mode(Mode.COMPILED_CONTRACT) or mode(Mode.SAME_PROJECT)"
            }
            executions.get().forEach(TapikExecution::validateConfiguration)
        } catch (failure: IllegalArgumentException) {
            throw GradleException(failure.message ?: "Invalid tapik generation configuration", failure)
        }
    }
}
