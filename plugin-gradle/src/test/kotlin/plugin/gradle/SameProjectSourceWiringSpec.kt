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
import java.util.zip.ZipFile

class SameProjectSourceWiringSpec : FunSpec({
    test("generate only when tapikGenerate is requested directly") {
        val project = sameProjectSourceFixture()

        val result = project.run("tapikGenerate")

        result.task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":classes") shouldBe null
        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":compileTapikGeneratedKotlin") shouldBe null
        project.generatedSources().any { source -> source.endsWith("/BooksClient.kt") } shouldBe true
        project.generatedClassFiles() shouldBe emptyList()
    }

    test("build late output before a custom main-output consumer") {
        val project = sameProjectSourceFixture()

        val result = project.run("copyMainOutput")

        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        project.copiedMainOutputEntries().apply {
            shouldContain("com/example/generated/api/Books/BooksClient.class")
            shouldContain(
                "META-INF/tapik/spring/webmvc/" +
                    "com.example.generated.api.Books.BooksGeneratedController.properties"
            )
        }

        project.delete("src/main/kotlin/com/example/api/Books.kt")
        val changed = project.run("copyMainOutput")

        changed.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        changed.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        project.copiedMainOutputEntries().apply {
            shouldNotContain("com/example/generated/api/Books/BooksClient.class")
            shouldNotContain(
                "META-INF/tapik/spring/webmvc/" +
                    "com.example.generated.api.Books.BooksGeneratedController.properties"
            )
            shouldContain("com/example/generated/api/Catalog/CatalogClient.class")
        }
    }

    test("build late output before a custom archive packages main output") {
        val project = sameProjectSourceFixture()

        val result = project.run("packageMainOutput")

        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        project.packagedMainOutputEntries().apply {
            shouldContain("com/example/generated/api/Books/BooksServer.class")
            shouldContain(
                "META-INF/tapik/spring/webmvc/" +
                    "com.example.generated.api.Books.BooksGeneratedController.properties"
            )
        }

        project.writeBuild(includeSpringTargets = false)
        val changed = project.run("packageMainOutput")

        changed.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        changed.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        project.packagedMainOutputEntries().apply {
            none { entry -> entry.startsWith("com/example/generated/") && entry.endsWith(".class") } shouldBe true
            none { entry -> entry.startsWith("META-INF/tapik/spring/webmvc/") } shouldBe true
            shouldContain("com/example/application/RuntimeProbe.class")
        }
    }

    test("compile and package same-project generated Kotlin for runtime use") {
        val project = sameProjectSourceFixture()

        val (result, reused) = repeatWithConfigurationCache { project.run("verifyTapikRuntime", "jar") }

        result.task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":verifyTapikRuntime")?.outcome shouldBe TaskOutcome.SUCCESS
        reused.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        reused.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.UP_TO_DATE
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

    test("load contracts and compile generated source with compileOnly dependencies") {
        val project = sameProjectSourceFixture(includeCompileOnlyModel = true)

        val result = project.run("jar")

        result.task(":tapik-compile-only-model:classes")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":compileTapikGeneratedKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        project.jarEntries().apply {
            shouldContain("com/example/generated/api/CompileOnly/CompileOnlyClient.class")
            shouldContain("com/example/generated/api/CompileOnly/CompileOnlyServer.class")
            none { entry -> entry == "com/example/compileonly/ExternalModel.class" } shouldBe true
        }
    }

    test("validate tapik versions on the same-project compile classpath") {
        val project =
            sameProjectSourceFixture(
                includeCompileOnlyModel = true,
                compileOnlyModelVersion = "9.9.9"
            )

        val result = project.runAndFail("tapikGenerate")

        result.output shouldContain "incompatible with resolved dependencies"
        result.output shouldContain "mainCompileClasspath"
        result.output shouldContain "dev.akif:tapik-compile-only-model:9.9.9"
        result.output shouldContain "project :tapik-compile-only-model"
    }
})

