package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.common.plugin.*

/** Generates Spring RestClient Kotlin clients from compiled Tapik APIs. */
object RestClientTarget : GenerationTarget {
    override val id: String = "spring-restclient"

    /**
     * Generates one composable client interface for every API in [request].
     *
     * @param request selected APIs and host-neutral target configuration.
     * @return generated Kotlin source artifacts in API order.
     * @throws IllegalArgumentException when configuration or an API type is invalid.
     * @throws CompiledApiInspectionException when an endpoint's compiled Kotlin type cannot be read.
     */
    override fun generate(request: GenerationRequest): GenerationResult {
        val configuration = RestClientTargetConfiguration.from(request.configuration)
        val usedClientNames = mutableSetOf<String>()
        val artifacts =
            request.apis.map { api ->
                val apiTypeName = api.javaClass.simpleName
                require(apiTypeName.isKotlinIdentifier()) {
                    "Spring RestClient generation requires a valid API type name, but was '$apiTypeName'"
                }
                val clientName = uniqueKotlinName(apiTypeName + configuration.clientSuffix, usedClientNames)
                GeneratedArtifact(
                    relativePath =
                        configuration.packageName.replace('.', '/') + "/" + clientName + ".kt",
                    mediaType = "text/x-kotlin",
                    kind = ArtifactKind.SOURCE,
                    content =
                        RestClientGenerator(
                            packageName = configuration.packageName,
                            clientName = clientName
                        ).generate(CompiledApiReader.read(api))
                )
            }
        return GenerationResult(artifacts)
    }
}

private data class RestClientTargetConfiguration(
    val packageName: String,
    val clientSuffix: String
) {
    companion object {
        fun from(configuration: TargetConfiguration): RestClientTargetConfiguration {
            val unknown = configuration.values.keys - SUPPORTED_CONFIGURATION
            require(unknown.isEmpty()) {
                "Unknown Spring RestClient target configuration: ${unknown.sorted().joinToString()}"
            }
            val packageName = configuration.scalar("packageName") ?: DEFAULT_PACKAGE
            require(packageName.split('.').all(String::isKotlinIdentifier)) {
                "Spring RestClient target 'packageName' must be a valid Kotlin package, but was '$packageName'"
            }
            val clientSuffix = configuration.scalar("clientSuffix") ?: DEFAULT_CLIENT_SUFFIX
            require(clientSuffix.isKotlinIdentifier()) {
                "Spring RestClient target 'clientSuffix' must be a valid Kotlin identifier, but was '$clientSuffix'"
            }
            return RestClientTargetConfiguration(packageName, clientSuffix)
        }
    }
}

private fun TargetConfiguration.scalar(name: String): String? =
    when (val value = this[name]) {
        null -> null
        is ScalarConfigurationValue -> value.value
        else -> throw IllegalArgumentException("Spring RestClient target '$name' must be a scalar value")
    }

private val SUPPORTED_CONFIGURATION: Set<String> = setOf("packageName", "clientSuffix")
private const val DEFAULT_PACKAGE: String = "dev.akif.tapik.generated"
private const val DEFAULT_CLIENT_SUFFIX: String = "Client"
