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
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.io.path.readText

class TapikGenerateFunctionalSpec : FunSpec({
    test("generate several selected targets then remove stale output without risking the last successful tree") {
        val project = generationProject()
        project.writeBuild(allTargetsBuild())

        val (first, reused) = repeatWithConfigurationCache { project.run("tapikGenerate") }

        first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        reused.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
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
        val failed = project.runAndFail("tapikGenerate")

        failed.output shouldContain "tapik execution 'invalid' for target 'openapi' requires a nonblank version"
        project.generatedFileContents() shouldBe successfulFiles

        project.writeBuild(restClientBuild())
        val recovered = project.run("tapikGenerate")
        recovered.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        val unchanged = project.run("tapikGenerate")
        unchanged.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
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
                "tapikTarget files('${descriptorRoot.gradleGroovyPath()}', '${testClasses.gradleGroovyPath()}')"
        )

        val (result, reused) = repeatWithConfigurationCache { project.run("tapikGenerate") }

        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        reused.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        project.generatedFiles().single { path -> path.name == "nested.txt" }.readText() shouldBe
            "Catalog|json,yaml|platform|true"
    }

    test("apply exclusions after inclusions") {
        val project = generationProject()
        project.writeBuild(
            """
            openApi('docs') {
                includeApis('Authors', 'Catalog')
                excludeApis('Authors')
                version('1.0.0')
                format(OpenApiFormat.JSON)
            }
            """.trimIndent()
        )

        val result = project.run("tapikGenerate")

        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        project.generatedFiles().map(Path::name) shouldContain "Catalog.openapi.json"
        project.generatedFiles().map(Path::name).any { name -> name == "Authors.openapi.json" } shouldBe false
    }

})

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
