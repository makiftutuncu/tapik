package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.ApiRegistry
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path

internal class MultiTargetCompilerFixture private constructor(
    private val project: Path
) {
    fun writeStableApi(
        target: String,
        apiName: String
    ) {
        writeSource(
            target,
            "Stable.kt",
            """
            package example.$target

            import dev.akif.tapik.Api

            object $apiName : Api()
            """
        )
    }

    fun writeMutableApi(
        target: String,
        apiName: String?
    ) {
        val declaration = apiName?.let { "object $it : Api()" } ?: "object NotAnApi"
        writeSource(
            target,
            "Mutable.kt",
            """
            package example.$target

            import dev.akif.tapik.Api

            $declaration
            """
        )
    }

    fun compile(target: String): BuildResult =
        GradleRunner
            .create()
            .withProjectDir(project.toFile())
            .withArguments("compileKotlin${target.replaceFirstChar { character -> character.uppercase() }}", "--stacktrace")
            .withPluginClasspath()
            .build()

    fun apiIds(target: String): List<String> {
        val output = project.resolve("build/classes/kotlin/$target/main")
        val provider = Files.readString(output.resolve(API_REGISTRY_SERVICE_PATH)).trim()
        return URLClassLoader(arrayOf(output.toUri().toURL()), javaClass.classLoader).use { classLoader ->
            val registry = classLoader.loadClass(provider).getDeclaredConstructor().newInstance() as ApiRegistry
            registry.apis.map { api -> api.apiId }
        }
    }

    private fun writeSource(
        target: String,
        fileName: String,
        content: String
    ) {
        val source = project.resolve("src/${target}Main/kotlin/example/$target/$fileName")
        Files.createDirectories(source.parent)
        Files.writeString(source, content.trimIndent())
    }

    companion object {
        fun create(): MultiTargetCompilerFixture {
            val project = Files.createTempDirectory("tapik-gradle-multi-target-compiler-")
            val tapikRepository = Path.of(requiredCompilerProperty("tapik.repository")).toUri()
            val tapikVersion = requiredCompilerProperty("tapik.version")

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

                rootProject.name = "multi-target-compiler"
                """.trimIndent()
            )
            Files.writeString(
                project.resolve("gradle.properties"),
                "kotlin.internal.suppressGradlePluginErrors=KotlinTargetAlreadyDeclaredError\n"
            )
            Files.writeString(
                project.resolve("build.gradle.kts"),
                """
                plugins {
                    id("org.jetbrains.kotlin.multiplatform")
                    id("dev.akif.tapik.plugin.gradle")
                }

                kotlin {
                    jvm("first")
                    jvm("second")
                    jvmToolchain(25)

                    sourceSets {
                        commonMain.dependencies {
                            implementation("dev.akif:tapik-core:$tapikVersion")
                        }
                    }
                }
                """.trimIndent()
            )
            return MultiTargetCompilerFixture(project)
        }
    }
}

private fun requiredCompilerProperty(name: String): String =
    requireNotNull(System.getProperty(name)) { "Missing test system property '$name'" }

private const val API_REGISTRY_SERVICE_PATH = "META-INF/services/dev.akif.tapik.ApiRegistry"
