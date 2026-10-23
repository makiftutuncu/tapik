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
                    mode = Mode.COMPILED_CONTRACT,
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

    test("diagnose targets placed on the contract classpath") {
        targetClassLoader(emptyList()).use { targetClassLoader ->
            val failure =
                shouldThrow<IllegalStateException> {
                    GradleGenerationLoader.generate(
                        contractClasspath = contractClasspath + targetClasspath,
                        mode = Mode.COMPILED_CONTRACT,
                        targetId = "openapi",
                        configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                        includeApis = emptySet(),
                        excludeApis = emptySet(),
                        targetClassLoader = targetClassLoader
                    )
                }

            failure.message shouldContain "Generation target 'openapi' was found on tapikContractClasspath"
            failure.message shouldContain "tapikTarget"
        }
    }

    test("diagnose an unknown target with sorted available IDs and dependency guidance") {
        targetClassLoader().use { targetClassLoader ->
            val failure = shouldThrow<IllegalArgumentException> {
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "missing",
                    configuration = emptyMap(),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "Unknown generation target 'missing' on tapikTargetClasspath"
            failure.message shouldContain "Available targets: openapi"
            failure.message shouldContain "tapikTarget"
        }
    }

    test("never discover API registries from the target classpath") {
        val misplaced = serviceDirectory(API_SERVICE, "example.MissingApiRegistry")
        targetClassLoader(targetClasspath + misplaced).use { targetClassLoader ->
            val result =
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    mode = Mode.COMPILED_CONTRACT,
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
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "openapi",
                    configuration = emptyMap(),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "tapikTargetClasspath"
            failure.message shouldContain "META-INF/services"
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
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "tapikContractClasspath"
            failure.message shouldContain "compiler-generated registry"
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
                    mode = Mode.COMPILED_CONTRACT,
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

    test("diagnose duplicate API IDs on the contract classpath") {
        val duplicates =
            serviceDirectory(
                API_SERVICE,
                DuplicateApiRegistryOne::class.qualifiedName!!,
                DuplicateApiRegistryTwo::class.qualifiedName!!
            )
        targetClassLoader().use { targetClassLoader ->
            val failure = shouldThrow<IllegalStateException> {
                GradleGenerationLoader.generate(
                    contractClasspath = listOf(duplicates),
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "Invalid API catalog on tapikContractClasspath"
            failure.message shouldContain "Duplicate"
        }
    }

    test("diagnose duplicate target IDs on the generation target classpath") {
        val duplicates =
            serviceDirectory(
                TARGET_SERVICE,
                DuplicateTargetRegistryOne::class.qualifiedName!!,
                DuplicateTargetRegistryTwo::class.qualifiedName!!
            )
        targetClassLoader(listOf(duplicates)).use { targetClassLoader ->
            val failure = shouldThrow<IllegalStateException> {
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "duplicate",
                    configuration = emptyMap(),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "Invalid generation target catalog on tapikTargetClasspath"
            failure.message shouldContain "duplicate"
        }
    }

    test("diagnose target linkage failures on the generation target classpath") {
        val broken = serviceDirectory(TARGET_SERVICE, LinkageFailingTargetRegistry::class.qualifiedName!!)
        targetClassLoader(listOf(broken)).use { targetClassLoader ->
            val failure = shouldThrow<IllegalStateException> {
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "openapi",
                    configuration = emptyMap(),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "tapikTargetClasspath"
            failure.cause!!.javaClass shouldBe NoClassDefFoundError::class.java
        }
    }

    test("diagnose API linkage failures on the contract classpath") {
        val broken = serviceDirectory(API_SERVICE, LinkageFailingApiRegistry::class.qualifiedName!!)
        targetClassLoader().use { targetClassLoader ->
            val failure = shouldThrow<IllegalStateException> {
                GradleGenerationLoader.generate(
                    contractClasspath = listOf(broken),
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }

            failure.message shouldContain "tapikContractClasspath"
            failure.cause!!.javaClass shouldBe NoClassDefFoundError::class.java
        }
    }

    test("diagnose an empty API catalog according to generation mode") {
        targetClassLoader().use { targetClassLoader ->
            val compiled = shouldThrow<IllegalArgumentException> {
                GradleGenerationLoader.generate(
                    contractClasspath = emptyList(),
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }
            compiled.message shouldContain "No APIs were discovered on tapikContractClasspath"
            compiled.message shouldContain "tapikContract"

            val sameProject = shouldThrow<IllegalArgumentException> {
                GradleGenerationLoader.generate(
                    contractClasspath = emptyList(),
                    mode = Mode.SAME_PROJECT,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }
            sameProject.message shouldContain "same project's main runtime classpath"
            sameProject.message shouldContain "public concrete Api"
        }
    }

    test("retain selection and target failures without classifying them as loading failures") {
        val throwing = serviceDirectory(TARGET_SERVICE, ThrowingTargetRegistry::class.qualifiedName!!)
        targetClassLoader().use { targetClassLoader ->
            val selectionFailure = shouldThrow<IllegalArgumentException> {
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "openapi",
                    configuration = mapOf("version" to ScalarGradleConfigurationValue("1.0.0")),
                    includeApis = emptySet(),
                    excludeApis = setOf("Authors", "Books", "Catalog"),
                    targetClassLoader = targetClassLoader
                )
            }
            selectionFailure.message shouldContain "API selection must retain at least one API"
        }

        targetClassLoader(listOf(throwing)).use { targetClassLoader ->
            val targetFailure = shouldThrow<IllegalArgumentException> {
                GradleGenerationLoader.generate(
                    contractClasspath = contractClasspath,
                    mode = Mode.COMPILED_CONTRACT,
                    targetId = "throwing",
                    configuration = emptyMap(),
                    includeApis = emptySet(),
                    excludeApis = emptySet(),
                    targetClassLoader = targetClassLoader
                )
            }
            targetFailure.message shouldBe "target failure"
        }
    }
})

private fun targetClassLoader(classpath: List<Path> = targetClasspath): URLClassLoader =
    URLClassLoader(
        classpath.map { path -> path.toUri().toURL() }.toTypedArray(),
        GradleGenerationLoaderSpec::class.java.classLoader
    )

private fun serviceDirectory(serviceName: String, vararg providers: String): Path {
    val directory = Files.createTempDirectory("tapik-gradle-service-")
    val service = directory.resolve("META-INF/services/$serviceName")
    Files.createDirectories(service.parent)
    Files.writeString(service, providers.joinToString(separator = "\n", postfix = "\n"))
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
