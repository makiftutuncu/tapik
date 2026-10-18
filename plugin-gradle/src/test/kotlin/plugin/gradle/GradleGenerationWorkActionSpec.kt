package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.gradle.workers.WorkerExecutor
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Properties
import javax.inject.Inject

class GradleGenerationWorkActionSpec : FunSpec({
    test("run generation through a classloader-isolated Gradle worker") {
        val project = Files.createTempDirectory("tapik-gradle-worker-")
        val descriptorRoot = project.resolve("worker-plugin")
        val descriptor =
            descriptorRoot.resolve(
                "META-INF/gradle-plugins/dev.akif.tapik.worker-harness.properties"
            )
        Files.createDirectories(descriptor.parent)
        Files.writeString(
            descriptor,
            "implementation-class=dev.akif.tapik.plugin.gradle.GenerationWorkerHarnessPlugin\n"
        )
        Files.writeString(project.resolve("settings.gradle"), "rootProject.name = 'worker-test'\n")
        Files.writeString(
            project.resolve("build.gradle"),
            """
            plugins {
                id 'dev.akif.tapik.worker-harness'
            }

            tasks.named('generationWorker') {
                contractClasspath.from(files(${workerContractClasspath.groovyFiles()}))
                targetClasspath.from(files(${workerTargetClasspath.groovyFiles()}))
                resultFile.set(layout.buildDirectory.file('worker/result.bin'))
            }
            """.trimIndent()
        )

        val result =
            GradleRunner
                .create()
                .withProjectDir(project.toFile())
                .withArguments("generationWorker", "--stacktrace")
                .withPluginClasspath(workerPluginClasspath() + descriptorRoot.toFile())
                .build()

        result.task(":generationWorker")?.outcome shouldBe TaskOutcome.SUCCESS
        GradleGenerationResultCodec
            .read(project.resolve("build/worker/result.bin"))
            .artifacts
            .shouldNotBeEmpty()
    }
})

internal class GenerationWorkerHarnessPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("generationWorker", GenerationWorkerHarness::class.java)
    }
}

internal abstract class GenerationWorkerHarness @Inject constructor(
    private val workerExecutor: WorkerExecutor
) : DefaultTask() {
    @get:Classpath
    abstract val contractClasspath: ConfigurableFileCollection

    @get:Classpath
    abstract val targetClasspath: ConfigurableFileCollection

    @get:OutputFile
    abstract val resultFile: RegularFileProperty

    @TaskAction
    fun runWorker() {
        val queue =
            workerExecutor.classLoaderIsolation { isolation ->
                isolation.classpath.from(targetClasspath)
            }
        queue.submit(GradleGenerationWorkAction::class.java) { parameters ->
            parameters.contractClasspath.from(contractClasspath)
            parameters.executionName.set("docs")
            parameters.targetId.set("openapi")
            parameters.configuration.set(mapOf("version" to ScalarGradleConfigurationValue("1.0.0")))
            parameters.includeApis.set(emptySet())
            parameters.excludeApis.set(emptySet())
            parameters.resultFile.set(resultFile)
        }
    }
}

private fun workerPluginClasspath(): List<File> {
    val properties = Properties()
    requireNotNull(
        GradleGenerationWorkActionSpec::class.java.classLoader
            .getResourceAsStream("plugin-under-test-metadata.properties")
    ).use(properties::load)
    val implementationClasspath =
        requireNotNull(properties.getProperty("implementation-classpath"))
            .split(File.pathSeparator)
            .map(::File)
    val testClasses = File(GenerationWorkerHarnessPlugin::class.java.protectionDomain.codeSource.location.toURI())
    return implementationClasspath + testClasses
}

private fun List<Path>.groovyFiles(): String =
    joinToString { path -> "'${path.toString().replace("'", "\\'")}'" }

private val workerContractClasspath: List<Path> = workerClasspathProperty("tapik.generation-worker.contract-classpath")
private val workerTargetClasspath: List<Path> = workerClasspathProperty("tapik.generation-worker.target-classpath")

private fun workerClasspathProperty(name: String): List<Path> =
    requireNotNull(System.getProperty(name)) { "Missing test system property '$name'" }
        .split(File.pathSeparator)
        .map(Path::of)
