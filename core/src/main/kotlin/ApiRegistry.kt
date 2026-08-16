package dev.akif.tapik

/**
 * APIs contributed by one compiled contract artifact.
 *
 * Compiler-generated implementations expose every concrete Kotlin `object` extending [Api].
 */
interface ApiRegistry {
    /** APIs in source declaration order. */
    val apis: List<Api>
}
