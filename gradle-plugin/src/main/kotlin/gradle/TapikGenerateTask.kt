package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.plugin.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

/**
 * Gradle task that scans compiled classes for Tapik endpoints and generates supporting artefacts.
 */
@CacheableTask
abstract class TapikGenerateTask : DefaultTask() {
    /** Base package that will be scanned for compiled `HttpEndpoint` declarations. */
    @get:Input
    abstract val basePackage: Property<String>

    /** Package segment appended to source packages for generated Kotlin sources. */
    @get:Input
    abstract val generatedPackageName: Property<String>

    /** Suffix appended to the source-level enclosing endpoints interface. */
    @get:Input
    abstract val endpointsSuffix: Property<String>

    /** Directory containing the textual endpoint summary output. */
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

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

    /** Client suffix keyed by generator identifier. */
    @get:Input
    abstract val generatorClientSuffixes: MapProperty<String, String>

    /** Server suffix keyed by generator identifier. */
    @get:Input
    abstract val generatorServerSuffixes: MapProperty<String, String>

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
        val clientSuffixesByGenerator = generatorClientSuffixes.getOrElse(emptyMap())
        val serverSuffixesByGenerator = generatorServerSuffixes.getOrElse(emptyMap())
        val generatorIds =
            (clientSuffixesByGenerator.keys + serverSuffixesByGenerator.keys).toSet()

        TapikCodeGenerationEngine(
            config = TapikCodeGenerationConfiguration(
                outputDirectory = outputDirectory.get().asFile,
                generatedSourcesDirectory = generatedSourcesDirectory.get().asFile,
                generatedPackageName = generatedPackageName.get().trim().ifBlank { "generated" },
                endpointsSuffix = endpointsSuffix.get(),
                basePackage = basePackage.get(),
                compiledClassesDirectory = compiledClassesDirectory.get().asFile,
                additionalClasspathDirectories = compiledClassesDirectories,
                generatorConfigurations =
                    generatorIds
                        .associateWith { generatorId ->
                            GeneratorConfiguration(
                                clientSuffix = clientSuffixesByGenerator.getOrElse(generatorId) { "Client" },
                                serverSuffix = serverSuffixesByGenerator.getOrElse(generatorId) { "Server" }
                            )
                        }
            ),
            logger = TapikGradleLogger(logger)
        ).generate()
    }
}
