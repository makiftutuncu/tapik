package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

class CompilerRegistryCachingSpec : FunSpec({
    test("relocate cached contract compilation before an incremental source change") {
        val fixture = CompilerRegistryCacheFixture.create()

        fixture.build().task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        fixture.apiIds() shouldContainExactly listOf("Authors", "Books")

        val relocated = fixture.relocate()
        relocated.build().task(":compileKotlin")?.outcome shouldBe TaskOutcome.FROM_CACHE
        relocated.apiIds() shouldContainExactly listOf("Authors", "Books")

        relocated.writeAuthors(revision = 2)
        relocated.build().task(":compileKotlin")?.outcome shouldBe TaskOutcome.SUCCESS
        relocated.apiIds() shouldContainExactly listOf("Authors", "Books")
    }
})

private class CompilerRegistryCacheFixture private constructor(
    private val root: Path,
    private val buildCache: Path
) {
    fun writeAuthors(revision: Int) {
        writeSource(
            "Authors.kt",
            """
            package example

            import dev.akif.tapik.Api

            const val authorsRevision: Int = $revision

            object Authors : Api()
            """
        )
    }

    fun build(): BuildResult =
        GradleRunner
            .create()
            .withProjectDir(root.toFile())
            .withArguments("jar", "--configuration-cache", "--build-cache", "--stacktrace")
            .withPluginClasspath()
            .build()

    fun apiIds(): List<String> {
        val jar = root.resolve("build/libs/contract.jar")
        return URLClassLoader(arrayOf(jar.toUri().toURL()), javaClass.classLoader).use { classLoader ->
            ServiceLoader
                .load(ApiRegistry::class.java, classLoader)
                .flatMap(ApiRegistry::apis)
                .map(Api::apiId)
        }
    }

    fun relocate(): CompilerRegistryCacheFixture {
        val relocated = createProject(Files.createTempDirectory("tapik-gradle-compiler-cache-relocated-"), buildCache)
        Files.walk(root.resolve("src")).use { paths ->
            paths.forEach { source ->
                val destination = relocated.root.resolve(root.relativize(source))
                if (Files.isDirectory(source)) Files.createDirectories(destination)
                else Files.copy(source, destination)
            }
        }
        return relocated
    }

    private fun writeSource(name: String, content: String) {
        val source = root.resolve("src/main/kotlin/example/$name")
        Files.createDirectories(source.parent)
        Files.writeString(source, content.trimIndent() + "\n")
    }

    companion object {
        fun create(): CompilerRegistryCacheFixture {
            val buildCache = Files.createTempDirectory("tapik-gradle-compiler-build-cache-")
            return createProject(Files.createTempDirectory("tapik-gradle-compiler-cache-"), buildCache).also { fixture ->
                fixture.writeAuthors(revision = 1)
                fixture.writeSource(
                    "Books.kt",
                    """
                    package example

                    import dev.akif.tapik.Api

                    object Books : Api()
                    """
                )
            }
        }

        private fun createProject(root: Path, buildCache: Path): CompilerRegistryCacheFixture {
            val repository = Path.of(requireNotNull(System.getProperty("tapik.repository"))).toUri()
            val version = requireNotNull(System.getProperty("tapik.version"))
            Files.writeString(
                root.resolve("settings.gradle.kts"),
                """
                buildCache {
                    local { directory = file("${buildCache.toString().replace("\\", "\\\\")}") }
                }

                dependencyResolutionManagement {
                    repositories {
                        maven {
                            url = uri("$repository")
                            content { includeGroup("dev.akif") }
                        }
                        mavenCentral { content { excludeGroup("dev.akif") } }
                    }
                }

                rootProject.name = "contract"
                """.trimIndent() + "\n"
            )
            Files.writeString(
                root.resolve("build.gradle.kts"),
                """
                plugins {
                    id("org.jetbrains.kotlin.jvm")
                    id("dev.akif.tapik.plugin.gradle")
                }

                dependencies {
                    implementation("dev.akif:tapik-core:$version")
                }

                kotlin { jvmToolchain(25) }

                tasks.jar { archiveFileName.set("contract.jar") }
                """.trimIndent() + "\n"
            )
            return CompilerRegistryCacheFixture(root, buildCache)
        }
    }
}
