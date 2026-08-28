package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.shouldContain

class ApiSelectionSpec : FunSpec({
    val authors = object : Api("Authors") {}
    val books = object : Api("Books") {}
    val rentals = object : Api("Rentals") {}
    val apis = listOf(authors, books, rentals)

    test("select all APIs by default") {
        ApiSelection().select(apis) shouldContainExactly apis
    }

    test("apply exact includes then exclusions without reordering APIs") {
        ApiSelection(
            includes = setOf("Rentals", "Authors"),
            excludes = setOf("Rentals")
        ).select(apis) shouldContainExactly listOf(authors)
    }

    test("snapshot filter sets") {
        val includes = linkedSetOf("Authors")
        val selection = ApiSelection(includes = includes)

        includes.clear()
        runCatching { (selection.includes as MutableSet).clear() }

        selection.includes shouldContainExactly setOf("Authors")
    }

    test("reject invalid filters") {
        shouldThrow<IllegalArgumentException> {
            ApiSelection(includes = setOf(""))
        }
        shouldThrow<IllegalArgumentException> {
            ApiSelection(includes = setOf("Missing")).select(apis)
        }.message shouldContain "Missing"
        shouldThrow<IllegalArgumentException> {
            ApiSelection(excludes = apis.map(Api::id).toSet()).select(apis)
        }
    }
})
