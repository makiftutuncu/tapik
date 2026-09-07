package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain

class MediaTypeSpec : FunSpec({
    test("normalize complete concrete media types while retaining parameter order") {
        val mediaType = MediaType(" \tApplication/Vnd.Example+JSON ; Profile=\"Book\"; CHARSET=\"UTF-8\" \t")
        mediaType.value shouldBe "application/vnd.example+json;profile=Book;charset=utf-8"
        mediaType.toString() shouldBe mediaType.value
        MediaType("text/plain; ;\t;charset=utf-8;;").value shouldBe "text/plain;charset=utf-8"
    }

    test("compare semantic identities with stable hash codes") {
        val first = MediaType("Text/Plain;Charset=\"UTF-8\";profile=Book")
        val second = MediaType("text/plain;PROFILE=\"Book\";charset=utf-8")
        first shouldBe second
        first.hashCode() shouldBe second.hashCode()
        setOf(first, second).size shouldBe 1
        first.value shouldNotBe second.value
        MediaType("text/plain") shouldNotBe MediaType("text/plain;charset=utf-8")
    }

    test("preserve opaque value case and decode equivalent quoting") {
        MediaType("multipart/mixed;boundary=A") shouldNotBe MediaType("multipart/mixed;boundary=a")
        MediaType("application/json;profile=Book") shouldNotBe MediaType("application/json;profile=book")
        MediaType("application/json;p=\"bo\\ok\"") shouldBe MediaType("application/json;p=book")
        MediaType("application/json;p=\"a;b=\\\"c\\\\d\"").value shouldBe "application/json;p=\"a;b=\\\"c\\\\d\""
        MediaType("application/json;p=\"\"").value shouldBe "application/json;p=\"\""
        MediaType("application/json;p=\"café\"").value shouldBe "application/json;p=\"café\""
    }

    test("reject malformed or ambiguous input at construction") {
        listOf(
            "", " ", "json", "/json", "application/", "application/json/extra", "application /json",
            "application/ json", "application/json, text/plain", "*/json", "application/*", "application/*+json",
            "application/j*s", "application/json;x", "application/json;=value", "application/json;x=",
            "application/json;x =value", "application/json;x= value", "application/json;x=a b",
            "application/json;x=\"unterminated", "application/json;x=\"a\"junk", "application/json;x=\"a\\",
            "application/json;x=a;X=a", "application/json;charset=\"\"", "application/json;charset=\"bad charset\"",
            "application/json\r\nX-Injected: yes", "application/json;x=\"a\nb\"", "application/json;x=\"a\u0000b\"",
            "application/json;x=\"a\u007fb\"", "application/json;x=\"☃\"", "application/json;x=\"\\\r\""
        ).forEach { value ->
            val error = shouldThrow<IllegalArgumentException> { MediaType(value) }
            error.message.orEmpty() shouldContain "Media type"
        }
    }
})
