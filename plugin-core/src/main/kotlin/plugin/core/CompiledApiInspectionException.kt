package dev.akif.tapik.plugin.core

/**
 * A failure to pair a compiled API's runtime values with Kotlin metadata.
 *
 * @param message contextual failure description.
 * @param cause optional metadata-reading failure.
 */
class CompiledApiInspectionException(
    message: String,
    cause: Throwable? = null
) : IllegalStateException(message, cause)
