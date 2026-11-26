package dev.akif.tapik.common.format

/**
 * A failure to derive a schema from format-integration metadata.
 *
 * @param message explanation of the type or wire shape that could not be represented.
 */
class SchemaDerivationException(
    message: String
) : IllegalArgumentException(message)
