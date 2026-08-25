package dev.akif.tapik.common.plugin

/**
 * A Kotlin type retained in compiled contract metadata.
 *
 * @property classifier class, type-alias, or type-parameter identity.
 * @property arguments generic type projections in declaration order.
 * @property nullable whether the source type is nullable.
 * @property definitelyNonNull whether the source type is definitely non-null.
 * @property abbreviation source type-alias form when this type was expanded by the compiler.
 * @property outerType containing type for an inner-class type.
 * @property flexibleUpperBound upper bound when this is a flexible platform type.
 */
data class KotlinType(
    val classifier: KotlinClassifier,
    val arguments: List<KotlinTypeProjection> = emptyList(),
    val nullable: Boolean = false,
    val definitelyNonNull: Boolean = false,
    val abbreviation: KotlinType? = null,
    val outerType: KotlinType? = null,
    val flexibleUpperBound: KotlinFlexibleTypeUpperBound? = null
)

/** A classifier identifying the declaration behind a [KotlinType]. */
sealed interface KotlinClassifier

/**
 * A Kotlin class classifier.
 *
 * @property name dot-separated Kotlin class name.
 */
data class KotlinClassClassifier(
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
    val variance: KotlinVariance,
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
    val type: KotlinType,
    val flexibilityId: String?
)
