package dev.akif.tapik.target.openapi

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.math.BigDecimal
import java.math.BigInteger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.yaml.snakeyaml.Yaml

class OpenApiYamlSpec : FunSpec({
    test("preserve scalar types including null numeric looking strings and large integers in YAML") {
        val values = linkedMapOf(
            "null" to JsonPrimitive("null"),
            "boolean" to JsonPrimitive("on"),
            "number" to JsonPrimitive("200"),
            "date" to JsonPrimitive("2026-09-04"),
            "multiline" to JsonPrimitive("first\nsecond"),
            "absent" to JsonNull,
            "enabled" to JsonPrimitive(true),
            "fraction" to JsonPrimitive(0.125),
            "large" to JsonPrimitive(BigInteger("123456789012345678901234567890"))
        )
        val document = OpenApiDocument(
            info = OpenApiInfo("Scalars", "1"),
            paths = emptyMap(),
            components = OpenApiComponents(values.mapValues { (_, value) -> OpenApiSchema(defaultValue = value) })
        )

        for (pretty in listOf(true, false)) {
            val schemas = Json.parseToJsonElement(document.toJson(pretty)).jsonObject
                .getValue("components").jsonObject.getValue("schemas").jsonObject
            schemas.getValue("large").jsonObject.getValue("default").jsonPrimitive.content shouldBe
                "123456789012345678901234567890"
        }
        Yaml().load<Any>(document.toYaml()) shouldBe Yaml().load(document.toJson())
        val rendered = Yaml().load<Map<String, Any>>(document.toYaml())
        val components = rendered.getValue("components") as Map<*, *>
        val schemas = components["schemas"] as Map<*, *>
        schemas.mapValues { (_, value) -> (value as Map<*, *>)["default"] } shouldBe linkedMapOf(
            "null" to "null",
            "boolean" to "on",
            "number" to "200",
            "date" to "2026-09-04",
            "multiline" to "first\nsecond",
            "absent" to null,
            "enabled" to true,
            "fraction" to 0.125,
            "large" to BigInteger("123456789012345678901234567890")
        )
    }

    test("preserve exact decimals nested in default and constant values") {
        val exact = "1234567890.1234567890123456789"
        val value = JsonObject(mapOf("values" to JsonArray(listOf(JsonPrimitive(BigDecimal(exact))))))
        val document = OpenApiDocument(
            info = OpenApiInfo("Decimals", "1"),
            paths = emptyMap(),
            components = OpenApiComponents(mapOf("Value" to OpenApiSchema(defaultValue = value, constantValue = value)))
        )
        for (pretty in listOf(true, false)) {
            document.toJson(pretty) shouldContain exact
        }
        document.toYaml() shouldContain exact
    }

    test("reject non-finite numbers in either renderer") {
        val document = OpenApiDocument(
            info = OpenApiInfo("Invalid", "1"),
            paths = emptyMap(),
            components = OpenApiComponents(mapOf("Value" to OpenApiSchema(defaultValue = JsonPrimitive(Double.NaN))))
        )
        shouldThrow<OpenApiGenerationException> { document.toJson() }
        shouldThrow<OpenApiGenerationException> { document.toYaml() }
    }
})
