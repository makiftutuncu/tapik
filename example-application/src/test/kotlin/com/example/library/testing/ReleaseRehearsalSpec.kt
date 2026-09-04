package com.example.library.testing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.nio.file.Files
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes

class ReleaseRehearsalSpec : FunSpec({
    test("exclude cached tapik artifacts when preparing the isolated consumer repository") {
        val cache = Files.createTempDirectory("tapik-cache-test-")
        val repository = Files.createTempDirectory("tapik-repository-test-")
        listOf("dev/akif/tapik-core/0.6.0/core.jar", "org/example/library/1/library.jar", "org/example/library/1/library.pom")
            .forEach { name ->
                val file = cache.resolve(name)
                Files.createDirectories(file.parent)
                Files.writeString(file, "fixture")
            }
        seedThirdPartyArtifacts(cache, repository)

        Files.exists(repository.resolve("dev/akif")) shouldBe false
        Files.readString(repository.resolve("org/example/library/1/library.jar")) shouldBe "fixture"
        Files.readString(repository.resolve("org/example/library/1/library.pom")) shouldBe "fixture"
    }

    test("reject a release without packaged source and documentation artifacts") {
        val root = Files.createTempDirectory("tapik-release-test-")
        Files.writeString(root.resolve("pom.xml"), """
            <project><groupId>dev.akif</groupId><artifactId>tapik-parent</artifactId><version>0.6.0</version>
            <modules><module>core</module></modules></project>
        """.trimIndent())
        Files.createDirectories(root.resolve("core"))
        Files.writeString(root.resolve("core/pom.xml"), "<project><artifactId>tapik-core</artifactId></project>")

        shouldThrow<IllegalStateException> {
            stageReleaseArtifacts(root, Files.createTempDirectory("tapik-stage-test-"))
        }.message shouldContain "release"
    }

    test("consume release artifacts from an independent Maven project")
        .config(enabled = java.lang.Boolean.getBoolean("tapik.release.rehearsal"), timeout = 20.minutes) {
            rehearseRelease(Path.of("..").toAbsolutePath().normalize())
        }
})
