package dev.akif.tapik.target.openapi

import dev.akif.tapik.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal fun <Value : Any, Representation : Any> OpenApiSchema.withPresence(
    presence: Presence<Value>,
    format: Format<Value, Representation>
): OpenApiSchema =
    when (presence) {
        Required,
        Optional -> this
        is Default<Value> -> copy(defaultValue = format.jsonValue(presence.value))
        is Fixed<Value> -> copy(constantValue = format.jsonValue(presence.value))
    }

private fun <Value : Any, Representation : Any> Format<Value, Representation>.jsonValue(value: Value): JsonElement =
    encode(value).jsonValue(schema)

private fun Any?.jsonValue(schema: Schema): JsonElement {
    if (this == null) {
        if (schema is NullableSchema) return JsonNull
        throw OpenApiGenerationException("OpenAPI default contains null without a nullable schema")
    }

    return when (schema) {
        is ScalarSchema -> scalarJsonValue(schema.type)
        is EnumSchema -> JsonPrimitive(toString())
        is ArraySchema -> JsonArray(iterable("array").map { element -> element.jsonValue(schema.items) })
        is ObjectSchema ->
            JsonObject(
                map("object").entries.associate { (key, value) ->
                    val name = key?.toString() ?: throw OpenApiGenerationException("OpenAPI object keys cannot be null")
                    val property =
                        schema.properties[name]
                            ?: throw OpenApiGenerationException(
                                "OpenAPI object default contains unknown property '$name'"
                            )
                    name to value.jsonValue(property.schema)
                }
            )
        is MapSchema ->
            JsonObject(
                map("map").entries.associate { (key, value) ->
                    val name =
                        key?.jsonObjectKey(schema.keys)
                            ?: throw OpenApiGenerationException("OpenAPI map keys cannot be null")
                    name to value.jsonValue(schema.values)
                }
            )
        is NullableSchema -> jsonValue(schema.schema)
        is ReferenceSchema ->
            throw OpenApiGenerationException(
                "OpenAPI default values require a structural schema, but '${schema.reference}' is reference-only"
            )
    }
}

private fun Any.scalarJsonValue(type: SchemaType): JsonPrimitive =
    try {
        when (type) {
            SchemaType.BOOLEAN -> JsonPrimitive(this as? Boolean ?: toString().toBooleanStrict())
            SchemaType.INTEGER -> JsonPrimitive(toString().toBigInteger())
            SchemaType.NUMBER -> JsonPrimitive(toString().toBigDecimal())
            SchemaType.STRING -> JsonPrimitive(toString())
        }
    } catch (_: IllegalArgumentException) {
        throw OpenApiGenerationException("Encoded value '$this' does not match OpenAPI schema type $type")
    }

private fun Any.iterable(expected: String): Iterable<*> =
    when (this) {
        is Iterable<*> -> this
        is Array<*> -> asIterable()
        else -> throw OpenApiGenerationException("OpenAPI $expected default requires an iterable representation")
    }

private fun Any.map(expected: String): Map<*, *> =
    this as? Map<*, *>
        ?: throw OpenApiGenerationException("OpenAPI $expected default requires a map representation")

private fun Any.jsonObjectKey(schema: Schema): String =
    when (schema) {
        is ScalarSchema -> {
            if (schema.type != SchemaType.STRING) {
                throw OpenApiGenerationException("OpenAPI map default keys must use a string schema")
            }
            toString()
        }
        is EnumSchema -> toString()
        else -> throw OpenApiGenerationException("OpenAPI map default keys require a string or enum schema")
    }
