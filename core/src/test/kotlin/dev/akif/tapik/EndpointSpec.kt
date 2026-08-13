package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class EndpointSpec : FunSpec({
    test("bind endpoint identity through property delegation") {
        val books =
            object : Api("Books") {
                val list by get(root / "books" + query.int("page").optional(default = 1))
            }
        val list: Endpoint<
            Paths0,
            Queries1<QueryParameter<Int, Default<Int>>>,
            Headers0,
            NoInput,
            DefaultOutputs,
            Ready
        > = books.list

        list.id shouldBe "Books.list"
        list.state shouldBe Ready("Books.list")
        list.method shouldBe Method.GET
        list.uri.toString() shouldBe "/books?page={page}"
        list.headers shouldBeSameInstanceAs noHeaders
        list.input shouldBeSameInstanceAs noInput
        list.outputs shouldBeSameInstanceAs DefaultOutputs
    }

    test("register ready endpoints in declaration order") {
        val books =
            object : Api("Books") {
                val list by get(root / "books")
                val create by post(root / "books")
                val remove by delete(root / "books" / path.uuid("bookId"))
            }

        books.endpoints shouldContainExactly listOf(books.list, books.create, books.remove)
        books.endpoints[0] shouldBeSameInstanceAs books.list
        books.endpoints[1] shouldBeSameInstanceAs books.create
        books.endpoints[2] shouldBeSameInstanceAs books.remove
    }

    test("provide every supported standard method builder") {
        val api =
            object : Api("Methods") {
                val getEndpoint by get(root)
                val headEndpoint by head(root)
                val postEndpoint by post(root)
                val putEndpoint by put(root)
                val patchEndpoint by patch(root)
                val deleteEndpoint by delete(root)
                val connectEndpoint by connect(root)
                val optionsEndpoint by options(root)
                val traceEndpoint by trace(root)
                val queryEndpoint by query(root)
            }

        api.endpoints.map { it.method } shouldBe Method.entries
    }

    test("compose draft endpoints with ordinary Kotlin functions") {
        val books =
            object : Api("Books") {
                private fun <P : Paths, Q : Queries> reusable(
                    endpoint: Endpoint<P, Q, Headers0, NoInput, DefaultOutputs, Draft>
                ): Endpoint<P, Q, Headers0, NoInput, DefaultOutputs, Draft> = endpoint

                val list by reusable(get(root / "books"))
            }

        books.list.id shouldBe "Books.list"
    }

    test("reject blank API identifiers") {
        shouldThrow<IllegalArgumentException> { object : Api("") {} }
        shouldThrow<IllegalArgumentException> { object : Api("   ") {} }
    }
})
