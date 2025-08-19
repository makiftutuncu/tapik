package dev.akif.tapik.codec

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import dev.akif.tapik.tuple.*
import kotlin.reflect.KClass

interface Codec<Source : Any, Target : Any> : Decoder<Target, Source>, Encoder<Source, Target> {
    val sourceClass: KClass<Source>
    val targetClass: KClass<Target>

    operator fun <Source2: Any, Target2: Any> plus(that: Codec<Source2, Target2>): Tuple2<Codec<*, *>, Codec<Source, Target>, Codec<Source2, Target2>> =
        Tuple2(this, that)

    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified I : Any, reified O : Any, C : Codec<I, O>> nullable(
            name: String,
            crossinline encoder: (I) -> O,
            crossinline decoder: (O) -> I?
        ): C =
            object : Codec<I, O> {
                override val sourceClass: KClass<I> = I::class
                override val targetClass: KClass<O> = O::class

                override fun decode(input: O): Either<NonEmptyList<String>, I> =
                    when (val value = decoder(input)) {
                        null -> "Cannot decode '$name' as ${I::class.simpleName}: $input".nel().left()
                        else -> value.right()
                    }

                override fun encode(input: I): O =
                    encoder(input)
            } as C

        @Suppress("UNCHECKED_CAST")
        inline fun <reified I: Any, reified O: Any, C : Codec<I, O>> unsafe(
            name: String,
            crossinline encoder: (I) -> O,
            crossinline decoder: (O) -> I
        ): C =
            object : Codec<I, O> {
                override val sourceClass: KClass<I> = I::class
                override val targetClass: KClass<O> = O::class

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
