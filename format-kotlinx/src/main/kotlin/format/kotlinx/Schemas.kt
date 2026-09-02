package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import dev.akif.tapik.common.format.SchemaDerivationException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

/** Derives a tapik schema from [serializer] using [Json.Default]. */
fun deriveSchema(serializer: KSerializer<*>): Schema = deriveSchema(Json.Default, serializer)

/** Derives a tapik schema from [descriptor] using [Json.Default]. */
fun deriveSchema(descriptor: SerialDescriptor): Schema = deriveSchema(Json.Default, descriptor)

/** Derives a tapik schema from [serializer] and the selected JSON [format]. */
fun deriveSchema(
    format: Json,
    serializer: KSerializer<*>
): Schema = deriveSchema(format, serializer.descriptor)

/** Derives a tapik schema from [descriptor] and the selected JSON [format]. */
fun deriveSchema(
    format: Json,
    descriptor: SerialDescriptor
): Schema = KotlinxSchemaDeriver(format).derive(descriptor)

internal class KotlinxSchemaDeriver(
    val format: Json
) {
    fun derive(
        descriptor: SerialDescriptor,
        activeSchemas: Set<String> = emptySet(),
        includeNullability: Boolean = true
    ): Schema = deriveDescriptor(descriptor, activeSchemas, includeNullability)

    @OptIn(ExperimentalSerializationApi::class)
    private fun deriveDescriptor(
        descriptor: SerialDescriptor,
        activeSchemas: Set<String>,
        includeNullability: Boolean
    ): Schema {
        if (includeNullability && descriptor.isNullable) {
            return NullableSchema(derive(descriptor, activeSchemas, includeNullability = false))
        }

        if (descriptor.isInline) {
            require(descriptor.elementsCount == 1) {
                "Inline descriptor '${descriptor.serialName}' must contain exactly one element"
            }
            return derive(descriptor.getElementDescriptor(0), activeSchemas)
                .named(descriptor.serialName)
        }

        val schemaName = schemaName(descriptor)
        val isReferenceable =
                descriptor.kind == StructureKind.CLASS ||
                descriptor.kind == StructureKind.OBJECT ||
                descriptor.kind is PolymorphicKind
        if (isReferenceable && schemaName in activeSchemas) {
            return ReferenceSchema(schemaName)
        }

        return when (descriptor.kind) {
            PrimitiveKind.BOOLEAN -> ScalarSchema(SchemaType.BOOLEAN)
            PrimitiveKind.BYTE,
            PrimitiveKind.SHORT,
            PrimitiveKind.INT -> ScalarSchema(SchemaType.INTEGER, format = "int32")
            PrimitiveKind.LONG -> ScalarSchema(SchemaType.INTEGER, format = "int64")
            PrimitiveKind.FLOAT -> ScalarSchema(SchemaType.NUMBER, format = "float")
            PrimitiveKind.DOUBLE -> ScalarSchema(SchemaType.NUMBER, format = "double")
            PrimitiveKind.CHAR,
            PrimitiveKind.STRING -> ScalarSchema(SchemaType.STRING)
            StructureKind.LIST ->
                ArraySchema(items = derive(descriptor.getElementDescriptor(0), activeSchemas))
            StructureKind.MAP ->
                MapSchema(
                    keys = derive(descriptor.getElementDescriptor(0), activeSchemas),
                    values = derive(descriptor.getElementDescriptor(1), activeSchemas)
                )
            StructureKind.CLASS,
            StructureKind.OBJECT -> deriveObjectSchema(descriptor, activeSchemas)
            SerialKind.ENUM ->
                EnumSchema(
                    values = List(descriptor.elementsCount, descriptor::getElementName),
                    name = descriptor.serialName
                )
            PolymorphicKind.OPEN,
            PolymorphicKind.SEALED -> derivePolymorphicSchema(descriptor, activeSchemas)
            SerialKind.CONTEXTUAL ->
                throw SchemaDerivationException(
                    "Unsupported Kotlin serialization descriptor kind '${descriptor.kind}' for '${descriptor.serialName}'"
                )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun deriveObjectSchema(
        descriptor: SerialDescriptor,
        activeSchemas: Set<String>
    ): ObjectSchema {
        if (format.configuration.classDiscriminatorMode == ClassDiscriminatorMode.ALL_JSON_OBJECTS) {
            throw SchemaDerivationException(
                "Kotlin serialization ClassDiscriminatorMode.ALL_JSON_OBJECTS is unsupported for '${descriptor.serialName}'"
            )
        }

        val properties = linkedMapOf<String, SchemaProperty>()
        val nestedActiveSchemas = activeSchemas + descriptor.serialName

        repeat(descriptor.elementsCount) { index ->
            properties[descriptor.getElementName(index)] =
                SchemaProperty(
                    schema = derive(descriptor.getElementDescriptor(index), nestedActiveSchemas),
                    required = !descriptor.isElementOptional(index),
                    deprecated = false
                )
        }

        return ObjectSchema(properties = properties, name = descriptor.serialName)
    }
}
