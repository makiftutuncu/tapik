package dev.akif.tapik.common.format

import dev.akif.tapik.Schema

/** Produces a schema from an integration-specific resolved [Context]. */
fun interface SchemaProvider<in Context : Any> {
    /**
     * Returns the schema describing [context].
     *
     * @param context resolved type information from the format integration.
     * @return the tapik schema for that type.
     */
    fun schema(context: Context): Schema
}

/**
 * Immutable keyed collection of integration-specific [SchemaProvider] registrations.
 *
 * Adding another provider for an existing key replaces the earlier provider in the returned registry.
 *
 * @param Key integration-specific key used to select a provider.
 * @param Context resolved type information passed to a provider.
 */
class SchemaRegistry<Key : Any, Context : Any> private constructor(
    private val providers: Map<Key, SchemaProvider<Context>>
) {
    /** Creates an empty registry. */
    constructor() : this(emptyMap())

    /**
     * Returns a registry containing [provider] under [key].
     *
     * @param key integration-specific lookup key.
     * @param provider schema provider to register.
     * @return a new registry with this key mapped to [provider].
     */
    fun withProvider(
        key: Key,
        provider: SchemaProvider<Context>
    ): SchemaRegistry<Key, Context> = SchemaRegistry(providers + (key to provider))

    /**
     * Returns whether a provider is registered for [key].
     *
     * @param key integration-specific lookup key.
     * @return `true` when a provider is registered for this key.
     */
    fun contains(key: Key): Boolean = key in providers

    /**
     * Resolves [context] through the provider registered for [key], or returns `null`.
     *
     * @param key integration-specific lookup key.
     * @param context resolved type information passed to the provider.
     * @return the derived schema, or `null` when no provider is registered.
     */
    fun schema(
        key: Key,
        context: Context
    ): Schema? = providers[key]?.schema(context)
}
