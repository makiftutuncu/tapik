package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.common.plugin.ArtifactKind
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path

class GradleGenerationLoaderSpec : FunSpec({
    test("load contracts and targets from their isolated classpath sides") {
        targetClassLoader().use { targetClassLoader ->
            val previous = Thread.currentThread().contextClassLoader
            val result =
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )

            result.artifacts.shouldNotBeEmpty()
            result.artifacts.all { artifact -> artifact.kind == ArtifactKind.DOCUMENTATION } shouldBe true
            Thread.currentThread().contextClassLoader shouldBe previous
        }
    }

    test("never discover targets from the contract classpath") {
        targetClassLoader(emptyList()).use { targetClassLoader ->
            val failure =
                shouldThrow<IllegalArgumentException> {
                    GradleGenerationLoader.generate(
                        contractClasspath = contractClasspath + targetClasspath,
                        targetId = "openapi",
                        configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                        includeApis = emptySet(),
                        excludeApis = emptySet(),
                        targetClassLoader = targetClassLoader
                    )
                }

            failure.message shouldContain "Unknown generation target 'openapi'"
        }
    }

    test("never discover API registries from the target classpath") {
        val misplaced = serviceDirectory(API_SERVICE, "example.MissingApiRegistry")
        targetClassLoader(targetClasspath + misplaced).use { targetClassLoader ->
            val result =
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )

            result.artifacts.shouldNotBeEmpty()
        }
    }

    test("diagnose broken target providers on the target classpath") {
        val broken = serviceDirectory(TARGET_SERVICE, "example.MissingTargetRegistry")
        targetClassLoader(listOf(broken)).use { targetClassLoader ->
            val failure = shouldThrow<IllegalStateException> {
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    targetId = "openapi",
                    configuration = emptyMap(),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "generation-target classpath"
            failure.cause!!.javaClass.name shouldBe "java.util.ServiceConfigurationError"
        }
    }

    test("diagnose broken API registries and restore the context classloader") {
        val broken = serviceDirectory(API_SERVICE, "example.MissingApiRegistry")
        targetClassLoader().use { targetClassLoader ->
            val previous = Thread.currentThread().contextClassLoader
            val failure = shouldThrow<IllegalStateException> {
                GradleGenerationLoader.generate(
                    contractClasspath = listOf(broken),
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "contract classpath"
            failure.cause!!.javaClass.name shouldBe "java.util.ServiceConfigurationError"
            Thread.currentThread().contextClassLoader shouldBe previous
        }
    }

    test("restore the context classloader when target generation fails") {
        targetClassLoader().use { targetClassLoader ->
            val previous = Thread.currentThread().contextClassLoader
            shouldThrow<IllegalArgumentException> {
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    targetId = "openapi",
                    configuration = emptyMap(),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }
            Thread.currentThread().contextClassLoader shouldBe previous
        }
    }
})

private fun targetClassLoader(classpath: List<Path> = targetClasspath): URLClassLoader =
    URLClassLoader(
        classpath.map { path -> path.toUri().toURL() }.toTypedArray(),
        GradleGenerationLoaderSpec::class.java.classLoader
    )

private fun serviceDirectory(serviceName: String, provider: String): Path {
    val directory = Files.createTempDirectory("tapik-gradle-service-")
    val service = directory.resolve("META-INF/services/$serviceName")
    Files.createDirectories(service.parent)
    Files.writeString(service, "$provider\n")
    return directory
}

private val contractClasspath: List<Path> = classpathProperty("tapik.generation-worker.contract-classpath")
private val targetClasspath: List<Path> = classpathProperty("tapik.generation-worker.target-classpath")

private fun classpathProperty(name: String): List<Path> =
    requireNotNull(System.getProperty(name)) { "Missing test system property '$name'" }
        .split(java.io.File.pathSeparator)
        .map(Path::of)

private const val API_SERVICE = "dev.akif.tapik.ApiRegistry"
private const val TARGET_SERVICE = "dev.akif.tapik.common.plugin.GenerationTargetRegistry"
