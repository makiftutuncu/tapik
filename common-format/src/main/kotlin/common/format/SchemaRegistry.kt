package dev.akif.tapik.common.format

import dev.akif.tapik.Schema

/** Produces a schema from an integration-specific resolved [Context]. */
fun interface SchemaProvider<in Context : Any> {
    /** Returns the schema describing [context]. */
    fun schema(context: Context): Schema
}

/**
 * Immutable keyed collection of integration-specific [SchemaProvider] registrations.
 *
 * Adding another provider for an existing key replaces the earlier provider in the returned registry.
 */
class SchemaRegistry<Key : Any, Context : Any> private constructor(
    private val providers: Map<Key, SchemaProvider<Context>>
) {
    /** Creates an empty registry. */
    constructor() : this(emptyMap())

    /** Returns a registry containing [provider] under [key]. */
    fun withProvider(
        key: Key,
        provider: SchemaProvider<Context>
    ): SchemaRegistry<Key, Context> = SchemaRegistry(providers + (key to provider))

    /** Returns whether a provider is registered for [key]. */
    fun contains(key: Key): Boolean = key in providers

    /** Resolves [context] through the provider registered for [key], or returns `null`. */
    fun schema(
        key: Key,
        context: Context
    ): Schema? = providers[key]?.schema(context)
}
