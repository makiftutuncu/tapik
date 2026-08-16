package dev.akif.tapik.plugin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.nio.file.Files

class ArtifactWriterSpec : FunSpec({
    test("write generated artifacts beneath one output directory") {
        val output = Files.createTempDirectory("tapik-artifacts-").apply { toFile().deleteOnExit() }
        val result =
            GenerationResult(
                listOf(
                    GeneratedArtifact("openapi/Books.json", "application/json", ArtifactKind.DOCUMENTATION, "{}"),
                    GeneratedArtifact("sources/Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class Client")
                )
            )

        val written = ArtifactWriter.write(result, output)

        written shouldContainExactly
            listOf(output.resolve("openapi/Books.json"), output.resolve("sources/Client.kt"))
        Files.readString(written[0]) shouldBe "{}"
        Files.readString(written[1]) shouldBe "class Client"
    }
})
