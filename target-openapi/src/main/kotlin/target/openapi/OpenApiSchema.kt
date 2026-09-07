package dev.akif.tapik.target.openapi

import kotlinx.serialization.json.JsonElement

/**
 * The JSON Schema keywords currently emitted by tapik's OpenAPI 3.2 target.
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
 * @property oneOf alternative schemas, exactly one of which must match.
 * @property discriminator optional hint for selecting a composed-schema alternative.
 * @property deprecated whether consumers should avoid the described value.
 * @property defaultValue documented receiver behavior when a value is absent.
 * @property constantValue the only accepted value.
 */
class OpenApiSchema(
    val reference: String? = null,
    types: List<String> = emptyList(),
    val format: String? = null,
    enumValues: List<String> = emptyList(),
    val items: OpenApiSchema? = null,
    properties: Map<String, OpenApiSchema> = emptyMap(),
    required: List<String> = emptyList(),
    val propertyNames: OpenApiSchema? = null,
    val additionalProperties: OpenApiSchema? = null,
    anyOf: List<OpenApiSchema> = emptyList(),
    oneOf: List<OpenApiSchema> = emptyList(),
    val discriminator: OpenApiDiscriminator? = null,
    val deprecated: Boolean = false,
    defaultValue: JsonElement? = null,
    constantValue: JsonElement? = null
) {
    val types: List<String> = types.snapshotList()

    val enumValues: List<String> = enumValues.snapshotList()

    val properties: Map<String, OpenApiSchema> = properties.snapshotMap()

    val required: List<String> = required.snapshotList()

    val anyOf: List<OpenApiSchema> = anyOf.snapshotList()

    val oneOf: List<OpenApiSchema> = oneOf.snapshotList()

    val defaultValue: JsonElement? = defaultValue?.snapshotJson()

    val constantValue: JsonElement? = constantValue?.snapshotJson()

    /** Returns [reference] for destructuring. */
    operator fun component1(): String? = reference

    /** Returns [types] for destructuring. */
    operator fun component2(): List<String> = types

    /** Returns [format] for destructuring. */
    operator fun component3(): String? = format

    /** Returns [enumValues] for destructuring. */
    operator fun component4(): List<String> = enumValues

    /** Returns [items] for destructuring. */
    operator fun component5(): OpenApiSchema? = items

    /** Returns [properties] for destructuring. */
    operator fun component6(): Map<String, OpenApiSchema> = properties

    /** Returns [required] for destructuring. */
    operator fun component7(): List<String> = required

    /** Returns [propertyNames] for destructuring. */
    operator fun component8(): OpenApiSchema? = propertyNames

    /** Returns [additionalProperties] for destructuring. */
    operator fun component9(): OpenApiSchema? = additionalProperties

    /** Returns [anyOf] for destructuring. */
    operator fun component10(): List<OpenApiSchema> = anyOf

    /** Returns [oneOf] for destructuring. */
    operator fun component11(): List<OpenApiSchema> = oneOf

    /** Returns [discriminator] for destructuring. */
    operator fun component12(): OpenApiDiscriminator? = discriminator

    /** Returns [deprecated] for destructuring. */
    operator fun component13(): Boolean = deprecated

    /** Returns [defaultValue] for destructuring. */
    operator fun component14(): JsonElement? = defaultValue

    /** Returns [constantValue] for destructuring. */
    operator fun component15(): JsonElement? = constantValue

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        reference: String? = this.reference,
        types: List<String> = this.types,
        format: String? = this.format,
        enumValues: List<String> = this.enumValues,
        items: OpenApiSchema? = this.items,
        properties: Map<String, OpenApiSchema> = this.properties,
        required: List<String> = this.required,
        propertyNames: OpenApiSchema? = this.propertyNames,
        additionalProperties: OpenApiSchema? = this.additionalProperties,
        anyOf: List<OpenApiSchema> = this.anyOf,
        oneOf: List<OpenApiSchema> = this.oneOf,
        discriminator: OpenApiDiscriminator? = this.discriminator,
        deprecated: Boolean = this.deprecated,
        defaultValue: JsonElement? = this.defaultValue,
        constantValue: JsonElement? = this.constantValue
    ): OpenApiSchema = OpenApiSchema(reference, types, format, enumValues, items, properties, required, propertyNames, additionalProperties, anyOf, oneOf, discriminator, deprecated, defaultValue, constantValue)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiSchema &&
                reference == other.reference &&
                types == other.types &&
                format == other.format &&
                enumValues == other.enumValues &&
                items == other.items &&
                properties == other.properties &&
                required == other.required &&
                propertyNames == other.propertyNames &&
                additionalProperties == other.additionalProperties &&
                anyOf == other.anyOf &&
                oneOf == other.oneOf &&
                discriminator == other.discriminator &&
                deprecated == other.deprecated &&
                defaultValue == other.defaultValue &&
                constantValue == other.constantValue)

    override fun hashCode(): Int {
        var result = reference.hashCode()
        result = 31 * result + types.hashCode()
        result = 31 * result + format.hashCode()
        result = 31 * result + enumValues.hashCode()
        result = 31 * result + items.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + required.hashCode()
        result = 31 * result + propertyNames.hashCode()
        result = 31 * result + additionalProperties.hashCode()
        result = 31 * result + anyOf.hashCode()
        result = 31 * result + oneOf.hashCode()
        result = 31 * result + discriminator.hashCode()
        result = 31 * result + deprecated.hashCode()
        result = 31 * result + defaultValue.hashCode()
        result = 31 * result + constantValue.hashCode()
        return result
    }

    override fun toString(): String =
        "OpenApiSchema(reference=$reference, types=$types, format=$format, enumValues=$enumValues, items=$items, properties=$properties, required=$required, propertyNames=$propertyNames, additionalProperties=$additionalProperties, anyOf=$anyOf, oneOf=$oneOf, discriminator=$discriminator, deprecated=$deprecated, defaultValue=$defaultValue, constantValue=$constantValue)"
}
