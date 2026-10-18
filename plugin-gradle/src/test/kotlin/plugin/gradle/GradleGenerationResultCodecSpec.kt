package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Files

class GradleGenerationResultCodecSpec : FunSpec({
    test("round trip complete ordered generation results") {
        val result =
            GenerationResult(
                listOf(
                    GeneratedArtifact(
                        relativePath = "first.kt",
                        mediaType = "text/x-kotlin",
                        kind = ArtifactKind.SOURCE,
                        content = "class First",
                        sharingKey = "shared"
                    ),
                    GeneratedArtifact(
                        relativePath = "second.yml",
                        mediaType = "application/yaml",
                        kind = ArtifactKind.DOCUMENTATION,
                        content = "openapi: 3.1.0"
                    )
                )
            )
        val file = Files.createTempFile("tapik-gradle-result-", ".bin")

        GradleGenerationResultCodec.write(file, result)

        GradleGenerationResultCodec.read(file) shouldBe result
    }
})
