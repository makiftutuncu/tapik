package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GeneratedArtifact
import dev.akif.tapik.common.plugin.GenerationResult
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class GradleGenerationAggregationSpec : FunSpec({
    test("route artifact kinds and preserve execution and artifact order") {
        val aggregate =
            aggregateGradleGenerationResults(
                listOf(
                    GradleExecutionGeneration(
                        "client",
                        GenerationResult(
                            listOf(
                                artifact("Client.kt", ArtifactKind.SOURCE, "client"),
                                artifact("client.properties", ArtifactKind.RESOURCE, "resource")
                            )
                        )
                    ),
                    GradleExecutionGeneration(
                        "docs",
                        GenerationResult(listOf(artifact("openapi.json", ArtifactKind.DOCUMENTATION, "docs")))
                    )
                )
            )

        aggregate.artifacts.map(GeneratedArtifact::relativePath) shouldContainExactly
            listOf("sources/Client.kt", "resources/client.properties", "documentation/openapi.json")
    }

    test("merge compatible shared artifacts at their first claim") {
        val first = artifact("BooksEndpoints.kt", sharingKey = "endpoints:Books", content = "shared")
        val aggregate =
            aggregateGradleGenerationResults(
                listOf(
                    GradleExecutionGeneration("client", GenerationResult(listOf(first))),
                    GradleExecutionGeneration("server", GenerationResult(listOf(first)))
                )
            )

        aggregate.artifacts shouldContainExactly listOf(first.copy(relativePath = "sources/BooksEndpoints.kt"))
    }

    test("reject exclusive collisions with both execution names") {
        val failure =
            shouldThrow<IllegalStateException> {
                aggregateGradleGenerationResults(
                    listOf(
                        GradleExecutionGeneration(
                            "first",
                            GenerationResult(listOf(artifact("contract.txt", content = "first")))
                        ),
                        GradleExecutionGeneration(
                            "second",
                            GenerationResult(listOf(artifact("contract.txt", content = "second")))
                        )
                    )
                )
            }

        failure.message shouldBe
            "tapik generated path 'sources/contract.txt' conflicts between executions 'first' and 'second'"
    }

    test("reject divergent content hidden behind one sharing key") {
        shouldThrow<IllegalStateException> {
            aggregateGradleGenerationResults(
                listOf(
                    GradleExecutionGeneration(
                        "client",
                        GenerationResult(
                            listOf(artifact("Endpoint.kt", sharingKey = "endpoint", content = "client"))
                        )
                    ),
                    GradleExecutionGeneration(
                        "server",
                        GenerationResult(
                            listOf(artifact("Endpoint.kt", sharingKey = "endpoint", content = "server"))
                        )
                    )
                )
            )
        }
    }
})

private fun artifact(
    path: String,
    kind: ArtifactKind = ArtifactKind.SOURCE,
    content: String = path,
    sharingKey: String? = null
): GeneratedArtifact =
    GeneratedArtifact(
        relativePath = path,
        mediaType = "text/plain",
        kind = kind,
        content = content,
        sharingKey = sharingKey
    )
