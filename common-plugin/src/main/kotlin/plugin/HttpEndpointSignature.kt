package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.TypeMetadata

internal data class HttpEndpointSignature(
    val name: String,
    val packageName: String,
    val file: String,
    val path: TypeMetadata,
    val parameters: TypeMetadata,
    val input: InputSignature,
    val outputs: TypeMetadata,
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

internal data class InputSignature(
    val type: TypeMetadata,
    val headers: TypeMetadata,
    val body: TypeMetadata
)
