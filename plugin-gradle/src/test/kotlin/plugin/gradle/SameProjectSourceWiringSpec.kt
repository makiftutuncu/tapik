package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarFile

class SameProjectSourceWiringSpec : FunSpec({
    test("compile and package same-project generated Kotlin for runtime use") {
        val project = sameProjectSourceFixture()

        val result = project.run("verifyTapikRuntime", "jar")

        result.task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":verifyTapikRuntime")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldContain "tapik late runtime OK"
        project.generatedDocumentation() shouldBe listOf("Books.openapi.json", "Catalog.openapi.json")
        project.generatedSources().count { source -> source.endsWith("/BooksEndpoints.kt") } shouldBe 1

        project.jarEntries().apply {
            shouldContain("com/example/generated/api/Books/BooksClient.class")
            shouldContain("com/example/generated/api/Books/BooksServer.class")
            shouldContain("com/example/generated/api/Books/BooksGeneratedController.class")
            shouldContain("com/example/generated/api/Books/ListResponse.class")
            shouldContain("com/example/generated/api/Books/ListResponse${'$'}Ok.class")
            shouldContain("META-INF/tapik/spring/webmvc/com.example.generated.api.Books.BooksGeneratedController.properties")
            shouldContain("com/example/application/RuntimeProbe.class")
            shouldNotContain("Books.openapi.json")
            filter { entry -> entry.endsWith(".kotlin_module") }.sorted() shouldBe
                listOf(
                    "META-INF/same-project-sources.kotlin_module",
                    "META-INF/same-project-sources_tapik_generated.kotlin_module"
                )
        }
    }

    test("remove stale late classes and resources without changing main output") {
        val project = sameProjectSourceFixture()
        project.run("jar")

        project.delete("src/main/kotlin/com/example/api/Books.kt")
        project.run("jar")

        project.jarEntries().apply {
            none { entry -> entry.startsWith("com/example/generated/api/Books/") && entry.endsWith(".class") } shouldBe true
            shouldNotContain("META-INF/tapik/spring/webmvc/com.example.generated.api.Books.BooksGeneratedController.properties")
            shouldContain("com/example/generated/api/Catalog/CatalogClient.class")
            shouldContain("com/example/generated/api/Catalog/CatalogServer.class")
            shouldContain("com/example/application/RuntimeProbe.class")
            filter { entry -> entry.endsWith(".kotlin_module") }.sorted() shouldBe
                listOf(
                    "META-INF/same-project-sources.kotlin_module",
                    "META-INF/same-project-sources_tapik_generated.kotlin_module"
                )
        }

        project.writeBuild(includeSpringTargets = false)
        val configurationChange = project.run("jar")

        configurationChange.task(":compileKotlin")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        configurationChange.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        configurationChange.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        project.jarEntries().apply {
            none { entry -> entry.startsWith("com/example/generated/") && entry.endsWith(".class") } shouldBe true
            none { entry -> entry.startsWith("META-INF/tapik/spring/webmvc/") } shouldBe true
            shouldContain("com/example/application/RuntimeProbe.class")
            filter { entry -> entry.endsWith(".kotlin_module") } shouldBe
                listOf("META-INF/same-project-sources.kotlin_module")
        }
    }

    test("explain why main source cannot use same-project generated declarations") {
        val project = sameProjectSourceFixture()
        project.writeMainSourceUsingGeneratedType()

        val result = project.runAndFail("compileKotlin")

        result.output shouldContain "Unresolved reference 'BooksClient'"
        result.output shouldContain
            "tapik same-project generated declarations are unavailable to ordinary main source; " +
            "move the API contract to a separately compiled project and use Mode.COMPILED_CONTRACT"
    }
})

