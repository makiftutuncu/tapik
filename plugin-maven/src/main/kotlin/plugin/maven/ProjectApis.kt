package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.common.plugin.ApiCatalog
import java.net.URLClassLoader
import java.nio.file.Path

internal object ProjectApis {
    fun <Result> use(
        classpath: List<Path>,
        parentClassLoader: ClassLoader,
        block: (List<Api>, ClassLoader) -> Result
    ): Result {
        val urls = classpath.map { path -> path.toUri().toURL() }.toTypedArray()
        return URLClassLoader(urls, parentClassLoader).use { classLoader ->
            withContextClassLoader(classLoader) {
                block(ApiCatalog.load(classLoader).apis, classLoader)
            }
        }
    }
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
