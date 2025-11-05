package dev.akif.tapik

/**
 * Common contract for tuple-like structures that can expose their elements as a list.
 *
 * @param T type of each element in the tuple.
 */
interface Listable<out T> {
    /**
     * Constructs a list of all the items in this.
     *
     * @return list of all the items in this.
     */
    fun toList(): List<T>
}
