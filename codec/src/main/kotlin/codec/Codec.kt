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

    /** Factory helpers for constructing common [Codec] variants. */
    companion object {
        /**
         * Builds a codec that forwards values without transforming them.
         *
         * @param T value type handled by the codec.
         * @param name human readable identifier used in error messages.
         * @return a codec that simply echoes the provided value for both encoding and decoding.
         * @see identity
         */
        inline fun <reified T : Any> identity(name: String): Codec<T, T> =
            object : Codec<T, T> {
                override val sourceClass: KClass<T> = T::class
                override val targetClass: KClass<T> = T::class

                override fun decode(input: T): EitherNel<String, T> = input.right()

                override fun encode(input: T): T = input
            }

        /**
         * Builds a codec that tolerates missing values by using a nullable decoding function.
         *
         * @param I domain type to decode and encode.
         * @param O serialized representation produced during encoding.
         * @param name human readable identifier used in error messages.
         * @param encoder transformation applied when encoding [I] to [O].
         * @param decoder nullable transformation that converts [O] back to [I]; returning `null` yields an error.
         * @return a codec that reports failures and uses [encoder] for serialization.
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
         * Builds a codec wrapping potentially unsafe conversion logic.
         *
         * @param I domain type to decode and encode.
         * @param O serialized representation produced during encoding.
         * @param name human readable identifier used in error messages.
         * @param encoder transformation applied when encoding [I] to [O].
         * @param decoder transformation that converts [O] back to [I]; thrown exceptions are captured as failures.
         * @return a codec that surfaces decoding exceptions as error messages.
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
 * Builds a new codec by transforming the decoded source type.
 *
 * @param Source domain type handled by the original codec.
 * @param Target serialized type emitted by the original codec.
 * @param Source2 alternative domain type exposed by the returned codec.
 * @param from transformation that maps the decoded [Source] value to [Source2].
 * @param to transformation that maps the incoming [Source2] value back to [Source].
 * @return a codec that delegates encoding/decoding through [from] and [to].
 * @see unsafeTransformTarget
 */
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

/**
 * Builds a new codec by transforming the encoded target type.
 *
 * @param Source domain type handled by the original codec.
 * @param Target serialized type emitted by the original codec.
 * @param Target2 alternative serialized type emitted by the returned codec.
 * @param from transformation that converts [Target2] into the original [Target] prior to decoding.
 * @param to transformation that converts the encoded [Target] into [Target2].
 * @return a codec that delegates through [from] and [to] for encoding and decoding respectively.
 * @see unsafeTransformSource
 */
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

/**
 * Converts a [StringCodec] into a [ByteArrayCodec] using UTF-8 byte conversion.
 *
 * @param T domain type handled by the codec.
 * @return a codec that encodes to byte arrays and decodes via string conversion.
 * @see unsafeTransformTarget
 */
fun <T : Any> StringCodec<T>.toByteArrayCodec(): ByteArrayCodec<T> =
    unsafeTransformTarget(
        from = { String(it) },
        to = { it.toByteArray() }
    )
