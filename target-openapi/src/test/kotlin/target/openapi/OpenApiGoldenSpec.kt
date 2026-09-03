package dev.akif.tapik.target.openapi

import dev.akif.tapik.*
import dev.akif.tapik.test.fixtures.library.Books
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.yaml.snakeyaml.Yaml

class OpenApiGoldenSpec : FunSpec({
    val documents = linkedMapOf(
        "books-api" to OpenApi.from(Books, version = "0.6.0"),
        "coverage-api" to OpenApi.from(
            OpenApiCoverage,
            OpenApiInfo("Coverage", "0.6.0", summary = "OpenAPI 3.2 coverage", description = "Complete DSL interpretation fixture")
        )
    )

    documents.forEach { (name, document) ->
        test("render complete $name golden document as equivalent pretty JSON compact JSON and YAML") {
            val expected = requireNotNull(OpenApiGoldenSpec::class.java.getResource("/openapi/$name.json"))
                .readText().trimEnd()

            document.toJson() shouldBe expected
            Json.parseToJsonElement(document.toJson(pretty = false)) shouldBe Json.parseToJsonElement(expected)
            Yaml().load<Any>(document.toYaml()) shouldBe Yaml().load(expected)
            document.toYaml() shouldBe document.toYaml()
        }
    }

    Method.entries.forEach { method ->
        test("render $method in its OpenAPI 3.2 path item field") {
            val api = object : Api("Methods") {
                val operation by when (method) {
                    Method.GET -> get(root)
                    Method.HEAD -> head(root)
                    Method.POST -> post(root)
                    Method.PUT -> put(root)
                    Method.PATCH -> patch(root)
                    Method.DELETE -> delete(root)
                    Method.CONNECT -> connect(root)
                    Method.OPTIONS -> options(root)
                    Method.TRACE -> trace(root)
                    Method.QUERY -> query(root)
                }
            }
            val path = Json.parseToJsonElement(OpenApi.from(api, version = "1").toJson())
                .jsonObject.getValue("paths").jsonObject.getValue("/").jsonObject
            val operation = if (method == Method.CONNECT) {
                path.keys shouldBe setOf("additionalOperations")
                path.getValue("additionalOperations").jsonObject.getValue("CONNECT").jsonObject
            } else {
                path.keys shouldBe setOf(method.name.lowercase())
                path.getValue(method.name.lowercase()).jsonObject
            }
            operation.getValue("operationId").jsonPrimitive.content shouldBe "Methods.operation"
            operation.getValue("responses").jsonObject.keys shouldBe setOf("200")
        }
    }
})
