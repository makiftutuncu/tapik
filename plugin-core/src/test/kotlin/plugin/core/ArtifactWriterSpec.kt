package dev.akif.tapik.plugin.core

import io.kotest.assertions.throwables.shouldThrow
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

        val written = ArtifactWriter.write(result, output, owner = "openapi")

        written shouldContainExactly
            listOf(output.resolve("openapi/Books.json"), output.resolve("sources/Client.kt"))
        Files.readString(written[0]) shouldBe "{}"
        Files.readString(written[1]) shouldBe "class Client"
    }

    test("replace paths previously owned by the same execution") {
        val output = Files.createTempDirectory("tapik-artifacts-").apply { toFile().deleteOnExit() }
        val first =
            GenerationResult(
                listOf(GeneratedArtifact("old/Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class OldClient"))
            )
        val second =
            GenerationResult(
                listOf(GeneratedArtifact("new/Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class NewClient"))
            )

        ArtifactWriter.write(first, output, owner = "rest-client")
        ArtifactWriter.write(second, output, owner = "rest-client")

        Files.exists(output.resolve("old/Client.kt")) shouldBe false
        Files.readString(output.resolve("new/Client.kt")) shouldBe "class NewClient"
    }

    test("keep paths owned by other executions and unowned files") {
        val output = Files.createTempDirectory("tapik-artifacts-").apply { toFile().deleteOnExit() }
        val documentation =
            GenerationResult(
                listOf(GeneratedArtifact("Books.json", "application/json", ArtifactKind.DOCUMENTATION, "{}"))
            )
        val sources =
            GenerationResult(
                listOf(GeneratedArtifact("BooksClient.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class BooksClient"))
            )
        Files.writeString(output.resolve("README.txt"), "unowned")

        ArtifactWriter.write(documentation, output, owner = "openapi")
        ArtifactWriter.write(sources, output, owner = "rest-client")
        ArtifactWriter.write(GenerationResult(emptyList()), output, owner = "openapi")

        Files.exists(output.resolve("Books.json")) shouldBe false
        Files.readString(output.resolve("BooksClient.kt")) shouldBe "class BooksClient"
        Files.readString(output.resolve("README.txt")) shouldBe "unowned"
    }

    test("reject a path owned by another execution") {
        val output = Files.createTempDirectory("tapik-artifacts-").apply { toFile().deleteOnExit() }
        val first =
            GenerationResult(
                listOf(GeneratedArtifact("Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class FirstClient"))
            )
        val second =
            GenerationResult(
                listOf(GeneratedArtifact("Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class SecondClient"))
            )
        ArtifactWriter.write(first, output, owner = "first")

        shouldThrow<IllegalStateException> {
            ArtifactWriter.write(second, output, owner = "second")
        }

        Files.readString(output.resolve("Client.kt")) shouldBe "class FirstClient"
    }
})
