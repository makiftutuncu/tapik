package dev.akif.tapik.openapi

import kotlinx.serialization.json.JsonElement

/**
 * The JSON Schema keywords currently emitted by Tapik's OpenAPI 3.2 target.
 *
 * Empty properties represent absent keywords rather than empty values in rendered output.
 *
 * @property reference optional JSON Reference URI.
 * @property types JSON Schema types accepted by the value.
 * @property format optional semantic format hint.
 * @property enumValues allowed string values.
 * @property items schema of array elements.
 * @property properties object properties in declaration order.
 * @property required required object property names in declaration order.
 * @property propertyNames schema constraining object property names.
 * @property additionalProperties schema of dynamically named object properties.
 * @property anyOf alternative schemas, at least one of which must match.
 * @property deprecated whether consumers should avoid the described value.
 * @property defaultValue documented receiver behavior when a value is absent.
 * @property constantValue the only accepted value.
 */
data class OpenApiSchema(
    val reference: String? = null,
    val types: List<String> = emptyList(),
    val format: String? = null,
    val enumValues: List<String> = emptyList(),
    val items: OpenApiSchema? = null,
    val properties: Map<String, OpenApiSchema> = emptyMap(),
    val required: List<String> = emptyList(),
    val propertyNames: OpenApiSchema? = null,
    val additionalProperties: OpenApiSchema? = null,
    val anyOf: List<OpenApiSchema> = emptyList(),
    val deprecated: Boolean = false,
    val defaultValue: JsonElement? = null,
    val constantValue: JsonElement? = null
)