private fun sameProjectSourceFixture(): SameProjectSourceFixture {
    val fixture = SameProjectSourceFixture(Files.createTempDirectory("tapik-gradle-same-project-sources-"))
    fixture.write("settings.gradle", "rootProject.name = 'same-project-sources'\n")
    fixture.writeBuild()
    fixture.writeApi("Catalog")
    fixture.writeApi("Books")
    fixture.write(
        "src/main/kotlin/com/example/application/RuntimeProbe.kt",
        """
        package com.example.application

        object RuntimeProbe {
            @JvmStatic
            fun main(args: Array<String>) {
                listOf(
                    "com.example.generated.api.Books.BooksClient",
                    "com.example.generated.api.Books.BooksServer",
                    "com.example.generated.api.Books.BooksGeneratedController",
                    "com.example.generated.api.Books.ListResponse" + 36.toChar() + "Ok"
                ).forEach { typeName -> Class.forName(typeName) }
                checkNotNull(
                    RuntimeProbe::class.java.classLoader.getResource(
                        "META-INF/tapik/spring/webmvc/" +
                            "com.example.generated.api.Books.BooksGeneratedController.properties"
                    )
                )
                println("tapik late runtime OK")
            }
        }
        """.trimIndent()
    )
    return fixture
}

private class SameProjectSourceFixture(private val root: Path) {
    fun writeBuild(includeSpringTargets: Boolean = true) {
        val springTargets =
            if (includeSpringTargets) {
                """
                springRestClient('client') {
                    packageName('com.example.generated')
                }
                springWebMvc('server') {
                    packageName('com.example.generated')
                }
                """.trimIndent()
            } else {
                ""
            }
        write(
            "build.gradle",
            """
            import dev.akif.tapik.plugin.gradle.Mode
            import dev.akif.tapik.plugin.gradle.OpenApiFormat

            plugins {
                id 'org.jetbrains.kotlin.jvm'
                id 'dev.akif.tapik.plugin.gradle'
            }

            repositories {
                maven { url = uri('${sameProjectSourceRepository.toUri()}') }
                mavenCentral()
            }

            dependencies {
                implementation 'dev.akif:tapik-core:$sameProjectSourceVersion'
                implementation 'dev.akif:tapik-target-spring-restclient:$sameProjectSourceVersion'
                implementation 'dev.akif:tapik-target-spring-webmvc:$sameProjectSourceVersion'
            }

            tapik {
                mode(Mode.SAME_PROJECT)
                openApi('docs') {
                    version('1.0.0')
                    format(OpenApiFormat.JSON)
                }
            ${springTargets.prependIndent("    ")}
            }

            tasks.register('verifyTapikRuntime', JavaExec) {
                classpath = sourceSets.main.runtimeClasspath
                mainClass = 'com.example.application.RuntimeProbe'
            }
            """.trimIndent()
        )
    }

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

    fun writeMainSourceUsingGeneratedType() {
        write(
            "src/main/kotlin/com/example/application/GeneratedConsumer.kt",
            """
            package com.example.application

            import com.example.generated.api.Books.BooksClient

            class GeneratedConsumer(val client: BooksClient)
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

    fun generatedDocumentation(): List<String> =
        Files.list(root.resolve("build/generated/tapik/documentation")).use { files ->
            files.map { path -> path.fileName.toString() }.sorted().toList()
        }

    fun generatedSources(): List<String> =
        Files.walk(root.resolve("build/generated/tapik/sources")).use { files ->
            files.filter(Files::isRegularFile)
                .map { path -> root.relativize(path).toString().replace('\\', '/') }
                .sorted()
                .toList()
        }

    fun jarEntries(): List<String> =
        JarFile(root.resolve("build/libs/same-project-sources.jar").toFile()).use { jar ->
            jar.entries().asSequence().map { entry -> entry.name }.toList()
        }

    fun run(vararg tasks: String) = runner(tasks).build()

    fun runAndFail(vararg tasks: String) = runner(tasks).buildAndFail()

    private fun runner(tasks: Array<out String>): GradleRunner =
        GradleRunner.create()
            .withProjectDir(root.toFile())
            .withArguments(*tasks, "--stacktrace")
            .withPluginClasspath()
}

private val sameProjectSourceRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })
private val sameProjectSourceVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }
