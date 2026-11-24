package dev.akif.tapik

/** An untyped schema describing values accepted by a [Format].
 *
 * @property name optional reusable schema name.
 */
sealed interface Schema {
    /** Optional reusable schema name. */
    val name: String?

    /**
     * Returns this schema with the reusable name [name].
     *
     * @param name non-blank name for the schema.
     * @return a copy of this schema with the supplied name.
     * @throws IllegalArgumentException when [name] is blank.
     */
    fun named(name: String): Schema
}

/** Scalar schema types needed by tapik's built-in formats. */
enum class SchemaType {
    /** A Boolean value. */
    BOOLEAN,

    /** An integer number. */
    INTEGER,

    /** A number that may include a fractional part. */
    NUMBER,

    /** A string value. */
    STRING
}

/**
 * A scalar [type] with an optional OpenAPI [format] and reusable [name].
 *
 * @property type kind of value described by this schema.
 * @property format optional format hint for the scalar type.
 * @property name optional reusable schema name.
 * @throws IllegalArgumentException when [format] or [name] is present but blank.
 */
data class ScalarSchema(
    /** Kind of value described by this schema. */
    val type: SchemaType,
    /** Optional format hint for the scalar type. */
    val format: String? = null,
    /** Optional reusable schema name. */
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
 * @property items schema for each array element.
 * @property name optional reusable schema name.
 * @throws IllegalArgumentException when [name] is present but blank.
 */
data class ArraySchema(
    /** Schema for each array element. */
    val items: Schema,
    /** Optional reusable schema name. */
    override val name: String? = null
) : Schema {
    init {
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): ArraySchema = copy(name = name)
}

/**
 * A finite set of string [values].
 *
 * @property name optional reusable schema name.
 * @throws IllegalArgumentException when [values] is empty, contains blanks or duplicates, or [name] is blank.
 */
class EnumSchema(
    values: List<String>,
    /** Optional reusable schema name. */
    override val name: String? = null
) : Schema {
    /** Unmodifiable snapshot of allowed string values in declaration order. */
    val values: List<String> = values.snapshotList()

    init {
        require(values.isNotEmpty()) { "Enum schema values must not be empty" }
        require(values.none(String::isBlank)) { "Enum schema values must not be blank" }
        require(values.distinct().size == values.size) { "Enum schema values must be unique" }
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): EnumSchema = copy(name = name)

    /** Returns [values] for destructuring. */
    operator fun component1(): List<String> = values

    /** Returns [name] for destructuring. */
    operator fun component2(): String? = name

    /** Returns a copy with the supplied [values] or [name]. */
    fun copy(
        values: List<String> = this.values,
        name: String? = this.name
    ): EnumSchema = EnumSchema(values, name)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other || other is EnumSchema && values == other.values && name == other.name

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int = 31 * values.hashCode() + name.hashCode()

    /** Returns a string representation of this value. */
    override fun toString(): String = "EnumSchema(values=$values, name=$name)"
}

/**
 * A property [schema] and its containing-object flags.
 *
 * @property schema property value schema.
 * @property required whether the containing object requires this property.
 * @property deprecated whether consumers should avoid using this property.
 */
data class SchemaProperty(
    /** Property value schema. */
    val schema: Schema,
    /** Whether the containing object requires this property. */
    val required: Boolean,
    /** Whether consumers should avoid using this property. */
    val deprecated: Boolean = false
)

/**
 * An object with named [properties].
 *
 * @property name optional reusable schema name.
 * @throws IllegalArgumentException when a property name or [name] is blank.
 */
class ObjectSchema(
    properties: Map<String, SchemaProperty>,
    /** Optional reusable schema name. */
    override val name: String? = null
) : Schema {
    /** Unmodifiable snapshot of object fields in declaration order. */
    val properties: Map<String, SchemaProperty> = properties.snapshotMap()

    init {
        require(properties.keys.none(String::isBlank)) { "Object schema property names must not be blank" }
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): ObjectSchema = copy(name = name)

    /** Returns [properties] for destructuring. */
    operator fun component1(): Map<String, SchemaProperty> = properties

    /** Returns [name] for destructuring. */
    operator fun component2(): String? = name

    /** Returns a copy with the supplied [properties] or [name]. */
    fun copy(
        properties: Map<String, SchemaProperty> = this.properties,
        name: String? = this.name
    ): ObjectSchema = ObjectSchema(properties, name)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other || other is ObjectSchema && properties == other.properties && name == other.name

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int = 31 * properties.hashCode() + name.hashCode()

    /** Returns a string representation of this value. */
    override fun toString(): String = "ObjectSchema(properties=$properties, name=$name)"
}

/**
 * A map whose keys and values use [keys] and [values] schemas.
 *
 * @property keys schema for map keys.
 * @property values schema for map values.
 * @property name optional reusable schema name.
 * @throws IllegalArgumentException when [name] is present but blank.
 */
data class MapSchema(
    /** Schema for map keys. */
    val keys: Schema,
    /** Schema for map values. */
    val values: Schema,
    /** Optional reusable schema name. */
    override val name: String? = null
) : Schema {
    init {
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): MapSchema = copy(name = name)
}

/**
 * A nullable form of [schema].
 *
 * @property schema schema for non-null values.
 * @property name optional reusable schema name.
 * @throws IllegalArgumentException when [name] is present but blank.
 */
data class NullableSchema(
    /** Schema for non-null values. */
    val schema: Schema,
    /** Optional reusable schema name. */
    override val name: String? = null
) : Schema {
    init {
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): NullableSchema = copy(name = name)
}

/**
 * A reference to a reusable schema identified by [reference].
 *
 * @property reference schema identifier or URI.
 * @property name optional reusable schema name.
 * @throws IllegalArgumentException when [reference] or [name] is blank.
 */
data class ReferenceSchema(
    /** Schema identifier or URI. */
    val reference: String,
    /** Optional reusable schema name. */
    override val name: String? = null
) : Schema {
    init {
        require(reference.isNotBlank()) { "Schema reference must not be blank" }
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): ReferenceSchema = copy(name = name)
}
