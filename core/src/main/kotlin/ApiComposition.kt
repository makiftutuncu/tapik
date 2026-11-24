package dev.akif.tapik

import kotlin.reflect.KProperty

/**
 * A direct, property-delegated inclusion of [api] in another API.
 *
 * The delegated property evaluates to [api] with its concrete type. [propertyName] becomes available when this value
 * is bound by Kotlin's property-delegation protocol.
 *
 * @param Included concrete API type retained by the delegated property.
 * @property api API included in the containing API.
 */
class ApiInclusion<out Included : Api> internal constructor(
    /** API included in the containing API. */
    val api: Included
) {
    private var owner: Api? = null
    private var name: String? = null

    /**
     * Property name locating [api] from its containing API.
     *
     * @throws IllegalArgumentException before the inclusion is assigned to a property.
     */
    val propertyName: String
        get() = requireNotNull(name) { "API inclusion has not been delegated" }

    /**
     * Registers this inclusion at the delegated property's declaration position.
     *
     * @param thisRef API that contains this inclusion.
     * @param property delegated property that defines the inclusion's position and name.
     * @return this inclusion, bound to the containing API.
     * @throws IllegalArgumentException when the inclusion creates a cycle, repeats an API, reuses a property name, or
     * introduces a duplicate API ID.
     * @throws IllegalStateException when this inclusion has already been delegated.
     */
    operator fun provideDelegate(
        thisRef: Api,
        property: KProperty<*>
    ): ApiInclusion<Included> {
        thisRef.register(this, property.name)
        return this
    }

    /**
     * Returns the original included API value.
     *
     * @param thisRef API that contains this inclusion.
     * @param property delegated property that defines this inclusion.
     * @return the API supplied when this inclusion was created.
     * @throws IllegalStateException when this inclusion is not bound to [thisRef] and [property].
     */
    operator fun getValue(
        thisRef: Api,
        property: KProperty<*>
    ): Included {
        check(owner === thisRef && name == property.name) {
            "API inclusion is not delegated to '${thisRef.apiId}.${property.name}'"
        }
        return api
    }

    internal fun bind(
        owner: Api,
        propertyName: String
    ) {
        check(this.owner == null) { "API inclusion has already been delegated" }
        this.owner = owner
        name = propertyName
    }
}

internal sealed interface ApiEntry

internal class EndpointEntry(
    val endpoint: Endpoint<*, *, *, *, *, Ready>
) : ApiEntry

internal class InclusionEntry(
    val inclusion: ApiInclusion<*>
) : ApiEntry
