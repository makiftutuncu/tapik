package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.UUID

class EndpointSpec : FunSpec({
    test("derive the API ID from its concrete type") {
        class Books : Api()

        Books().id shouldBe "Books"
    }

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
            DefaultOutput,
            Ready
        > = books.list

        list.id shouldBe "Books.list"
        list.state shouldBe Ready("Books.list")
        list.method shouldBe Method.GET
        list.uri.toString() shouldBe "/books?page={page}"
        list.headers shouldBeSameInstanceAs noHeaders
        list.input shouldBeSameInstanceAs noInput
        list.outputs shouldBeSameInstanceAs DefaultOutput
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

    test("expose registered endpoints as snapshots") {
        val books =
            object : Api("Books") {
                val list by get(root / "books")
                val create by post(root / "books")
            }

        (books.endpoints as MutableList).clear()

        books.endpoints shouldContainExactly listOf(books.list, books.create)
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
                    endpoint: Endpoint<P, Q, Headers0, NoInput, DefaultOutput, Draft>
                ): Endpoint<P, Q, Headers0, NoInput, DefaultOutput, Draft> = endpoint

                val list by reusable(get(root / "books"))
            }

        books.list.id shouldBe "Books.list"
    }

    test("initialize and append request headers while retaining exact types") {
        val requestId = header.uuid("X-Request-Id")
        val source = header.string("X-Source").fixed("tapik")
        val trace = header.string("X-Trace").optional()
        val books =
            object : Api("Books") {
                val create by
                    post(root / "books")
                        .headers(headersOf(requestId, source))
                        .header(trace)
            }
        val create: Endpoint<
            Paths0,
            Queries0,
            Headers3<
                Header<UUID, Required>,
                Header<String, Fixed<String>>,
                Header<String, Optional>
            >,
            NoInput,
            DefaultOutput,
            Ready
        > = books.create

        create.headers.values shouldBe listOf(requestId, source, trace)
    }

    test("reject duplicate endpoint header names case insensitively") {
        shouldThrow<IllegalArgumentException> {
            object : Api("Books") {
                val create by
                    post(root / "books")
                        .header(header.string("X-Request-Id"))
                        .header(header.uuid("x-request-id"))
            }
        }
    }

    test("reject blank API identifiers") {
        shouldThrow<IllegalArgumentException> { object : Api("") {} }
        shouldThrow<IllegalArgumentException> { object : Api("   ") {} }
    }
})
