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

    test("provide common HTTP headers with canonical names") {
        val headers =
            listOf(
                Header.Accept to "Accept",
                Header.AcceptEncoding to "Accept-Encoding",
                Header.AcceptLanguage to "Accept-Language",
                Header.Authorization to "Authorization",
                Header.CacheControl to "Cache-Control",
                Header.Connection to "Connection",
                Header.ContentDisposition to "Content-Disposition",
                Header.ContentEncoding to "Content-Encoding",
                Header.ContentLanguage to "Content-Language",
                Header.ContentLength to "Content-Length",
                Header.ContentLocation to "Content-Location",
                Header.ContentRange to "Content-Range",
                Header.ContentType to "Content-Type",
                Header.Cookie to "Cookie",
                Header.Date to "Date",
                Header.ETag to "ETag",
                Header.Expires to "Expires",
                Header.Host to "Host",
                Header.IfMatch to "If-Match",
                Header.IfModifiedSince to "If-Modified-Since",
                Header.IfNoneMatch to "If-None-Match",
                Header.IfRange to "If-Range",
                Header.IfUnmodifiedSince to "If-Unmodified-Since",
                Header.LastModified to "Last-Modified",
                Header.Location to "Location",
                Header.Origin to "Origin",
                Header.Pragma to "Pragma",
                Header.Range to "Range",
                Header.Referer to "Referer",
                Header.RetryAfter to "Retry-After",
                Header.Server to "Server",
                Header.SetCookie to "Set-Cookie",
                Header.TransferEncoding to "Transfer-Encoding",
                Header.Upgrade to "Upgrade",
                Header.UserAgent to "User-Agent",
                Header.Vary to "Vary",
                Header.WWWAuthenticate to "WWW-Authenticate"
            )

        headers.map { (header, _) -> header.name } shouldBe headers.map { (_, name) -> name }
    }

    test("preserve catalog header value types and presence") {
        val authorization: Header<String, Required> = Header.Authorization
        val contentLength: Header<Long, Required> = Header.ContentLength

        authorization.format shouldBeSameInstanceAs format.string
        authorization.presence shouldBe Required
        contentLength.format shouldBeSameInstanceAs format.long
        contentLength.presence shouldBe Required
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
