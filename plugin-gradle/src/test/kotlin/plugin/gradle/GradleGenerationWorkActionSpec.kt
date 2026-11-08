package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.GenerationResult
import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry
import dev.akif.tapik.common.plugin.ScalarConfigurationValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
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
import java.util.UUID
import javax.inject.Inject
import kotlin.io.path.name
import kotlin.io.path.readText

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

        fun build() =
            GradleRunner
                .create()
                .withProjectDir(project.toFile())
                .withArguments("generationWorker", "--configuration-cache", "--stacktrace")
                .withPluginClasspath(workerPluginClasspath() + descriptorRoot.toFile())
                .build()

        val (result, reused) = repeatWithConfigurationCache(::build)

        result.task(":generationWorker")?.outcome shouldBe TaskOutcome.SUCCESS
        reused.task(":generationWorker")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        GradleGenerationResultCodec
            .read(project.resolve("build/worker/result.bin"))
            .artifacts
            .shouldNotBeEmpty()
    }

    test("invoke delayed target executions in declaration order without overlap") {
        val eventDirectory = Files.createTempDirectory("tapik-gradle-worker-order-")
        val descriptorRoot = Files.createTempDirectory("tapik-gradle-worker-target-")
        val descriptor =
            descriptorRoot.resolve("META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry")
        Files.createDirectories(descriptor.parent)
        Files.writeString(descriptor, InvocationOrderTargetRegistry::class.qualifiedName + "\n")
        val testClasses = Path.of(InvocationOrderTargetRegistry::class.java.protectionDomain.codeSource.location.toURI())
        val project = generationProject()
        project.writeBuild(
            tapikBlock =
                listOf(
                    invocationOrderExecution("first", 750, eventDirectory),
                    invocationOrderExecution("second", 250, eventDirectory),
                    invocationOrderExecution("third", 0, eventDirectory)
                ).joinToString("\n"),
            additionalDependencies =
                "tapikTarget files('${descriptorRoot.gradleGroovyPath()}', '${testClasses.gradleGroovyPath()}')"
        )

        val result = project.run("tapikGenerate")

        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        val events =
            listOf("first", "second", "third")
                .flatMap { name ->
                    listOf("start", "finish").map { phase ->
                        val time = Files.readString(eventDirectory.resolve("$name.$phase")).toLong()
                        time to "$name:$phase"
                    }
                }
                .sortedBy(Pair<Long, String>::first)
                .map(Pair<Long, String>::second)
        events shouldContainExactly
            listOf(
                "first:start",
                "first:finish",
                "second:start",
                "second:finish",
                "third:start",
                "third:finish"
            )
    }

    test("reuse one target catalog and contract classloader for every execution") {
        val descriptorRoot = Files.createTempDirectory("tapik-gradle-worker-reuse-")
        val descriptor =
            descriptorRoot.resolve("META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry")
        Files.createDirectories(descriptor.parent)
        Files.writeString(descriptor, ReuseTrackingTargetRegistry::class.qualifiedName + "\n")
        val testClasses = Path.of(ReuseTrackingTargetRegistry::class.java.protectionDomain.codeSource.location.toURI())
        val project = generationProject()
        project.writeBuild(
            tapikBlock =
                """
                target('first', 'reuse-tracking') {
                    value('output', 'first.txt')
                }
                target('second', 'reuse-tracking') {
                    value('output', 'second.txt')
                }
                """.trimIndent(),
            additionalDependencies =
                "tapikTarget files('${descriptorRoot.gradleGroovyPath()}', '${testClasses.gradleGroovyPath()}')"
        )

        val result = project.run("tapikGenerate")

        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        val fingerprints =
            project.generatedFiles()
                .filter { path -> path.name == "first.txt" || path.name == "second.txt" }
                .map(Path::readText)
        fingerprints.distinct().size shouldBe 1
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
            parameters.mode.set(Mode.COMPILED_CONTRACT)
            parameters.executions.set(
                listOf(
                    GradleGenerationWorkExecution(
                        executionName = "docs",
                        targetId = "openapi",
                        configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                        includeApis = emptySet(),
                        excludeApis = emptySet(),
                        resultFileName = resultFile.get().asFile.name
                    )
                )
            )
            parameters.resultDirectory.fileValue(resultFile.get().asFile.parentFile)
        }
    }
}

class InvocationOrderTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(InvocationOrderTarget)
}

class ReuseTrackingTargetRegistry : GenerationTargetRegistry {
    private val catalogIdentity = UUID.randomUUID().toString()

    override val targets: List<GenerationTarget> = listOf(ReuseTrackingTarget(catalogIdentity))
}

private class ReuseTrackingTarget(
    private val catalogIdentity: String
) : GenerationTarget {
    override val id: String = "reuse-tracking"

    override fun generate(request: GenerationRequest): GenerationResult {
        val apiClassLoader = request.apis.first().javaClass.classLoader
        return GenerationResult(
            listOf(
                GeneratedArtifact(
                    relativePath = request.scalar("output"),
                    mediaType = "text/plain",
                    kind = ArtifactKind.DOCUMENTATION,
                    content = "$catalogIdentity:${System.identityHashCode(apiClassLoader)}"
                )
            )
        )
    }
}

private object InvocationOrderTarget : GenerationTarget {
    override val id: String = "invocation-order"

    override fun generate(request: GenerationRequest): GenerationResult {
        val name = request.scalar("name")
        val eventDirectory = Path.of(request.scalar("eventDirectory"))
        Files.createDirectories(eventDirectory)
        Files.writeString(eventDirectory.resolve("$name.start"), System.nanoTime().toString())
        Thread.sleep(request.scalar("delayMillis").toLong())
        Files.writeString(eventDirectory.resolve("$name.finish"), System.nanoTime().toString())
        return GenerationResult(
            listOf(
                GeneratedArtifact(
                    relativePath = "$name.txt",
                    mediaType = "text/plain",
                    kind = ArtifactKind.DOCUMENTATION,
                    content = name
                )
            )
        )
    }
}

private fun GenerationRequest.scalar(name: String): String =
    (requireNotNull(configuration[name]) as ScalarConfigurationValue).value

private fun invocationOrderExecution(
    name: String,
    delayMillis: Long,
    eventDirectory: Path
): String =
    """
    target('$name', 'invocation-order') {
        value('name', '$name')
        value('delayMillis', '$delayMillis')
        value('eventDirectory', '${eventDirectory.gradleGroovyPath()}')
    }
    """.trimIndent()

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
