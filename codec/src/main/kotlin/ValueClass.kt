package dev.akif.tapik

/**
 * Contract for API-facing value classes that expose a single underlying primitive or scalar [T].
 *
 * Implement this interface on value classes to make it straightforward to derive Tapik string codecs
 * from the wrapped [value].
 *
 * @param T underlying value type exposed by the wrapper.
 * @property value wrapped scalar value used during transport encoding.
 * @see dev.akif.tapik.codec.StringCodecs.ofValueClass
 */
interface ValueClass<T> {
    val value: T
}
