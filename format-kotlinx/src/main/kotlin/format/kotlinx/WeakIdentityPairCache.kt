package dev.akif.tapik.format.kotlinx

import java.lang.ref.WeakReference

internal class WeakIdentityPairCache<First : Any, Second : Any, Value : Any> {
    private val lock: Any = Any()
    private val entries: MutableMap<WeakIdentityPair<First, Second>, WeakReference<Value>> = HashMap()

    val size: Int
        get() = synchronized(lock) {
            removeStaleEntries()
            entries.size
        }

    fun getOrPut(
        first: First,
        second: Second,
        create: () -> Value
    ): Value = synchronized(lock) {
        removeStaleEntries()
        val key = WeakIdentityPair(first, second)
        entries[key]?.get()?.let { return@synchronized it }
        create().also { value -> entries[key] = WeakReference(value) }
    }

    private fun removeStaleEntries() {
        entries.entries.removeIf { (key, value) -> key.isStale || value.get() == null }
    }
}

private class WeakIdentityPair<First : Any, Second : Any>(
    first: First,
    second: Second
) {
    private val firstReference: WeakReference<First> = WeakReference(first)
    private val secondReference: WeakReference<Second> = WeakReference(second)
    private val identityHashCode: Int =
        31 * System.identityHashCode(first) + System.identityHashCode(second)

    val isStale: Boolean
        get() = firstReference.get() == null || secondReference.get() == null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is WeakIdentityPair<*, *>) {
            return false
        }
        val first = firstReference.get() ?: return false
        val second = secondReference.get() ?: return false
        return first === other.firstReference.get() && second === other.secondReference.get()
    }

    override fun hashCode(): Int = identityHashCode
}
