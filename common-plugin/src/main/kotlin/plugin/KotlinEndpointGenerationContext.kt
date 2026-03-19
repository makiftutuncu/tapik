package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.OutputMatchMetadata

/**
 * Shared naming and response-shape information exposed to Kotlin endpoint generators.
 *
 * @property contractTypeName generated Kotlin type name for the endpoint contract that owns shared helpers.
 * @property endpointReference short reference to the original Tapik endpoint definition.
 * @property methodName generated method name used by Kotlin integrations.
 * @property summaryLines summary documentation lines rendered from the endpoint description.
 * @property detailLines detailed documentation lines rendered from the endpoint details.
 * @property response generated public response shape shared by Kotlin integrations.
 */
data class KotlinEndpointGenerationContext(
    val contractTypeName: String,
    val endpointReference: String,
    val methodName: String,
    val summaryLines: List<String>,
    val detailLines: List<String>,
    val response: KotlinEndpointResponseModel
)

/**
 * Describes the generated public response type for a Kotlin endpoint contract.
 *
 * @property typeName public Kotlin type name exposed by generated APIs.
 * @property variants concrete response variants derived from endpoint outputs.
 */
data class KotlinEndpointResponseModel(
    val typeName: String,
    val variants: List<Variant>
) {
    /**
     * Describes one generated response variant.
     *
     * @property typeName Kotlin type name for the variant.
     * @property match status matcher metadata represented by the variant.
     * @property statusFieldName generated property name that stores a runtime status for non-exact
     * output variants, or `null` when the variant has an exact status.
     * @property fields constructor fields exposed by the variant.
     */
    data class Variant(
        val typeName: String,
        val match: OutputMatchMetadata,
        val statusFieldName: String?,
        val fields: List<Field>
    )

    /**
     * Describes one constructor/property field on a generated response variant.
     *
     * @property name generated Kotlin property name.
     * @property type generated Kotlin property type.
     */
    data class Field(
        val name: String,
        val type: String
    )
}
