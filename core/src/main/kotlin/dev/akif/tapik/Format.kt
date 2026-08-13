package dev.akif.tapik

/**
 * A [codec] and [schema] describing a non-null Kotlin [Value] in serialized [Representation] form.
 *
 * @param Value Kotlin value type.
 * @param Representation serialized representation type.
 */
data class Format<Value : Any, Representation : Any>(
    val codec: Codec<Value, Representation>,
    val schema: Schema
) {
    /** Factory namespace for cached formats of common types. */
    companion object : FormatDefaults<String> by StringFormats

    /** Decodes [representation]. */
    fun decode(representation: Representation): DecodeResult<Value> = codec.decode(representation)

    /** Encodes [value]. */
    fun encode(value: Value): Representation = codec.encode(value)

    /**
     * Transforms this format to [TransformedValue], converting exceptions from [decode] into structured failures.
     *
     * The serialized representation and schema remain unchanged.
     */
    fun <TransformedValue : Any> transform(
        decode: (Value) -> TransformedValue,
        encode: (TransformedValue) -> Value
    ): Format<TransformedValue, Representation> =
        Format(
            codec =
                Codec(
                    decoder =
                        Decoder { representation ->
                            when (val result = this.decode(representation)) {
                                is DecodeResult.Success ->
                                    try {
                                        DecodeResult.Success(decode(result.value))
                                    } catch (cause: Exception) {
                                        DecodeResult.Failure(
                                            DecodeError(
                                                message = cause.message ?: "Decoded value transformation failed",
                                                cause = cause
                                            )
                                        )
                                    }

                                is DecodeResult.Failure -> result
                            }
                        },
                    encoder = Encoder { value -> this.encode(encode(value)) }
                ),
            schema = schema
        )

    /**
     * Transforms this format to [TransformedValue] while allowing exceptions from [decode] to propagate.
     *
     * The serialized representation and schema remain unchanged.
     */
    fun <TransformedValue : Any> transformOrThrow(
        decode: (Value) -> TransformedValue,
        encode: (TransformedValue) -> Value
    ): Format<TransformedValue, Representation> =
        Format(
            codec =
                Codec(
                    decoder =
                        Decoder { representation ->
                            when (val result = this.decode(representation)) {
                                is DecodeResult.Success -> DecodeResult.Success(decode(result.value))
                                is DecodeResult.Failure -> result
                            }
                        },
                    encoder = Encoder { value -> this.encode(encode(value)) }
                ),
            schema = schema
        )

    /** Returns this format with a schema [name]. */
    fun named(name: String): Format<Value, Representation> = copy(schema = schema.named(name))
}

/** A format serialized as a [String]. */
typealias StringFormat<Value> = Format<Value, String>

/** A format serialized as a [ByteArray]. */
typealias ByteArrayFormat<Value> = Format<Value, ByteArray>

/** Shortcut to the default formats on [Format.Companion]. */
val format: Format.Companion
    get() = Format.Companion

/**
 * Lifts this element format into a format for ordered lists.
 *
 * Decoding preserves input order and accumulates every element failure.
 */
fun <Value : Any, Representation : Any> Format<Value, Representation>.repeated(): Format<List<Value>, List<Representation>> =
    Format(
        codec =
            Codec(
                decoder =
                    Decoder { representations ->
                        val values = mutableListOf<Value>()
                        val errors = mutableListOf<DecodeError>()

                        representations.forEach { representation ->
                            when (val result = decode(representation)) {
                                is DecodeResult.Success -> values += result.value
                                is DecodeResult.Failure -> errors += result.errors
                            }
                        }

                        if (errors.isEmpty()) {
                            DecodeResult.Success(values)
                        } else {
                            DecodeResult.Failure(errors)
                        }
                    },
                encoder = Encoder { values -> values.map(::encode) }
            ),
        schema = ArraySchema(items = schema)
    )
