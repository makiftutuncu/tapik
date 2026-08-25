package dev.akif.tapik

/**
 * APIs contributed by one compiled contract artifact.
 *
 * Compiler-generated implementations expose every supported concrete class and object extending [Api]. Registry list
 * order is unspecified; generation catalogs establish canonical API-ID order across providers.
 */
interface ApiRegistry {
    /** APIs contributed by this provider. */
    val apis: List<Api>
}
