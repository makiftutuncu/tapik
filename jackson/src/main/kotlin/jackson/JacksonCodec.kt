package dev.akif.tapik.jackson

import arrow.core.*
import com.fasterxml.jackson.databind.ObjectMapper
import dev.akif.tapik.codec.ByteArrayCodec
import kotlin.reflect.KClass

/**
 * [ByteArrayCodec] implementation that delegates JSON processing to a Jackson [ObjectMapper].
 *
 * @param T domain type handled by the codec.
 * @property name human-readable identifier used when reporting failures.
 * @property mapper Jackson mapper used for serialization and deserialization.
 * @property sourceClass runtime [KClass] representing the decoded type.
 */
class JacksonCodec<T : Any>(
    val name: String,
    val mapper: ObjectMapper,
    override val sourceClass: KClass<T>
) : ByteArrayCodec<T> {
    /** Runtime [KClass] representing the byte array payload produced by [encode]. */
    override val targetClass: KClass<ByteArray> = ByteArray::class

    /**
     * Attempts to decode the provided [input] using the configured Jackson [mapper].
     *
     * @param input JSON payload received from the client.
     * @return a decoded domain object or accumulated error messages when decoding fails.
     */
    override fun decode(input: ByteArray): EitherNel<String, T> =
        try {
            mapper.readValue(input, sourceClass.java).right()
        } catch (e: Exception) {
            "Cannot decode '$name' as ${sourceClass.simpleName}: $input: $e".leftNel()
        }

    /**
     * Encodes the given [input] into a JSON [ByteArray] using the configured Jackson [mapper].
     *
     * @param input domain value to serialize.
     * @return serialized JSON payload.
     */
    override fun encode(input: T): ByteArray = mapper.writeValueAsBytes(input)
}
