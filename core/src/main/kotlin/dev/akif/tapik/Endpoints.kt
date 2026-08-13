package dev.akif.tapik

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/** Binding state carried by an [Endpoint]. */
sealed interface EndpointState

/** An endpoint definition that has not yet been bound to an API property. */
data object Draft : EndpointState

/**
 * An endpoint bound to a property with a qualified [id].
 *
 * @throws IllegalArgumentException when [id] is blank.
 */
data class Ready(
    val id: String
) : EndpointState {
    init {
        require(id.isNotBlank()) { "Endpoint ID must not be blank" }
    }
}

/**
 * An immutable typed HTTP endpoint definition.
 *
 * @param P path-variable tuple type.
 * @param Q query-parameter tuple type.
 * @param H header tuple type.
 * @param I request-input type.
 * @param O output type.
 * @param S binding state.
 * @property method HTTP method.
 * @property uri URI definition.
 * @property headers request headers.
 * @property input request input.
 * @property outputs response alternatives.
 * @property state binding state.
 */
@ConsistentCopyVisibility
data class Endpoint<
    out P : Paths,
    out Q : Queries,
    out H : Headers,
    out I : Input,
    out O : Outputs,
    out S : EndpointState
> internal constructor(
    val method: Method,
    val uri: Uri<P, Q>,
    val headers: H,
    val input: I,
    val outputs: O,
    val state: S
)

/** Qualified API and property identifier of this ready endpoint. */
val <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, O, Ready>.id: String
    get() = state.id

/**
 * Binds this draft endpoint to [property] in [thisRef] before the property is first read.
 *
 * @return a delegate that always returns the same ready endpoint value.
 */
operator fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, O, Draft>.provideDelegate(
        thisRef: Api,
        property: KProperty<*>
    ): ReadOnlyProperty<Api, Endpoint<P, Q, H, I, O, Ready>> {
        val ready =
            Endpoint(
                method = method,
                uri = uri,
                headers = headers,
                input = input,
                outputs = outputs,
                state = Ready("${thisRef.id}.${property.name}")
            )
        thisRef.register(ready)
        return ReadOnlyProperty { _, _ -> ready }
    }
