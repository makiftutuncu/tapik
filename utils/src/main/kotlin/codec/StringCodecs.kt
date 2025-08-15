package dev.akif.tapik.codec

object StringCodecs {
    val noop: StringCodec<String> = Codec.noop()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> default(): StringCodec<T> =
        when (T::class) {
            String::class -> noop as StringCodec<T>
            else -> throw error("Unsupported String codec type ${T::class}")
        }
}
