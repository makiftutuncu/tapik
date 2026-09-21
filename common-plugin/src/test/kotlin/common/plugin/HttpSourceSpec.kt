package dev.akif.tapik.common.plugin

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HttpSourceSpec : FunSpec({
    test("derive a path template without query parameters") {
        val uri = root / "authors" / path.uuid("authorId") + query.string("projection")

        uri.pathTemplate() shouldBe "/authors/{authorId}"
        root.pathTemplate() shouldBe "/"
    }

    test("render a remaining path and its ordinary prefix") {
        val uri = root / "authors" / path.uuid("authorId") / path.remaining("document")

        uri.pathTemplate() shouldBe "/authors/{authorId}/{*document}"
        uri.pathTemplateBeforeRemaining() shouldBe "/authors/{authorId}"
    }

    test("derive stable Kotlin status variant names") {
        Status.Ok.kotlinVariantName() shouldBe "Ok"
        Status.Created.kotlinVariantName() shouldBe "Created"
        Status.NonAuthoritativeInformation.kotlinVariantName() shouldBe "NonAuthoritativeInformation"
        Status.NoContent.kotlinVariantName() shouldBe "NoContent"
        Status.ImUsed.kotlinVariantName() shouldBe "ImUsed"
        Status.UriTooLong.kotlinVariantName() shouldBe "UriTooLong"
        Status.ImATeapot.kotlinVariantName() shouldBe "ImATeapot"
        Status.HttpVersionNotSupported.kotlinVariantName() shouldBe "HttpVersionNotSupported"
        Status.BadRequest.kotlinVariantName() shouldBe "BadRequest"
        Status.NotFound.kotlinVariantName() shouldBe "NotFound"
        Status.Conflict.kotlinVariantName() shouldBe "Conflict"
        Status.InternalServerError.kotlinVariantName() shouldBe "InternalServerError"
        Status(299).kotlinVariantName() shouldBe "Status299"
    }

    test("derive stable Kotlin status matcher variant names") {
        ExactStatus(Status.Ok).kotlinVariantName() shouldBe "Ok"
        statusesOf(Status.Ok, Status.Created).kotlinVariantName() shouldBe "OkOrCreated"
        statusesIn(400..499).kotlinVariantName() shouldBe "Status400To499"
        statusMatching("successful extension status") { true }.kotlinVariantName() shouldBe
            "SuccessfulExtensionStatus"
    }
})
