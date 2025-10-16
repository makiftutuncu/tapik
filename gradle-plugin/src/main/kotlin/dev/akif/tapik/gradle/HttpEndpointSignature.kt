package dev.akif.tapik.gradle

import dev.akif.tapik.gradle.metadata.TypeMetadata

data class HttpEndpointSignature(
    val name: String,
    val packageName: String,
    val file: String,
    val parameters: TypeMetadata,
    val inputHeaders: TypeMetadata,
    val inputBody: TypeMetadata,
    val outputHeaders: TypeMetadata,
    val outputBodies: TypeMetadata,
    val imports: List<String>,
    val rawType: String,
    val ownerInternalName: String,
    val methodName: String
) {
    val uniqueKey: String =
        buildString {
            append(packageName)
            append('/')
            append(file)
            append('#')
            append(name)
        }
}
