package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.UUID

class QueryParameterSpec : FunSpec({
    test("build a required query parameter from a custom format") {
        val bookIdFormat = format.uuid.transform(decode = ::QueryBookId, encode = QueryBookId::value)
        val bookId: QueryParameter<QueryBookId, Required> =
            query(name = "bookId", format = bookIdFormat)

        bookId.name shouldBe "bookId"
        bookId.format shouldBeSameInstanceAs bookIdFormat
        bookId.presence shouldBe Required
    }

    test("provide built-in query parameter factories") {
        query shouldBeSameInstanceAs QueryParameter.Companion
        query.boolean("value").format shouldBeSameInstanceAs format.boolean
        query.int("value").format shouldBeSameInstanceAs format.int
        query.string("value").format shouldBeSameInstanceAs format.string
        query.uuid("value").format shouldBeSameInstanceAs format.uuid
        query.localDate("value").format shouldBeSameInstanceAs format.localDate
        query.instant("value").format shouldBeSameInstanceAs format.instant
    }

    test("represent required, optional, and defaulted presence in types") {
        val required: QueryParameter<String, Required> = query.string("term")
        val optional: QueryParameter<String, Optional> = required.optional()
        val defaulted: QueryParameter<Int, Default<Int>> = query.int("page").optional(default = 1)

        optional.presence shouldBe Optional
        defaulted.presence shouldBe Default(1)
    }

    test("turn a repeated query parameter into a list without consuming another tuple position") {
        val authorId = UUID.randomUUID()
        val authors: RepeatedQueryParameter<UUID, Required> = query.uuid("authorId").repeated()
        val optionalAuthors: RepeatedQueryParameter<UUID, Optional> = authors.optional()
        val defaultedAuthors: RepeatedQueryParameter<UUID, Default<List<UUID>>> =
            authors.optional(default = emptyList())
        val uri: Uri<Paths0, Queries1<RepeatedQueryParameter<UUID, Optional>>> = root + optionalAuthors

        authors.format.decode(listOf(authorId.toString())) shouldBe DecodeResult.Success(listOf(authorId))
        authors.format.encode(listOf(authorId)) shouldBe listOf(authorId.toString())
        authors.format.schema shouldBe ArraySchema(items = format.uuid.schema)
        optionalAuthors.presence shouldBe Optional
        defaultedAuthors.presence shouldBe Default(emptyList())
        uri.queries.values shouldBe listOf(optionalAuthors)
        uri.toString() shouldBe "/?authorId={authorId}"
    }

    test("reject invalid query parameter names") {
        shouldThrow<IllegalArgumentException> { query.string("") }
        shouldThrow<IllegalArgumentException> { query.string("   ") }
        shouldThrow<IllegalArgumentException> { query.string("book id") }
        shouldThrow<IllegalArgumentException> { query.string("book&id") }
        shouldThrow<IllegalArgumentException> { query.string("book=id") }
    }

    test("append query parameters while retaining exact value and presence types") {
        val term = query.string("term").optional()
        val page = query.int("page").optional(default = 1)
        val uri: Uri<
            Paths0,
            Queries2<QueryParameter<String, Optional>, QueryParameter<Int, Default<Int>>>
        > = root / "books" + term + page

        uri.queries.values shouldBe listOf(term, page)
        uri.toString() shouldBe "/books?term={term}&page={page}"
    }

    test("reject duplicate query definitions") {
        val uri = root + query.uuid("authorId")

        shouldThrow<IllegalArgumentException> { uri + query.uuid("authorId").repeated() }
    }

    test("support eight query parameters") {
        val uri =
            root +
                query.string("one") +
                query.string("two") +
                query.string("three") +
                query.string("four") +
                query.string("five") +
                query.string("six") +
                query.string("seven") +
                query.string("eight")

        uri.queries.values.map { it.name } shouldBe
            listOf("one", "two", "three", "four", "five", "six", "seven", "eight")
        uri.toString() shouldBe
            "/?one={one}&two={two}&three={three}&four={four}&five={five}&six={six}&seven={seven}&eight={eight}"
    }
})

private data class QueryBookId(
    val value: UUID
)
