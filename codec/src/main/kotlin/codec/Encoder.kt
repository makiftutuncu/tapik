package dev.akif.tapik.codec

/**
 * Converts domain values into a serialized representation.
 *
 * @param I domain input type to be encoded.
 * @param O serialized output type produced by the encoder.
 */
fun interface Encoder<in I, out O> {
    /**
     * Encodes the provided [input] into the serialized representation.
     *
     * @param input domain value to convert.
     * @return the serialized representation ready for transport.
     */
    fun encode(input: I): O
}
