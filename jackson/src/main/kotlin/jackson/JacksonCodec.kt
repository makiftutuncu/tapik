package dev.akif.tapik.jackson

import arrow.core.*
import com.fasterxml.jackson.databind.ObjectMapper
import dev.akif.tapik.codec.ByteArrayCodec
import kotlin.reflect.KClass

class JacksonCodec<T : Any>(
    val name: String,
    val mapper: ObjectMapper,
    override val sourceClass: KClass<T>
): ByteArrayCodec<T> {
    override val targetClass: KClass<ByteArray> = ByteArray::class

    override fun decode(input: ByteArray): Either<NonEmptyList<String>, T> =
        try {
            mapper.readValue(input, sourceClass.java).right()
        } catch (e: Exception) {
            "Cannot decode '$name' as ${sourceClass.simpleName}: $input: $e".nel().left()
        }

    override fun encode(input: T): ByteArray =
        mapper.writeValueAsBytes(input)
}
