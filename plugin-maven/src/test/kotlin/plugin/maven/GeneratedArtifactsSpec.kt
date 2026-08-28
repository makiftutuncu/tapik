package dev.akif.tapik.plugin.maven

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.apache.maven.project.MavenProject
import java.nio.file.Path

class GeneratedArtifactsSpec : FunSpec({
    test("register only generated runtime resources with Maven") {
        val project = MavenProject()
        val output = Path.of("target/generated/tapik").toAbsolutePath()
        val generation =
            MavenGeneration(
                written =
                    listOf(
                        output.resolve("generated/BooksServer.kt"),
                        output.resolve("META-INF/tapik/spring/webmvc/BooksGeneratedController.properties")
                    ),
                containsSources = true,
                resourcePaths = listOf("META-INF/tapik/spring/webmvc/BooksGeneratedController.properties")
            )

        project.registerGeneratedArtifacts(output, generation)

        project.compileSourceRoots shouldContainExactly listOf(output.toString())
        project.resources.single().run {
            directory shouldBe output.toString()
            includes shouldContainExactly generation.resourcePaths
        }
    }
})
