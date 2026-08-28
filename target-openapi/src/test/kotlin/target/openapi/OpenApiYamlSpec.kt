package dev.akif.tapik.target.openapi

import dev.akif.tapik.test.fixtures.library.Books
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.yaml.snakeyaml.Yaml

class OpenApiYamlSpec : FunSpec({
    test("render YAML with the same document values as JSON") {
        val document = OpenApi.from(Books, version = "0.6.0")

        Yaml().load<Any>(document.toYaml()) shouldBe Yaml().load(document.toJson())
        document.toYaml() shouldBe document.toYaml()
    }
})
