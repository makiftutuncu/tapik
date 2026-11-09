package dev.akif.tapik.plugin.compiler

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

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

    test("rebuild relocated local state from a cacheable root-relative snapshot") {
        val original = compilerWorkspace()
        val originalSources = original.resolve("src/main/kotlin")
        val originalState = original.resolve("local/api-index")
        val originalSnapshot = original.resolve("cache/api-index")
        val originalAuthors = originalSources.resolve("example/Authors.kt").writtenSource()
        val originalBooks = originalSources.resolve("example/Books.kt").writtenSource()

        ApiRegistryIndex(originalState, originalSnapshot, listOf(originalSources)).synchronize(
            incremental = false,
            activeSources = setOf(originalAuthors, originalBooks),
            compiledSources =
                mapOf(
                    originalAuthors to listOf(RegisteredApiType("example/Authors", ApiInstantiation.OBJECT)),
                    originalBooks to listOf(RegisteredApiType("example/Books", ApiInstantiation.OBJECT))
                )
        )

        val relocated = compilerWorkspace()
        val relocatedSources = relocated.resolve("src/main/kotlin")
        val relocatedState = relocated.resolve("local/api-index")
        val relocatedSnapshot = relocated.resolve("cache/api-index")
        val relocatedAuthors = relocatedSources.resolve("example/Authors.kt").writtenSource()
        val relocatedBooks = relocatedSources.resolve("example/Books.kt").writtenSource()
        Files.createDirectories(requireNotNull(relocatedSnapshot.parent))
        Files.copy(originalSnapshot, relocatedSnapshot, REPLACE_EXISTING)

        ApiRegistryIndex(relocatedState, relocatedSnapshot, listOf(relocatedSources)).synchronize(
            incremental = true,
            activeSources = setOf(relocatedAuthors, relocatedBooks),
            compiledSources =
                mapOf(
                    relocatedAuthors to listOf(RegisteredApiType("example/Writers", ApiInstantiation.CONSTRUCTOR))
                )
        ) shouldContainExactly
            listOf(
                RegisteredApiType("example/Writers", ApiInstantiation.CONSTRUCTOR),
                RegisteredApiType("example/Books", ApiInstantiation.OBJECT)
            )
        Files.isRegularFile(relocatedState) shouldBe true
    }
})

private fun Path.writtenSource(): Path =
    apply {
        Files.createDirectories(requireNotNull(parent))
        Files.writeString(this, "// source")
    }
