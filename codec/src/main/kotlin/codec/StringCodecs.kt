package dev.akif.tapik.codec

import dev.akif.tapik.codec.Codec.Companion.nullable
import dev.akif.tapik.codec.Codec.Companion.unsafe
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

object StringCodecs: Defaults<StringCodec<Unit>, StringCodec<Boolean>, StringCodec<Byte>, StringCodec<Short>, StringCodec<Int>, StringCodec<Long>, StringCodec<Float>, StringCodec<Double>, StringCodec<BigInteger>, StringCodec<BigDecimal>, StringCodec<String>, StringCodec<UUID>> {
    override fun unit(name: String): StringCodec<Unit> =
        nullable(name, { "" } ) { }

    override fun boolean(name: String): StringCodec<Boolean> =
        nullable(name, { it.toString() }) { it.toBooleanStrictOrNull() }

    override fun byte(name: String): StringCodec<Byte> =
        nullable(name, { it.toString() }) { it.toByteOrNull() }

    override fun short(name: String): StringCodec<Short> =
        nullable(name, { it.toString() }) { it.toShortOrNull() }

    override fun int(name: String): StringCodec<Int> =
        nullable(name, { it.toString() }) { it.toIntOrNull() }

    override fun long(name: String): StringCodec<Long> =
        nullable(name, { it.toString() }) { it.toLongOrNull() }

    override fun float(name: String): StringCodec<Float> =
        nullable(name, { it.toString() }) { it.toFloatOrNull() }

    override fun double(name: String): StringCodec<Double> =
        nullable(name, { it.toString() }) { it.toDoubleOrNull() }

    override fun bigInteger(name: String): StringCodec<BigInteger> =
        unsafe(name, { it.toString() }) { BigInteger(it) }

    override fun bigDecimal(name: String): StringCodec<BigDecimal> =
        unsafe(name, { it.toString() }) { BigDecimal(it) }

    override fun string(name: String): StringCodec<String> =
        nullable(name, { it }) { it }

    override fun uuid(name: String): StringCodec<UUID> =
        unsafe(name, { it.toString() }) { UUID.fromString(it) }
}
