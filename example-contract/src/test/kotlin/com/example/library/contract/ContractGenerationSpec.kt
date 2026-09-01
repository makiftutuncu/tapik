package com.example.library.contract

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Path

class ContractGenerationSpec : FunSpec({
    test("generate OpenAPI from APIs in the same Maven module") {
        listOf("Authors", "Books", "Catalog").forEach { api ->
            Files.isRegularFile(Path.of("target/generated/tapik/$api.openapi.yml")) shouldBe true
        }
    }
})
