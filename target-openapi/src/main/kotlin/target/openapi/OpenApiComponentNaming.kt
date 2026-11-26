package dev.akif.tapik.target.openapi

/**
 * Maps format-provided schema names to OpenAPI component names.
 *
 * Custom policies should return a stable name for each input. Names must contain only ASCII letters, digits, `.`, `_`,
 * or `-`. Different schema names may map to the same component only when their schemas are identical; otherwise
 * generation fails with [OpenApiGenerationException].
 *
 * @see Simple
 * @see Qualified
 */
fun interface OpenApiComponentNaming {
    /**
     * Returns the component name for [schemaName].
     *
     * @param schemaName name supplied by a tapik schema.
     * @return name to use under `components.schemas`.
     */
    fun name(schemaName: String): String

    /** Predefined OpenAPI component naming policies. */
    companion object {
        /** Keeps only the final dot-separated part of each schema name. */
        val Simple: OpenApiComponentNaming = OpenApiComponentNaming { it.substringAfterLast('.') }

        /** Keeps each format-provided schema name unchanged. */
        val Qualified: OpenApiComponentNaming = OpenApiComponentNaming { it }
    }
}
