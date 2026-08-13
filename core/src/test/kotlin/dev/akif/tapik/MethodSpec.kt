package dev.akif.tapik

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MethodSpec : FunSpec({
    test("contain all standard methods supported by the endpoint DSL") {
        Method.entries shouldBe
            listOf(
                Method.GET,
                Method.HEAD,
                Method.POST,
                Method.PUT,
                Method.PATCH,
                Method.DELETE,
                Method.CONNECT,
                Method.OPTIONS,
                Method.TRACE,
                Method.QUERY
            )
    }
})
