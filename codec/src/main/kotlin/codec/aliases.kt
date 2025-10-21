package dev.akif.tapik.codec

/**
 * Codec that serializes a [T] value to a [ByteArray] payload.
 *
 * @param T domain type handled by the codec.
 * @see Codec
 */
typealias ByteArrayCodec<T> = Codec<T, ByteArray>

/**
 * Codec that serializes a [T] value to a [String] payload.
 *
 * @param T domain type handled by the codec.
 * @see Codec
 */
typealias StringCodec<T> = Codec<T, String>
