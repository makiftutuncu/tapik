package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import kotlin.io.path.readText

class TapikExtensionDslSpec : FunSpec({
    test("configure typed executions from Kotlin DSL") {
        val project = Files.createTempDirectory("tapik-gradle-kotlin-dsl-")
        Files.writeString(project.resolve("settings.gradle.kts"), "rootProject.name = \"consumer\"\n")
        Files.writeString(project.resolve("build.gradle.kts"), dslExample("kotlin.gradle.kts"))

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
        Files.writeString(project.resolve("build.gradle"), dslExample("groovy.gradle"))

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

private fun dslExample(name: String): String =
    requireNotNull(TapikExtensionDslSpec::class.java.getResource("/examples/gradle/$name")) {
        "Missing Gradle DSL example '$name'"
    }.toURI().let(java.nio.file.Path::of).readText()
