package dev.akif.tapik.codec

import arrow.core.Either
import arrow.core.NonEmptyList

fun interface Decoder<in I, out O> {
    fun decode(input: I): Either<NonEmptyList<String>, O>
}
