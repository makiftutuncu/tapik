package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.TypeMetadata

/**
 * Captures the essential metadata derived from inspecting a Tapik HTTP endpoint declaration.
 *
 * @property name public name exposed for generated clients.
 * @property packageName package containing the endpoint definition.
 * @property file Kotlin file (without extension) hosting the endpoint declaration.
 * @property path type describing the ordered path segments.
 * @property parameters tuple type describing the captured path and query parameters.
 * @property input input type describing required headers and body.
 * @property outputs tuple type describing candidate response branches; individual entries are `Output`.
 * @property imports fully-qualified types that must be imported when generating clients.
 * @property rawType string representation of the full endpoint type.
 * @property ownerInternalName internal JVM name of the class or file that hosts the endpoint.
 * @property methodName method or property accessor that materialises the endpoint.
 */
data class HttpEndpointSignature(
    val name: String,
    val packageName: String,
    val file: String,
    val path: TypeMetadata,
    val parameters: TypeMetadata,
    val input: InputSignature,
    val outputs: TypeMetadata,
    val imports: List<String>,
    val rawType: String,
    val ownerInternalName: String,
    val methodName: String
) {
    /**
     * Composite key used to deduplicate endpoint declarations across analysis strategies.
     */
    val uniqueKey: String =
        buildString {
            append(packageName)
            append('/')
            append(file)
            append('#')
            append(name)
        }
}

/**
 * Captures the input type information of an HTTP endpoint.
 *
 * @property type concrete `Input` type with headers and body captured.
 * @property headers tuple type describing required headers.
 * @property body body type consumed by the endpoint.
 */
data class InputSignature(
    val type: TypeMetadata,
    val headers: TypeMetadata,
    val body: TypeMetadata
)
