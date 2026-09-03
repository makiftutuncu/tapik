package dev.akif.tapik.target.openapi

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

/**
 * Renders this document as deterministic block-style YAML.
 *
 * @return the complete OpenAPI document as YAML.
 * @throws OpenApiGenerationException when a default or constant contains an invalid numeric literal.
 */
fun OpenApiDocument.toYaml(): String = Yaml(dumperOptions()).dump(json().yamlValue())

private fun dumperOptions(): DumperOptions =
    DumperOptions().apply {
        defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        indent = 2
        indicatorIndent = 0
        isPrettyFlow = true
        splitLines = false
    }

private fun JsonElement.yamlValue(): Any? =
    when (this) {
        JsonNull -> null
        is JsonArray -> map(JsonElement::yamlValue)
        is JsonObject -> entries.associateTo(linkedMapOf()) { (key, value) -> key to value.yamlValue() }
        is JsonPrimitive -> yamlValue()
    }

private fun JsonPrimitive.yamlValue(): Any {
    if (isString) return content
    return booleanOrNull ?: content.toBigIntegerOrNull() ?: content.toBigDecimal()
}
