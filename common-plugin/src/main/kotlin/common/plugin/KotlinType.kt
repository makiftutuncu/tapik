package dev.akif.tapik.common.plugin

/**
 * A Kotlin type retained in compiled API metadata.
 *
 * @property classifier class, type-alias, or type-parameter identity.
 * @property nullable whether the source type is nullable.
 * @property definitelyNonNull whether the source type is definitely non-null.
 * @property abbreviation source type-alias form when this type was expanded by the compiler.
 * @property outerType containing type for an inner-class type.
 * @property flexibleUpperBound upper bound when this is a flexible platform type.
 */
class KotlinType(
    /** Class, type-alias, or type-parameter identity. */
    val classifier: KotlinClassifier,
    arguments: List<KotlinTypeProjection> = emptyList(),
    /** Whether the source type is nullable. */
    val nullable: Boolean = false,
    /** Whether the source type is definitely non-null. */
    val definitelyNonNull: Boolean = false,
    /** Source type-alias form when this type was expanded by the compiler. */
    val abbreviation: KotlinType? = null,
    /** Containing type for an inner-class type. */
    val outerType: KotlinType? = null,
    /** Upper bound when this is a flexible platform type. */
    val flexibleUpperBound: KotlinFlexibleTypeUpperBound? = null
) {
    /** Generic type projections in declaration order. */
    val arguments: List<KotlinTypeProjection> = arguments.snapshotList()

    /** Returns [classifier] for destructuring. */
    operator fun component1(): KotlinClassifier = classifier

    /** Returns [arguments] for destructuring. */
    operator fun component2(): List<KotlinTypeProjection> = arguments

    /** Returns [nullable] for destructuring. */
    operator fun component3(): Boolean = nullable

    /** Returns [definitelyNonNull] for destructuring. */
    operator fun component4(): Boolean = definitelyNonNull

    /** Returns [abbreviation] for destructuring. */
    operator fun component5(): KotlinType? = abbreviation

    /** Returns [outerType] for destructuring. */
    operator fun component6(): KotlinType? = outerType

    /** Returns [flexibleUpperBound] for destructuring. */
    operator fun component7(): KotlinFlexibleTypeUpperBound? = flexibleUpperBound

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        classifier: KotlinClassifier = this.classifier,
        arguments: List<KotlinTypeProjection> = this.arguments,
        nullable: Boolean = this.nullable,
        definitelyNonNull: Boolean = this.definitelyNonNull,
        abbreviation: KotlinType? = this.abbreviation,
        outerType: KotlinType? = this.outerType,
        flexibleUpperBound: KotlinFlexibleTypeUpperBound? = this.flexibleUpperBound
    ): KotlinType = KotlinType(classifier, arguments, nullable, definitelyNonNull, abbreviation, outerType, flexibleUpperBound)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is KotlinType &&
                classifier == other.classifier &&
                arguments == other.arguments &&
                nullable == other.nullable &&
                definitelyNonNull == other.definitelyNonNull &&
                abbreviation == other.abbreviation &&
                outerType == other.outerType &&
                flexibleUpperBound == other.flexibleUpperBound)

    override fun hashCode(): Int {
        var result = classifier.hashCode()
        result = 31 * result + arguments.hashCode()
        result = 31 * result + nullable.hashCode()
        result = 31 * result + definitelyNonNull.hashCode()
        result = 31 * result + abbreviation.hashCode()
        result = 31 * result + outerType.hashCode()
        result = 31 * result + flexibleUpperBound.hashCode()
        return result
    }

    override fun toString(): String =
        "KotlinType(classifier=$classifier, arguments=$arguments, nullable=$nullable, definitelyNonNull=$definitelyNonNull, abbreviation=$abbreviation, outerType=$outerType, flexibleUpperBound=$flexibleUpperBound)"
}

/** A classifier identifying the declaration behind a [KotlinType]. */
sealed interface KotlinClassifier

/**
 * A Kotlin class classifier.
 *
 * @property name dot-separated Kotlin class name.
 */
data class KotlinClassClassifier(
    /** Dot-separated Kotlin class name. */
    val name: String
) : KotlinClassifier {
    init {
        require(name.isNotBlank()) { "Kotlin class name must not be blank" }
    }
}

/**
 * A Kotlin type-alias classifier.
 *
 * @property name dot-separated Kotlin type-alias name.
 */
data class KotlinTypeAliasClassifier(
    /** Dot-separated Kotlin type-alias name. */
    val name: String
) : KotlinClassifier {
    init {
        require(name.isNotBlank()) { "Kotlin type-alias name must not be blank" }
    }
}

/**
 * A Kotlin type-parameter classifier.
 *
 * @property id metadata-local type-parameter ID.
 */
data class KotlinTypeParameterClassifier(
    /** Metadata-local type-parameter ID. */
    val id: Int
) : KotlinClassifier {
    init {
        require(id >= 0) { "Kotlin type-parameter ID must not be negative" }
    }
}

/** A generic argument of a [KotlinType]. */
sealed interface KotlinTypeProjection

/** A star-projected Kotlin type argument. */
data object KotlinStarProjection : KotlinTypeProjection

/**
 * A typed Kotlin generic projection.
 *
 * @property variance use-site variance.
 * @property type projected type.
 */
data class KotlinTypedProjection(
    /** Use-site variance. */
    val variance: KotlinVariance,
    /** Projected type. */
    val type: KotlinType
) : KotlinTypeProjection

/** Use-site variance retained for a typed Kotlin projection. */
enum class KotlinVariance {
    /** Invariant projection. */
    INVARIANT,

    /** Contravariant `in` projection. */
    IN,

    /** Covariant `out` projection. */
    OUT
}

/**
 * The upper bound of a flexible Kotlin platform type.
 *
 * @property type upper-bound type.
 * @property flexibilityId compiler-defined flexibility category.
 */
data class KotlinFlexibleTypeUpperBound(
    /** Upper-bound type. */
    val type: KotlinType,
    /** Compiler-defined flexibility category. */
    val flexibilityId: String?
)
