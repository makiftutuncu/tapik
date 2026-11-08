package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

class ParallelGenerationSpec : FunSpec({
    test("keep project output ownership separate during parallel generation") {
        val project = parallelGenerationProject()

        val first = project.run()
        first.task(":catalog:tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        first.task(":books:tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        project.path("catalog/build/generated/tapik/documentation/Catalog.json").exists() shouldBe true
        project.path("books/build/generated/tapik/documentation/Books.json").exists() shouldBe true
        val catalog = project.invocation("catalog")
        val books = project.invocation("books")
        (catalog.start < books.finish && books.start < catalog.finish) shouldBe true

        val second = project.run()
        second.task(":catalog:tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        second.task(":books:tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        second.output shouldContain "Reusing configuration cache."
    }
})

private fun parallelGenerationProject(): ParallelGenerationProject {
    val root = Files.createTempDirectory("tapik-gradle-parallel-")
    val buildCache = Files.createTempDirectory("tapik-gradle-parallel-cache-")
    val eventDirectory = Files.createTempDirectory("tapik-gradle-parallel-events-")
    val descriptorRoot = Files.createTempDirectory("tapik-gradle-parallel-target-")
    val descriptor =
        descriptorRoot.resolve("META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry")
    Files.createDirectories(descriptor.parent)
    Files.writeString(descriptor, InvocationOrderTargetRegistry::class.qualifiedName + "\n")
    val testClasses = Path.of(InvocationOrderTargetRegistry::class.java.protectionDomain.codeSource.location.toURI())
    val project = ParallelGenerationProject(root, eventDirectory)
    project.write(
        "settings.gradle",
        """
        buildCache {
            local {
                directory = file('${buildCache.parallelGroovyPath()}')
            }
        }

        rootProject.name = 'parallel-generation'
        include 'catalog', 'books'
        """.trimIndent()
    )
    project.write(
        "build.gradle",
        """
        allprojects {
            repositories {
                maven { url = uri('${parallelRepository.toUri()}') }
                mavenCentral()
            }
        }
        """.trimIndent()
    )
    project.writeProject("catalog", "Catalog", descriptorRoot, testClasses)
    project.writeProject("books", "Books", descriptorRoot, testClasses)
    return project
}

private class ParallelGenerationProject(
    private val root: Path,
    private val eventDirectory: Path
) {
    fun writeProject(
        project: String,
        apiId: String,
        descriptorRoot: Path,
        testClasses: Path
    ) {
        write(
            "$project/build.gradle",
            """
            import dev.akif.tapik.plugin.gradle.Mode
            import dev.akif.tapik.plugin.gradle.OpenApiFormat

            plugins {
                id 'org.jetbrains.kotlin.jvm'
                id 'dev.akif.tapik.plugin.gradle'
            }

            dependencies {
                tapikContract 'dev.akif:tapik-example-contract:$parallelVersion'
                tapikTarget files(
                    '${descriptorRoot.parallelGroovyPath()}',
                    '${testClasses.parallelGroovyPath()}'
                )
            }

            tapik {
                mode(Mode.COMPILED_CONTRACT)
                openApi('docs') {
                    includeApis('$apiId')
                    version('1.0.0')
                    format(OpenApiFormat.JSON)
                    output('$apiId.json')
                }
                target('delay', 'invocation-order') {
                    value('name', '$project')
                    value('delayMillis', '750')
                    value('eventDirectory', '${eventDirectory.parallelGroovyPath()}')
                }
            }

            kotlin {
                jvmToolchain(25)
            }
            """.trimIndent()
        )
    }

    fun write(relativePath: String, content: String) {
        val path = root.resolve(relativePath)
        Files.createDirectories(path.parent)
        Files.writeString(path, content + "\n")
    }

    fun path(relativePath: String): Path = root.resolve(relativePath)

    fun invocation(name: String): InvocationInterval =
        InvocationInterval(
            start = Files.readString(eventDirectory.resolve("$name.start")).toLong(),
            finish = Files.readString(eventDirectory.resolve("$name.finish")).toLong()
        )

    fun run() =
        GradleRunner.create()
            .withProjectDir(root.toFile())
            .withArguments(
                ":catalog:tapikGenerate",
                ":books:tapikGenerate",
                "--parallel",
                "--configuration-cache",
                "--build-cache",
                "--stacktrace"
            )
            .withPluginClasspath()
            .build()
}

private data class InvocationInterval(
    val start: Long,
    val finish: Long
)

private fun Path.parallelGroovyPath(): String = toString().replace("'", "\\'")

private val parallelRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })
private val parallelVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }
