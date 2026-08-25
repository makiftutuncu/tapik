package dev.akif.tapik.plugin.core

/**
 * Returns the typed argument at [index].
 *
 * @param index zero-based generic argument index.
 * @param location contract location used in diagnostics.
 * @throws IllegalArgumentException when the argument is absent or star-projected.
 */
fun KotlinType.argument(
    index: Int,
    location: String
): KotlinType {
    val projection =
        arguments.getOrNull(index)
            ?: throw IllegalArgumentException("$location is missing type argument $index")
    return (projection as? KotlinTypedProjection)?.type
        ?: throw IllegalArgumentException("$location type argument $index must not be star-projected")
}

/**
 * Returns the value elements carried by a Tapik tuple type.
 *
 * @param location contract location used in diagnostics.
 * @throws IllegalArgumentException when this is not a Tapik tuple or an element is star-projected.
 */
fun KotlinType.tupleElements(location: String): List<KotlinType> {
    val name = (classifier as? KotlinClassClassifier)?.name
    if (name == "dev.akif.tapik.Tuple0") return emptyList()
    require(name != null && TUPLE_CLASSIFIER.matches(name)) {
        "$location must be a Tapik tuple, but was '$classifier'"
    }
    return arguments.drop(1).mapIndexed { index, projection ->
        (projection as? KotlinTypedProjection)?.type
            ?: throw IllegalArgumentException("$location element ${index + 1} must not be star-projected")
    }
}

private val TUPLE_CLASSIFIER: Regex = Regex("dev\\.akif\\.tapik\\.Tuple[1-8]")
