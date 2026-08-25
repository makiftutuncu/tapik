package dev.akif.tapik.plugin.openapi

import dev.akif.tapik.Api
import dev.akif.tapik.common.plugin.*
import dev.akif.tapik.test.fixtures.library.Books
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class OpenApiTargetSpec : FunSpec({
    test("generate one complete document artifact for every API") {
        val empty = object : Api("Empty") {}
        val expected =
            requireNotNull(OpenApiTargetSpec::class.java.getResource("/openapi/books-api.json"))
                .readText()
                .trimEnd()

        val result =
            OpenApiTarget.generate(
                GenerationRequest(
                    apis = listOf(Books, empty),
                    configuration = targetConfigurationOf("version" to "0.6.0")
                )
            )

        result.artifacts.map { it.relativePath } shouldContainExactly
            listOf("Books.openapi.json", "Empty.openapi.json")
        result.artifacts.first().kind shouldBe ArtifactKind.DOCUMENTATION
        result.artifacts.first().mediaType shouldBe "application/json"
        result.artifacts.first().content shouldBe expected
    }

    test("apply host-neutral OpenAPI target configuration") {
        val result =
            OpenApiTarget.generate(
                GenerationRequest(
                    apis = listOf(Books),
                    configuration =
                        targetConfigurationOf(
                            "version" to "1.2.3",
                            "pretty" to "false",
                            "componentNaming" to "qualified",
                            "output" to "contracts/{api}.json"
                        )
                )
            )

        result.artifacts.single().relativePath shouldBe "contracts/Books.json"
        result.artifacts.single().content.contains('\n') shouldBe false
        result.artifacts.single().content shouldBe
            OpenApi.from(
                api = Books,
                version = "1.2.3",
                componentNaming = OpenApiComponentNaming.Qualified
            ).toJson(pretty = false)
    }

    test("reject invalid OpenAPI target configuration") {
        shouldThrow<IllegalArgumentException> {
            OpenApiTarget.generate(GenerationRequest(listOf(Books)))
        }
        shouldThrow<IllegalArgumentException> {
            OpenApiTarget.generate(
                GenerationRequest(
                    listOf(Books),
                    targetConfigurationOf("version" to "1", "unknown" to "value")
                )
            )
        }
    }
})
