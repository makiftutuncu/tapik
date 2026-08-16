package dev.akif.tapik.openapi

/**
 * Maps format-provided schema names to OpenAPI component names.
 *
 * @see Simple
 * @see Qualified
 */
fun interface OpenApiComponentNaming {
    /**
     * Returns the component name for [schemaName].
     *
     * @param schemaName name supplied by a Tapik schema.
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
