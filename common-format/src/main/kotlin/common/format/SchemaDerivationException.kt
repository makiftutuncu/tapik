package dev.akif.tapik.common.format

/** A source type that a format integration cannot represent with the current Tapik schema model. */
class SchemaDerivationException(
    message: String
) : IllegalArgumentException(message)
