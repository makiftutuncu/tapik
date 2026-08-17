package dev.akif.tapik.plugin.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class GenerationTargetCatalogSpec : FunSpec({
    test("collect registered targets in deterministic order") {
        val catalog =
            GenerationTargetCatalog.from(
                listOf(
                    registryOf(target("spring-restclient")),
                    registryOf(target("openapi"))
                )
            )

        catalog.targets.map(GenerationTarget::id) shouldContainExactly
            listOf("openapi", "spring-restclient")
    }

    test("reject duplicate target IDs across registries") {
        shouldThrow<IllegalArgumentException> {
            GenerationTargetCatalog.from(
                listOf(
                    registryOf(target("openapi")),
                    registryOf(target("openapi"))
                )
            )
        }
    }

    test("load target registries through the shared service") {
        val catalog = GenerationTargetCatalog.load(GenerationTargetCatalogSpec::class.java.classLoader)

        catalog.targets.map(GenerationTarget::id) shouldContainExactly listOf("service")
    }
})

private fun registryOf(vararg targets: GenerationTarget): GenerationTargetRegistry =
    object : GenerationTargetRegistry {
        override val targets: List<GenerationTarget> = targets.toList()
    }

private fun target(id: String): GenerationTarget =
    object : GenerationTarget {
        override val id: String = id

        override fun generate(request: GenerationRequest): GenerationResult = GenerationResult(emptyList())
    }

class ServiceGenerationTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> = listOf(target("service"))
}
