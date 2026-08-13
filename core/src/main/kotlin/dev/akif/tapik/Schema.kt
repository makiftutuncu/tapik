package dev.akif.tapik

/** An untyped schema describing values accepted by a [Format]. */
sealed interface Schema {
    /** Optional reusable schema name. */
    val name: String?

    /** Returns this schema with [name]. */
    fun named(name: String): Schema
}

/** Scalar schema types needed by Tapik's built-in formats. */
enum class SchemaType {
    BOOLEAN,
    INTEGER,
    NUMBER,
    STRING
}

/**
 * A scalar [type] with an optional OpenAPI [format] and reusable [name].
 *
 * @throws IllegalArgumentException when [format] or [name] is present but blank.
 */
data class ScalarSchema(
    val type: SchemaType,
    val format: String? = null,
    override val name: String? = null
) : Schema {
    init {
        require(format == null || format.isNotBlank()) { "Schema format must not be blank" }
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): ScalarSchema = copy(name = name)
}

/**
 * An array whose elements are described by [items], with an optional reusable [name].
 *
 * @throws IllegalArgumentException when [name] is present but blank.
 */
data class ArraySchema(
    val items: Schema,
    override val name: String? = null
) : Schema {
    init {
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): ArraySchema = copy(name = name)
}
