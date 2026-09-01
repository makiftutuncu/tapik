package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

private object AuthorsApi : Api("Authors") {
    val list by get(root / "authors")
    val get by get(root / "authors" / path.string("authorId"))
}

private object BooksApi : Api("Books") {
    val list by get(root / "books")
}

private object CatalogApi : Api("Catalog") {
    val authors by including(AuthorsApi)
    val health by get(root / "health")
    val books by including(BooksApi)
}

private object PlatformApi : Api("Platform") {
    val catalog by including(CatalogApi)
}

private class CyclicParent : Api("Parent") {
    val child by including(CyclicChild(this))
}

private class CyclicChild(parent: CyclicParent) : Api("Child") {
    val parent by including(parent)
}

class ApiCompositionSpec : FunSpec({
    test("include APIs as exactly typed delegated values") {
        val authors: AuthorsApi = CatalogApi.authors

        authors shouldBeSameInstanceAs AuthorsApi
        CatalogApi.includedApis.map { inclusion -> inclusion.propertyName to inclusion.api } shouldContainExactly
            listOf("authors" to AuthorsApi, "books" to BooksApi)
    }

    test("flatten included endpoints at their declaration positions") {
        CatalogApi.endpoints shouldContainExactly
            listOf(AuthorsApi.list, AuthorsApi.get, CatalogApi.health, BooksApi.list)
        CatalogApi.endpoints[0] shouldBeSameInstanceAs AuthorsApi.list
        CatalogApi.endpoints.map { endpoint -> endpoint.id } shouldContainExactly
            listOf("Authors.list", "Authors.get", "Catalog.health", "Books.list")
    }

    test("preserve identity and order through nested composition") {
        PlatformApi.endpoints shouldContainExactly CatalogApi.endpoints
        PlatformApi.catalog.authors.list shouldBeSameInstanceAs AuthorsApi.list
        AuthorsApi.endpoints shouldContainExactly listOf(AuthorsApi.list, AuthorsApi.get)
    }

    test("expose direct inclusions as snapshots") {
        (CatalogApi.includedApis as MutableList).clear()

        CatalogApi.includedApis.map { it.api } shouldContainExactly listOf(AuthorsApi, BooksApi)
    }

    test("reject direct and transitive inclusion cycles") {
        shouldThrow<IllegalArgumentException> {
            object : Api("Self") {
                val self by including(this)
            }
        }

        shouldThrow<IllegalArgumentException> { CyclicParent() }
    }

    test("reject duplicate API and endpoint identities in one composed tree") {
        val first =
            object : Api("Shared") {
                val first by get(root / "first")
            }
        val second =
            object : Api("Shared") {
                val second by get(root / "second")
            }

        shouldThrow<IllegalArgumentException> {
            object : Api("Combined") {
                val firstApi by including(first)
                val secondApi by including(second)
            }
        }

        shouldThrow<IllegalArgumentException> {
            object : Api("Combined") {
                val firstApi by including(first)
                val repeated by including(first)
            }
        }
    }
})
