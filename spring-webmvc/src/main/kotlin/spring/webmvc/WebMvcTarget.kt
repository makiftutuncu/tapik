package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.plugin.core.*

/** Generates Spring WebMVC Kotlin server interfaces from compiled Tapik APIs. */
object WebMvcTarget : GenerationTarget {
    override val id: String = "spring-webmvc"

    override fun generate(request: GenerationRequest): GenerationResult {
        val configuration = WebMvcTargetConfiguration.from(request.configuration)
        return GenerationResult(
            request.apis.map { api ->
                val apiTypeName = api.javaClass.simpleName
                require(apiTypeName.isWebMvcKotlinIdentifier()) {
                    "Spring WebMVC generation requires a valid API type name, but was '$apiTypeName'"
                }
                val serverName = apiTypeName + configuration.serverSuffix
                GeneratedArtifact(
                    relativePath = configuration.packageName.replace('.', '/') + "/$serverName.kt",
                    mediaType = "text/x-kotlin",
                    kind = ArtifactKind.SOURCE,
                    content =
                        WebMvcGenerator(
                            packageName = configuration.packageName,
                            serverName = serverName
                        ).generate(CompiledApiReader.read(api))
                )
            }
        )
    }
}

private data class WebMvcTargetConfiguration(
    val packageName: String,
    val serverSuffix: String
) {
    companion object {
        fun from(configuration: TargetConfiguration): WebMvcTargetConfiguration {
            val unknown = configuration.values.keys - SUPPORTED_CONFIGURATION
            require(unknown.isEmpty()) {
                "Unknown Spring WebMVC target configuration: ${unknown.sorted().joinToString()}"
            }
            val packageName = configuration.scalar("packageName") ?: DEFAULT_PACKAGE
            require(packageName.split('.').all(String::isWebMvcKotlinIdentifier)) {
                "Spring WebMVC target 'packageName' must be a valid Kotlin package, but was '$packageName'"
            }
            val serverSuffix = configuration.scalar("serverSuffix") ?: DEFAULT_SERVER_SUFFIX
            require(serverSuffix.isWebMvcKotlinIdentifier()) {
                "Spring WebMVC target 'serverSuffix' must be a valid Kotlin identifier, but was '$serverSuffix'"
            }
            return WebMvcTargetConfiguration(packageName, serverSuffix)
        }
    }
}

private fun TargetConfiguration.scalar(name: String): String? =
    when (val value = this[name]) {
        null -> null
        is ScalarConfigurationValue -> value.value
        else -> throw IllegalArgumentException("Spring WebMVC target '$name' must be a scalar value")
    }

private fun String.isWebMvcKotlinIdentifier(): Boolean =
    isNotEmpty() &&
        first().let { character -> character == '_' || character.isLetter() } &&
        drop(1).all { character -> character == '_' || character.isLetterOrDigit() } &&
        this !in WEBMVC_KOTLIN_KEYWORDS

private val SUPPORTED_CONFIGURATION: Set<String> = setOf("packageName", "serverSuffix")
private const val DEFAULT_PACKAGE: String = "dev.akif.tapik.generated"
private const val DEFAULT_SERVER_SUFFIX: String = "Server"

private val WEBMVC_KOTLIN_KEYWORDS: Set<String> =
    setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "if", "in", "interface",
        "is", "null", "object", "package", "return", "super", "this", "throw", "true", "try", "typealias",
        "typeof", "val", "var", "when", "while"
    )
