package dev.akif.tapik.common.plugin

import dev.akif.tapik.Status
import dev.akif.tapik.div
import dev.akif.tapik.path
import dev.akif.tapik.plus
import dev.akif.tapik.query
import dev.akif.tapik.root
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HttpSourceSpec : FunSpec({
    test("derive a path template without query parameters") {
        val uri = root / "authors" / path.uuid("authorId") + query.string("projection")

        uri.pathTemplate() shouldBe "/authors/{authorId}"
        root.pathTemplate() shouldBe "/"
    }

    test("derive stable Kotlin status variant names") {
        Status.Ok.kotlinVariantName() shouldBe "Ok"
        Status.Created.kotlinVariantName() shouldBe "Created"
        Status.NoContent.kotlinVariantName() shouldBe "NoContent"
        Status.BadRequest.kotlinVariantName() shouldBe "BadRequest"
        Status.NotFound.kotlinVariantName() shouldBe "NotFound"
        Status.Conflict.kotlinVariantName() shouldBe "Conflict"
        Status.InternalServerError.kotlinVariantName() shouldBe "InternalServerError"
        Status(418).kotlinVariantName() shouldBe "Status418"
    }
})
