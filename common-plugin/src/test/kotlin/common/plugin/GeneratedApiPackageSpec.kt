package dev.akif.tapik.common.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GeneratedApiPackageSpec : FunSpec({
    test("derive namespaces from the complete binary identity") {
        generatedApiPackage("generated", "books.Catalog") shouldBe "generated.books.Catalog"
        generatedApiPackage("generated", "authors.Catalog") shouldBe "generated.authors.Catalog"
        generatedApiPackage("custom.prefix", "books.Catalog") shouldBe "custom.prefix._external.books.Catalog"
    }

    test("remove the configured parent without duplicating the shared source root") {
        generatedApiPackage("com.example.library.generated", "com.example.library.contract.Authors") shouldBe
            "com.example.library.generated.contract.Authors"
        generatedApiPackage("com.example.library.generated", "com.example.library.Authors") shouldBe
            "com.example.library.generated.Authors"
        generatedApiPackage("com.example.library.generated", "com.example.library.contract.Outer\$Authors") shouldBe
            "com.example.library.generated.contract.Outer_0024Authors"
    }

    test("keep external identities distinct from relative local names and require a complete root boundary") {
        val names = listOf(
            "com.example.library.contract.Authors", "contract.Authors", "com.example.library2.contract.Authors",
            "com.example.contract.Authors", "com.example.library._external.contract.Authors", "com.example.library"
        )
        val packages = names.map { generatedApiPackage("com.example.library.generated", it) }
        packages shouldBe listOf(
            "com.example.library.generated.contract.Authors",
            "com.example.library.generated._external.contract.Authors",
            "com.example.library.generated._external.com.example.library2.contract.Authors",
            "com.example.library.generated._external.com.example.contract.Authors",
            "com.example.library.generated._005fexternal.contract.Authors",
            "com.example.library.generated._external.com.example.library"
        )
        packages.toSet().size shouldBe names.size
    }

    test("encode nesting underscores unicode keywords and leading digits without collisions") {
        val names = listOf("a.Outer\$Catalog", "a.Outer_0024Catalog", "a.Outer.Catalog", "when.Çatalog", "a.1Catalog", "a._k_1Catalog")
        val namespaces = names.map { generatedApiPackage("generated", it) }
        namespaces shouldBe listOf(
            "generated.a.Outer_0024Catalog",
            "generated.a.Outer_005f0024Catalog",
            "generated.a.Outer.Catalog",
            "generated._k_when._00c7atalog",
            "generated.a._k_1Catalog",
            "generated.a._005fk_005f1Catalog"
        )
        namespaces.toSet().size shouldBe names.size
        namespaces.all { namespace -> namespace.split('.').all(String::isKotlinIdentifier) } shouldBe true
    }

    test("reject invalid prefixes and missing binary-name segments") {
        shouldThrow<IllegalArgumentException> { generatedApiPackage("invalid-prefix", "books.Catalog") }
        listOf("", ".Catalog", "books..Catalog", "books.").forEach { name ->
            shouldThrow<IllegalArgumentException> { generatedApiPackage("generated", name) }
        }
    }
})
