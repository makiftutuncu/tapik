package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.common.plugin.ApiCatalog
import dev.akif.tapik.common.plugin.GenerationTargetRegistry
import dev.akif.tapik.common.plugin.ServiceProviderSession
import java.nio.file.Path
import java.util.ServiceConfigurationError

internal object ProjectApis {
    fun <Result> use(
        classpath: List<Path>,
        parentClassLoader: ClassLoader,
        block: (List<Api>, ClassLoader) -> Result
    ): Result {
        return ServiceProviderSession.open(
            classpath = classpath,
            parent = parentClassLoader,
            serviceTypes = setOf(ApiRegistry::class.java, GenerationTargetRegistry::class.java)
        ).use { session ->
            session.run { classLoader ->
                block(load(classLoader), classLoader)
            }
        }
    }

    private fun load(classLoader: ClassLoader): List<Api> =
        try {
            ApiCatalog.load(classLoader).apis
        } catch (cause: ServiceConfigurationError) {
            throw loadingFailure(cause)
        } catch (cause: LinkageError) {
            throw loadingFailure(cause)
        }

    private fun loadingFailure(cause: Throwable): IllegalStateException =
        IllegalStateException(
            "Failed to load API registries from the project classpath. " +
                "Ensure compiled contract artifacts and their runtime dependencies are present and use compatible tapik versions.",
            cause
        )
}
