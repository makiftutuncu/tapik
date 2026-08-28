package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class GenerationEngineSpec : FunSpec({
    test("dispatch all APIs and configuration to the selected target") {
        val authors = object : Api("Authors") {}
        val books = object : Api("Books") {}
        val target = RecordingTarget()
        val configuration = targetConfigurationOf("version" to "0.6.0")

        val result =
            GenerationEngine(listOf(target)).generate(
                targetId = "recording",
                apis = listOf(authors, books),
                configuration = configuration
            )

        target.request?.apis shouldContainExactly listOf(authors, books)
        target.request?.configuration shouldBe configuration
        result.artifacts.single().relativePath shouldBe "result.txt"
    }

    test("select APIs before target execution") {
        val authors = object : Api("Authors") {}
        val books = object : Api("Books") {}
        val target = RecordingTarget()

        GenerationEngine(listOf(target)).generate(
            targetId = "recording",
            apis = listOf(authors, books),
            selection = ApiSelection(includes = setOf("Books"))
        )

        target.request?.apis shouldContainExactly listOf(books)
    }

    test("reject invalid target selection and input") {
        val api = object : Api("Books") {}

        shouldThrow<IllegalArgumentException> {
            GenerationEngine(listOf(RecordingTarget(), RecordingTarget()))
        }
        shouldThrow<IllegalArgumentException> {
            GenerationEngine(listOf(RecordingTarget())).generate("missing", listOf(api))
        }
        shouldThrow<IllegalArgumentException> {
            GenerationEngine(listOf(RecordingTarget())).generate("recording", emptyList())
        }
        shouldThrow<IllegalArgumentException> {
            GenerationEngine(listOf(RecordingTarget())).generate(
                "recording",
                listOf(api, object : Api("Books") {})
            )
        }
    }

    test("reject unsafe and duplicate artifact paths") {
        listOf("../outside.txt", "/outside.txt", "C:/outside.txt", "inside//file.txt").forEach { path ->
            shouldThrow<IllegalArgumentException> {
                GeneratedArtifact(
                    relativePath = path,
                    mediaType = "text/plain",
                    kind = ArtifactKind.RESOURCE,
                    content = "outside"
                )
            }
        }

        val artifact =
            GeneratedArtifact(
                relativePath = "inside.txt",
                mediaType = "text/plain",
                kind = ArtifactKind.RESOURCE,
                content = "inside"
            )
        shouldThrow<IllegalArgumentException> { GenerationResult(listOf(artifact, artifact)) }
    }

    test("reject duplicate scalar configuration names") {
        shouldThrow<IllegalArgumentException> {
            targetConfigurationOf("version" to "1", "version" to "2")
        }
    }
})

private class RecordingTarget : GenerationTarget {
    override val id: String = "recording"
    var request: GenerationRequest? = null

    override fun generate(request: GenerationRequest): GenerationResult {
        this.request = request
        return GenerationResult(
            listOf(
                GeneratedArtifact(
                    relativePath = "result.txt",
                    mediaType = "text/plain",
                    kind = ArtifactKind.RESOURCE,
                    content = "generated"
                )
            )
        )
    }
}
