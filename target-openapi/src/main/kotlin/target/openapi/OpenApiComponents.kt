package dev.akif.tapik.target.openapi

/**
 * Reusable OpenAPI components.
 *
 * @property schemas schemas keyed by component name in first-use order.
 */
class OpenApiComponents(
    schemas: Map<String, OpenApiSchema> = emptyMap()
) {
    val schemas: Map<String, OpenApiSchema> = schemas.snapshotMap()

    /** Returns [schemas] for destructuring. */
    operator fun component1(): Map<String, OpenApiSchema> = schemas

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        schemas: Map<String, OpenApiSchema> = this.schemas
    ): OpenApiComponents = OpenApiComponents(schemas)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiComponents &&
                schemas == other.schemas)

    override fun hashCode(): Int = schemas.hashCode()

    override fun toString(): String =
        "OpenApiComponents(schemas=$schemas)"
}
