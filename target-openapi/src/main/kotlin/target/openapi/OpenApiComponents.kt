package dev.akif.tapik.target.openapi

/**
 * Reusable OpenAPI components.
 *
 * @param schemas schemas keyed by component name in first-use order.
 */
class OpenApiComponents(
    schemas: Map<String, OpenApiSchema> = emptyMap()
) {
    /** Unmodifiable snapshot of schemas keyed by component name in first-use order. */
    val schemas: Map<String, OpenApiSchema> = schemas.snapshotMap()

    /** Returns [schemas] for destructuring. */
    operator fun component1(): Map<String, OpenApiSchema> = schemas

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        schemas: Map<String, OpenApiSchema> = this.schemas
    ): OpenApiComponents = OpenApiComponents(schemas)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiComponents &&
                schemas == other.schemas)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int = schemas.hashCode()

    /** Returns a string representation of this value. */
    override fun toString(): String =
        "OpenApiComponents(schemas=$schemas)"
}
