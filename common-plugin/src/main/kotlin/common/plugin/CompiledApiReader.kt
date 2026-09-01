package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api
import dev.akif.tapik.Endpoint
import dev.akif.tapik.Ready
import dev.akif.tapik.id
import kotlin.metadata.KmClassifier
import kotlin.metadata.KmType
import kotlin.metadata.KmVariance
import kotlin.metadata.Visibility
import kotlin.metadata.isDefinitelyNonNull
import kotlin.metadata.isNullable
import kotlin.metadata.visibility
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.KmTypeProjection as KmTypeProjectionMetadata

/** Reads exact endpoint property types from the Kotlin metadata of a compiled [Api]. */
object CompiledApiReader {
    /**
     * Pairs [api] and its runtime endpoints with their compiled property types.
     *
     * @param api compiled runtime API value.
     * @return runtime values and their exact Kotlin types in endpoint declaration order.
     * @throws CompiledApiInspectionException when metadata is unavailable, unreadable, or inconsistent with [api].
     */
    fun read(api: Api): CompiledApi {
        val locations = locations(api, emptyList())
        val endpoints =
            api.endpoints.map { endpoint ->
                val location =
                    locations.singleOrNull { candidate -> candidate.endpoint === endpoint }
                        ?: throw CompiledApiInspectionException(
                            "Cannot locate endpoint '${endpoint.id}' from API '${api.id}'"
                        )
                CompiledEndpoint(endpoint, location.type, location.propertyPath)
            }
        return CompiledApi(api, endpoints)
    }

    private fun locations(
        api: Api,
        parentPath: List<String>
    ): List<EndpointLocation> {
        val properties = properties(api)
        val nested =
            api.includedApis.flatMap { inclusion ->
                val property = requiredProperty(api, inclusion.propertyName, properties, "Inclusion")
                val actualType = (property.type.classifier as? KotlinClassClassifier)?.name
                val expectedType = inclusion.api.javaClass.canonicalName
                if (actualType != expectedType) {
                    throw CompiledApiInspectionException(
                        "Inclusion property '${api.id}.${inclusion.propertyName}' must retain concrete type " +
                            "'$expectedType', but has type '${property.type.classifier}'"
                    )
                }
                locations(inclusion.api, parentPath + inclusion.propertyName)
            }
        val nestedEndpoints = nested.map(EndpointLocation::endpoint)
        val prefix = "${api.id}."
        val direct =
            api.endpoints
                .filter { endpoint -> nestedEndpoints.none { nestedEndpoint -> nestedEndpoint === endpoint } }
                .map { endpoint ->
                    if (!endpoint.id.startsWith(prefix)) {
                        throw CompiledApiInspectionException(
                            "Endpoint '${endpoint.id}' does not belong to API '${api.id}'"
                        )
                    }
                    val propertyName = endpoint.id.removePrefix(prefix)
                    val property = requiredProperty(api, propertyName, properties, "Endpoint")
                    if (property.type.classifier != ENDPOINT_CLASSIFIER) {
                        throw CompiledApiInspectionException(
                            "Compiled property '$propertyName' for endpoint '${endpoint.id}' has type " +
                                "'${property.type.classifier}'"
                        )
                    }
                    EndpointLocation(endpoint, property.type, parentPath + propertyName)
                }
        return direct + nested
    }

    private fun requiredProperty(
        api: Api,
        propertyName: String,
        properties: Map<String, CompiledProperty>,
        kind: String
    ): CompiledProperty {
        val property =
            properties[propertyName]
                ?: throw CompiledApiInspectionException(
                    "Cannot find compiled property '$propertyName' in API '${api.id}'"
                )
        if (property.visibility != Visibility.PUBLIC) {
            throw CompiledApiInspectionException(
                "$kind property '${api.id}.$propertyName' must be public for generated targets"
            )
        }
        return property
    }

    private fun properties(api: Api): Map<String, CompiledProperty> =
        buildMap {
            var apiType: Class<*>? = api.javaClass
            while (apiType != null && apiType != Api::class.java) {
                metadataProperties(apiType).forEach { (name, type) -> putIfAbsent(name, type) }
                apiType = apiType.superclass
            }
        }

    private fun metadataProperties(apiType: Class<*>): Map<String, CompiledProperty> {
        val metadata =
            apiType.getAnnotation(Metadata::class.java)
                ?: throw CompiledApiInspectionException(
                    "API type '${apiType.name}' does not contain Kotlin metadata"
                )
        val classMetadata =
            try {
                KotlinClassMetadata.readStrict(metadata) as? KotlinClassMetadata.Class
            } catch (cause: Exception) {
                throw CompiledApiInspectionException(
                    "Cannot read Kotlin metadata for API type '${apiType.name}'",
                    cause
                )
            } ?: throw CompiledApiInspectionException(
                "Kotlin metadata for API type '${apiType.name}' does not describe a class"
        )
        return classMetadata.kmClass.properties.associate { property ->
            property.name to CompiledProperty(property.returnType.toKotlinType(), property.visibility)
        }
    }
}

private data class EndpointLocation(
    val endpoint: Endpoint<*, *, *, *, *, Ready>,
    val type: KotlinType,
    val propertyPath: List<String>
)

private data class CompiledProperty(
    val type: KotlinType,
    val visibility: Visibility
)

private fun KmType.toKotlinType(): KotlinType =
    KotlinType(
        classifier = classifier.toKotlinClassifier(),
        arguments = arguments.map(KmTypeProjectionMetadata::toKotlinTypeProjection),
        nullable = isNullable,
        definitelyNonNull = isDefinitelyNonNull,
        abbreviation = abbreviatedType?.toKotlinType(),
        outerType = outerType?.toKotlinType(),
        flexibleUpperBound =
            flexibleTypeUpperBound?.let { upperBound ->
                KotlinFlexibleTypeUpperBound(
                    type = upperBound.type.toKotlinType(),
                    flexibilityId = upperBound.typeFlexibilityId
                )
            }
    )

private fun KmClassifier.toKotlinClassifier(): KotlinClassifier =
    when (this) {
        is KmClassifier.Class -> KotlinClassClassifier(name.kotlinName())
        is KmClassifier.TypeAlias -> KotlinTypeAliasClassifier(name.kotlinName())
        is KmClassifier.TypeParameter -> KotlinTypeParameterClassifier(id)
    }

private fun KmTypeProjectionMetadata.toKotlinTypeProjection(): KotlinTypeProjection {
    val projectedType = type ?: return KotlinStarProjection
    val projectedVariance = variance ?: return KotlinStarProjection
    return KotlinTypedProjection(projectedVariance.toKotlinVariance(), projectedType.toKotlinType())
}

private fun KmVariance.toKotlinVariance(): KotlinVariance =
    when (this) {
        KmVariance.INVARIANT -> KotlinVariance.INVARIANT
        KmVariance.IN -> KotlinVariance.IN
        KmVariance.OUT -> KotlinVariance.OUT
    }

private fun String.kotlinName(): String = replace('/', '.')

private val ENDPOINT_CLASSIFIER: KotlinClassClassifier = KotlinClassClassifier("dev.akif.tapik.Endpoint")
