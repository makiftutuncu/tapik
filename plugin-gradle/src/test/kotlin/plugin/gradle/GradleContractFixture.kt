package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader
import java.util.jar.JarFile

internal class GradleContractFixture private constructor(
    private val project: Path
) {
    private val source = project.resolve("src/main/kotlin/example/Contracts.kt")

    val jar: Path
        get() = project.resolve("build/libs/contract.jar")

    fun writeSource(content: String) {
        Files.createDirectories(source.parent)
        Files.writeString(source, content.trimIndent())
    }

    fun build(vararg arguments: String): BuildResult =
        GradleRunner
            .create()
            .withProjectDir(project.toFile())
            .withArguments(*arguments, "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

    fun registryArtifacts(): RegistryArtifacts =
        JarFile(jar.toFile()).use { archive ->
            val service = archive.getJarEntry(API_REGISTRY_SERVICE_PATH)
            val provider = service?.let { entry -> archive.getInputStream(entry).bufferedReader().readText().trim() }
            val classes =
                archive
                    .entries()
                    .asSequence()
                    .map { entry -> entry.name }
                    .filter { name -> name.matches(GENERATED_REGISTRY_CLASS) }
                    .toList()
            RegistryArtifacts(provider, classes)
        }

    fun apiIds(): List<String> =
        URLClassLoader(arrayOf(jar.toUri().toURL()), javaClass.classLoader).use { classLoader ->
            ServiceLoader
                .load(ApiRegistry::class.java, classLoader)
                .flatMap(ApiRegistry::apis)
                .map(Api::apiId)
        }

    companion object {
        fun create(): GradleContractFixture {
            val project = Files.createTempDirectory("tapik-gradle-contract-")
            val tapikRepository = Path.of(requiredProperty("tapik.repository")).toUri()
            val tapikVersion = requiredProperty("tapik.version")

            Files.writeString(
                project.resolve("settings.gradle.kts"),
                """
                pluginManagement {
                    repositories {
                        gradlePluginPortal()
                        mavenCentral()
                    }
                }

                dependencyResolutionManagement {
                    repositories {
                        maven {
                            url = uri("$tapikRepository")
                            content { includeGroup("dev.akif") }
                        }
                        mavenCentral { content { excludeGroup("dev.akif") } }
                    }
                }

                rootProject.name = "contract"
                """.trimIndent()
            )
            Files.writeString(
                project.resolve("build.gradle.kts"),
                """
                plugins {
                    id("org.jetbrains.kotlin.jvm")
                    id("dev.akif.tapik.plugin.gradle")
                }

                dependencies {
                    implementation("dev.akif:tapik-core:$tapikVersion")
                }

                kotlin {
                    jvmToolchain(25)
                }

                tasks.jar {
                    archiveFileName.set("contract.jar")
                }
                """.trimIndent()
            )
            return GradleContractFixture(project)
        }
    }
}

internal data class RegistryArtifacts(
    val provider: String?,
    val classes: List<String>
)

private fun requiredProperty(name: String): String =
    requireNotNull(System.getProperty(name)) { "Missing test system property '$name'" }

private const val API_REGISTRY_SERVICE_PATH = "META-INF/services/dev.akif.tapik.ApiRegistry"
private val GENERATED_REGISTRY_CLASS =
    Regex("dev/akif/tapik/generated/TapikApiRegistry_[0-9a-f]{16}\\.class")
