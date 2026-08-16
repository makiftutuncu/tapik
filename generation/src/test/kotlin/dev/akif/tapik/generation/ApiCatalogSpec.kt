package dev.akif.tapik.generation

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class ApiCatalogSpec : FunSpec({
    test("collect all registered APIs in deterministic order") {
        val books = object : Api("Books") {}
        val authors = object : Api("Authors") {}
        val catalog =
            ApiCatalog.from(
                listOf(
                    registryOf(books),
                    registryOf(authors)
                )
            )

        catalog.apis shouldContainExactly listOf(authors, books)
    }

    test("reject duplicate API IDs across registries") {
        val first = object : Api("Books") {}
        val second = object : Api("Books") {}

        shouldThrow<IllegalArgumentException> {
            ApiCatalog.from(listOf(registryOf(first), registryOf(second)))
        }
    }

    test("load compiler-contributed registries through the shared service") {
        val catalog = ApiCatalog.load(ApiCatalogSpec::class.java.classLoader)

        catalog.apis.map(Api::id) shouldContainExactly listOf("Service")
    }
})

private fun registryOf(vararg apis: Api): ApiRegistry =
    object : ApiRegistry {
        override val apis: List<Api> = apis.toList()
    }

class ServiceApiRegistry : ApiRegistry {
    override val apis: List<Api> = listOf(object : Api("Service") {})
}
