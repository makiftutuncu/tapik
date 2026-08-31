package dev.akif.tapik.plugin.maven

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Files

class GeneratedResourcesSpec : FunSpec({
    test("copy only declared generated resources into main output") {
        val workspace = Files.createTempDirectory("tapik-generated-resources-").apply { toFile().deleteOnExit() }
        val generationDirectory = workspace.resolve("generated")
        val outputDirectory = workspace.resolve("classes")
        val resource = "META-INF/tapik/example.properties"
        Files.createDirectories(generationDirectory.resolve("META-INF/tapik"))
        Files.writeString(generationDirectory.resolve(resource), "type=example.Generated")
        Files.writeString(generationDirectory.resolve("Generated.kt"), "public class Generated")

        copyGeneratedResources(generationDirectory, listOf(resource), outputDirectory)

        Files.readString(outputDirectory.resolve(resource)) shouldBe "type=example.Generated"
        Files.exists(outputDirectory.resolve("Generated.kt")) shouldBe false
    }
})
