package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StatusSpec : FunSpec({
    test("retain any valid HTTP status code") {
        Status(299).code shouldBe 299
        Status(599).code shouldBe 599
    }

    test("provide named standard statuses as ordinary values") {
        Status.Ok shouldBe Status(200)
        Status.Created shouldBe Status(201)
        Status.NoContent shouldBe Status(204)
        Status.BadRequest shouldBe Status(400)
        Status.NotFound shouldBe Status(404)
        Status.Conflict shouldBe Status(409)
        Status.InternalServerError shouldBe Status(500)
    }

    test("reject codes outside the HTTP status domain") {
        shouldThrow<IllegalArgumentException> { Status(99) }
        shouldThrow<IllegalArgumentException> { Status(600) }
    }
})
