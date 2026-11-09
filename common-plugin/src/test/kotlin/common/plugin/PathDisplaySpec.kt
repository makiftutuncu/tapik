package dev.akif.tapik.common.plugin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files

class PathDisplaySpec : FunSpec({
    test("display paths relative to the project directory") {
        val project = Files.createTempDirectory("tapik-path-display-")
        val generated = project.resolve("build/generated/tapik/Authors.yml")

        generated.displayRelativeTo(project) shouldBe "build/generated/tapik/Authors.yml"
    }

    test("fall back to an absolute path across filesystem providers") {
        val project = Files.createTempDirectory("tapik-path-display-")
        val archive = Files.createTempFile("tapik-path-display-", ".zip")
        Files.delete(archive)
        FileSystems.newFileSystem(URI.create("jar:${archive.toUri()}"), mapOf("create" to "true")).use { fileSystem ->
            val generated = fileSystem.getPath("/generated/Authors.yml")

            generated.displayRelativeTo(project) shouldBe
                generated.toAbsolutePath().normalize().toString().replace(generated.fileSystem.separator, "/")
        }
    }
})
