package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.UUID

class HeaderSpec : FunSpec({
    test("provide a named empty headers value") {
        val headers: Headers0 = noHeaders

        headers shouldBeSameInstanceAs Tuple0
        headers.values shouldBe emptyList()
    }

    test("build a required header from a custom format") {
        val bookIdFormat = format.uuid.transform(decode = ::HeaderBookId, encode = HeaderBookId::value)
        val bookId: Header<HeaderBookId, Required> = header(name = "X-Book-Id", format = bookIdFormat)

        bookId.name shouldBe "X-Book-Id"
        bookId.format shouldBeSameInstanceAs bookIdFormat
        bookId.presence shouldBe Required
    }

    test("provide built-in header factories") {
        header shouldBeSameInstanceAs Header.Companion
        header.boolean("X-Value").format shouldBeSameInstanceAs format.boolean
        header.int("X-Value").format shouldBeSameInstanceAs format.int
        header.string("X-Value").format shouldBeSameInstanceAs format.string
        header.uuid("X-Value").format shouldBeSameInstanceAs format.uuid
        header.localDate("X-Value").format shouldBeSameInstanceAs format.localDate
        header.instant("X-Value").format shouldBeSameInstanceAs format.instant
    }

    test("represent all header presence modes in types") {
        val required: Header<String, Required> = header.string("X-Required")
        val optional: Header<String, Optional> = required.optional()
        val defaulted: Header<Int, Default<Int>> = header.int("X-Page").optional(default = 1)
        val fixed: Header<String, Fixed<String>> = header.string("X-Source").fixed("tapik")

        optional.presence shouldBe Optional
        defaulted.presence shouldBe Default(1)
        fixed.presence shouldBe Fixed("tapik")
    }

    test("reject invalid HTTP field names") {
        shouldThrow<IllegalArgumentException> { header.string("") }
        shouldThrow<IllegalArgumentException> { header.string("X Header") }
        shouldThrow<IllegalArgumentException> { header.string("X:Header") }
        shouldThrow<IllegalArgumentException> { header.string("Ünicode") }
    }

    test("group headers while retaining their exact types and order") {
        val requestId = header.uuid("X-Request-Id")
        val source = header.string("X-Source").fixed("tapik")
        val headers: Headers2<Header<UUID, Required>, Header<String, Fixed<String>>> =
            headersOf(requestId, source)

        headers.values shouldBe listOf(requestId, source)
    }

    test("reject duplicate header names case insensitively") {
        shouldThrow<IllegalArgumentException> {
            headersOf(header.string("X-Request-Id"), header.uuid("x-request-id"))
        }
    }

    test("validate headers at the bulk endpoint boundary") {
        val invalid = Headers2(header.string("X-Request-Id"), header.uuid("x-request-id"))

        shouldThrow<IllegalArgumentException> {
            object : Api("Books") {
                val list by get(root / "books").headers(invalid)
            }
        }
    }

    test("support eight headers") {
        val headers =
            headersOf(
                header.string("one"),
                header.string("two"),
                header.string("three"),
                header.string("four"),
                header.string("five"),
                header.string("six"),
                header.string("seven"),
                header.string("eight")
            )

        headers.values.map { it.name } shouldBe
            listOf("one", "two", "three", "four", "five", "six", "seven", "eight")
    }
})

private data class HeaderBookId(
    val value: UUID
)
