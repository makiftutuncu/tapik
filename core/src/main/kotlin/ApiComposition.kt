package dev.akif.tapik

import kotlin.reflect.KProperty

/**
 * An API included as a property of another API.
 *
 * The property keeps the concrete type of [api], and its declaration position determines where the included endpoints
 * appear in the containing API.
 *
 * @param Included concrete type of the included API.
 * @property api API included in the containing API.
 */
class ApiInclusion<out Included : Api> internal constructor(
    /** API included in the containing API. */
    val api: Included
) {
    private var owner: Api? = null
    private var name: String? = null

    /**
     * Name of the property that includes [api].
     *
     * @throws IllegalArgumentException before the inclusion is assigned to a property.
     */
    val propertyName: String
        get() = requireNotNull(name) { "API inclusion has not been delegated" }

    /**
     * Registers [api] in [thisRef] using [property]'s name and declaration position.
     *
     * @param thisRef API that contains this inclusion.
     * @param property property that defines the inclusion's position and name.
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
     * Returns the API assigned to the inclusion property.
     *
     * @param thisRef API that contains this inclusion.
     * @param property property that defines this inclusion.
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
