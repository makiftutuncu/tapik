package dev.akif.tapik.target.openapi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.booleanOrNull

// JsonElement's serializer otherwise converts numbers outside its integer range through Double.
@OptIn(ExperimentalSerializationApi::class)
internal fun JsonElement.withExactNumbers(): JsonElement =
    when (this) {
        JsonNull -> this
        is JsonArray -> JsonArray(map(JsonElement::withExactNumbers))
        is JsonObject -> JsonObject(mapValues { (_, value) -> value.withExactNumbers() })
        is JsonPrimitive -> {
            if (isString || booleanOrNull != null) {
                this
            } else {
                if (!JSON_NUMBER.matches(content)) {
                    throw OpenApiGenerationException("OpenAPI numeric value '$content' must be a finite JSON number")
                }
                JsonUnquotedLiteral(content)
            }
        }
    }

private val JSON_NUMBER = Regex("-?(0|[1-9][0-9]*)(\\.[0-9]+)?([eE][+-]?[0-9]+)?")
