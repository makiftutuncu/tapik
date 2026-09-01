package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class StatusMatcherSpec : FunSpec({
    test("match one of a non-empty status set") {
        val matcher = statusesOf(Status.Ok, Status.Created, Status.NoContent)

        matcher.statuses shouldBe setOf(Status.Ok, Status.Created, Status.NoContent)
        matcher.matches(Status.Ok) shouldBe true
        matcher.matches(Status.Created) shouldBe true
        matcher.matches(Status.BadRequest) shouldBe false
    }

    test("normalize duplicate statuses") {
        statusesOf(Status.Ok, Status.Created, Status.Ok).statuses shouldBe setOf(Status.Ok, Status.Created)
    }

    test("match a non-empty range inside the HTTP status domain") {
        val matcher = statusesIn(400..499)

        matcher.range shouldBe 400..499
        matcher.matches(Status.BadRequest) shouldBe true
        matcher.matches(Status.InternalServerError) shouldBe false
        shouldThrow<IllegalArgumentException> { StatusRange(500..499) }
        shouldThrow<IllegalArgumentException> { StatusRange(99..199) }
        shouldThrow<IllegalArgumentException> { StatusRange(500..600) }
    }

    test("retain a description for a custom predicate") {
        val matcher = statusMatching("successful extension status") { status -> status.code in 290..299 }

        matcher.description shouldBe "successful extension status"
        matcher.matches(Status(299)) shouldBe true
        matcher.matches(Status.BadRequest) shouldBe false
        shouldThrow<IllegalArgumentException> { statusMatching("  ") { true } }
    }

    test("report a custom predicate failure while validating outputs") {
        val failure =
            shouldThrow<IllegalArgumentException> {
                object : Api("Broken") {
                    val endpoint by
                        get(root)
                            .output(Status.Ok with noBody)
                            .output(statusMatching("broken matcher") { error("boom") } with noBody)
                }
            }

        failure.message shouldContain "broken matcher"
        failure.message shouldContain "200"
        failure.cause?.message shouldBe "boom"
    }
})
