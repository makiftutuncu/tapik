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
        shouldThrow<IllegalArgumentException> { path.remaining("") }
        shouldThrow<IllegalArgumentException> { path.remaining("book/archive") }
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

    test("append a typed remaining path as the final path value") {
        val archive = path.remaining("archive")
        val uri: Uri<RemainingPaths2<UUID>, Queries0> =
            root / "books" / path.uuid("bookId") / archive

        uri.segments shouldBe
            listOf(
                PathSegment.Literal("books"),
                uri.paths._1,
                archive
            )
        uri.paths._2 shouldBe archive
        uri.toString() shouldBe "/books/{bookId}/{*archive}"
        (uri + query.boolean("download")).toString() shouldBe "/books/{bookId}/{*archive}?download={download}"
    }

    test("encode and decode one or more remaining path segments") {
        val archive = path.remaining("archive")

        archive.format.encode(listOf("covers", "2026", "report")) shouldBe "covers/2026/report"
        archive.format.decode("/covers/2026/report") shouldBe
            DecodeResult.Success(listOf("covers", "2026", "report"))
        archive.format.decode("") shouldBe
            DecodeResult.Failure(DecodeError("A remaining path must contain at least one segment"))
        archive.format.decode("/covers//report") shouldBe
            DecodeResult.Failure(DecodeError("A remaining path must not contain empty segments"))
        shouldThrow<IllegalArgumentException> { archive.format.encode(emptyList()) }
        shouldThrow<IllegalArgumentException> { archive.format.encode(listOf("covers/archive")) }
    }

    test("reject duplicate variable names") {
        val uri = root / "books" / path.uuid("id") / "authors"

        shouldThrow<IllegalArgumentException> { uri / path.uuid("id") }
        shouldThrow<IllegalArgumentException> { uri / path.remaining("id") }
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

        uri.paths.values.map(PathValue<*>::name) shouldBe
            listOf("one", "two", "three", "four", "five", "six", "seven", "eight")
        uri.toString() shouldBe "/{one}/{two}/{three}/{four}/{five}/{six}/{seven}/{eight}"
    }

    test("support a remaining path as the eighth path value") {
        val uri: Uri<RemainingPaths8<String, String, String, String, String, String, String>, Queries0> =
            root /
                path.string("one") /
                path.string("two") /
                path.string("three") /
                path.string("four") /
                path.string("five") /
                path.string("six") /
                path.string("seven") /
                path.remaining("rest")

        uri.paths.values.map(PathValue<*>::name) shouldBe
            listOf("one", "two", "three", "four", "five", "six", "seven", "rest")
        uri.toString() shouldBe "/{one}/{two}/{three}/{four}/{five}/{six}/{seven}/{*rest}"
    }
})

private data class PathBookId(
    val value: UUID
)
