package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.common.plugin.*

/** Generates Spring WebMVC Kotlin server interfaces from compiled Tapik APIs. */
object WebMvcTarget : GenerationTarget {
    override val id: String = "spring-webmvc"

    override fun generate(request: GenerationRequest): GenerationResult {
        val configuration = WebMvcTargetConfiguration.from(request.configuration)
        val usedTypeNames = mutableSetOf<String>()
        return GenerationResult(
            request.apis.flatMap { api ->
                val apiTypeName = api.javaClass.simpleName
                require(apiTypeName.isKotlinIdentifier()) {
                    "Spring WebMVC generation requires a valid API type name, but was '$apiTypeName'"
                }
                val serverName = uniqueKotlinName(apiTypeName + configuration.serverSuffix, usedTypeNames)
                val controllerName = uniqueKotlinName(apiTypeName + configuration.controllerSuffix, usedTypeNames)
                val controllerType = "${configuration.packageName}.$controllerName"
                listOf(
                    GeneratedArtifact(
                        relativePath = configuration.packageName.replace('.', '/') + "/$serverName.kt",
                        mediaType = "text/x-kotlin",
                        kind = ArtifactKind.SOURCE,
                        content =
                            WebMvcGenerator(
                                packageName = configuration.packageName,
                                serverName = serverName,
                                controllerName = controllerName
                            ).generate(CompiledApiReader.read(api))
                    ),
                    GeneratedArtifact(
                        relativePath = "META-INF/tapik/spring/webmvc/$controllerType.imports",
                        mediaType = "text/plain",
                        kind = ArtifactKind.RESOURCE,
                        content = "$controllerType\n"
                    )
                )
            }
        )
    }
}

private data class WebMvcTargetConfiguration(
    val packageName: String,
    val serverSuffix: String,
    val controllerSuffix: String
) {
    companion object {
        fun from(configuration: TargetConfiguration): WebMvcTargetConfiguration {
            val unknown = configuration.values.keys - SUPPORTED_CONFIGURATION
            require(unknown.isEmpty()) {
                "Unknown Spring WebMVC target configuration: ${unknown.sorted().joinToString()}"
            }
            val packageName = configuration.scalar("packageName") ?: DEFAULT_PACKAGE
            require(packageName.split('.').all(String::isKotlinIdentifier)) {
                "Spring WebMVC target 'packageName' must be a valid Kotlin package, but was '$packageName'"
            }
            val serverSuffix = configuration.scalar("serverSuffix") ?: DEFAULT_SERVER_SUFFIX
            require(serverSuffix.isKotlinIdentifier()) {
                "Spring WebMVC target 'serverSuffix' must be a valid Kotlin identifier, but was '$serverSuffix'"
            }
            val controllerSuffix = configuration.scalar("controllerSuffix") ?: DEFAULT_CONTROLLER_SUFFIX
            require(controllerSuffix.isKotlinIdentifier()) {
                "Spring WebMVC target 'controllerSuffix' must be a valid Kotlin identifier, but was '$controllerSuffix'"
            }
            return WebMvcTargetConfiguration(packageName, serverSuffix, controllerSuffix)
        }
    }
}

private fun TargetConfiguration.scalar(name: String): String? =
    when (val value = this[name]) {
        null -> null
        is ScalarConfigurationValue -> value.value
        else -> throw IllegalArgumentException("Spring WebMVC target '$name' must be a scalar value")
    }

private val SUPPORTED_CONFIGURATION: Set<String> = setOf("packageName", "serverSuffix", "controllerSuffix")
private const val DEFAULT_PACKAGE: String = "dev.akif.tapik.generated"
private const val DEFAULT_SERVER_SUFFIX: String = "Server"
private const val DEFAULT_CONTROLLER_SUFFIX: String = "GeneratedController"
