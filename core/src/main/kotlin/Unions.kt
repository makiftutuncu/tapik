package dev.akif.tapik

/**
 * A wire-property hint for selecting one alternative of a [UnionSchema].
 *
 * [mapping] associates wire values with neutral schema references. [defaultMapping] identifies the alternative used
 * when the property is absent or does not have an explicit mapping.
 *
 * @throws IllegalArgumentException when [propertyName] or a discriminating value in [mapping] is blank.
 */
class SchemaDiscriminator(
    val propertyName: String,
    mapping: Map<String, ReferenceSchema> = emptyMap(),
    val defaultMapping: ReferenceSchema? = null
) {
    val mapping: Map<String, ReferenceSchema> = mapping.snapshotMap()

    init {
        require(propertyName.isNotBlank()) { "Schema discriminator property name must not be blank" }
        require(mapping.keys.none(String::isBlank)) { "Schema discriminator mapping values must not be blank" }
    }

    operator fun component1(): String = propertyName

    operator fun component2(): Map<String, ReferenceSchema> = mapping

    operator fun component3(): ReferenceSchema? = defaultMapping

    fun copy(
        propertyName: String = this.propertyName,
        mapping: Map<String, ReferenceSchema> = this.mapping,
        defaultMapping: ReferenceSchema? = this.defaultMapping
    ): SchemaDiscriminator = SchemaDiscriminator(propertyName, mapping, defaultMapping)

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is SchemaDiscriminator &&
            propertyName == other.propertyName &&
            mapping == other.mapping &&
            defaultMapping == other.defaultMapping

    override fun hashCode(): Int =
        31 * (31 * propertyName.hashCode() + mapping.hashCode()) + defaultMapping.hashCode()

    override fun toString(): String =
        "SchemaDiscriminator(propertyName=$propertyName, mapping=$mapping, defaultMapping=$defaultMapping)"
}

/**
 * A schema matching exactly one of [alternatives], optionally aided by [discriminator].
 *
 * @throws IllegalArgumentException when fewer than two distinct alternatives are supplied or [name] is blank.
 */
class UnionSchema(
    alternatives: List<Schema>,
    val discriminator: SchemaDiscriminator? = null,
    override val name: String? = null
) : Schema {
    val alternatives: List<Schema> = alternatives.snapshotList()

    init {
        require(alternatives.size >= 2) { "Union schema must contain at least two alternatives" }
        require(alternatives.distinct().size == alternatives.size) {
            "Union schema alternatives must be unique"
        }
        require(name == null || name.isNotBlank()) { "Schema name must not be blank" }
    }

    override fun named(name: String): UnionSchema = copy(name = name)

    operator fun component1(): List<Schema> = alternatives

    operator fun component2(): SchemaDiscriminator? = discriminator

    operator fun component3(): String? = name

    fun copy(
        alternatives: List<Schema> = this.alternatives,
        discriminator: SchemaDiscriminator? = this.discriminator,
        name: String? = this.name
    ): UnionSchema = UnionSchema(alternatives, discriminator, name)

    override fun equals(other: Any?): Boolean =
        this === other ||
            other is UnionSchema &&
            alternatives == other.alternatives &&
            discriminator == other.discriminator &&
            name == other.name

    override fun hashCode(): Int =
        31 * (31 * alternatives.hashCode() + discriminator.hashCode()) + name.hashCode()

    override fun toString(): String =
        "UnionSchema(alternatives=$alternatives, discriminator=$discriminator, name=$name)"
}
