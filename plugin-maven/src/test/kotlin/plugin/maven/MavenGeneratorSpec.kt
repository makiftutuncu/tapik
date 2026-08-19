package dev.akif.tapik.plugin.maven

import dev.akif.tapik.fixtures.library.Books
import dev.akif.tapik.plugin.openapi.OpenApi
import dev.akif.tapik.plugin.openapi.toJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
                parentClassLoader = MavenGeneratorSpec::class.java.classLoader
            )

        generation.containsSources shouldBe false
        generation.written.single() shouldBe output.resolve("Books.openapi.json")
        Files.readString(generation.written.single()) shouldBe
            OpenApi.from(Books, version = "0.6.0").toJson()
    }

    test("keep the project class loader active while APIs are consumed") {
        val previous = Thread.currentThread().contextClassLoader

        ProjectApis.use(
            classpath = emptyList(),
            parentClassLoader = MavenGeneratorSpec::class.java.classLoader
        ) { apis ->
            apis.single() shouldBe Books
            Thread.currentThread().contextClassLoader shouldNotBe previous
        }

        Thread.currentThread().contextClassLoader shouldBe previous
    }
})
