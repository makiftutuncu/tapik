package dev.akif.tapik.test.fixtures.library

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class RentalsSpec : FunSpec({
    test("declare rental endpoints in source order") {
        Rentals.endpoints.map { it.id } shouldContainExactly
            listOf("Rentals.list", "Rentals.get", "Rentals.create", "Rentals.returnBook")
        Rentals.endpoints.map { it.method } shouldContainExactly
            listOf(Method.GET, Method.GET, Method.POST, Method.POST)
        Rentals.endpoints.map { it.uri.toString() } shouldContainExactly
            listOf(
                "/rentals?bookId={bookId}&dueBefore={dueBefore}",
                "/rentals/{rentalId}",
                "/rentals",
                "/rentals/{rentalId}/return"
            )
    }

    test("exercise rental request and response features") {
        Rentals.list.uri.queries._1.format shouldBe bookIdFormat
        Rentals.list.uri.queries._2.format shouldBe StringFormats.localDate
        Rentals.get.uri.paths.values.single().format shouldBe rentalIdFormat
        Rentals.create.input.shouldBeInstanceOf<BodyInput<*>>()
        Rentals.create.outputs.values.size shouldBe 3
        Rentals.returnBook.outputs._1.matcher.matches(Status.NoContent) shouldBe true
        Rentals.returnBook.outputs._2.matcher.matches(Status.NotFound) shouldBe true
        Rentals.returnBook.outputs._3.matcher.matches(Status.Conflict) shouldBe true
        Rentals.returnBook.tags shouldBe setOf("rentals")
    }
})
