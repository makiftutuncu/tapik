package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.exists

class CompiledContractWiringSpec : FunSpec({
    test("compile main source against generated declarations and package generated resources only") {
        val project = compiledContractProject()

        val (result, reused) = repeatWithConfigurationCache { project.run(":application:build") }

        result.task(":contract:compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":contract:jar")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":contract:tapikGenerate") shouldBe null
        result.task(":custom-target:jar")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":application:tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":application:compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":application:processResources")?.outcome shouldBe TaskOutcome.SUCCESS
        reused.task(":application:tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        project.path("application/build/classes/kotlin/main/com/example/application/CatalogHandler.class").exists() shouldBe true
        project.path("application/build/classes/kotlin/main/com/example/generated/contract/Catalog/CatalogServer.class").exists() shouldBe true
        project.path("application/build/classes/kotlin/main/com/example/generated/contract/Catalog/CatalogClient.class").exists() shouldBe true
        project.path("application/build/generated/tapik/documentation/Catalog.openapi.json").exists() shouldBe true
        project.path("application/build/generated/tapik/documentation/custom/Catalog.txt").exists() shouldBe true

        val jar = project.path("application/build/libs/application.jar")
        JarFile(jar.toFile()).use { archive ->
            val entries = archive.entries().asSequence().map { entry -> entry.name }.toList()
            entries.joinToString("\n") shouldContain
                "META-INF/tapik/spring/webmvc/com.example.generated.contract.Catalog.CatalogGeneratedController.properties"
            entries.joinToString("\n") shouldNotContain "Catalog.openapi.json"
            entries.joinToString("\n") shouldNotContain "custom/Catalog.txt"
        }
    }
})

private fun compiledContractProject(): CompiledContractProject {
    val root = Files.createTempDirectory("tapik-gradle-compiled-contract-")
    val project = CompiledContractProject(root)
    project.write("settings.gradle", "rootProject.name = 'fixture'\ninclude 'contract', 'custom-target', 'application'\n")
    project.write(
        "build.gradle",
        """
        allprojects {
            repositories {
                maven { url = uri('${compiledFixtureRepository.toUri()}') }
                mavenCentral()
            }
        }
        """.trimIndent()
    )
    project.write(
        "contract/build.gradle",
        """
        plugins {
            id 'org.jetbrains.kotlin.jvm'
            id 'dev.akif.tapik.plugin.gradle'
        }

        dependencies {
            implementation 'dev.akif:tapik-core:$compiledFixtureVersion'
        }
        """.trimIndent()
    )
    project.write(
        "contract/src/main/kotlin/com/example/contract/Catalog.kt",
        """
        package com.example.contract

        import dev.akif.tapik.*

        class Catalog : Api() {
            val list by get(root / "catalog")
        }
        """.trimIndent()
    )
    project.write(
        "custom-target/build.gradle",
        """
        plugins {
            id 'org.jetbrains.kotlin.jvm'
        }

        dependencies {
            implementation 'dev.akif:tapik-common-plugin:$compiledFixtureVersion'
        }
        """.trimIndent()
    )
    project.write(
        "custom-target/src/main/kotlin/com/example/target/FixtureTargetRegistry.kt",
        """
        package com.example.target

        import dev.akif.tapik.common.plugin.ArtifactKind
        import dev.akif.tapik.common.plugin.GeneratedArtifact
        import dev.akif.tapik.common.plugin.GenerationRequest
        import dev.akif.tapik.common.plugin.GenerationResult
        import dev.akif.tapik.common.plugin.GenerationTarget
        import dev.akif.tapik.common.plugin.GenerationTargetRegistry

        class FixtureTargetRegistry : GenerationTargetRegistry {
            override val targets = listOf(FixtureTarget)
        }

        object FixtureTarget : GenerationTarget {
            override val id = "fixture-docs"

            override fun generate(request: GenerationRequest) =
                GenerationResult(
                    request.apis.map { api ->
                        GeneratedArtifact(
                            relativePath = "custom/${'$'}{api.apiId}.txt",
                            mediaType = "text/plain",
                            kind = ArtifactKind.DOCUMENTATION,
                            content = api.apiId
                        )
                    }
                )
        }
        """.trimIndent()
    )
    project.write(
        "custom-target/src/main/resources/META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry",
        "com.example.target.FixtureTargetRegistry\n"
    )
    project.write(
        "application/build.gradle",
        """
        import dev.akif.tapik.plugin.gradle.Mode
        import dev.akif.tapik.plugin.gradle.OpenApiFormat

        plugins {
            id 'org.jetbrains.kotlin.jvm'
            id 'dev.akif.tapik.plugin.gradle'
        }

        dependencies {
            implementation project(':contract')
            implementation 'dev.akif:tapik-target-spring-restclient:$compiledFixtureVersion'
            implementation 'dev.akif:tapik-target-spring-webmvc:$compiledFixtureVersion'
            tapikContract project(':contract')
            tapikTarget project(':custom-target')
        }

        tapik {
            mode(Mode.COMPILED_CONTRACT)
            openApi('docs') {
                includeApis('Catalog')
                version('1.0.0')
                format(OpenApiFormat.JSON)
            }
            springRestClient('client') {
                includeApis('Catalog')
                packageName('com.example.generated')
            }
            springWebMvc('server') {
                includeApis('Catalog')
                packageName('com.example.generated')
            }
            target('custom-docs', 'fixture-docs') {
                includeApis('Catalog')
            }
        }
        """.trimIndent()
    )
    project.write(
        "application/src/main/kotlin/com/example/application/CatalogHandler.kt",
        """
        package com.example.application

        import com.example.contract.Catalog
        import com.example.generated.contract.Catalog.CatalogClient
        import com.example.generated.contract.Catalog.CatalogServer
        import com.example.generated.contract.Catalog.ListResponse

        class CatalogHandler : CatalogServer {
            override val catalogApi = Catalog()

            override fun list(): ListResponse = ListResponse.Ok
        }

        class CatalogClientConsumer(val client: CatalogClient)
        """.trimIndent()
    )
    return project
}

private class CompiledContractProject(private val root: Path) {
    fun write(relativePath: String, content: String) {
        val path = root.resolve(relativePath)
        Files.createDirectories(path.parent)
        Files.writeString(path, content + "\n")
    }

    fun path(relativePath: String): Path = root.resolve(relativePath)

    fun run(vararg tasks: String) =
        GradleRunner.create()
            .withProjectDir(root.toFile())
            .withArguments(*tasks, "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()
}

private val compiledFixtureRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })
private val compiledFixtureVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }
