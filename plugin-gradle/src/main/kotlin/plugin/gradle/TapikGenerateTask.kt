package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactWriter
import dev.akif.tapik.common.plugin.reportGeneratedPaths
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
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

/**
 * Aggregates configured tapik generation executions behind one cacheable Gradle output owner.
 *
 * The task generates every execution before atomically materializing the merged result below [outputRoot]. In
 * [Mode.COMPILED_CONTRACT], main Kotlin and resource tasks consume its outputs. In [Mode.SAME_PROJECT], it depends on
 * main classes; separate lifecycle wiring compiles and packages late source and resources when the corresponding
 * lifecycle tasks are requested. Invoking `tapikGenerate` directly does not compile those late sources.
 */
@CacheableTask
abstract class TapikGenerateTask @Inject constructor(
    private val workerExecutor: WorkerExecutor
) : DefaultTask() {
    @get:Input
    internal abstract val kotlinJvmProject: Property<Boolean>

    @get:Input
    internal abstract val projectType: Property<String>

    @get:Input
    internal abstract val gradleVersion: Property<String>

    @get:Input
    internal abstract val javaVersion: Property<String>

    @get:Input
    internal abstract val kotlinVersion: Property<String>

    /** Explicit source of compiled API contracts. */
    @get:Optional
    @get:Input
    abstract val mode: Property<Mode>

    /** Version of the applied tapik Gradle plugin. */
    @get:Input
    abstract val pluginVersion: Property<String>

    /** Resolved tapik module versions keyed by classpath, coordinates, and selected component identity. */
    @get:Input
    abstract val tapikDependencyVersions: MapProperty<String, String>

    /** Generation executions in declaration order. */
    @get:Nested
    abstract val executions: ListProperty<TapikExecution>

    /**
     * Resolved contract artifacts and their runtime dependencies.
     *
     * These come from `tapikContractClasspath` in compiled-contract mode or main output and its runtime classpath in
     * same-project mode.
     */
    @get:Classpath
    abstract val contractClasspath: ConfigurableFileCollection

    /** Resolved generation targets and their runtime dependencies from `tapikTargetClasspath`. */
    @get:Classpath
    abstract val targetClasspath: ConfigurableFileCollection

    /** Root directory solely owned by this task, containing routed artifact-kind subdirectories. */
    @get:OutputDirectory
    abstract val outputRoot: DirectoryProperty

    /** Project directory used to report generated paths. */
    @get:Internal
    abstract val projectDirectory: DirectoryProperty

    /** Generates all configured target executions and materializes one merged result. */
    @TaskAction
    internal fun generate() {
        validateEnvironment()
        validateConfiguration()

        val configuredExecutions = executions.get()
        val resultDirectory = temporaryDir.toPath().resolve("results")
        Files.createDirectories(resultDirectory)
        val queue =
            workerExecutor.classLoaderIsolation { isolation ->
                isolation.classpath.from(targetClasspath)
            }
        val workExecutions =
            configuredExecutions.mapIndexed { index, execution ->
                GradleGenerationWorkExecution(
                    executionName = execution.name,
                    targetId = execution.targetId,
                    configuration = LinkedHashMap(execution.generationConfiguration()),
                    includeApis = LinkedHashSet(execution.includeApis.get()),
                    excludeApis = LinkedHashSet(execution.excludeApis.get()),
                    resultFileName = "$index.bin"
                )
            }
        val resultFiles = workExecutions.map { execution -> resultDirectory.resolve(execution.resultFileName) }
        resultFiles.forEach(Files::deleteIfExists)
        queue.submit(GradleGenerationWorkAction::class.java) { parameters ->
            parameters.contractClasspath.from(contractClasspath)
            parameters.mode.set(mode)
            parameters.executions.set(workExecutions)
            parameters.resultDirectory.fileValue(resultDirectory.toFile())
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
        reportGeneratedPaths(written, projectPath) { path ->
            logger.lifecycle("Generated $path")
        }
    }

    internal fun validateEnvironment() {
        try {
            require(kotlinJvmProject.get()) {
                "tapik Gradle generation supports Kotlin/JVM projects only; detected ${projectType.get()}"
            }
            require(isSupportedGradleVersion(gradleVersion.get())) {
                "tapik Gradle generation supports Gradle 9.1 through 9.8; detected ${gradleVersion.get()}"
            }
            require(javaVersion.get() == "25") {
                "tapik Gradle generation supports Java 25; detected Java ${javaVersion.get()}"
            }
            require(kotlinVersion.get() == "2.4.10") {
                "tapik Gradle generation supports Kotlin 2.4.10; detected Kotlin ${kotlinVersion.get()}"
            }
        } catch (failure: IllegalArgumentException) {
            throw GradleException(failure.message ?: "Unsupported tapik Gradle environment", failure)
        }
    }

    internal fun validateConfiguration() {
        try {
            require(executions.get().isNotEmpty()) { "tapik generation requires at least one execution" }
            require(mode.isPresent) {
                "tapik generation requires mode(Mode.COMPILED_CONTRACT) or mode(Mode.SAME_PROJECT)"
            }
            executions.get().forEach(TapikExecution::validateConfiguration)
            val incompatible =
                tapikDependencyVersions.getOrElse(emptyMap())
                    .filterValues { version -> version != pluginVersion.get() }
                    .toSortedMap()
            require(incompatible.isEmpty()) {
                buildString {
                    append("tapik Gradle plugin ${pluginVersion.get()} is incompatible with resolved dependencies: ")
                    append(
                        incompatible.entries.joinToString { (component, _) ->
                            component
                        }
                    )
                    append(". Align every dev.akif:tapik-* dependency with the plugin version.")
                }
            }
        } catch (failure: IllegalArgumentException) {
            throw GradleException(failure.message ?: "Invalid tapik generation configuration", failure)
        }
    }
}
