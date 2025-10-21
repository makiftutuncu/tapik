package dev.akif.tapik.gradle

import dev.akif.tapik.gradle.metadata.TypeMetadata

/**
 * Captures the essential metadata derived from inspecting a Tapik HTTP endpoint declaration.
 *
 * @property name public name exposed for generated clients.
 * @property packageName package containing the endpoint definition.
 * @property file Kotlin file (without extension) hosting the endpoint declaration.
 * @property uriWithParameters type describing the URI template and captured parameters.
 * @property inputHeaders tuple type describing the required input headers.
 * @property inputBody body type consumed by the endpoint.
 * @property outputHeaders tuple type describing the output headers returned by the endpoint.
 * @property outputBodies tuple type describing candidate response bodies.
 * @property imports fully-qualified types that must be imported when generating clients.
 * @property rawType string representation of the full endpoint type.
 * @property ownerInternalName internal JVM name of the class or file that hosts the endpoint.
 * @property methodName method or property accessor that materialises the endpoint.
 */
data class HttpEndpointSignature(
    val name: String,
    val packageName: String,
    val file: String,
    val uriWithParameters: TypeMetadata,
    val inputHeaders: TypeMetadata,
    val inputBody: TypeMetadata,
    val outputHeaders: TypeMetadata,
    val outputBodies: TypeMetadata,
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
