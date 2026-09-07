package dev.akif.tapik

/**
 * A structured decoding error.
 *
 * @property message human-readable explanation of the failure.
 * @property location optional logical location of the invalid value.
 * @property cause optional exception that caused the failure.
 */
data class DecodeError(
    val message: String,
    val location: String? = null,
    val cause: Throwable? = null
)

/** The result of decoding a representation into a Kotlin value. */
sealed interface DecodeResult<out Value : Any> {
    /** A successfully decoded [value]. */
    data class Success<Value : Any>(
        val value: Value
    ) : DecodeResult<Value>

    /**
     * One or more errors encountered while decoding.
     *
     * @throws IllegalArgumentException when [errors] is empty.
     */
    class Failure(
        errors: List<DecodeError>
    ) : DecodeResult<Nothing> {
        /** Errors in decoding order, protected from caller mutation. */
        val errors: List<DecodeError> = errors.snapshotList()

        init {
            require(this.errors.isNotEmpty()) { "A decode failure must contain at least one error" }
        }

        /** Creates a failure containing one [error]. */
        constructor(error: DecodeError) : this(listOf(error))

        /** Returns [errors] for destructuring. */
        operator fun component1(): List<DecodeError> = errors

        /** Returns a copy, snapshotting structural collection inputs. */
        fun copy(
            errors: List<DecodeError> = this.errors
        ): Failure = Failure(errors)

        override fun equals(other: Any?): Boolean =
            this === other ||
                (other is Failure &&
                    errors == other.errors)

        override fun hashCode(): Int = errors.hashCode()

        override fun toString(): String =
            "Failure(errors=$errors)"
    }
}

/** Decodes a serialized [Representation] into a [Value]. */
fun interface Decoder<in Representation : Any, out Value : Any> {
    /** Decodes [representation]. */
    fun decode(representation: Representation): DecodeResult<Value>
}

/** Encodes a [Value] into its serialized [Representation]. */
fun interface Encoder<in Value : Any, out Representation : Any> {
    /** Encodes [value]. */
    fun encode(value: Value): Representation
}

/**
 * Bidirectional conversion between a Kotlin [Value] and serialized [Representation].
 *
 * @property decoder serialized-to-Kotlin conversion.
 * @property encoder Kotlin-to-serialized conversion.
 */
data class Codec<Value : Any, Representation : Any>(
    val decoder: Decoder<Representation, Value>,
    val encoder: Encoder<Value, Representation>
) {
    /** Decodes [representation]. */
    fun decode(representation: Representation): DecodeResult<Value> = decoder.decode(representation)

    /** Encodes [value]. */
    fun encode(value: Value): Representation = encoder.encode(value)
}
