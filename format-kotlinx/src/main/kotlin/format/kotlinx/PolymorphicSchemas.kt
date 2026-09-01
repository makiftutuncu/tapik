package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import dev.akif.tapik.common.format.SchemaDerivationException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.capturedKClass
import kotlinx.serialization.descriptors.getPolymorphicDescriptors
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
internal fun KotlinxSchemaDeriver.derivePolymorphicSchema(
    descriptor: SerialDescriptor,
    activeSchemas: Set<String>
): Schema {
    requireObjectPolymorphism(descriptor)
    val name = schemaName(descriptor)
    val alternatives = polymorphicAlternatives(descriptor)
    if (alternatives.size < 2) {
        throw SchemaDerivationException(
            "Kotlin serialization polymorphic type '$name' must have at least two finite alternatives"
        )
    }

    val discriminatorName =
        descriptor.annotations
            .filterIsInstance<JsonClassDiscriminator>()
            .singleOrNull()
            ?.discriminator
            ?: format.configuration.classDiscriminator
    val nestedActiveSchemas = activeSchemas + name
    val schemas =
        alternatives.map { (serialName, alternative) ->
            deriveDiscriminatedAlternative(
                descriptor = alternative,
                serialName = serialName,
                discriminatorName = discriminatorName,
                activeSchemas = nestedActiveSchemas
            )
        }

    return UnionSchema(
        alternatives = schemas,
        discriminator =
            SchemaDiscriminator(
                propertyName = discriminatorName,
                mapping =
                    alternatives.associateTo(linkedMapOf()) { (serialName, _) ->
                        serialName to ReferenceSchema(serialName)
                    }
            ),
        name = name
    )
}

@OptIn(ExperimentalSerializationApi::class)
internal fun schemaName(descriptor: SerialDescriptor): String =
    if (descriptor.kind == PolymorphicKind.OPEN) {
        descriptor.capturedKClass?.qualifiedName
            ?: throw SchemaDerivationException(
                "Open Kotlin serialization descriptor '${descriptor.serialName}' does not expose its base type"
            )
    } else {
        descriptor.serialName
    }

@OptIn(ExperimentalSerializationApi::class)
private fun KotlinxSchemaDeriver.requireObjectPolymorphism(descriptor: SerialDescriptor) {
    if (format.configuration.useArrayPolymorphism) {
        throw SchemaDerivationException(
            "Kotlin serialization array polymorphism is unsupported for '${schemaName(descriptor)}'"
        )
    }
    if (format.configuration.classDiscriminatorMode != ClassDiscriminatorMode.POLYMORPHIC) {
        throw SchemaDerivationException(
            "Kotlin serialization polymorphism for '${schemaName(descriptor)}' requires ClassDiscriminatorMode.POLYMORPHIC"
        )
    }
    if (format.configuration.namingStrategy != null) {
        throw SchemaDerivationException(
            "Kotlin serialization naming strategies are unsupported for polymorphic type '${schemaName(descriptor)}'"
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun KotlinxSchemaDeriver.polymorphicAlternatives(
    descriptor: SerialDescriptor
): List<Pair<String, SerialDescriptor>> =
    when (descriptor.kind) {
        PolymorphicKind.SEALED -> {
            if (descriptor.elementsCount != 2) {
                throw SchemaDerivationException(
                    "Sealed Kotlin serialization descriptor '${descriptor.serialName}' must contain type and value elements"
                )
            }
            val values = descriptor.getElementDescriptor(1)
            List(values.elementsCount) { index ->
                values.getElementName(index) to values.getElementDescriptor(index)
            }
        }
        PolymorphicKind.OPEN -> {
            val baseClass =
                descriptor.capturedKClass
                    ?: throw SchemaDerivationException(
                        "Open Kotlin serialization descriptor '${descriptor.serialName}' does not expose its base type"
                    )
            if (format.serializersModule.hasDefaultProvider(baseClass)) {
                throw SchemaDerivationException(
                    "Open Kotlin serialization type '${schemaName(descriptor)}' uses a default polymorphic provider"
                )
            }
            format.serializersModule
                .getPolymorphicDescriptors(descriptor)
                .sortedBy(SerialDescriptor::serialName)
                .map { alternative -> alternative.serialName to alternative }
        }
        else -> error("Expected a polymorphic descriptor, found '${descriptor.kind}'")
    }

private fun KotlinxSchemaDeriver.deriveDiscriminatedAlternative(
    descriptor: SerialDescriptor,
    serialName: String,
    discriminatorName: String,
    activeSchemas: Set<String>
): ObjectSchema {
    if (descriptor.kind != StructureKind.CLASS && descriptor.kind != StructureKind.OBJECT) {
        throw SchemaDerivationException(
            "Kotlin serialization polymorphic alternative '$serialName' must encode as an object"
        )
    }
    val schema = derive(descriptor, activeSchemas) as? ObjectSchema
        ?: throw SchemaDerivationException(
            "Kotlin serialization polymorphic alternative '$serialName' must derive an object schema"
        )
    if (discriminatorName in schema.properties) {
        throw SchemaDerivationException(
            "Kotlin serialization polymorphic alternative '$serialName' conflicts with discriminator '$discriminatorName'"
        )
    }

    return ObjectSchema(
        properties =
            linkedMapOf(
                discriminatorName to
                    SchemaProperty(
                        schema = EnumSchema(listOf(serialName)),
                        required = true
                    )
            ) + schema.properties,
        name = schema.name
    )
}
