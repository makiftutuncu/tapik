package dev.akif.tapik.plugin.maven

import dev.akif.tapik.test.fixtures.library.Authors
import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.test.fixtures.library.Rentals
import dev.akif.tapik.target.openapi.OpenApi
import dev.akif.tapik.target.openapi.toYaml
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
        generation.written shouldContainExactly
            listOf(
                output.resolve("Authors.openapi.yml"),
                output.resolve("Books.openapi.yml"),
                output.resolve("Rentals.openapi.yml")
            )
        Files.readString(generation.written[0]) shouldBe OpenApi.from(Authors, version = "0.6.0").toYaml()
        Files.readString(generation.written[1]) shouldBe OpenApi.from(Books, version = "0.6.0").toYaml()
        Files.readString(generation.written[2]) shouldBe OpenApi.from(Rentals, version = "0.6.0").toYaml()
    }

    test("apply API filters before generation") {
        val output = Files.createTempDirectory("tapik-maven-").apply { toFile().deleteOnExit() }

        val generation =
            MavenGenerator().generate(
                classpath = emptyList(),
                targetId = "openapi",
                targetConfiguration = mapOf("version" to "0.6.0"),
                includeApis = setOf("Authors", "Rentals"),
                excludeApis = setOf("Rentals"),
                outputDirectory = output,
                executionId = "openapi-filtered",
                parentClassLoader = MavenGeneratorSpec::class.java.classLoader,
                pluginVersion = "0.6.0",
                projectTapikVersions = setOf("0.6.0")
            )

        generation.written shouldContainExactly listOf(output.resolve("Authors.openapi.yml"))
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
            listOf(
                "META-INF/tapik/spring/webmvc/dev.akif.tapik.generated.AuthorsGeneratedController.properties",
                "META-INF/tapik/spring/webmvc/dev.akif.tapik.generated.BooksGeneratedController.properties",
                "META-INF/tapik/spring/webmvc/dev.akif.tapik.generated.RentalsGeneratedController.properties"
            )
    }

    test("keep the project class loader active while APIs are consumed") {
        val previous = Thread.currentThread().contextClassLoader

        ProjectApis.use(
            classpath = emptyList(),
            parentClassLoader = MavenGeneratorSpec::class.java.classLoader
        ) { apis, _ ->
            apis shouldContainExactly listOf(Authors, Books, Rentals)
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
            targetConfiguration =
                mapOf("version" to "0.6.0", "format" to "json", "output" to "old/{api}.json"),
            outputDirectory = output,
            executionId = "documentation",
            parentClassLoader = parentClassLoader,
            pluginVersion = "0.6.0",
            projectTapikVersions = setOf("0.6.0")
        )
        generator.generate(
            classpath = emptyList(),
            targetId = "openapi",
            targetConfiguration =
                mapOf("version" to "0.6.0", "format" to "json", "output" to "new/{api}.json"),
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
