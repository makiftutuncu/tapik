package dev.akif.tapik.gradle

import kotlinx.serialization.Serializable

@Serializable
data class HttpEndpointDescription(
    val name: String,
    val packageName: String,
    val file: String,
    val parameters: TypeDescription,
    val inputHeaders: TypeDescription,
    val inputBody: TypeDescription,
    val outputHeaders: TypeDescription,
    val outputBodies: TypeDescription,
    val imports: List<String>,
    val rawType: String
)
