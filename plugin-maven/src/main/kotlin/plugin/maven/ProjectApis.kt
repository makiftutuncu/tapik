package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.common.plugin.ApiCatalog
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.ServiceConfigurationError

internal object ProjectApis {
    fun <Result> use(
        classpath: List<Path>,
        parentClassLoader: ClassLoader,
        block: (List<Api>, ClassLoader) -> Result
    ): Result {
        val urls = classpath.map { path -> path.toUri().toURL() }.toTypedArray()
        return URLClassLoader(urls, parentClassLoader).use { classLoader ->
            withContextClassLoader(classLoader) {
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
