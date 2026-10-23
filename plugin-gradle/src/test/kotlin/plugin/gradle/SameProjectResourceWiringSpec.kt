package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.exists
import kotlin.io.path.name

class SameProjectResourceWiringSpec : FunSpec({
    test("generate documentation and package resources from the same project on clean and non-clean builds") {
        val project = sameProjectFixture()

        val first = project.run("tapikGenerate")

        first.task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        first.task(":processResources")?.outcome shouldBe TaskOutcome.NO_SOURCE
        first.task(":classes")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        first.task(":compileTapikGeneratedKotlin") shouldBe null
        project.generatedDocumentation() shouldBe listOf("Catalog.openapi.json")
        project.generatedResources() shouldBe listOf("Books.txt", "Catalog.txt")

        val jarResult = project.run("jar")
        jarResult.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        val jar = project.path("build/libs/same-project.jar")
        JarFile(jar.toFile()).use { archive ->
            val entries = archive.entries().asSequence().map { entry -> entry.name }.toList()
            entries shouldContain "META-INF/tapik-fixture/Books.txt"
            entries shouldContain "META-INF/tapik-fixture/Catalog.txt"
            entries shouldNotContain "Catalog.openapi.json"
        }

        project.delete("src/main/kotlin/com/example/api/Books.kt")
        project.writeApi("Authors")
        val changed = project.run("tapikGenerate")

        changed.task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        changed.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        project.generatedDocumentation() shouldBe listOf("Catalog.openapi.json")
        project.generatedResources() shouldBe listOf("Authors.txt", "Catalog.txt")
    }
})

private fun sameProjectFixture(): SameProjectFixture {
    val root = Files.createTempDirectory("tapik-gradle-same-project-")
    val fixture = SameProjectFixture(root)
    fixture.write(
        "settings.gradle",
        "rootProject.name = 'same-project'\ninclude 'resource-target'\n"
    )
    fixture.write(
        "build.gradle",
        """
        import dev.akif.tapik.plugin.gradle.Mode
        import dev.akif.tapik.plugin.gradle.OpenApiFormat

        plugins {
            id 'org.jetbrains.kotlin.jvm'
            id 'dev.akif.tapik.plugin.gradle'
        }

        repositories {
            maven { url = uri('${sameProjectRepository.toUri()}') }
            mavenCentral()
        }

        dependencies {
            implementation 'dev.akif:tapik-core:$sameProjectVersion'
            tapikTarget project(':resource-target')
        }

        tapik {
            mode(Mode.SAME_PROJECT)
            openApi('docs') {
                includeApis('Catalog')
                version('1.0.0')
                format(OpenApiFormat.JSON)
            }
            target('resources', 'fixture-resources') {
            }
        }
        """.trimIndent()
    )
    fixture.write(
        "resource-target/build.gradle",
        """
        plugins {
            id 'org.jetbrains.kotlin.jvm'
        }

        repositories {
            maven { url = uri('${sameProjectRepository.toUri()}') }
            mavenCentral()
        }

        dependencies {
            implementation 'dev.akif:tapik-common-plugin:$sameProjectVersion'
        }
        """.trimIndent()
    )
    fixture.write(
        "resource-target/src/main/kotlin/com/example/target/ResourceTargetRegistry.kt",
        """
        package com.example.target

        import dev.akif.tapik.common.plugin.ArtifactKind
        import dev.akif.tapik.common.plugin.GeneratedArtifact
        import dev.akif.tapik.common.plugin.GenerationRequest
        import dev.akif.tapik.common.plugin.GenerationResult
        import dev.akif.tapik.common.plugin.GenerationTarget
        import dev.akif.tapik.common.plugin.GenerationTargetRegistry

        class ResourceTargetRegistry : GenerationTargetRegistry {
            override val targets = listOf(ResourceTarget)
        }

        object ResourceTarget : GenerationTarget {
            override val id = "fixture-resources"

            override fun generate(request: GenerationRequest) =
                GenerationResult(
                    request.apis.map { api ->
                        GeneratedArtifact(
                            relativePath = "META-INF/tapik-fixture/${'$'}{api.apiId}.txt",
                            mediaType = "text/plain",
                            kind = ArtifactKind.RESOURCE,
                            content = api.apiId
                        )
                    }
                )
        }
        """.trimIndent()
    )
    fixture.write(
        "resource-target/src/main/resources/META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry",
        "com.example.target.ResourceTargetRegistry\n"
    )
    fixture.writeApi("Catalog")
    fixture.writeApi("Books")
    return fixture
}

private class SameProjectFixture(private val root: Path) {
    fun writeApi(name: String) {
        write(
            "src/main/kotlin/com/example/api/$name.kt",
            """
            package com.example.api

            import dev.akif.tapik.*

            class $name : Api() {
                val list by get(root / "${name.lowercase()}")
            }
            """.trimIndent()
        )
    }

    fun write(relativePath: String, content: String) {
        val path = root.resolve(relativePath)
        Files.createDirectories(path.parent)
        Files.writeString(path, content + "\n")
    }

    fun delete(relativePath: String) {
        Files.delete(root.resolve(relativePath))
    }

    fun path(relativePath: String): Path = root.resolve(relativePath)

    fun generatedDocumentation(): List<String> = generatedFiles("documentation")

    fun generatedResources(): List<String> = generatedFiles("resources/META-INF/tapik-fixture")

    fun run(vararg tasks: String) =
        GradleRunner.create()
            .withProjectDir(root.toFile())
            .withArguments(*tasks, "--stacktrace")
            .withPluginClasspath()
            .build()

    private fun generatedFiles(directory: String): List<String> {
        val path = root.resolve("build/generated/tapik/$directory")
        if (!path.exists()) return emptyList()
        return Files.list(path).use { files -> files.map(Path::name).sorted().toList() }
    }
}

private val sameProjectRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })
private val sameProjectVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }
