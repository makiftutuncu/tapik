package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.UUID

class PathVariableSpec : FunSpec({
    test("build a path variable from a custom format") {
        val bookIdFormat = format.uuid.transform(decode = ::PathBookId, encode = PathBookId::value)
        val bookId = path<PathBookId>(name = "bookId", format = bookIdFormat)

        bookId.name shouldBe "bookId"
        bookId.format shouldBeSameInstanceAs bookIdFormat
    }

    test("expose built-in factories through the PathVariable companion") {
        path shouldBeSameInstanceAs PathVariable.Companion
        path.boolean("value").format shouldBeSameInstanceAs format.boolean
        path.byte("value").format shouldBeSameInstanceAs format.byte
        path.short("value").format shouldBeSameInstanceAs format.short
        path.int("value").format shouldBeSameInstanceAs format.int
        path.long("value").format shouldBeSameInstanceAs format.long
        path.float("value").format shouldBeSameInstanceAs format.float
        path.double("value").format shouldBeSameInstanceAs format.double
        path.bigInteger("value").format shouldBeSameInstanceAs format.bigInteger
        path.bigDecimal("value").format shouldBeSameInstanceAs format.bigDecimal
        path.string("value").format shouldBeSameInstanceAs format.string
        path.uuid("value").format shouldBeSameInstanceAs format.uuid
        path.localDate("value").format shouldBeSameInstanceAs format.localDate
        path.localTime("value").format shouldBeSameInstanceAs format.localTime
        path.localDateTime("value").format shouldBeSameInstanceAs format.localDateTime
        path.offsetTime("value").format shouldBeSameInstanceAs format.offsetTime
        path.offsetDateTime("value").format shouldBeSameInstanceAs format.offsetDateTime
        path.instant("value").format shouldBeSameInstanceAs format.instant
        path.duration("value").format shouldBeSameInstanceAs format.duration
        path.period("value").format shouldBeSameInstanceAs format.period
    }

    test("reject invalid path variable names") {
        shouldThrow<IllegalArgumentException> { path.string("") }
        shouldThrow<IllegalArgumentException> { path.string("   ") }
        shouldThrow<IllegalArgumentException> { path.string("book/id") }
        shouldThrow<IllegalArgumentException> { path.string("{bookId}") }
        shouldThrow<IllegalArgumentException> { path.string("book id") }
    }

    test("append variables while retaining their exact types and positions") {
        val bookId = path("bookId", format.uuid.transform(decode = ::PathBookId, encode = PathBookId::value))
        val authorId = path.uuid("authorId")
        val uri: Uri<
            Paths2<PathBookId, UUID>,
            Queries0
        > = root / "books" / bookId / "authors" / authorId

        uri.segments shouldBe
            listOf(
                PathSegment.Literal("books"),
                bookId,
                PathSegment.Literal("authors"),
                authorId
            )
        uri.paths.values shouldBe listOf(bookId, authorId)
        uri.toString() shouldBe "/books/{bookId}/authors/{authorId}"
    }

    test("reject duplicate variable names") {
        val uri = root / "books" / path.uuid("id") / "authors"

        shouldThrow<IllegalArgumentException> { uri / path.uuid("id") }
    }

    test("support eight path variables") {
        val uri =
            root /
                path.string("one") /
                path.string("two") /
                path.string("three") /
                path.string("four") /
                path.string("five") /
                path.string("six") /
                path.string("seven") /
                path.string("eight")

        uri.paths.values.map(PathVariable<*>::name) shouldBe
            listOf("one", "two", "three", "four", "five", "six", "seven", "eight")
        uri.toString() shouldBe "/{one}/{two}/{three}/{four}/{five}/{six}/{seven}/{eight}"
    }
})

private data class PathBookId(
    val value: UUID
)
