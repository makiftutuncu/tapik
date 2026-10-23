package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.util.Properties

class TapikGradlePluginSpec : FunSpec({
    test("publish the historical plugin descriptor") {
        val properties = Properties()
        val descriptorPath = "META-INF/gradle-plugins/dev.akif.tapik.plugin.gradle.properties"
        val descriptor =
            requireNotNull(javaClass.classLoader.getResourceAsStream(descriptorPath))
        descriptor.use(properties::load)

        properties.getProperty("implementation-class") shouldBe TapikGradlePlugin::class.qualifiedName
    }

    test("apply the historical plugin ID") {
        val project = Files.createTempDirectory("tapik-gradle-plugin-")
        Files.writeString(project.resolve("settings.gradle.kts"), "rootProject.name = \"consumer\"\n")
        Files.writeString(
            project.resolve("build.gradle.kts"),
            """
            plugins {
                id("dev.akif.tapik.plugin.gradle")
            }
            """.trimIndent()
        )

        fun build() =
            GradleRunner
                .create()
                .withProjectDir(project.toFile())
                .withArguments("help", "--configuration-cache", "--stacktrace")
                .withPluginClasspath()
                .build()

        val (result, _) = repeatWithConfigurationCache(::build)

        result.task(":help")?.outcome shouldBe TaskOutcome.SUCCESS
    }
})
