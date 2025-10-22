package dev.akif.tapik

/**
 * Marker interface that provides helper extensions common to generated clients.
 *
 * Client implementations receive these utilities so that generated DSL pieces can
 * safely cast metadata objects and unwrap required values without duplicating logic.
 */
interface Client {
    /**
     * Casts the receiver to a [QueryParameter] of [T], failing with a descriptive error otherwise.
     *
     * @receiver candidate value provided by the client DSL.
     * @return the receiver cast to [QueryParameter] with the expected value type.
     * @throws IllegalStateException when the receiver is not a [QueryParameter] of the expected type.
     * @see QueryParameter
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Any?.asQueryParameter(): QueryParameter<T> =
        this as? QueryParameter<T> ?: error("Expected QueryParameter but got ${this?.let { it::class.java.name }}")

    /**
     * Casts the receiver to [HeaderValues] of [T], failing with a descriptive error otherwise.
     *
     * @receiver candidate value provided by the client DSL.
     * @return the receiver cast to [HeaderValues] with the expected value type.
     * @throws IllegalStateException when the receiver is not a [HeaderValues] instance of the expected type.
     * @see HeaderValues
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Any?.asHeaderValues(): HeaderValues<T> =
        this as? HeaderValues<T> ?: error("Expected HeaderValues but got ${this?.let { it::class.java.name }}")

    /**
     * Returns the default value of the query parameter, throwing if it is missing.
     *
     * @return the non-null default configured on the parameter.
     * @throws IllegalArgumentException when no default value has been configured.
     * @see QueryParameter.optional
     */
    fun <T : Any> QueryParameter<T>.getDefaultOrFail(): T =
        requireNotNull(default) { "Default value of query parameter '$name' is missing" }

    /**
     * Returns the first header value from the collection, throwing if it is missing.
     *
     * @return the first value contained in the header.
     * @throws IllegalArgumentException when the collection of values is empty.
     * @see HeaderValues.values
     */
    fun <T : Any> HeaderValues<T>.getFirstValueOrFail(): T =
        requireNotNull(values.firstOrNull()) { "First value of header '$name' is missing" }
}
