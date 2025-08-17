package dev.akif.tapik.codec

fun interface Encoder<in I, out O> {
    fun encode(input: I): O
}
