package dev.akif.tapik.test.fixtures.library

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.types.shouldBeSameInstanceAs

class LibrarySpec : FunSpec({
    test("compose every library API in domain order") {
        Library.endpoints shouldContainExactly Authors.endpoints + Books.endpoints + Rentals.endpoints
        Library.authors.list shouldBeSameInstanceAs Authors.list
        Library.books.get shouldBeSameInstanceAs Books.get
        Library.rentals.returnBook shouldBeSameInstanceAs Rentals.returnBook
    }
})
