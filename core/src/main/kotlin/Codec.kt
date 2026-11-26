package dev.akif.tapik

/**
 * A structured decoding error.
 *
 * @property message human-readable explanation of the failure.
 * @property location optional logical location of the invalid value.
 * @property cause optional exception that caused the failure.
 */
data class DecodeError(
    /** Human-readable explanation of the failure. */
    val message: String,
    /** Optional logical location of the invalid value. */
    val location: String? = null,
    /** Optional exception that caused the failure. */
    val cause: Throwable? = null
)

/** The result of decoding a representation into a Kotlin value. */
sealed interface DecodeResult<out Value : Any> {
    /** A successfully decoded [value].
     *
     * @property value decoded Kotlin value.
     */
    data class Success<Value : Any>(
        /** Decoded Kotlin value. */
        val value: Value
    ) : DecodeResult<Value>

    /**
     * One or more errors encountered while decoding.
     *
     * @param errors decoding errors, copied in their original order.
     * @throws IllegalArgumentException when [errors] is empty.
     */
    class Failure(
        errors: List<DecodeError>
    ) : DecodeResult<Nothing> {
        /** Unmodifiable snapshot of errors in decoding order. */
        val errors: List<DecodeError> = errors.snapshotList()

        init {
            require(this.errors.isNotEmpty()) { "A decode failure must contain at least one error" }
        }

        /**
         * Creates a failure containing one [error].
         *
         * @param error decoding error to include.
         */
        constructor(error: DecodeError) : this(listOf(error))

        /** Returns [errors] for destructuring. */
        operator fun component1(): List<DecodeError> = errors

        /** Returns a copy, snapshotting structural collection inputs. */
        fun copy(
            errors: List<DecodeError> = this.errors
        ): Failure = Failure(errors)

        /** Compares this value with [other]. */
        override fun equals(other: Any?): Boolean =
            this === other ||
                (other is Failure &&
                    errors == other.errors)

        /** Returns a hash code consistent with [equals]. */
        override fun hashCode(): Int = errors.hashCode()

        /** Returns a string representation of this value. */
        override fun toString(): String =
            "Failure(errors=$errors)"
    }
}

/** Decodes a serialized [Representation] into a [Value]. */
fun interface Decoder<in Representation : Any, out Value : Any> {
    /**
     * Decodes [representation]. Return [DecodeResult.Failure] for invalid input; exceptions thrown by this decoder
     * propagate to the caller.
     */
    fun decode(representation: Representation): DecodeResult<Value>
}

/** Encodes a [Value] into its serialized [Representation]. */
fun interface Encoder<in Value : Any, out Representation : Any> {
    /** Encodes [value]. Exceptions thrown by this encoder propagate to the caller. */
    fun encode(value: Value): Representation
}

/**
 * Bidirectional conversion between a Kotlin [Value] and serialized [Representation].
 *
 * @param Value Kotlin value type.
 * @param Representation serialized representation type.
 * @property decoder serialized-to-Kotlin conversion.
 * @property encoder Kotlin-to-serialized conversion.
 */
data class Codec<Value : Any, Representation : Any>(
    /** Serialized-to-Kotlin conversion. */
    val decoder: Decoder<Representation, Value>,
    /** Kotlin-to-serialized conversion. */
    val encoder: Encoder<Value, Representation>
) {
    /** Decodes [representation]; exceptions thrown by [decoder] propagate to the caller. */
    fun decode(representation: Representation): DecodeResult<Value> = decoder.decode(representation)

    /** Encodes [value]; exceptions thrown by [encoder] propagate to the caller. */
    fun encode(value: Value): Representation = encoder.encode(value)
}
