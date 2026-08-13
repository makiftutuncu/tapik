package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class UriSpec : FunSpec({
    test("represent the root as a typed URI value") {
        val uri: Uri<Paths0, Queries0> = root

        uri.path.shouldBeEmpty()
        uri.pathVariables.values.shouldBeEmpty()
        uri.queryParameters.values.shouldBeEmpty()
    }

    test("compose an already-encoded multi-segment fragment") {
        val uri: Uri<Paths0, Queries0> = root / "books/%E2%9C%93%2Farchived"

        uri.path shouldBe listOf(PathSegment.Literal("books"), PathSegment.Literal("%E2%9C%93%2Farchived"))
    }

    test("normalize leading and trailing separators while retaining internal content") {
        val uri = root / "///books/authors///" / "/rentals/"

        uri.path shouldBe
            listOf(PathSegment.Literal("books"), PathSegment.Literal("authors"), PathSegment.Literal("rentals"))
    }

    test("reuse URI values without modifying their source") {
        val books = root / "books/"
        val rentals = books / "rentals"

        books.path shouldBe listOf(PathSegment.Literal("books"))
        rentals.path shouldBe listOf(PathSegment.Literal("books"), PathSegment.Literal("rentals"))
    }

    test("reject fragments without a segment") {
        shouldThrow<IllegalArgumentException> { root / "" }
        shouldThrow<IllegalArgumentException> { root / "/" }
        shouldThrow<IllegalArgumentException> { root / "///" }
    }

    test("reject empty segments inside a fragment") {
        shouldThrow<IllegalArgumentException> { root / "books//authors" }
        shouldThrow<IllegalArgumentException> { root / "/books///authors/" }
    }

    test("render root and literal paths") {
        root.toString() shouldBe "/"
        (root / "books/authors").toString() shouldBe "/books/authors"
    }
})
