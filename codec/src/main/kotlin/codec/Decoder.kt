package dev.akif.tapik.codec

import arrow.core.EitherNel

fun interface Decoder<in I, out O> {
    fun decode(input: I): EitherNel<String, O>
}
