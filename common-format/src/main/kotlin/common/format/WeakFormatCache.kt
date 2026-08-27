package dev.akif.tapik.common.format

import java.lang.ref.WeakReference

/**
 * Caches derived formats without retaining their configuration, type key, or value.
 *
 * Configuration keys always use identity because two equal serialization configurations can still behave differently.
 * [typeKeyEquality] lets an integration choose whether its type representation has identity or structural semantics.
 */
class WeakFormatCache<Configuration : Any, Type : Any, Value : Any>(
    private val typeKeyEquality: FormatCacheKeyEquality
) {
    private val lock: Any = Any()
    private val entries: MutableMap<WeakFormatCacheKey<Configuration, Type>, WeakReference<Value>> = HashMap()

    /** Number of live entries currently held by this cache. */
    val size: Int
        get() = synchronized(lock) {
            removeStaleEntries()
            entries.size
        }

    /** Returns the live value for [configuration] and [type], or atomically creates and caches it. */
    fun getOrPut(
        configuration: Configuration,
        type: Type,
        create: () -> Value
    ): Value = synchronized(lock) {
        removeStaleEntries()
        val key = WeakFormatCacheKey(configuration, type, typeKeyEquality)
        entries[key]?.get()?.let { return@synchronized it }
        create().also { value -> entries[key] = WeakReference(value) }
    }

    private fun removeStaleEntries() {
        entries.entries.removeIf { (key, value) -> key.isStale || value.get() == null }
    }
}

private class WeakFormatCacheKey<Configuration : Any, Type : Any>(
    configuration: Configuration,
    type: Type,
    private val typeKeyEquality: FormatCacheKeyEquality
) {
    private val configurationReference: WeakReference<Configuration> = WeakReference(configuration)
    private val typeReference: WeakReference<Type> = WeakReference(type)
    private val cachedHashCode: Int =
        31 * System.identityHashCode(configuration) +
            when (typeKeyEquality) {
                FormatCacheKeyEquality.IDENTITY -> System.identityHashCode(type)
                FormatCacheKeyEquality.STRUCTURAL -> type.hashCode()
            }

    val isStale: Boolean
        get() = configurationReference.get() == null || typeReference.get() == null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is WeakFormatCacheKey<*, *> || typeKeyEquality != other.typeKeyEquality) {
            return false
        }
        val configuration = configurationReference.get() ?: return false
        val type = typeReference.get() ?: return false
        val otherType = other.typeReference.get() ?: return false
        return configuration === other.configurationReference.get() &&
            when (typeKeyEquality) {
                FormatCacheKeyEquality.IDENTITY -> type === otherType
                FormatCacheKeyEquality.STRUCTURAL -> type == otherType
            }
    }

    override fun hashCode(): Int = cachedHashCode
}
