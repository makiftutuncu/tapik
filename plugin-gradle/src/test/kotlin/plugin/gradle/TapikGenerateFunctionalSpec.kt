package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.GenerationResult
import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetRegistry
import dev.akif.tapik.common.plugin.ListConfigurationValue
import dev.akif.tapik.common.plugin.ObjectConfigurationValue
import dev.akif.tapik.common.plugin.ScalarConfigurationValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText

class TapikGenerateFunctionalSpec : FunSpec({
    test("generate several selected targets then remove stale output without risking the last successful tree") {
        val project = generationProject()
        project.writeBuild(allTargetsBuild())

        val first = project.run("tapikGenerate")

        first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        first.output shouldContain "Generated build/generated/tapik/documentation/Catalog.openapi.json"
        project.generatedFiles().map(Path::name) shouldContain "Catalog.openapi.json"
        project.generatedFiles().map(Path::name).count { name -> name == "BooksEndpoints.kt" } shouldBe 1
        project.generatedFiles().map(Path::name).any { name -> name.endsWith("Client.kt") } shouldBe true
        project.generatedFiles().map(Path::name).any { name -> name.endsWith("GeneratedController.properties") } shouldBe true
        project.generatedFiles().map(Path::name).any { name -> name == "Authors.openapi.json" } shouldBe false

        project.writeBuild(restClientBuild())
        val second = project.run("tapikGenerate")

        second.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        project.generatedFiles().map(Path::name).any { name -> name.endsWith("Client.kt") } shouldBe true
        project.generatedFiles().map(Path::name).any { name -> name.endsWith("GeneratedController.properties") } shouldBe false
        project.generatedFiles().map(Path::name).any { name -> name.endsWith("openapi.json") } shouldBe false

        val successfulFiles = project.generatedFileContents()
        project.writeBuild(invalidBuild())
        project.runAndFail("tapikGenerate")

        project.generatedFileContents() shouldBe successfulFiles
    }

    test("pass nested configuration to a custom isolated target") {
        val descriptorRoot = Files.createTempDirectory("tapik-gradle-custom-target-")
        val descriptor =
            descriptorRoot.resolve("META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry")
        Files.createDirectories(descriptor.parent)
        Files.writeString(descriptor, NestedConfigurationTargetRegistry::class.qualifiedName + "\n")
        val testClasses =
            Path.of(NestedConfigurationTargetRegistry::class.java.protectionDomain.codeSource.location.toURI())
        val project = generationProject()
        project.writeBuild(
            tapikBlock =
                """
                target('nested-docs', 'nested-configuration') {
                    includeApis('Catalog')
                    list('formats', 'json', 'yaml')
                    objectValue('metadata') { metadata ->
                        metadata.value('owner', 'platform')
                        metadata.objectValue('release') { release ->
                            release.value('stable', true)
                        }
                    }
                }
                """.trimIndent(),
            additionalDependencies =
                "tapikTarget files('${descriptorRoot.groovyPath()}', '${testClasses.groovyPath()}')"
        )

        val result = project.run("tapikGenerate")

        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        project.generatedFiles().single { path -> path.name == "nested.txt" }.readText() shouldBe
            "Catalog|json,yaml|platform|true"
    }
})

private fun generationProject(): GenerationProject {
    val root = Files.createTempDirectory("tapik-gradle-generation-")
    Files.writeString(root.resolve("settings.gradle"), "rootProject.name = 'consumer'\n")
    return GenerationProject(root)
}

private class GenerationProject(private val root: Path) {
    fun writeBuild(tapikBlock: String, additionalDependencies: String = "") {
        Files.writeString(
            root.resolve("build.gradle"),
            """
            import dev.akif.tapik.plugin.gradle.Mode
            import dev.akif.tapik.plugin.gradle.OpenApiFormat

            plugins {
                id 'org.jetbrains.kotlin.jvm'
                id 'dev.akif.tapik.plugin.gradle'
            }

            repositories {
                maven { url = uri('${tapikRepository.toUri()}') }
                mavenCentral()
            }

            dependencies {
                tapikContract 'dev.akif:tapik-example-contract:$tapikVersion'
                $additionalDependencies
            }

            tapik {
                mode(Mode.COMPILED_CONTRACT)
                $tapikBlock
            }
            """.trimIndent()
        )
    }

    fun run(task: String) =
        runner(task).build()

    fun runAndFail(task: String) =
        runner(task).buildAndFail()

    fun generatedFiles(): List<Path> {
        val generated = root.resolve("build/generated/tapik")
        if (!generated.exists()) return emptyList()
        return Files.walk(generated).use { paths ->
            paths.filter(Path::isRegularFile)
                .filter { path -> path.name != ".tapik-ownership" }
                .sorted()
                .toList()
        }
    }

    fun generatedFileContents(): List<Pair<String, String>> =
        generatedFiles().map { path -> root.relativize(path).toString() to path.readText() }

    private fun runner(task: String): GradleRunner =
        GradleRunner.create()
            .withProjectDir(root.toFile())
            .withArguments(task, "--stacktrace")
            .withPluginClasspath()
}

private fun allTargetsBuild(): String =
    """
    openApi('docs') {
        includeApis('Catalog')
        version('1.0.0')
        format(OpenApiFormat.JSON)
    }
    springRestClient('client') {
        packageName('com.example.generated')
    }
    springWebMvc('server') {
        packageName('com.example.generated')
    }
    """.trimIndent()

private fun restClientBuild(): String =
    """
    springRestClient('client') {
        packageName('com.example.generated')
    }
    """.trimIndent()

private fun invalidBuild(): String =
    """
    springRestClient('client') {
        packageName('com.example.generated')
    }
    openApi('invalid') {
    }
    """.trimIndent()

private val tapikRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })
private val tapikVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }

private fun Path.groovyPath(): String = toString().replace("'", "\\'")

class NestedConfigurationTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(NestedConfigurationTarget)
}

private object NestedConfigurationTarget : GenerationTarget {
    override val id: String = "nested-configuration"

    override fun generate(request: GenerationRequest): GenerationResult {
        val formats = request.configuration["formats"] as ListConfigurationValue
        val metadata = request.configuration["metadata"] as ObjectConfigurationValue
        val release = metadata.values.getValue("release") as ObjectConfigurationValue
        val content =
            listOf(
                request.apis.single().apiId,
                formats.values.joinToString(",") { value -> (value as ScalarConfigurationValue).value },
                (metadata.values.getValue("owner") as ScalarConfigurationValue).value,
                (release.values.getValue("stable") as ScalarConfigurationValue).value
            ).joinToString("|")
        return GenerationResult(
            listOf(
                GeneratedArtifact(
                    relativePath = "nested.txt",
                    mediaType = "text/plain",
                    kind = ArtifactKind.DOCUMENTATION,
                    content = content
                )
            )
        )
    }
}
