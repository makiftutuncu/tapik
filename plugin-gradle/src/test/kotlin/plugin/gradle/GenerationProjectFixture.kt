package dev.akif.tapik.plugin.gradle

import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarOutputStream
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText

internal fun generationProject(): GenerationProject {
    val root = Files.createTempDirectory("tapik-gradle-generation-")
    Files.writeString(root.resolve("settings.gradle"), "rootProject.name = 'consumer'\n")
    return GenerationProject(root)
}

internal class GenerationProject(private val root: Path) {
    fun includeTapikProject(module: String, version: String) {
        Files.writeString(
            root.resolve("settings.gradle"),
            "rootProject.name = 'consumer'\ninclude '$module'\n"
        )
        val project = root.resolve(module)
        Files.createDirectories(project)
        Files.writeString(
            project.resolve("build.gradle"),
            """
            plugins {
                id 'java-library'
            }

            group = 'dev.akif'
            version = '$version'
            """.trimIndent() + "\n"
        )
    }

    fun includeSubstitutedTapikBuild(module: String, version: String) {
        val directoryName = "included-$module"
        Files.writeString(
            root.resolve("settings.gradle"),
            """
            rootProject.name = 'consumer'
            includeBuild('$directoryName') {
                dependencySubstitution {
                    substitute module('dev.akif:$module') using project(':')
                }
            }
            """.trimIndent() + "\n"
        )
        val build = root.resolve(directoryName)
        Files.createDirectories(build)
        Files.writeString(build.resolve("settings.gradle"), "rootProject.name = '$module'\n")
        Files.writeString(
            build.resolve("build.gradle"),
            """
            plugins {
                id 'java-library'
            }

            group = 'dev.akif'
            version = '$version'
            """.trimIndent() + "\n"
        )
    }

    fun writeBuild(
        tapikBlock: String,
        additionalDependencies: String = "",
        contractDependencies: String =
            "tapikContract 'dev.akif:tapik-example-contract:$gradleTestTapikVersion'"
    ) {
        Files.writeString(
            root.resolve("build.gradle"),
            """
            import dev.akif.tapik.plugin.gradle.Mode
            import dev.akif.tapik.plugin.gradle.OpenApiFormat

            plugins {
                id 'org.jetbrains.kotlin.jvm'
                id 'dev.akif.tapik.plugin.gradle'
            }

            repositories {
                maven { url = uri('${root.resolve("repository").toUri()}') }
                maven { url = uri('${gradleTestTapikRepository.toUri()}') }
                mavenCentral()
            }

            dependencies {
                $contractDependencies
                $additionalDependencies
            }

            tapik {
                mode(Mode.COMPILED_CONTRACT)
                $tapikBlock
            }
            """.trimIndent()
        )
    }

    fun publishTapikModule(module: String, version: String) {
        val directory = root.resolve("repository/dev/akif/$module/$version")
        Files.createDirectories(directory)
        Files.writeString(
            directory.resolve("$module-$version.pom"),
            """
            <project xmlns="http://maven.apache.org/POM/4.0.0">
              <modelVersion>4.0.0</modelVersion>
              <groupId>dev.akif</groupId>
              <artifactId>$module</artifactId>
              <version>$version</version>
            </project>
            """.trimIndent()
        )
        JarOutputStream(Files.newOutputStream(directory.resolve("$module-$version.jar"))).use { }
    }

    fun run(task: String) = runner(task).build()

    fun runAndFail(task: String) = runner(task).buildAndFail()

    fun generatedFiles(): List<Path> {
        val generated = root.resolve("build/generated/tapik")
        if (!generated.exists()) return emptyList()
        return Files.walk(generated).use { paths ->
            paths.filter(Path::isRegularFile)
                .filter { path -> path.name != ".tapik-ownership" }
                .sorted()
                .toList()
        }
    }

    fun generatedFileContents(): List<Pair<String, String>> =
        generatedFiles().map { path -> root.relativize(path).toString() to path.readText() }

    private fun runner(task: String): GradleRunner =
        GradleRunner.create()
            .withProjectDir(root.toFile())
            .withArguments(task, "--configuration-cache", "--stacktrace")
            .withPluginClasspath()
}

internal val gradleTestTapikRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })

internal val gradleTestTapikVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }

internal fun Path.gradleGroovyPath(): String = toString().replace('\\', '/').replace("'", "\\'")
