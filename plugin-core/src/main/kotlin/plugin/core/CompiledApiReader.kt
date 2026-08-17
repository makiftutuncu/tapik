package dev.akif.tapik.plugin.core

import dev.akif.tapik.Api
import dev.akif.tapik.id
import kotlin.metadata.KmClassifier
import kotlin.metadata.KmType
import kotlin.metadata.KmVariance
import kotlin.metadata.isDefinitelyNonNull
import kotlin.metadata.isNullable
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
        val propertyTypes = propertyTypes(api)
        val prefix = "${api.id}."
        val endpoints =
            api.endpoints.map { endpoint ->
                if (!endpoint.id.startsWith(prefix)) {
                    throw CompiledApiInspectionException(
                        "Endpoint '${endpoint.id}' does not belong to API '${api.id}'"
                    )
                }
                val propertyName = endpoint.id.removePrefix(prefix)
                val type =
                    propertyTypes[propertyName]
                        ?: throw CompiledApiInspectionException(
                            "Cannot find compiled property '$propertyName' for endpoint '${endpoint.id}'"
                        )
                if (type.classifier != ENDPOINT_CLASSIFIER) {
                    throw CompiledApiInspectionException(
                        "Compiled property '$propertyName' for endpoint '${endpoint.id}' has type '${type.classifier}'"
                    )
                }
                CompiledEndpoint(endpoint, type)
            }
        return CompiledApi(api, endpoints)
    }

    private fun propertyTypes(api: Api): Map<String, KotlinType> =
        buildMap {
            var apiType: Class<*>? = api.javaClass
            while (apiType != null && apiType != Api::class.java) {
                metadataProperties(apiType).forEach { (name, type) -> putIfAbsent(name, type) }
                apiType = apiType.superclass
            }
        }

    private fun metadataProperties(apiType: Class<*>): Map<String, KotlinType> {
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
            property.name to property.returnType.toKotlinType()
        }
    }
}

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
