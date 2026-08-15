package dev.akif.tapik.fixtures.library

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BooksApiSpec : FunSpec({
    test("declare the initial endpoints in source order") {
        BooksApi.endpoints.map { it.id } shouldContainExactly
            listOf("Books.list", "Books.get", "Books.create")
        BooksApi.endpoints.map { it.method } shouldContainExactly
            listOf(Method.GET, Method.GET, Method.POST)
        BooksApi.endpoints.map { it.uri.toString() } shouldContainExactly
            listOf("/books?page={page}", "/books/{bookId}", "/books")
    }

    test("exercise request and response contract features") {
        BooksApi.list.headers.values.single().name shouldBe "X-Request-Id"
        BooksApi.list.outputs.values.size shouldBe 1
        BooksApi.get.uri.paths.values.single().format shouldBe bookIdFormat
        BooksApi.get.outputs.values.size shouldBe 2
        BooksApi.create.input.shouldBeInstanceOf<BodyInput<*>>()
        BooksApi.create.outputs.values.size shouldBe 2
        BooksApi.create.tags shouldBe setOf("books")
    }

    test("use the transformed book identifier format") {
        val id = BookId("book-1")

        bookIdFormat.decode("book-1") shouldBe DecodeResult.Success(id)
        bookIdFormat.encode(id) shouldBe "book-1"
        bookIdFormat.schema.name shouldBe "BookId"
    }
})
