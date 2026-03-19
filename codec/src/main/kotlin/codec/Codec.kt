package dev.akif.tapik.codec

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import kotlin.reflect.KClass

/**
 * Bidirectional codec that converts between domain [Source] values and their serialized [Target] representations.
 *
 * Implementations expose both encoding and decoding functionality together with runtime type metadata so that
 * calling code can select codecs without relying on Java reflection.
 *
 * @param Source domain model type produced by [decode].
 * @param Target serialized data type consumed by [encode].
 * @property sourceClass runtime [KClass] describing the supported [Source] type.
 * @property targetClass runtime [KClass] describing the supported [Target] type.
 * @see Decoder
 * @see Encoder
 */
interface Codec<Source : Any, Target : Any> :
    Decoder<Target, Source>,
    Encoder<Source, Target> {
    val sourceClass: KClass<Source>
    val targetClass: KClass<Target>

    /** Factory helpers for common [Codec] construction patterns. */
    companion object {
        /**
         * Builds a codec that forwards values without transforming them.
         *
         * This exists so code that expects a named codec factory can keep the same call shape even when
         * the source and target types are identical.
         *
         * @param T value type handled by the codec.
         * @param name unused placeholder kept for symmetry with other codec factories.
         */
        inline fun <reified T : Any> identity(name: String): Codec<T, T> =
            object : Codec<T, T> {
                override val sourceClass: KClass<T> = T::class
                override val targetClass: KClass<T> = T::class

                override fun decode(input: T): EitherNel<String, T> = input.right()

                override fun encode(input: T): T = input
            }

        /**
         * Builds a codec around a decoder that reports failure by returning `null`.
         *
         * @param I domain type to decode and encode.
         * @param O serialized representation produced during encoding.
         * @param name human readable identifier included in decode errors.
         * @param encoder transformation applied when encoding [I] to [O].
         * @param decoder transformation that converts [O] back to [I]; returning `null` marks the input invalid.
         */
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

        /**
         * Builds a codec around a decoder that may throw while parsing.
         *
         * @param I domain type to decode and encode.
         * @param O serialized representation produced during encoding.
         * @param name human readable identifier included in decode errors.
         * @param encoder transformation applied when encoding [I] to [O].
         * @param decoder transformation that converts [O] back to [I]; thrown exceptions are captured as failures.
         */
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

/**
 * Adapts a codec to a different source type while reusing the original target representation.
 *
 * Decoding still starts with the receiver, then maps the decoded value through [from]. Encoding goes
 * the other way by converting [Source2] back to [Source] through [to] before delegating.
 *
 * The transformation is unsafe because neither mapping is validated or wrapped; exceptions from [from]
 * or [to] escape to the caller.
 */
inline fun <Source : Any, Target : Any, reified Source2 : Any> Codec<Source, Target>.unsafeTransformSource(
    crossinline from: (Source) -> Source2,
    crossinline to: (Source2) -> Source
): Codec<Source2, Target> {
    val codec = this
    return object : Codec<Source2, Target> {
        override val sourceClass: KClass<Source2> = Source2::class

        override val targetClass: KClass<Target> = codec.targetClass

        override fun decode(input: Target): EitherNel<String, Source2> = codec.decode(input).map { from(it) }

        override fun encode(input: Source2): Target = codec.encode(to(input))
    }
}

/**
 * Adapts a codec to a different target type while keeping the original source type.
 *
 * Decoding first converts the incoming [Target2] value into the receiver's [Target] with [from].
 * Encoding delegates to the receiver, then maps the encoded value into [Target2] with [to].
 *
 * The transformation is unsafe because neither mapping is validated or wrapped; exceptions from [from]
 * or [to] escape to the caller.
 */
inline fun <Source : Any, Target : Any, reified Target2 : Any> Codec<Source, Target>.unsafeTransformTarget(
    crossinline from: (Target2) -> Target,
    crossinline to: (Target) -> Target2
): Codec<Source, Target2> {
    val codec = this
    return object : Codec<Source, Target2> {
        override val sourceClass: KClass<Source> = codec.sourceClass

        override val targetClass: KClass<Target2> = Target2::class

        override fun decode(input: Target2): EitherNel<String, Source> = codec.decode(from(input))

        override fun encode(input: Source): Target2 = to(codec.encode(input))
    }
}

/**
 * Converts a [StringCodec] into a [ByteArrayCodec] by bridging through Kotlin's default string/byte
 * array conversions.
 */
fun <T : Any> StringCodec<T>.toByteArrayCodec(): ByteArrayCodec<T> =
    unsafeTransformTarget(
        from = { it.decodeToString() },
        to = { it.encodeToByteArray() }
    )
