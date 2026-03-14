package dev.akif.tapik.plugin

/**
 * Generator-specific configuration shared across Tapik plugin frontends.
 *
 * @property clientSuffix suffix appended to aggregate client interface names and nested client contracts.
 * @property serverSuffix suffix appended to aggregate server interface names and nested server contracts.
 */
data class GeneratorConfiguration(
    val clientSuffix: String = "Client",
    val serverSuffix: String = "Server"
)
