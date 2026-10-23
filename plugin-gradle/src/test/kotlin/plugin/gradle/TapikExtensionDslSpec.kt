package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files

class TapikExtensionDslSpec : FunSpec({
    test("configure typed executions from Kotlin DSL") {
        val project = Files.createTempDirectory("tapik-gradle-kotlin-dsl-")
        Files.writeString(project.resolve("settings.gradle.kts"), "rootProject.name = \"consumer\"\n")
        Files.writeString(
            project.resolve("build.gradle.kts"),
            """
            import dev.akif.tapik.plugin.gradle.OpenApiComponentNaming
            import dev.akif.tapik.plugin.gradle.OpenApiFormat
            import dev.akif.tapik.plugin.gradle.Mode

            plugins {
                id("org.jetbrains.kotlin.jvm")
                id("dev.akif.tapik.plugin.gradle")
            }

            tapik {
                mode(Mode.COMPILED_CONTRACT)
                outputRoot(layout.buildDirectory.dir("generated/contracts"))

                openApi("public") {
                    includeApis("Catalog")
                    version("1.0.0")
                    format(OpenApiFormat.JSON)
                    componentNaming(OpenApiComponentNaming.QUALIFIED)
                }

                springRestClient("client") {
                    clientSuffix("Client")
                }
            }
            """.trimIndent()
        )

        fun build() =
            GradleRunner
                .create()
                .withProjectDir(project.toFile())
                .withArguments("tasks", "--configuration-cache", "--stacktrace")
                .withPluginClasspath()
                .build()

        val (result, _) = repeatWithConfigurationCache(::build)

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    test("configure typed executions from Groovy DSL") {
        val project = Files.createTempDirectory("tapik-gradle-groovy-dsl-")
        Files.writeString(project.resolve("settings.gradle"), "rootProject.name = 'consumer'\n")
        Files.writeString(
            project.resolve("build.gradle"),
            """
            import dev.akif.tapik.plugin.gradle.OpenApiFormat
            import dev.akif.tapik.plugin.gradle.Mode

            plugins {
                id 'org.jetbrains.kotlin.jvm'
                id 'dev.akif.tapik.plugin.gradle'
            }

            tapik {
                mode(Mode.SAME_PROJECT)

                openApi('docs') {
                    version('1.0.0')
                    format(OpenApiFormat.YAML)
                }

                springWebMvc('server') {
                    includeApis('Books')
                    packageName('com.example.generated')
                    serverSuffix('Server')
                    controllerSuffix('GeneratedController')
                }
            }
            """.trimIndent()
        )

        fun build() =
            GradleRunner
                .create()
                .withProjectDir(project.toFile())
                .withArguments("tasks", "--configuration-cache", "--stacktrace")
                .withPluginClasspath()
                .build()

        val (result, _) = repeatWithConfigurationCache(::build)

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
    }
})
