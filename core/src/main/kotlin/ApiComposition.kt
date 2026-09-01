package dev.akif.tapik

import kotlin.reflect.KProperty

/**
 * A direct, property-delegated inclusion of [api] in another API.
 *
 * The delegated property evaluates to [api] with its concrete type. [propertyName] becomes available when this value
 * is bound by Kotlin's property-delegation protocol.
 */
class ApiInclusion<out Included : Api> internal constructor(
    val api: Included
) {
    private var owner: Api? = null
    private var name: String? = null

    /** Property name locating [api] from its containing API. */
    val propertyName: String
        get() = requireNotNull(name) { "API inclusion has not been delegated" }

    /** Registers this inclusion at the delegated property's declaration position. */
    operator fun provideDelegate(
        thisRef: Api,
        property: KProperty<*>
    ): ApiInclusion<Included> {
        thisRef.register(this, property.name)
        return this
    }

    /** Returns the original included API value. */
    operator fun getValue(
        thisRef: Api,
        property: KProperty<*>
    ): Included {
        check(owner === thisRef && name == property.name) {
            "API inclusion is not delegated to '${thisRef.id}.${property.name}'"
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