private fun sameProjectSourceFixture(
    includeCompileOnlyModel: Boolean = false,
    compileOnlyModelVersion: String = sameProjectSourceVersion
): SameProjectSourceFixture {
    val fixture =
        SameProjectSourceFixture(
            Files.createTempDirectory("tapik-gradle-same-project-sources-"),
            if (includeCompileOnlyModel) compileOnlyModelVersion else null
        )
    fixture.write(
        "settings.gradle",
        buildString {
            appendLine("rootProject.name = 'same-project-sources'")
            if (includeCompileOnlyModel) appendLine("include 'tapik-compile-only-model'")
        }
    )
    if (includeCompileOnlyModel) {
        fixture.writeCompileOnlyModel()
    }
    fixture.writeBuild()
    fixture.writeApi("Catalog")
    fixture.writeApi("Books")
    if (includeCompileOnlyModel) {
        fixture.writeCompileOnlyApi()
    }
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

private class SameProjectSourceFixture(
    private val root: Path,
    private val compileOnlyModelVersion: String?
) {
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
                ${if (compileOnlyModelVersion != null) "compileOnly project(':tapik-compile-only-model')" else ""}
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

            tasks.register('copyMainOutput', Sync) {
                from sourceSets.main.output
                into layout.buildDirectory.dir('copied-main-output')
            }

            tasks.register('packageMainOutput', Zip) {
                from sourceSets.main.output
                archiveFileName = 'main-output.zip'
                destinationDirectory = layout.buildDirectory.dir('archives')
            }
            """.trimIndent()
        )
    }

    fun writeCompileOnlyModel() {
        write(
            "tapik-compile-only-model/build.gradle",
            """
            plugins {
                id 'java-library'
            }

            group = 'dev.akif'
            version = '$compileOnlyModelVersion'

            java {
                toolchain {
                    languageVersion = JavaLanguageVersion.of(25)
                }
            }
            """.trimIndent()
        )
        write(
            "tapik-compile-only-model/src/main/java/com/example/compileonly/ExternalModel.java",
            """
            package com.example.compileonly;

            public record ExternalModel(String value) {}
            """.trimIndent()
        )
    }

    fun writeCompileOnlyApi() {
        write(
            "src/main/kotlin/com/example/api/CompileOnly.kt",
            """
            package com.example.api

            import com.example.compileonly.ExternalModel
            import dev.akif.tapik.*

            class CompileOnly : Api() {
                val external =
                    path(
                        "external",
                        format.string.transform(
                            decode = ::ExternalModel,
                            encode = ExternalModel::value
                        )
                    )
                val find by get(root / "compile-only" / external)
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

    fun generatedClassFiles(): List<String> {
        val output = root.resolve("build/classes/kotlin/tapikGenerated")
        if (Files.notExists(output)) return emptyList()
        return Files.walk(output).use { files ->
            files.filter(Files::isRegularFile)
                .map { path -> output.relativize(path).toString().replace('\\', '/') }
                .sorted()
                .toList()
        }
    }

    fun jarEntries(): List<String> =
        JarFile(root.resolve("build/libs/same-project-sources.jar").toFile()).use { jar ->
            jar.entries().asSequence().map { entry -> entry.name }.toList()
        }

    fun copiedMainOutputEntries(): List<String> {
        val output = root.resolve("build/copied-main-output")
        return Files.walk(output).use { files ->
            files.filter(Files::isRegularFile)
                .map { path -> output.relativize(path).toString().replace('\\', '/') }
                .sorted()
                .toList()
        }
    }

    fun packagedMainOutputEntries(): List<String> =
        ZipFile(root.resolve("build/archives/main-output.zip").toFile()).use { archive ->
            archive.entries().asSequence().map { entry -> entry.name }.toList()
        }

    fun run(vararg tasks: String) = runner(tasks).build()

    fun runAndFail(vararg tasks: String) = runner(tasks).buildAndFail()

    private fun runner(tasks: Array<out String>): GradleRunner =
        GradleRunner.create()
            .withProjectDir(root.toFile())
            .withArguments(*tasks, "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
}

private val sameProjectSourceRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })
private val sameProjectSourceVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }
