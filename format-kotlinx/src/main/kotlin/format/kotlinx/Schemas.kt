package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import dev.akif.tapik.common.format.SchemaDerivationException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind

/** Derives a Tapik schema from [serializer]. */
fun deriveSchema(serializer: KSerializer<*>): Schema = deriveSchema(serializer.descriptor)

/** Derives a Tapik schema from [descriptor]. */
fun deriveSchema(descriptor: SerialDescriptor): Schema =
    deriveSchema(descriptor, activeObjects = emptySet(), includeNullability = true)

@OptIn(ExperimentalSerializationApi::class)
private fun deriveSchema(
    descriptor: SerialDescriptor,
    activeObjects: Set<String>,
    includeNullability: Boolean
): Schema {
    if (includeNullability && descriptor.isNullable) {
        return NullableSchema(deriveSchema(descriptor, activeObjects, includeNullability = false))
    }

    if (descriptor.isInline) {
        require(descriptor.elementsCount == 1) {
            "Inline descriptor '${descriptor.serialName}' must contain exactly one element"
        }
        return deriveSchema(descriptor.getElementDescriptor(0), activeObjects, includeNullability = true)
            .named(descriptor.serialName)
    }

    val isObject = descriptor.kind == StructureKind.CLASS || descriptor.kind == StructureKind.OBJECT
    if (isObject && descriptor.serialName in activeObjects) {
        return ReferenceSchema(descriptor.serialName)
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
            ArraySchema(
                items = deriveSchema(descriptor.getElementDescriptor(0), activeObjects, includeNullability = true)
            )
        StructureKind.MAP ->
            MapSchema(
                keys = deriveSchema(descriptor.getElementDescriptor(0), activeObjects, includeNullability = true),
                values = deriveSchema(descriptor.getElementDescriptor(1), activeObjects, includeNullability = true)
            )
        StructureKind.CLASS,
        StructureKind.OBJECT -> deriveObjectSchema(descriptor, activeObjects)
        kotlinx.serialization.descriptors.SerialKind.ENUM ->
            EnumSchema(
                values = List(descriptor.elementsCount, descriptor::getElementName),
                name = descriptor.serialName
            )
        PolymorphicKind.OPEN,
        PolymorphicKind.SEALED,
        kotlinx.serialization.descriptors.SerialKind.CONTEXTUAL ->
            throw SchemaDerivationException(
                "Unsupported Kotlin serialization descriptor kind '${descriptor.kind}' for '${descriptor.serialName}'"
            )
    }
}

private fun deriveObjectSchema(
    descriptor: SerialDescriptor,
    activeObjects: Set<String>
): ObjectSchema {
    val properties = linkedMapOf<String, SchemaProperty>()
    val nestedActiveObjects = activeObjects + descriptor.serialName

    repeat(descriptor.elementsCount) { index ->
        properties[descriptor.getElementName(index)] =
            SchemaProperty(
                schema =
                    deriveSchema(
                        descriptor.getElementDescriptor(index),
                        nestedActiveObjects,
                        includeNullability = true
                    ),
                required = !descriptor.isElementOptional(index),
                deprecated = false
            )
    }

    return ObjectSchema(properties = properties, name = descriptor.serialName)
}
