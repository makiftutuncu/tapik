package dev.akif.tapik.target.openapi

/**
 * OpenAPI hint for selecting one alternative of a composed schema.
 *
 * @property propertyName wire property containing the discriminating value.
 * @param mapping explicit values keyed to schema reference URIs in declaration order.
 * @property defaultMapping schema reference URI used when no explicit or implicit mapping applies.
 */
class OpenApiDiscriminator(
    /** Wire property containing the discriminating value. */
    val propertyName: String,
    mapping: Map<String, String> = emptyMap(),
    /** Schema reference URI used when no explicit or implicit mapping applies. */
    val defaultMapping: String? = null
) {
    /** Unmodifiable snapshot mapping explicit discriminating values to schema reference URIs in declaration order. */
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

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiDiscriminator &&
                propertyName == other.propertyName &&
                mapping == other.mapping &&
                defaultMapping == other.defaultMapping)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int {
        var result = propertyName.hashCode()
        result = 31 * result + mapping.hashCode()
        result = 31 * result + defaultMapping.hashCode()
        return result
    }

    /** Returns a string representation of this value. */
    override fun toString(): String =
        "OpenApiDiscriminator(propertyName=$propertyName, mapping=$mapping, defaultMapping=$defaultMapping)"
}
