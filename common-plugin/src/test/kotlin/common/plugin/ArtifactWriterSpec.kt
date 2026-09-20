package dev.akif.tapik.common.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
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
                listOf(
                    GeneratedArtifact("old/Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class OldClient"),
                    GeneratedArtifact("Shared.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class SharedV1")
                )
            )
        val second =
            GenerationResult(
                listOf(
                    GeneratedArtifact("new/Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class NewClient"),
                    GeneratedArtifact("Shared.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class SharedV2")
                )
            )

        ArtifactWriter.write(first, output, owner = "rest-client")
        ArtifactWriter.write(second, output, owner = "rest-client")

        Files.exists(output.resolve("old/Client.kt")) shouldBe false
        Files.readString(output.resolve("new/Client.kt")) shouldBe "class NewClient"
        Files.readString(output.resolve("Shared.kt")) shouldBe "class SharedV2"
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

    test("share an explicitly identified artifact between executions") {
        val output = Files.createTempDirectory("tapik-artifacts-").apply { toFile().deleteOnExit() }
        fun shared(content: String) =
            GenerationResult(
                listOf(
                    GeneratedArtifact(
                        "BooksEndpoints.kt",
                        "text/x-kotlin",
                        ArtifactKind.SOURCE,
                        content,
                        sharingKey = "endpoint-types:books"
                    )
                )
            )

        ArtifactWriter.write(shared("sealed interface ListResponse"), output, owner = "rest-client")
        ArtifactWriter.write(shared("sealed interface ListResponse"), output, owner = "webmvc")
        ArtifactWriter.write(shared("sealed interface UpdatedListResponse"), output, owner = "rest-client")
        ArtifactWriter.write(shared("sealed interface UpdatedListResponse"), output, owner = "webmvc")
        ArtifactWriter.write(GenerationResult(emptyList()), output, owner = "rest-client")

        Files.readString(output.resolve("BooksEndpoints.kt")) shouldBe "sealed interface UpdatedListResponse"

        ArtifactWriter.write(GenerationResult(emptyList()), output, owner = "webmvc")

        Files.exists(output.resolve("BooksEndpoints.kt")) shouldBe false
    }

    test("reject different sharing identities for the same path") {
        val output = Files.createTempDirectory("tapik-artifacts-").apply { toFile().deleteOnExit() }
        fun shared(key: String) =
            GenerationResult(
                listOf(
                    GeneratedArtifact("Shared.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class Shared", key)
                )
            )

        ArtifactWriter.write(shared("first"), output, owner = "first")

        shouldThrow<IllegalStateException> {
            ArtifactWriter.write(shared("second"), output, owner = "second")
        }
    }

    test("reject unowned generated paths before changing output or ownership") {
        val output = Files.createTempDirectory("tapik-artifacts-").apply { toFile().deleteOnExit() }
        val previous =
            GenerationResult(
                listOf(GeneratedArtifact("stale/Previous.kt", "text/x-kotlin", ArtifactKind.SOURCE, "class Previous"))
            )
        ArtifactWriter.write(previous, output, owner = "spring")
        val manifest = output.resolve(".tapik-ownership")
        val manifestBytes = Files.readAllBytes(manifest)
        val collisions =
            listOf(
                GeneratedArtifact("source/Client.kt", "text/x-kotlin", ArtifactKind.SOURCE, "generated source"),
                GeneratedArtifact("resource/service.properties", "text/plain", ArtifactKind.RESOURCE, "generated resource"),
                GeneratedArtifact("documentation/openapi.json", "application/json", ArtifactKind.DOCUMENTATION, "{}")
            )
        val originalBytes = collisions.associate { artifact ->
            val path = output.resolve(artifact.relativePath)
            Files.createDirectories(path.parent)
            val bytes = "unowned ${artifact.kind}".encodeToByteArray()
            Files.write(path, bytes)
            path to bytes
        }

        val failure = shouldThrow<IllegalStateException> {
            ArtifactWriter.write(GenerationResult(collisions), output, owner = "spring")
        }

        requireNotNull(failure.message) shouldContain "source/Client.kt"
        failure.message shouldContain "spring"
        originalBytes.forEach { (path, bytes) -> Files.readAllBytes(path) shouldBe bytes }
        Files.readString(output.resolve("stale/Previous.kt")) shouldBe "class Previous"
        Files.readAllBytes(manifest) shouldBe manifestBytes
    }
})
