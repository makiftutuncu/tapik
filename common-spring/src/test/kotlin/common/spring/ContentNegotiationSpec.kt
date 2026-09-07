package dev.akif.tapik.common.spring

import dev.akif.tapik.MediaType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ContentNegotiationSpec : FunSpec({
    val json = MediaType("application/json")
    val xml = MediaType("application/xml")

    test("select by quality then specificity then declaration order") {
        selectResponseMediaType("application/json;q=0.5, application/xml;q=0.9", listOf(json, xml)) shouldBe xml
        selectResponseMediaType("application/*;q=0.8, application/json;q=0.8", listOf(xml, json)) shouldBe json
        selectResponseMediaType("application/*", listOf(json, xml)) shouldBe json
    }

    test("let the most specific quality zero range exclude a representation") {
        selectResponseMediaType("application/json;q=0, */*;q=1", listOf(json)) shouldBe null
        selectResponseMediaType("application/json;q=0, */*;q=1", listOf(json, xml)) shouldBe xml
    }

    test("match wildcards and media type parameters") {
        val profiledJson = MediaType("application/json;profile=v2")

        selectResponseMediaType("application/*", listOf(profiledJson)) shouldBe profiledJson
        selectResponseMediaType("application/json;profile=v2", listOf(profiledJson)) shouldBe profiledJson
        selectResponseMediaType("application/json;profile=v1", listOf(profiledJson)) shouldBe null
    }

    test("handle missing invalid and empty accept values") {
        selectResponseMediaType(null, listOf(json, xml)) shouldBe json
        selectResponseMediaType("   ", listOf(json, xml)) shouldBe json
        selectResponseMediaType("not a media type", listOf(json, xml)) shouldBe null
        selectResponseMediaType("application/json", emptyList()) shouldBe null
    }

    test("match parameter names and charset values case-insensitively") {
        val plain = MediaType("text/plain;charset=utf-8")
        listOf("text/plain;charset=UTF-8", "TEXT/PLAIN;CHARSET=\"UTF-8\"").forEach { accept ->
            selectResponseMediaType(accept, listOf(plain)) shouldBe plain
        }
        selectResponseMediaType("text/plain;charset=iso-8859-1", listOf(plain)) shouldBe null
        selectResponseMediaType("text/plain;charset=UTF-8", listOf(MediaType.PlainText)) shouldBe null
        val profiled = MediaType("application/json;profile=Book")
        selectResponseMediaType("application/json;PROFILE=Book", listOf(profiled)) shouldBe profiled
    }

    test("match quoted tokens and escaped parameter values without folding opaque case") {
        val profiled = MediaType("application/json;profile=Book")
        listOf("application/json;profile=\"Book\"", "application/json;profile=\"Bo\\ok\"").forEach { accept ->
            selectResponseMediaType(accept, listOf(profiled)) shouldBe profiled
        }
        selectResponseMediaType("application/json;profile=\"book\"", listOf(profiled)) shouldBe null
        selectResponseMediaType("application/json;profile='Book'", listOf(profiled)) shouldBe null
        val boundary = MediaType("multipart/mixed;boundary=\"a;b\"")
        selectResponseMediaType("multipart/mixed;boundary=\"a\\;b\"", listOf(boundary)) shouldBe boundary
        selectResponseMediaType("multipart/mixed;boundary=\"A;b\"", listOf(boundary)) shouldBe null
    }

    test("retain parameter specificity and zero exclusions after semantic matching") {
        val plain = MediaType("text/plain;charset=utf-8")
        selectResponseMediaType("text/plain;CHARSET=\"UTF-8\";Q=0, text/*;q=1", listOf(plain)) shouldBe null
        selectResponseMediaType(
            "text/plain;CHARSET=\"UTF-8\";q=0.2, text/plain;q=1, application/json;q=0.5",
            listOf(plain, json)
        ) shouldBe json
        val profiled = MediaType("application/json;profile=v2")
        selectResponseMediaType(
            "application/json;PROFILE=\"v2\";q=0.5, application/json;q=0.5",
            listOf(json, profiled)
        ) shouldBe profiled
    }

    test("do not count uppercase quality as a representation parameter or a specificity advantage") {
        selectResponseMediaType("application/json;Q=0.5, application/xml;q=0.5", listOf(xml, json)) shouldBe xml
        selectResponseMediaType("application/json;Q=0.9, application/xml;q=0.5", listOf(xml, json)) shouldBe json
        val plain = MediaType("text/plain;charset=utf-8")
        val profiled = MediaType("text/plain;charset=utf-8;profile=v2")
        selectResponseMediaType("text/plain;CHARSET=\"UTF-8\";Q=0.5", listOf(profiled, plain)) shouldBe profiled
    }

    test("reject invalid quality values regardless of parameter name case") {
        listOf("q", "Q").forEach { name ->
            listOf("invalid", "-0.1", "1.1", "NaN", "Infinity").forEach { value ->
                selectResponseMediaType("application/json;$name=$value", listOf(json)) shouldBe null
            }
        }
    }
})
