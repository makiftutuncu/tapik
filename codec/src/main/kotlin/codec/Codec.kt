package dev.akif.tapik.codec

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import dev.akif.tapik.types.*
import kotlin.reflect.KClass

interface Codec<Source : Any, Target : Any> : Decoder<Target, Source>, Encoder<Source, Target> {
    val sourceClass: KClass<Source>
    val targetClass: KClass<Target>

    operator fun <Source2: Any, Target2: Any> plus(that: Codec<Source2, Target2>): Tuple2<Codec<*, *>, Codec<Source, Target>, Codec<Source2, Target2>> =
        Tuple2(this, that)

    companion object {
        inline fun <reified T : Any> identity(name: String): Codec<T, T> =
            object : Codec<T, T> {
                override val sourceClass: KClass<T> = T::class
                override val targetClass: KClass<T> = T::class
                override fun decode(input: T): Either<NonEmptyList<String>, T> = input.right()
                override fun encode(input: T): T = input
            }

        inline fun <reified I : Any, reified O : Any> nullable(
            name: String,
            crossinline encoder: (I) -> O,
            crossinline decoder: (O) -> I?
        ): Codec<I, O> =
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
            }

        inline fun <reified I: Any, reified O: Any> unsafe(
            name: String,
            crossinline encoder: (I) -> O,
            crossinline decoder: (O) -> I
        ): Codec<I, O> =
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
            }
    }
}

inline fun <Source: Any, Target: Any, reified Target2: Any> Codec<Source, Target>.unsafeTransform(
    crossinline from: (Target2) -> Target,
    crossinline to: (Target) -> Target2,
): Codec<Source, Target2> =
    object : Codec<Source, Target2> {
        override val sourceClass: KClass<Source> = this@unsafeTransform.sourceClass

        override val targetClass: KClass<Target2> = Target2::class

        override fun decode(input: Target2): Either<NonEmptyList<String>, Source> =
            this@unsafeTransform.decode(from(input))

        override fun encode(input: Source): Target2 =
            to(this@unsafeTransform.encode(input))
    }

fun <T : Any> StringCodec<T>.toByteArrayCodec(): ByteArrayCodec<T> =
    unsafeTransform(
        from = { String(it) },
        to = { it.toByteArray() }
    )
