package dev.akif.tapik.codec

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nel
import arrow.core.right

interface Codec<Source, Target> : Decoder<Target, Source>, Encoder<Source, Target> {
    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified I, O, C : Codec<I, O>> nullable(
            name: String,
            crossinline encoder: (I) -> O,
            crossinline decoder: (O) -> I?
        ): C =
            object : Codec<I, O> {
                override fun decode(input: O): Either<NonEmptyList<String>, I> =
                    when (val value = decoder(input)) {
                        null -> "Cannot decode '$name' as ${I::class.simpleName}: $input".nel().left()
                        else -> value.right()
                    }

                override fun encode(input: I): O =
                    encoder(input)
            } as C

        @Suppress("UNCHECKED_CAST")
        inline fun <reified I, O, C : Codec<I, O>> unsafe(
            name: String,
            crossinline encoder: (I) -> O,
            crossinline decoder: (O) -> I
        ): C =
            object : Codec<I, O> {
                override fun decode(input: O): Either<NonEmptyList<String>, I> =
                    try {
                        decoder(input).right()
                    } catch (e: Exception) {
                        "Cannot decode '$name' as ${I::class.simpleName}: $input: $e".nel().left()
                    }

                override fun encode(input: I): O =
                    encoder(input)
            } as C
    }
}
