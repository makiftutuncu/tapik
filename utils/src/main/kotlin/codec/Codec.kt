package dev.akif.tapik.codec

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.right

interface Codec<Source, Target> : Decoder<Target, Source>, Encoder<Source, Target> {
    companion object {
        fun <T> noop(): Codec<T, T> = object : Codec<T, T> {
            override fun decode(input: T): Either<NonEmptyList<String>, T> = input.right()
            override fun encode(input: T): T = input
        }
    }
}
