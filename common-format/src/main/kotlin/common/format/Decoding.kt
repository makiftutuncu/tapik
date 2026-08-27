package dev.akif.tapik.common.format

import dev.akif.tapik.DecodeError
import dev.akif.tapik.DecodeResult

/** Runs [decode] and converts an exception into a decoding failure retaining its cause. */
inline fun <Value : Any> decodeCatching(
    fallbackMessage: String,
    decode: () -> Value
): DecodeResult<Value> =
    try {
        DecodeResult.Success(decode())
    } catch (cause: Exception) {
        DecodeResult.Failure(
            DecodeError(
                message = cause.message ?: fallbackMessage,
                cause = cause
            )
        )
    }
