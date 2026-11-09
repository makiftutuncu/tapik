package dev.akif.tapik.common.plugin

import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.Enumeration

/**
 * Owns a disposable classloader whose selected service resources are visible only from its own `classpath`.
 *
 * Ordinary classes and resources retain normal parent-first delegation. [run] temporarily installs the owned loader
 * as the current thread's context classloader and restores the previous value after success or failure.
 */
class ServiceProviderSession private constructor(
    private val classLoader: ChildOnlyServiceClassLoader
) : AutoCloseable {
    /** Runs [block] with this session's isolated loader as both argument and thread context classloader. */
    fun <Result> run(block: (ClassLoader) -> Result): Result = withContextClassLoader(classLoader, block)

    /** Closes the disposable loader owned by this session. */
    override fun close() {
        classLoader.close()
    }

    companion object {
        /** Runs [block] with [classLoader] as the thread context loader without taking ownership of it. */
        fun <Result> runWith(
            classLoader: ClassLoader,
            block: (ClassLoader) -> Result
        ): Result = withContextClassLoader(classLoader, block)

        /**
         * Opens a session over `classpath`, delegating ordinary loading to [parent] while isolating descriptors for
         * [serviceTypes].
         */
        fun open(
            classpath: Iterable<Path>,
            parent: ClassLoader,
            serviceTypes: Set<Class<*>>
        ): ServiceProviderSession =
            ServiceProviderSession(
                ChildOnlyServiceClassLoader(
                    urls = classpath.map { path -> path.toUri().toURL() }.toTypedArray(),
                    parent = parent,
                    childOnlyResources = serviceTypes.mapTo(hashSetOf(), ::serviceResourceName)
                )
            )
    }
}

private fun <Result> withContextClassLoader(
    classLoader: ClassLoader,
    block: (ClassLoader) -> Result
): Result {
    val thread = Thread.currentThread()
    val previous = thread.contextClassLoader
    return try {
        thread.contextClassLoader = classLoader
        block(classLoader)
    } finally {
        thread.contextClassLoader = previous
    }
}

private class ChildOnlyServiceClassLoader(
    urls: Array<URL>,
    parent: ClassLoader,
    private val childOnlyResources: Set<String>
) : URLClassLoader(urls, parent) {
    override fun getResource(name: String): URL? =
        if (name in childOnlyResources) findResource(name) else super.getResource(name)

    override fun getResources(name: String): Enumeration<URL> =
        if (name in childOnlyResources) findResources(name) else super.getResources(name)
}

private fun serviceResourceName(type: Class<*>): String = "META-INF/services/${type.name}"
