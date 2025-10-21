package dev.akif.tapik.codec

import arrow.core.EitherNel

/**
 * Converts serialized payloads into strongly typed domain values.
 *
 * @param I serialized input type the decoder consumes.
 * @param O domain output type produced after decoding.
 */
fun interface Decoder<in I, out O> {
    /**
     * Attempts to decode the provided [input].
     *
     * @param input serialized value supplied by the transport layer.
     * @return an [EitherNel] containing the decoded value or collected error messages.
     */
    fun decode(input: I): EitherNel<String, O>
}
