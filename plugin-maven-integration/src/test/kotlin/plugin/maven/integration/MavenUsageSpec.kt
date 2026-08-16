package dev.akif.tapik.plugin.maven.integration

import dev.akif.tapik.ApiRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

class MavenUsageSpec : FunSpec({
    test("compile API definitions and generate OpenAPI through one Tapik Maven plugin") {
        val registry = ServiceLoader.load(ApiRegistry::class.java).single()
        registry.apis shouldContainExactly listOf(Books)

        val actual = Files.readString(Path.of("target/generated/tapik/Books.openapi.json"))
        val expected =
            requireNotNull(MavenUsageSpec::class.java.getResource("/openapi/books.json"))
                .readText()

        actual shouldBe expected.trimEnd()
    }
})
