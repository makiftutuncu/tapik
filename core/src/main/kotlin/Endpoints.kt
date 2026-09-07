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
 * @property documentation human-readable endpoint documentation.
 * @property tags endpoint tags using set semantics.
 * @property state binding state.
 */
class Endpoint<
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
    val documentation: EndpointDocumentation,
    tags: Set<String>,
    val state: S
) {
    val tags: Set<String> = tags.snapshotSet()

    /** Returns [method] for destructuring. */
    operator fun component1(): Method = method

    /** Returns [uri] for destructuring. */
    operator fun component2(): Uri<P, Q> = uri

    /** Returns [headers] for destructuring. */
    operator fun component3(): H = headers

    /** Returns [input] for destructuring. */
    operator fun component4(): I = input

    /** Returns [outputs] for destructuring. */
    operator fun component5(): O = outputs

    /** Returns [documentation] for destructuring. */
    operator fun component6(): EndpointDocumentation = documentation

    /** Returns [tags] for destructuring. */
    operator fun component7(): Set<String> = tags

    /** Returns [state] for destructuring. */
    operator fun component8(): S = state

    /** Returns a copy, snapshotting structural collection inputs. */
    internal fun copy(
        method: Method = this.method,
        uri: Uri<@UnsafeVariance P, @UnsafeVariance Q> = this.uri,
        headers: @UnsafeVariance H = this.headers,
        input: @UnsafeVariance I = this.input,
        outputs: @UnsafeVariance O = this.outputs,
        documentation: EndpointDocumentation = this.documentation,
        tags: Set<String> = this.tags,
        state: @UnsafeVariance S = this.state
    ): Endpoint<P, Q, H, I, O, S> = Endpoint(method, uri, headers, input, outputs, documentation, tags, state)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is Endpoint<*, *, *, *, *, *> &&
                method == other.method &&
                uri == other.uri &&
                headers == other.headers &&
                input == other.input &&
                outputs == other.outputs &&
                documentation == other.documentation &&
                tags == other.tags &&
                state == other.state)

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + input.hashCode()
        result = 31 * result + outputs.hashCode()
        result = 31 * result + documentation.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }

    override fun toString(): String =
        "Endpoint(method=$method, uri=$uri, headers=$headers, input=$input, outputs=$outputs, documentation=$documentation, tags=$tags, state=$state)"
}

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
                documentation = documentation,
                tags = tags,
                state = Ready("${thisRef.id}.${property.name}")
            )
        thisRef.register(ready)
        return ReadOnlyProperty { _, _ -> ready }
    }
