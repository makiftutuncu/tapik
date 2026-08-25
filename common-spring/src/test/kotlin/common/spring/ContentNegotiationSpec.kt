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
})
