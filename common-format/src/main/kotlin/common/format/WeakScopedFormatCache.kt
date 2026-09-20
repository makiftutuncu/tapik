package dev.akif.tapik.common.format

import java.lang.ref.WeakReference

/**
 * Adds a weak, identity-compared scope to [WeakFormatCache].
 *
 * Neither the scope nor the nested configuration, type, or value is retained solely by this cache.
 */
class WeakScopedFormatCache<Scope : Any, Configuration : Any, Type : Any, Value : Any>(
    private val typeKeyEquality: FormatCacheKeyEquality
) {
    private val lock: Any = Any()
    private val caches:
        MutableMap<WeakScopeKey<Scope>, WeakFormatCache<Configuration, Type, Value>> = HashMap()

    /** Returns the live value for [scope], [configuration], and [type], or atomically creates and caches it. */
    fun getOrPut(
        scope: Scope,
        configuration: Configuration,
        type: Type,
        create: () -> Value
    ): Value =
        synchronized(lock) {
            caches.keys.removeIf(WeakScopeKey<Scope>::isStale)
            caches
                .getOrPut(WeakScopeKey(scope)) { WeakFormatCache(typeKeyEquality) }
                .getOrPut(configuration, type, create)
        }
}

private class WeakScopeKey<Scope : Any>(scope: Scope) {
    private val reference: WeakReference<Scope> = WeakReference(scope)
    private val cachedHashCode: Int = System.identityHashCode(scope)

    val isStale: Boolean
        get() = reference.get() == null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WeakScopeKey<*>) return false
        return reference.get()?.let { scope -> scope === other.reference.get() } == true
    }

    override fun hashCode(): Int = cachedHashCode
}
