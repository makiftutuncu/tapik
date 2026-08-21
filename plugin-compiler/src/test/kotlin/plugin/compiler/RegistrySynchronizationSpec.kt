package dev.akif.tapik.plugin.compiler

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.cli.common.ExitCode
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.ServiceLoader

class RegistrySynchronizationSpec : FunSpec({
    test("remove the generated registry when a non-clean recompilation has no APIs") {
        val workspace = compilerWorkspace()
        val initial =
            compile(
                """
                package example

                import dev.akif.tapik.*

                object Books : Api()
                """.trimIndent(),
                workspace
            )
        withClue(initial.messages) { initial.exitCode shouldBe ExitCode.OK }

        val recompiled =
            compile(
                """
                package example

                object NotAnApi
                """.trimIndent(),
                workspace
            )
        withClue(recompiled.messages) { recompiled.exitCode shouldBe ExitCode.OK }

        URLClassLoader(
            arrayOf(recompiled.outputDirectory.toUri().toURL()),
            RegistrySynchronizationSpec::class.java.classLoader
        ).use { classLoader ->
            ServiceLoader.load(ApiRegistry::class.java, classLoader).toList().shouldBeEmpty()
        }
        generatedRegistryClasses(recompiled.outputDirectory).shouldBeEmpty()
        Files.exists(recompiled.outputDirectory.resolve(API_REGISTRY_SERVICE_PATH)) shouldBe false
    }

    test("replace rather than accumulate a registry after APIs change") {
        val workspace = compilerWorkspace()
        val initial =
            compile(
                """
                package example

                import dev.akif.tapik.*

                object Books : Api()
                """.trimIndent(),
                workspace
            )
        withClue(initial.messages) { initial.exitCode shouldBe ExitCode.OK }

        val recompiled =
            compile(
                """
                package example

                import dev.akif.tapik.*

                object Authors : Api()
                """.trimIndent(),
                workspace
            )
        withClue(recompiled.messages) { recompiled.exitCode shouldBe ExitCode.OK }

        URLClassLoader(
            arrayOf(recompiled.outputDirectory.toUri().toURL()),
            RegistrySynchronizationSpec::class.java.classLoader
        ).use { classLoader ->
            val registry = ServiceLoader.load(ApiRegistry::class.java, classLoader).single()
            registry.apis.map(Api::id) shouldContainExactly listOf("Authors")
        }
        generatedRegistryClasses(recompiled.outputDirectory).shouldHaveSize(1)
    }

    test("preserve manually supplied registry providers") {
        val workspace = compilerWorkspace()
        val output = Files.createDirectories(workspace.resolve("classes"))
        val service = output.resolve(API_REGISTRY_SERVICE_PATH)
        Files.createDirectories(requireNotNull(service.parent))
        Files.writeString(service, "example.ManualRegistry\n")

        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class ManualRegistry : ApiRegistry {
                    override val apis: List<Api> = emptyList()
                }

                object Books : Api()
                """.trimIndent(),
                workspace
            )
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }

        URLClassLoader(
            arrayOf(compilation.outputDirectory.toUri().toURL()),
            RegistrySynchronizationSpec::class.java.classLoader
        ).use { classLoader ->
            val registries = ServiceLoader.load(ApiRegistry::class.java, classLoader).toList()
            registries.shouldHaveSize(2)
            registries.flatMap(ApiRegistry::apis).map(Api::id) shouldContainExactly listOf("Books")
        }

        val recompiled =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class ManualRegistry : ApiRegistry {
                    override val apis: List<Api> = emptyList()
                }
                """.trimIndent(),
                workspace
            )
        withClue(recompiled.messages) { recompiled.exitCode shouldBe ExitCode.OK }

        URLClassLoader(
            arrayOf(recompiled.outputDirectory.toUri().toURL()),
            RegistrySynchronizationSpec::class.java.classLoader
        ).use { classLoader ->
            val registry = ServiceLoader.load(ApiRegistry::class.java, classLoader).single()
            registry::class.java.name shouldBe "example.ManualRegistry"
        }
        generatedRegistryClasses(recompiled.outputDirectory).shouldBeEmpty()
    }

    test("replace stale hashed registry artifacts") {
        val workspace = compilerWorkspace()
        val output = Files.createDirectories(workspace.resolve("classes"))
        val service = output.resolve(API_REGISTRY_SERVICE_PATH)
        Files.createDirectories(requireNotNull(service.parent))
        Files.writeString(service, "$STALE_REGISTRY_NAME\n")
        val staleClass = output.resolve(STALE_REGISTRY_NAME.replace('.', '/') + ".class")
        Files.createDirectories(requireNotNull(staleClass.parent))
        Files.write(staleClass, byteArrayOf())

        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                object Books : Api()
                """.trimIndent(),
                workspace
            )
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }

        val provider = Files.readAllLines(service).single()
        provider.startsWith("dev.akif.tapik.generated.TapikApiRegistry_") shouldBe true
        (provider == STALE_REGISTRY_NAME) shouldBe false
        generatedRegistryClasses(output).shouldHaveSize(1)
        URLClassLoader(
            arrayOf(compilation.outputDirectory.toUri().toURL()),
            RegistrySynchronizationSpec::class.java.classLoader
        ).use { classLoader ->
            val registry = ServiceLoader.load(ApiRegistry::class.java, classLoader).single()
            registry.apis.map(Api::id) shouldContainExactly listOf("Books")
        }
    }
})

private const val STALE_REGISTRY_NAME: String =
    "dev.akif.tapik.generated.TapikApiRegistry_0123456789abcdef"
