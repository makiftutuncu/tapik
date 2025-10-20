package dev.akif.tapik.codec

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import kotlin.reflect.KClass

interface Codec<Source : Any, Target : Any> :
    Decoder<Target, Source>,
    Encoder<Source, Target> {
    val sourceClass: KClass<Source>
    val targetClass: KClass<Target>

    companion object {
        inline fun <reified T : Any> identity(name: String): Codec<T, T> =
            object : Codec<T, T> {
                override val sourceClass: KClass<T> = T::class
                override val targetClass: KClass<T> = T::class

                override fun decode(input: T): EitherNel<String, T> = input.right()

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

                override fun decode(input: O): EitherNel<String, I> =
                    when (val value = decoder(input)) {
                        null -> "Cannot decode '$name' as ${I::class.simpleName}: $input".leftNel()
                        else -> value.right()
                    }

                override fun encode(input: I): O = encoder(input)
            }

        inline fun <reified I : Any, reified O : Any> unsafe(
            name: String,
            crossinline encoder: (I) -> O,
            crossinline decoder: (O) -> I
        ): Codec<I, O> =
            object : Codec<I, O> {
                override val sourceClass: KClass<I> = I::class
                override val targetClass: KClass<O> = O::class

                override fun decode(input: O): EitherNel<String, I> =
                    try {
                        decoder(input).right()
                    } catch (e: Exception) {
                        "Cannot decode '$name' as ${I::class.simpleName}: $input: $e".leftNel()
                    }

                override fun encode(input: I): O = encoder(input)
            }
    }
}

inline fun <Source : Any, Target : Any, reified Source2 : Any> Codec<Source, Target>.unsafeTransformSource(
    crossinline from: (Source) -> Source2,
    crossinline to: (Source2) -> Source
): Codec<Source2, Target> =
    object : Codec<Source2, Target> {
        override val sourceClass: KClass<Source2> = Source2::class

        override val targetClass: KClass<Target> = this@unsafeTransformSource.targetClass

        override fun decode(input: Target): EitherNel<String, Source2> =
            this@unsafeTransformSource.decode(input).map {
                from(it)
            }

        override fun encode(input: Source2): Target = this@unsafeTransformSource.encode(to(input))
    }

inline fun <Source : Any, Target : Any, reified Target2 : Any> Codec<Source, Target>.unsafeTransformTarget(
    crossinline from: (Target2) -> Target,
    crossinline to: (Target) -> Target2
): Codec<Source, Target2> =
    object : Codec<Source, Target2> {
        override val sourceClass: KClass<Source> = this@unsafeTransformTarget.sourceClass

        override val targetClass: KClass<Target2> = Target2::class

        override fun decode(input: Target2): EitherNel<String, Source> = this@unsafeTransformTarget.decode(from(input))

        override fun encode(input: Source): Target2 = to(this@unsafeTransformTarget.encode(input))
    }

fun <T : Any> StringCodec<T>.toByteArrayCodec(): ByteArrayCodec<T> =
    unsafeTransformTarget(
        from = { String(it) },
        to = { it.toByteArray() }
    )
