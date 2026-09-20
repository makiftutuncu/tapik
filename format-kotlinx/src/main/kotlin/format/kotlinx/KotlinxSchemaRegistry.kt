package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.Schema
import dev.akif.tapik.common.format.SchemaProvider
import dev.akif.tapik.common.format.SchemaRegistry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.serializer

/** Produces a schema for a resolved Kotlin serialization descriptor. */
typealias KotlinxSchemaProvider = SchemaProvider<SerialDescriptor>

/**
 * Immutable custom-schema registrations used during recursive Kotlin serialization schema derivation.
 *
 * Registrations match descriptor serial names. Adding another registration for the same serial name replaces the
 * earlier registration in the returned registry.
 */
class KotlinxSchemaRegistry private constructor(
    private val registry: SchemaRegistry<String, SerialDescriptor>
) {
    /** Creates an empty schema registry. */
    constructor() : this(SchemaRegistry())

    /** Returns a registry that uses [schema] whenever [descriptor] is encountered. */
    fun withSchema(
        descriptor: SerialDescriptor,
        schema: Schema
    ): KotlinxSchemaRegistry = withSchemaProvider(descriptor) { schema }

    /** Returns a registry that uses [schema] whenever [serializer]'s descriptor is encountered. */
    fun <Value : Any> withSchema(
        serializer: KSerializer<Value>,
        schema: Schema
    ): KotlinxSchemaRegistry = withSchema(serializer.descriptor, schema)

    /** Returns a registry that invokes [provider] whenever [descriptor] is encountered. */
    fun withSchemaProvider(
        descriptor: SerialDescriptor,
        provider: KotlinxSchemaProvider
    ): KotlinxSchemaRegistry = KotlinxSchemaRegistry(registry.withProvider(descriptor.schemaKey, provider))

    /** Returns a registry that invokes [provider] whenever [serializer]'s descriptor is encountered. */
    fun <Value : Any> withSchemaProvider(
        serializer: KSerializer<Value>,
        provider: KotlinxSchemaProvider
    ): KotlinxSchemaRegistry = withSchemaProvider(serializer.descriptor, provider)

    /** Returns a registry that uses [schema] whenever [Value] is encountered. */
    inline fun <reified Value : Any> withSchema(schema: Schema): KotlinxSchemaRegistry =
        withSchema(serializer<Value>(), schema)

    /** Returns a registry that invokes [provider] whenever [Value] is encountered. */
    inline fun <reified Value : Any> withSchemaProvider(provider: KotlinxSchemaProvider): KotlinxSchemaRegistry =
        withSchemaProvider(serializer<Value>(), provider)

    internal fun schema(descriptor: SerialDescriptor): Schema? =
        registry.schema(descriptor.schemaKey, descriptor)

    companion object {
        /** Empty registry used by Kotlin serialization format builders unless another registry is supplied. */
        @JvmField
        val Default: KotlinxSchemaRegistry = KotlinxSchemaRegistry()
    }
}

internal val SerialDescriptor.schemaKey: String
    get() = serialName.removeSuffix("?")
