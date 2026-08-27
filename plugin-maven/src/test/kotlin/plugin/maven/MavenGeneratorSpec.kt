package dev.akif.tapik.plugin.maven

import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.plugin.openapi.OpenApi
import dev.akif.tapik.plugin.openapi.toJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.shouldContain
import java.nio.file.Files

class MavenGeneratorSpec : FunSpec({
    test("generate OpenAPI from every registered project API") {
        val output = Files.createTempDirectory("tapik-maven-").apply { toFile().deleteOnExit() }

        val generation =
            MavenGenerator().generate(
                classpath = emptyList(),
                targetId = "openapi",
                targetConfiguration = mapOf("version" to "0.6.0"),
                outputDirectory = output,
                executionId = "openapi",
                parentClassLoader = MavenGeneratorSpec::class.java.classLoader,
                pluginVersion = "0.6.0",
                projectTapikVersions = setOf("0.6.0")
            )

        generation.containsSources shouldBe false
        generation.written.single() shouldBe output.resolve("Books.openapi.json")
        Files.readString(generation.written.single()) shouldBe
            OpenApi.from(Books, version = "0.6.0").toJson()
    }

    test("retain generated runtime resource paths for the Maven host") {
        val output = Files.createTempDirectory("tapik-maven-").apply { toFile().deleteOnExit() }

        val generation =
            MavenGenerator().generate(
                classpath = emptyList(),
                targetId = "spring-webmvc",
                targetConfiguration = mapOf("packageName" to "dev.akif.tapik.generated"),
                outputDirectory = output,
                executionId = "webmvc",
                parentClassLoader = MavenGeneratorSpec::class.java.classLoader,
                pluginVersion = "0.6.0",
                projectTapikVersions = setOf("0.6.0")
            )

        generation.containsSources shouldBe true
        generation.resourcePaths shouldContainExactly
            listOf("META-INF/tapik/spring/webmvc/dev.akif.tapik.generated.BooksGeneratedController.imports")
    }

    test("keep the project class loader active while APIs are consumed") {
        val previous = Thread.currentThread().contextClassLoader

        ProjectApis.use(
            classpath = emptyList(),
            parentClassLoader = MavenGeneratorSpec::class.java.classLoader
        ) { apis, _ ->
            apis.single() shouldBe Books
            Thread.currentThread().contextClassLoader shouldNotBe previous
        }

        Thread.currentThread().contextClassLoader shouldBe previous
    }

    test("replace artifacts from the same Maven execution") {
        val output = Files.createTempDirectory("tapik-maven-").apply { toFile().deleteOnExit() }
        val generator = MavenGenerator()
        val parentClassLoader = MavenGeneratorSpec::class.java.classLoader

        generator.generate(
            classpath = emptyList(),
            targetId = "openapi",
            targetConfiguration = mapOf("version" to "0.6.0", "output" to "old/{api}.json"),
            outputDirectory = output,
            executionId = "documentation",
            parentClassLoader = parentClassLoader,
            pluginVersion = "0.6.0",
            projectTapikVersions = setOf("0.6.0")
        )
        generator.generate(
            classpath = emptyList(),
            targetId = "openapi",
            targetConfiguration = mapOf("version" to "0.6.0", "output" to "new/{api}.json"),
            outputDirectory = output,
            executionId = "documentation",
            parentClassLoader = parentClassLoader,
            pluginVersion = "0.6.0",
            projectTapikVersions = setOf("0.6.0")
        )

        Files.exists(output.resolve("old/Books.json")) shouldBe false
        Files.exists(output.resolve("new/Books.json")) shouldBe true
    }

    test("reject version skew before loading APIs or targets") {
        val output = Files.createTempDirectory("tapik-maven-").apply { toFile().deleteOnExit() }

        val error =
            shouldThrow<IllegalArgumentException> {
                MavenGenerator().generate(
                    classpath = listOf(output.resolve("missing-project-classpath")),
                    targetId = "missing",
                    targetConfiguration = emptyMap(),
                    outputDirectory = output,
                    executionId = "version-check",
                    parentClassLoader = MavenGeneratorSpec::class.java.classLoader,
                    pluginVersion = "0.6.0",
                    projectTapikVersions = setOf("0.5.0")
                )
            }

        error.message shouldContain "Tapik version mismatch"
    }
})
