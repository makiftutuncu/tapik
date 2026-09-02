package dev.akif.tapik.target.openapi

/**
 * A valid tapik contract that cannot be represented by the current OpenAPI target.
 *
 * @param message explanation of the unsupported or inconsistent construct.
 */
class OpenApiGenerationException(
    message: String
) : IllegalArgumentException(message)
