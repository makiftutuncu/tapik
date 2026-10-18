package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.Api
import dev.akif.tapik.common.plugin.ApiCatalog
import dev.akif.tapik.common.plugin.ApiSelection
import dev.akif.tapik.common.plugin.GenerationEngine
import dev.akif.tapik.common.plugin.GenerationResult
import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetCatalog
import java.net.URLClassLoader
import java.net.URL
import java.nio.file.Path
import java.util.Enumeration
import java.util.ServiceConfigurationError

internal object GradleGenerationLoader {
    fun generate(
        contractClasspath: List<Path>,
        targetId: String,
        configuration: Map<String, GradleConfigurationValue>,
        includeApis: Set<String>,
        excludeApis: Set<String>,
        targetClassLoader: ClassLoader
    ): GenerationResult {
        val targets = loadTargets(targetClassLoader)
        val available = targets.map(GenerationTarget::id)
        require(targetId in available) {
            "Unknown generation target '$targetId' on the generation-target classpath. " +
                "Available targets: ${available.joinToString().ifEmpty { "none" }}"
        }
        val urls = contractClasspath.map { path -> path.toUri().toURL() }.toTypedArray()
        return ContractClassLoader(urls, targetClassLoader).use { contractClassLoader ->
            withContextClassLoader(contractClassLoader) {
                val apis = loadApis(contractClassLoader)
                GenerationEngine(targets).generate(
                    targetId = targetId,
                    apis = apis,
                    configuration = configuration.toTargetConfiguration(),
                    selection = ApiSelection(includes = includeApis, excludes = excludeApis)
                )
            }
        }
    }

    private fun loadTargets(classLoader: ClassLoader): List<GenerationTarget> =
        try {
            GenerationTargetCatalog.load(classLoader).targets
        } catch (cause: ServiceConfigurationError) {
            throw targetLoadingFailure(cause)
        } catch (cause: LinkageError) {
            throw targetLoadingFailure(cause)
        }

    private fun loadApis(classLoader: ClassLoader): List<Api> =
        try {
            ApiCatalog.load(classLoader).apis
        } catch (cause: ServiceConfigurationError) {
            throw apiLoadingFailure(cause)
        } catch (cause: LinkageError) {
            throw apiLoadingFailure(cause)
        }

    private fun targetLoadingFailure(cause: Throwable): IllegalStateException =
        IllegalStateException(
            "Failed to load generation targets from the generation-target classpath. " +
                "Ensure target providers, runtime dependencies, and tapik versions are compatible.",
            cause
        )

    private fun apiLoadingFailure(cause: Throwable): IllegalStateException =
        IllegalStateException(
            "Failed to load API registries from the contract classpath. " +
                "Ensure compiled contracts, runtime dependencies, and tapik versions are compatible.",
            cause
        )
}

private class ContractClassLoader(
    urls: Array<URL>,
    parent: ClassLoader
) : URLClassLoader(urls, parent) {
    override fun getResource(name: String): URL? =
        if (name == API_REGISTRY_SERVICE) findResource(name) else super.getResource(name)

    override fun getResources(name: String): Enumeration<URL> =
        if (name == API_REGISTRY_SERVICE) findResources(name) else super.getResources(name)
}

private inline fun <Result> withContextClassLoader(
    classLoader: ClassLoader,
    block: () -> Result
): Result {
    val thread = Thread.currentThread()
    val previous = thread.contextClassLoader
    return try {
        thread.contextClassLoader = classLoader
        block()
    } finally {
        thread.contextClassLoader = previous
    }
}

private const val API_REGISTRY_SERVICE: String = "META-INF/services/dev.akif.tapik.ApiRegistry"
