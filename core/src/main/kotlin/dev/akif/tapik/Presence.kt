package dev.akif.tapik

/**
 * Describes whether a named [Value] may be absent and whether absence has a replacement value.
 *
 * @param Value decoded Kotlin value type.
 */
sealed interface Presence<out Value : Any>

/** A value that must be present. */
data object Required : Presence<Nothing>

/** A value that may be absent without a replacement. */
data object Optional : Presence<Nothing>

/**
 * A value that may be absent and is then replaced by [value].
 *
 * @param Value decoded Kotlin value type.
 * @property value replacement used when the value is absent.
 */
data class Default<Value : Any>(
    val value: Value
) : Presence<Value>
