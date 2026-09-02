package dev.akif.tapik.common.plugin

/**
 * A compiled Kotlin type prepared for source generation.
 *
 * [source] preserves source-facing aliases and nesting. [expandedType] retains the compiler-expanded identity used by
 * generators for semantic decisions that must not depend on source spelling.
 */
data class KotlinSourceType(
    val source: String,
    val expandedType: KotlinType
) {
    /** Whether the expanded type has the class classifier named [name]. */
    fun expandsTo(name: String): Boolean =
        (expandedType.classifier as? KotlinClassClassifier)?.name == name

    /** Returns the nullable source form of this type without losing its expanded identity. */
    fun asNullable(): KotlinSourceType =
        if (expandedType.nullable) {
            this
        } else {
            copy(source = "$source?", expandedType = expandedType.asNullable())
        }
}

/**
 * Converts this compiled type to a lossless source-generation view at [location].
 *
 * @throws IllegalArgumentException when this type contains a shape tapik cannot reproduce in Kotlin source.
 */
fun KotlinType.toKotlinSourceType(location: String): KotlinSourceType {
    validateSourceShape(location)
    return KotlinSourceType(source = renderSource(location), expandedType = this)
}

private fun KotlinType.validateSourceShape(location: String) {
    require(flexibleUpperBound == null) {
        "$location has a flexible Kotlin type, which tapik cannot reproduce in generated source"
    }
    require(!definitelyNonNull) {
        "$location has a definitely-non-null Kotlin type, which tapik cannot reproduce in generated source"
    }
    require(classifier !is KotlinTypeParameterClassifier) {
        val parameter = classifier as KotlinTypeParameterClassifier
        "$location has unresolved Kotlin type parameter ${parameter.id}"
    }
    arguments.forEachIndexed { index, projection ->
        if (projection is KotlinTypedProjection) {
            projection.type.validateSourceShape("$location type argument ${index + 1}")
        }
    }
    abbreviation?.validateSourceShape("$location abbreviation")
    outerType?.validateSourceShape("$location outer type")
}

private fun KotlinType.renderSource(location: String): String {
    abbreviation?.let { return it.renderSource("$location abbreviation") }
    val name = classifier.name()
    val owner = outerType
    val qualifiedName =
        if (owner == null) {
            name
        } else {
            val ownerName = owner.classifier.name()
            val nestedName = name.removePrefix("$ownerName.")
            require(nestedName != name && '.' !in nestedName) {
                "$location inner type '$name' is not contained by '$ownerName'"
            }
            "${owner.renderSource("$location outer type")}.$nestedName"
        }
    val renderedArguments =
        arguments
            .takeIf(List<*>::isNotEmpty)
            ?.joinToString(prefix = "<", postfix = ">") { projection -> projection.renderSource(location) }
            .orEmpty()
    return qualifiedName + renderedArguments + if (nullable) "?" else ""
}

private fun KotlinClassifier.name(): String =
    when (this) {
        is KotlinClassClassifier -> name
        is KotlinTypeAliasClassifier -> name
        is KotlinTypeParameterClassifier -> error("Unresolved Kotlin type parameter $id passed source validation")
    }

private fun KotlinTypeProjection.renderSource(location: String): String =
    when (this) {
        KotlinStarProjection -> "*"
        is KotlinTypedProjection -> {
            val prefix =
                when (variance) {
                    KotlinVariance.INVARIANT -> ""
                    KotlinVariance.IN -> "in "
                    KotlinVariance.OUT -> "out "
                }
            prefix + type.renderSource("$location type argument")
        }
    }

private fun KotlinType.asNullable(): KotlinType =
    copy(
        nullable = true,
        abbreviation = abbreviation?.copy(nullable = true)
    )
