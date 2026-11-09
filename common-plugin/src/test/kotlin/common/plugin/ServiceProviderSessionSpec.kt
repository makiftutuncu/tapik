package dev.akif.tapik.common.plugin

import dev.akif.tapik.ApiRegistry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

class ServiceProviderSessionSpec : FunSpec({
    test("isolate selected service resources to the owned classpath") {
        val parentResources = serviceDirectory(ApiRegistry::class.java, "example.ParentRegistry")
        val ownedResources = serviceDirectory(ApiRegistry::class.java, "example.OwnedRegistry")
        URLClassLoader(arrayOf(parentResources.toUri().toURL()), javaClass.classLoader).use { parent ->
            ServiceProviderSession.open(
                classpath = listOf(ownedResources),
                parent = parent,
                serviceTypes = setOf(ApiRegistry::class.java)
            ).use { session ->
                session.run { classLoader ->
                    classLoader.getResources(serviceName(ApiRegistry::class.java)).toList() shouldContainExactly
                        listOf(ownedResources.resolve(serviceName(ApiRegistry::class.java)).toUri().toURL())
                }
            }
        }
    }

    test("retain ordinary parent-first resource visibility") {
        val parentResources = resourceDirectory("example/resource.txt", "parent")
        val ownedResources = resourceDirectory("example/resource.txt", "owned")
        URLClassLoader(arrayOf(parentResources.toUri().toURL()), javaClass.classLoader).use { parent ->
            ServiceProviderSession.open(
                classpath = listOf(ownedResources),
                parent = parent,
                serviceTypes = setOf(ApiRegistry::class.java)
            ).use { session ->
                session.run { classLoader ->
                    classLoader.getResources("example/resource.txt").toList() shouldContainExactly
                        listOf(
                            parentResources.resolve("example/resource.txt").toUri().toURL(),
                            ownedResources.resolve("example/resource.txt").toUri().toURL()
                        )
                }
            }
        }
    }

    test("install and restore the context classloader on success and failure") {
        val previous = Thread.currentThread().contextClassLoader
        ServiceProviderSession.open(emptyList(), previous, setOf(ApiRegistry::class.java)).use { session ->
            session.run { classLoader -> Thread.currentThread().contextClassLoader shouldBe classLoader }
            Thread.currentThread().contextClassLoader shouldBe previous

            shouldThrow<IllegalStateException> {
                session.run { classLoader ->
                    Thread.currentThread().contextClassLoader shouldBe classLoader
                    error("session failure")
                }
            }
            Thread.currentThread().contextClassLoader shouldBe previous
        }
    }

    test("close the disposable classloader after successful and failing use") {
        listOf(false, true).forEach { fail ->
            val resourceName = "example/session-$fail.txt"
            val jar = resourceJar(resourceName, "session")
            lateinit var classLoader: ClassLoader
            val result =
                runCatching {
                    ServiceProviderSession.open(
                        classpath = listOf(jar),
                        parent = javaClass.classLoader,
                        serviceTypes = setOf(ApiRegistry::class.java)
                    ).use { session ->
                        session.run { activeClassLoader ->
                            classLoader = activeClassLoader
                            classLoader.getResource(resourceName) shouldNotBe null
                            if (fail) error("session failure")
                        }
                    }
                }

            result.isFailure shouldBe fail
            classLoader.getResource(resourceName) shouldBe null
        }
    }
})

private fun serviceDirectory(type: Class<*>, provider: String): Path =
    resourceDirectory(serviceName(type), "$provider\n")

private fun resourceDirectory(name: String, content: String): Path {
    val directory = Files.createTempDirectory("tapik-service-session-")
    val resource = directory.resolve(name)
    Files.createDirectories(resource.parent)
    Files.writeString(resource, content)
    return directory
}

private fun serviceName(type: Class<*>): String = "META-INF/services/${type.name}"

private fun resourceJar(name: String, content: String): Path {
    val jar = Files.createTempFile("tapik-service-session-", ".jar")
    JarOutputStream(Files.newOutputStream(jar)).use { output ->
        output.putNextEntry(JarEntry(name))
        output.write(content.toByteArray())
        output.closeEntry()
    }
    return jar
}
