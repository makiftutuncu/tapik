package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.plugin.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.*
import java.io.File

/**
 * Gradle task that scans compiled classes for Tapik endpoints and generates supporting artefacts.
 */
@CacheableTask
abstract class TapikGenerateTask : DefaultTask() {
    /** Packages that will be scanned for compiled `HttpEndpoint` declarations. */
    @get:Input
    abstract val endpointPackages: ListProperty<String>

    /** Directory containing the textual endpoint summary output. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    /** Source directory containing the Kotlin declarations that define Tapik endpoints. */
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDirectory: DirectoryProperty

    /** Directory holding compiled classes associated with the project under analysis. */
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:Optional
    abstract val compiledClassesDirectory: DirectoryProperty

    /** Directory where generated client source files are written. */
    @get:OutputDirectory
    abstract val generatedSourcesDirectory: DirectoryProperty

    /** Additional class directories (for example, from other projects) to include on the analysis classpath. */
    @get:Input
    abstract val additionalClassDirectories: ListProperty<String>

    /** Identifiers of generators that should be executed. */
    @get:Input
    abstract val enabledGeneratorIds: SetProperty<String>

    /** Runtime classpath used when analysing endpoint bytecode via reflection fallback. */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val runtimeClasspath: ConfigurableFileCollection

    /**
     * Executes endpoint discovery and generates Tapik metadata and client sources.
     */
    @TaskAction
    fun generate() {
        val compiledClassesDirectories =
            additionalClassDirectories
                .getOrElse(emptyList())
                .map { File(it) } +
                runtimeClasspath.files

        GenerateTask(
            config = GenerateTaskConfiguration(
                outputDirectory = outputDirectory.get().asFile,
                generatedSourcesDirectory = generatedSourcesDirectory.get().asFile,
                endpointPackages = endpointPackages.get(),
                compiledClassesDirectory = compiledClassesDirectory.get().asFile,
                additionalClasspathDirectories = compiledClassesDirectories,
                enabledGeneratorIds = enabledGeneratorIds.getOrElse(emptySet())
            ),
            log = logger::lifecycle,
            logDebug = logger::debug,
            logWarn = logger::warn
        ).generate()
    }
}
