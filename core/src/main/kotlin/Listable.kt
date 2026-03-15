package dev.akif.tapik

/**
 * Common contract for fixed-arity structures that also need an iterable view.
 *
 * Tapik models headers, parameters, and outputs as tuples so it can preserve element types during
 * DSL construction and still hand interpreters a regular [List] when they need to iterate.
 *
 * @param T type of each exposed element.
 */
interface Listable<out T> {
    /**
     * Returns the elements in declaration order.
     *
     * This is the bridge from Tapik's typed product types to the list-oriented code used by
     * generators and interpreters.
     */
    fun toList(): List<T>
}
