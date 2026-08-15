package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TagSpec : FunSpec({
    test("initialize append and replace endpoint tags with set semantics") {
        val books =
            object : Api("Books") {
                val list by
                    get(root / "books", tags = setOf("initial", "books"))
                        .tag("books")
                        .tag("catalog")
                        .tags(setOf("public"))
                        .tag("books")
            }

        books.list.tags shouldBe setOf("public", "books")
    }

    test("reject blank endpoint tags") {
        shouldThrow<IllegalArgumentException> {
            object : Api("Books") {
                val list by get(root / "books").tag(" ")
            }
        }
    }
})
