package dev.akif.tapik

/**
 * Shared helper methods used by generated code across client and controller generators.
 */
interface Helpers {
    /**
     * Casts the receiver to a [QueryParameter] of [T], failing with a descriptive error otherwise.
     *
     * @param T expected query parameter value type.
     * @receiver candidate value provided by generated code.
     * @return receiver cast to [QueryParameter] with the expected value type.
     * @throws IllegalStateException when receiver is not a [QueryParameter] of the expected type.
     * @see QueryParameter
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Any?.asQueryParameter(): QueryParameter<T> =
        this as? QueryParameter<T> ?: error("Expected QueryParameter but got ${this?.let { it::class.java.name }}")

    /**
     * Returns the default query parameter value, failing when it is missing or explicitly `null`.
     *
     * @param T query parameter value type.
     * @receiver query parameter definition to inspect.
     * @return non-null default value declared for the query parameter.
     * @throws IllegalArgumentException when no default value is configured or it is `null`.
     */
    fun <T : Any> QueryParameter<T>.getDefaultOrFail(): T =
        requireNotNull(default.getOrNull()) { "Default value of query parameter '$name' is missing" }

    /**
     * Casts the receiver to [HeaderValues] of [T], failing with a descriptive error otherwise.
     *
     * @param T expected header value type.
     * @receiver candidate value provided by generated code.
     * @return receiver cast to [HeaderValues] with the expected value type.
     * @throws IllegalStateException when receiver is not a [HeaderValues] instance of the expected type.
     * @see HeaderValues
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> Any?.asHeaderValues(): HeaderValues<T> =
        this as? HeaderValues<T> ?: error("Expected HeaderValues but got ${this?.let { it::class.java.name }}")

    /**
     * Returns the first header value from the receiver, failing when it is missing.
     *
     * @param T header value type.
     * @receiver header values container to inspect.
     * @return first value contained in the receiver.
     * @throws IllegalArgumentException when the receiver has no entries.
     * @see HeaderValues.values
     */
    fun <T : Any> HeaderValues<T>.getFirstValueOrFail(): T =
        requireNotNull(values.firstOrNull()) { "First value of header '$name' is missing" }
}
