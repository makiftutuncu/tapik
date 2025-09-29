package dev.akif.tapik.gradle

import kotlinx.serialization.Serializable

@Serializable
data class HttpEndpointsReport(
    val outputPackage: String,
    val endpointPackages: List<String>,
    val endpoints: List<HttpEndpointDescription>
)
