package dev.akif.tapik.target.openapi

/**
 * OpenAPI hint for selecting one alternative of a composed schema.
 *
 * @property propertyName wire property containing the discriminating value.
 * @property mapping explicit discriminating values to schema reference URIs in declaration order.
 * @property defaultMapping schema reference URI used when no explicit or implicit mapping applies.
 */
class OpenApiDiscriminator(
    val propertyName: String,
    mapping: Map<String, String> = emptyMap(),
    val defaultMapping: String? = null
) {
    val mapping: Map<String, String> = mapping.snapshotMap()

    /** Returns [propertyName] for destructuring. */
    operator fun component1(): String = propertyName

    /** Returns [mapping] for destructuring. */
    operator fun component2(): Map<String, String> = mapping

    /** Returns [defaultMapping] for destructuring. */
    operator fun component3(): String? = defaultMapping

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        propertyName: String = this.propertyName,
        mapping: Map<String, String> = this.mapping,
        defaultMapping: String? = this.defaultMapping
    ): OpenApiDiscriminator = OpenApiDiscriminator(propertyName, mapping, defaultMapping)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiDiscriminator &&
                propertyName == other.propertyName &&
                mapping == other.mapping &&
                defaultMapping == other.defaultMapping)

    override fun hashCode(): Int {
        var result = propertyName.hashCode()
        result = 31 * result + mapping.hashCode()
        result = 31 * result + defaultMapping.hashCode()
        return result
    }

    override fun toString(): String =
        "OpenApiDiscriminator(propertyName=$propertyName, mapping=$mapping, defaultMapping=$defaultMapping)"
}
