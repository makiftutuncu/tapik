package dev.akif.tapik.fixtures.library

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BooksSpec : FunSpec({
    test("declare the initial endpoints in source order") {
        Books.endpoints.map { it.id } shouldContainExactly
            listOf("Books.list", "Books.get", "Books.create")
        Books.endpoints.map { it.method } shouldContainExactly
            listOf(Method.GET, Method.GET, Method.POST)
        Books.endpoints.map { it.uri.toString() } shouldContainExactly
            listOf("/books?page={page}&authorId={authorId}", "/books/{bookId}", "/books")
    }

    test("exercise request and response contract features") {
        Books.list.headers.values.single().name shouldBe "X-Request-Id"
        Books.list.uri.queries.values[1].shouldBeInstanceOf<RepeatedQueryParameter<*, *>>()
        Books.list.outputs.values.size shouldBe 1
        Books.get.uri.paths.values.single().format shouldBe bookIdFormat
        Books.get.outputs.values.size shouldBe 2
        Books.create.input.shouldBeInstanceOf<BodyInput<*>>()
        Books.create.outputs.values.size shouldBe 2
        Books.create.tags shouldBe setOf("books")
    }

    test("use the transformed book identifier format") {
        val id = BookId("book-1")

        bookIdFormat.decode("book-1") shouldBe DecodeResult.Success(id)
        bookIdFormat.encode(id) shouldBe "book-1"
        bookIdFormat.schema.name shouldBe "BookId"
    }
})
