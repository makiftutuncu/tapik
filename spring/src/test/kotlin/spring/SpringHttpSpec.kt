package dev.akif.tapik.spring

import dev.akif.tapik.MediaType
import dev.akif.tapik.Method
import dev.akif.tapik.Status
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpHeaders

class SpringHttpSpec : FunSpec({
    test("round-trip every Tapik HTTP method") {
        Method.entries.forEach { method ->
            method.toHttpMethod().toMethod() shouldBe method
        }
    }

    test("round-trip arbitrary Tapik statuses") {
        val status = Status(299)

        status.toHttpStatusCode().toStatus() shouldBe status
    }

    test("round-trip complete media types") {
        val mediaType = MediaType("application/problem+json;charset=UTF-8")

        mediaType.toSpringMediaType().toMediaType() shouldBe mediaType
    }

    test("copy all Spring header values") {
        val headers = HttpHeaders()
        headers.addAll("X-Trace", listOf("first", "second"))

        headers.toTapikHeaders() shouldContainExactly
            mapOf("X-Trace" to listOf("first", "second"))
    }
})
