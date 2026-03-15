@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.codec

import dev.akif.tapik.ValueClass
import dev.akif.tapik.codec.Codec.Companion.identity
import dev.akif.tapik.codec.Codec.Companion.nullable
import dev.akif.tapik.codec.Codec.Companion.unsafe
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import kotlin.reflect.full.primaryConstructor

/**
 * Default [StringCodec] factories for the scalar types used throughout Tapik's HTTP DSL.
 *
 * This object centralizes the library's built-in string parsing rules so headers, parameters, and
 * bodies all use the same conversions and error reporting.
 */
object StringCodecs : Defaults<StringCodec<Unit>, StringCodec<Boolean>, StringCodec<Byte>, StringCodec<Short>, StringCodec<Int>, StringCodec<Long>, StringCodec<Float>, StringCodec<Double>, StringCodec<BigInteger>, StringCodec<BigDecimal>, StringCodec<String>, StringCodec<UUID>> {
    override fun unit(name: String): StringCodec<Unit> = nullable(name, { "" }) { }

    override fun boolean(name: String): StringCodec<Boolean> =
        nullable(name, {
            it.toString()
        }) { it.toBooleanStrictOrNull() }

    override fun byte(name: String): StringCodec<Byte> = nullable(name, { it.toString() }) { it.toByteOrNull() }

    override fun short(name: String): StringCodec<Short> = nullable(name, { it.toString() }) { it.toShortOrNull() }

    override fun int(name: String): StringCodec<Int> = nullable(name, { it.toString() }) { it.toIntOrNull() }

    override fun long(name: String): StringCodec<Long> = nullable(name, { it.toString() }) { it.toLongOrNull() }

    override fun float(name: String): StringCodec<Float> = nullable(name, { it.toString() }) { it.toFloatOrNull() }

    override fun double(name: String): StringCodec<Double> = nullable(name, { it.toString() }) { it.toDoubleOrNull() }

    override fun bigInteger(name: String): StringCodec<BigInteger> = unsafe(name, { it.toString() }) { BigInteger(it) }

    override fun bigDecimal(name: String): StringCodec<BigDecimal> = unsafe(name, { it.toString() }) { BigDecimal(it) }

    override fun string(name: String): StringCodec<String> = identity(name)

    override fun uuid(name: String): StringCodec<UUID> = unsafe(name, { it.toString() }) { UUID.fromString(it) }

    /**
     * Creates a [StringCodec] for a [ValueClass] wrapper by delegating to Tapik's default string codec and
     * constructing the wrapper through its primary constructor.
     *
     * This is intended for value classes such as `UserId`, `OrderNumber`, or similar wrapper types that
     * expose a single [ValueClass.value] property.
     *
     * @param V wrapped scalar value type used by the [ValueClass].
     * @param T API wrapper type encoded and decoded by the returned codec.
     * @param fallbackName name used in codec diagnostics when [T]'s simple name is unavailable.
     * @param toString function that converts the wrapped [V] into its string representation.
     * @return a [StringCodec] that round-trips [T] using its wrapped [ValueClass.value].
     * @throws IllegalArgumentException when [T] does not expose a primary constructor.
     * @see ValueClass
     */
    inline fun <V, reified T : ValueClass<V>> ofValueClass(
        fallbackName: String = "value",
        crossinline toString: (V) -> String = { it.toString() }
    ): StringCodec<T> {
        val constructor =
            requireNotNull(T::class.primaryConstructor) {
                "Cannot construct ${T::class.qualifiedName ?: fallbackName} because it does not declare a primary constructor"
            }

        return string(T::class.simpleName?.replaceFirstChar { it.lowercase() } ?: fallbackName)
            .unsafeTransformSource(
                from = { constructor.call(it) },
                to = { toString(it.value) }
            )
    }
}
