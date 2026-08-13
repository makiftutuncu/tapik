package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MethodSpec : FunSpec({
    test("represent every standard OpenAPI 3.2 operation as a distinct type") {
        val get: Method.Get = Method.Get
        val put: Method.Put = Method.Put
        val post: Method.Post = Method.Post
        val delete: Method.Delete = Method.Delete
        val options: Method.Options = Method.Options
        val head: Method.Head = Method.Head
        val patch: Method.Patch = Method.Patch
        val trace: Method.Trace = Method.Trace
        val query: Method.Query = Method.Query

        listOf(get, put, post, delete, options, head, patch, trace, query).map(Method::token) shouldBe
            listOf("GET", "PUT", "POST", "DELETE", "OPTIONS", "HEAD", "PATCH", "TRACE", "QUERY")
    }

    test("preserve the case of a valid custom HTTP method") {
        Method.Custom("COPY").token shouldBe "COPY"
        Method.Custom("copy").token shouldBe "copy"
    }

    test("reject invalid or duplicated custom HTTP methods") {
        shouldThrow<IllegalArgumentException> { Method.Custom("") }
        shouldThrow<IllegalArgumentException> { Method.Custom("HAS SPACE") }
        shouldThrow<IllegalArgumentException> { Method.Custom("BAD/METHOD") }
        shouldThrow<IllegalArgumentException> { Method.Custom("POST") }
    }
})
