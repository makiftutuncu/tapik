package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api
import java.util.Collections
import java.util.LinkedHashSet

/**
 * Exact API-ID filters applied before target execution.
 *
 * An empty [includes] set selects every available API. [excludes] are applied afterward.
 *
 * @throws IllegalArgumentException when either set contains a blank ID.
 */
class ApiSelection(
    includes: Set<String> = emptySet(),
    excludes: Set<String> = emptySet()
) {
    /** API IDs eligible for selection, or empty to include all APIs. */
    val includes: Set<String> = includes.snapshot("include")

    /** API IDs removed after includes are applied. */
    val excludes: Set<String> = excludes.snapshot("exclude")

    /**
     * Applies this selection without changing [apis] order.
     *
     * @throws IllegalArgumentException when a configured ID is unknown or no API remains.
     */
    fun select(apis: List<Api>): List<Api> {
        val availableIds = apis.mapTo(mutableSetOf(), Api::id)
        val unknownIds = (includes + excludes) - availableIds
        require(unknownIds.isEmpty()) {
            "API selection references unknown IDs: ${unknownIds.sorted().joinToString()}"
        }
        val selected =
            apis.filter { api ->
                (includes.isEmpty() || api.id in includes) && api.id !in excludes
            }
        require(selected.isNotEmpty()) { "API selection must retain at least one API" }
        return selected
    }

    override fun equals(other: Any?): Boolean =
        this === other || other is ApiSelection && includes == other.includes && excludes == other.excludes

    override fun hashCode(): Int = 31 * includes.hashCode() + excludes.hashCode()

    override fun toString(): String = "ApiSelection(includes=$includes, excludes=$excludes)"
}

private fun Set<String>.snapshot(kind: String): Set<String> {
    require(none(String::isBlank)) { "API $kind filters must not contain blank IDs" }
    return Collections.unmodifiableSet(LinkedHashSet(this))
}
