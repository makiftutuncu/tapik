package dev.akif.tapik.plugin.maven

import dev.akif.tapik.plugin.core.GenerationRequest
import dev.akif.tapik.plugin.core.GenerationResult
import dev.akif.tapik.plugin.core.GenerationTarget
import dev.akif.tapik.plugin.core.GenerationTargetRegistry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.net.URLClassLoader
import java.nio.file.Files

class MavenTargetLoadingSpec : FunSpec({
    test("diagnose a target provider present only on the project classpath") {
        val projectDependency = targetProviderDirectory(ProjectOnlyTargetRegistry::class.java.name)

        val error =
            shouldThrow<IllegalArgumentException> {
                generateWith(targetId = "project-only", classpath = listOf(projectDependency))
            }

        error.message shouldContain "Generation target 'project-only' was found on the project classpath"
        error.message shouldContain "plugin dependencies"
    }

    test("list plugin-visible targets when a target is unknown") {
        val error =
            shouldThrow<IllegalArgumentException> {
                generateWith(targetId = "missing")
            }

        error.message shouldContain "Unknown generation target 'missing'"
        error.message shouldContain "Available targets: openapi, spring-restclient, spring-webmvc"
        error.message shouldContain "plugin dependencies"
    }

    test("diagnose broken target providers in the Maven plugin realm") {
        val brokenProvider = targetProviderDirectory("example.MissingTargetRegistry")
        URLClassLoader(
            arrayOf(brokenProvider.toUri().toURL()),
            MavenTargetLoadingSpec::class.java.classLoader
        ).use { pluginClassLoader ->
            val error =
                shouldThrow<IllegalStateException> {
                    generateWith(targetId = "missing", parentClassLoader = pluginClassLoader)
                }

            error.message shouldContain "Failed to load generation targets from the Maven plugin classpath"
            error.message shouldContain "compatible Tapik versions"
        }
    }
})

private fun generateWith(
    targetId: String,
    classpath: List<java.nio.file.Path> = emptyList(),
    parentClassLoader: ClassLoader = MavenTargetLoadingSpec::class.java.classLoader
) {
    val output = Files.createTempDirectory("tapik-maven-target-").apply { toFile().deleteOnExit() }
    MavenGenerator().generate(
        classpath = classpath,
        targetId = targetId,
        targetConfiguration = emptyMap(),
        outputDirectory = output,
        executionId = "target-loading",
        parentClassLoader = parentClassLoader,
        pluginVersion = "0.6.0",
        projectTapikVersions = setOf("0.6.0")
    )
}

private fun targetProviderDirectory(providerName: String): java.nio.file.Path {
    val directory = Files.createTempDirectory("tapik-target-provider-").apply { toFile().deleteOnExit() }
    val service =
        directory.resolve(
            "META-INF/services/dev.akif.tapik.plugin.core.GenerationTargetRegistry"
        )
    Files.createDirectories(service.parent)
    Files.writeString(service, "$providerName\n")
    return directory
}

class ProjectOnlyTargetRegistry : GenerationTargetRegistry {
    override val targets: List<GenerationTarget> =
        listOf(
            object : GenerationTarget {
                override val id: String = "project-only"

                override fun generate(request: GenerationRequest): GenerationResult =
                    GenerationResult(emptyList())
            }
        )
}
