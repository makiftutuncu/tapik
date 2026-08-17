package dev.akif.tapik.plugin.core

import java.util.ServiceLoader

/** A provider of generation targets made available to host adapters. */
interface GenerationTargetRegistry {
    /** Targets contributed by this registry. */
    val targets: List<GenerationTarget>
}

/**
 * A deterministic collection of targets from every available [GenerationTargetRegistry].
 *
 * @property targets all registered targets ordered by [GenerationTarget.id].
 */
class GenerationTargetCatalog private constructor(
    val targets: List<GenerationTarget>
) {
    /** Creates and discovers target catalogs. */
    companion object {
        /**
         * Collects targets from [registries].
         *
         * @param registries target providers to combine.
         * @return a deterministic target catalog.
         * @throws IllegalArgumentException when a target ID is blank or duplicated.
         */
        fun from(registries: Iterable<GenerationTargetRegistry>): GenerationTargetCatalog {
            val targets = registries.flatMap(GenerationTargetRegistry::targets)
            require(targets.none { target -> target.id.isBlank() }) {
                "Generation target IDs must not be blank"
            }
            val duplicateIds =
                targets
                    .groupingBy(GenerationTarget::id)
                    .eachCount()
                    .filterValues { count -> count > 1 }
                    .keys
            require(duplicateIds.isEmpty()) {
                "Generation target IDs must be unique, but found duplicates: ${duplicateIds.sorted().joinToString()}"
            }
            return GenerationTargetCatalog(targets.sortedBy(GenerationTarget::id))
        }

        /**
         * Loads target registries visible to [classLoader].
         *
         * @param classLoader class loader containing generation target providers.
         * @return a deterministic target catalog.
         * @throws IllegalArgumentException when a target ID is blank or duplicated.
         */
        fun load(classLoader: ClassLoader): GenerationTargetCatalog =
            from(ServiceLoader.load(GenerationTargetRegistry::class.java, classLoader))
    }
}
