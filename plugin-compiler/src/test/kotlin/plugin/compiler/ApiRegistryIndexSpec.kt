package dev.akif.tapik.plugin.compiler

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.nio.file.Files

class ApiRegistryIndexSpec : FunSpec({
    test("aggregate dirty sources with unchanged incremental entries") {
        val workspace = compilerWorkspace()
        val state = workspace.resolve("api-index")
        val books = workspace.resolve("Books.kt").apply { Files.writeString(this, "object Books") }
        val authors = workspace.resolve("Authors.kt").apply { Files.writeString(this, "object Authors") }
        val index = ApiRegistryIndex(state)

        index.synchronize(
            incremental = false,
            activeSources = setOf(books, authors),
            compiledSources =
                mapOf(
                    books to listOf(RegisteredApiType("example/Books", ApiInstantiation.OBJECT)),
                    authors to listOf(RegisteredApiType("example/Authors", ApiInstantiation.OBJECT))
                )
        )

        index.synchronize(
            incremental = true,
            activeSources = setOf(books, authors),
            compiledSources =
                mapOf(books to listOf(RegisteredApiType("example/Library", ApiInstantiation.CONSTRUCTOR)))
        ) shouldContainExactly
            listOf(
                RegisteredApiType("example/Authors", ApiInstantiation.OBJECT),
                RegisteredApiType("example/Library", ApiInstantiation.CONSTRUCTOR)
            )
    }

    test("remove deleted sources and empty build state") {
        val workspace = compilerWorkspace()
        val state = workspace.resolve("api-index")
        val books = workspace.resolve("Books.kt").apply { Files.writeString(this, "object Books") }
        val index = ApiRegistryIndex(state)

        index.synchronize(
            incremental = false,
            activeSources = setOf(books),
            compiledSources =
                mapOf(books to listOf(RegisteredApiType("example/Books", ApiInstantiation.OBJECT)))
        )
        Files.delete(books)

        index.synchronize(
            incremental = true,
            activeSources = emptySet(),
            compiledSources = emptyMap()
        ).isEmpty() shouldBe true
        Files.exists(state) shouldBe false
    }
})
