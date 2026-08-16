package dev.akif.tapik.plugin.core

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import java.util.ServiceLoader

/**
 * A deterministic collection of APIs from every available [ApiRegistry].
 *
 * @property apis all registered APIs ordered by [Api.id].
 */
class ApiCatalog private constructor(
    val apis: List<Api>
) {
    /** Creates and discovers API catalogs. */
    companion object {
        /**
         * Collects APIs from [registries].
         *
         * @param registries registry providers to combine.
         * @return a deterministic API catalog.
         * @throws IllegalArgumentException when two APIs have the same ID.
         */
        fun from(registries: Iterable<ApiRegistry>): ApiCatalog {
            val apis = registries.flatMap(ApiRegistry::apis)
            requireUniqueApiIds(apis)
            return ApiCatalog(apis.sortedBy(Api::id))
        }

        /**
         * Loads registry providers visible to [classLoader].
         *
         * @param classLoader class loader containing compiled contract artifacts.
         * @return a deterministic API catalog.
         * @throws IllegalArgumentException when two APIs have the same ID.
         */
        fun load(classLoader: ClassLoader): ApiCatalog =
            from(ServiceLoader.load(ApiRegistry::class.java, classLoader))
    }
}

internal fun requireUniqueApiIds(apis: List<Api>) {
    val duplicateIds =
        apis
            .groupingBy(Api::id)
            .eachCount()
            .filterValues { count -> count > 1 }
            .keys
    require(duplicateIds.isEmpty()) {
        "API IDs must be unique, but found duplicates: ${duplicateIds.sorted().joinToString()}"
    }
}
