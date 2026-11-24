package dev.akif.tapik.format.jackson

import dev.akif.tapik.Schema
import dev.akif.tapik.common.format.SchemaProvider
import dev.akif.tapik.common.format.SchemaRegistry
import tools.jackson.databind.JavaType
import kotlin.reflect.KClass

/** Produces a schema for a resolved Jackson type. */
typealias JacksonSchemaProvider = SchemaProvider<JavaType>

/**
 * Immutable custom-schema registrations used during recursive Jackson schema derivation.
 *
 * Registrations match the resolved raw Java class. Adding another registration for the same class replaces the
 * earlier registration in the returned registry.
 */
class JacksonSchemaRegistry private constructor(
    private val registry: SchemaRegistry<Class<*>, JavaType>
) {
    /** Creates an empty schema registry. */
    constructor() : this(SchemaRegistry())

    /**
     * Returns a registry that uses [schema] whenever [type] is encountered.
     *
     * @param type class whose resolved Jackson types use this schema.
     * @param schema caller-supplied schema.
     * @return a new registry with this registration.
     */
    fun <Value : Any> withSchema(type: Class<Value>, schema: Schema): JacksonSchemaRegistry =
        withSchemaProvider(type) { schema }

    /**
     * Returns a registry that uses [schema] whenever [type] is encountered.
     *
     * @param type Kotlin class whose resolved Jackson types use this schema.
     * @param schema caller-supplied schema.
     * @return a new registry with this registration.
     */
    fun <Value : Any> withSchema(type: KClass<Value>, schema: Schema): JacksonSchemaRegistry =
        withSchema(type.java, schema)

    /**
     * Returns a registry that invokes [provider] whenever [type] is encountered.
     *
     * @param type class whose resolved Jackson types use this provider.
     * @param provider derives a schema from the resolved Jackson type.
     * @return a new registry with this registration.
     */
    fun <Value : Any> withSchemaProvider(
        type: Class<Value>,
        provider: JacksonSchemaProvider
    ): JacksonSchemaRegistry = JacksonSchemaRegistry(registry.withProvider(type, provider))

    /**
     * Returns a registry that invokes [provider] whenever [type] is encountered.
     *
     * @param type Kotlin class whose resolved Jackson types use this provider.
     * @param provider derives a schema from the resolved Jackson type.
     * @return a new registry with this registration.
     */
    fun <Value : Any> withSchemaProvider(
        type: KClass<Value>,
        provider: JacksonSchemaProvider
    ): JacksonSchemaRegistry = withSchemaProvider(type.java, provider)

    /**
     * Returns a registry that uses [schema] whenever [Value] is encountered.
     *
     * @param Value Kotlin type whose resolved Jackson types use this schema.
     * @param schema caller-supplied schema.
     * @return a new registry with this registration.
     */
    inline fun <reified Value : Any> withSchema(schema: Schema): JacksonSchemaRegistry =
        withSchema(Value::class, schema)

    /**
     * Returns a registry that invokes [provider] whenever [Value] is encountered.
     *
     * @param Value Kotlin type whose resolved Jackson types use this provider.
     * @param provider derives a schema from the resolved Jackson type.
     * @return a new registry with this registration.
     */
    inline fun <reified Value : Any> withSchemaProvider(provider: JacksonSchemaProvider): JacksonSchemaRegistry =
        withSchemaProvider(Value::class, provider)

    internal fun schema(type: JavaType): Schema? = schema(type.rawClass, type)

    internal fun schema(type: Class<*>, resolvedType: JavaType): Schema? = registry.schema(type, resolvedType)

    internal fun contains(type: JavaType): Boolean = registry.contains(type.rawClass)

    /** Default Jackson schema registry. */
    companion object {
        /** Empty registry used by Jackson format builders unless another registry is supplied. */
        @JvmField
        val Default: JacksonSchemaRegistry = JacksonSchemaRegistry()
    }
}
