package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.fixtures.SampleEndpoints
import java.io.File
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.test.Test
import kotlin.test.assertTrue
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir

class TapikCodeGenerationEngineTest {
    companion object {
        @JvmStatic
        @TempDir(cleanup = CleanupMode.ALWAYS)
        lateinit var temporaryDir: Path
    }

    @Test
    fun `generate produces endpoint summary and client sources`() {
        val compiledDir = File(SampleEndpoints::class.java.protectionDomain.codeSource.location.toURI())
        val outputDir = temporaryDir.resolve("out").createDirectories().toFile()
        val generatedDir = temporaryDir.resolve("generated").createDirectories().toFile()

        TapikCodeGenerationEngine(
            config = TapikCodeGenerationConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                endpointsSuffix = "Endpoints",
                basePackage = "dev.akif.tapik.plugin",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = emptyList(),
                generatorConfigurations =
                    mapOf(
                        "spring-restclient" to GeneratorConfiguration(),
                        "spring-webmvc" to GeneratorConfiguration(),
                        "markdown-docs" to GeneratorConfiguration()
                    )
            ),
            logger = TapikLogger.Console
        ).generate()

        val summaryFile = File(outputDir, "tapik-endpoints.txt")
        assertTrue(
            summaryFile.exists(),
            "Expected endpoint summary file, files: ${File(summaryFile.parent).list()?.toList()}"
        )
        val summaryLines = summaryFile.readText().lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
        assertTrue(summaryLines.any { "user" in it && "GET" in it }, "Summary should list generated endpoint")

        val generatedInterface = File(generatedDir, "dev/akif/tapik/plugin/fixtures/generated/SampleEndpoints.kt")
        assertTrue(
            generatedInterface.exists(),
            "Generated client interface should exist, files: ${File(generatedInterface.parent).list()?.toList()}"
        )
        val interfaceContent = generatedInterface.readText()
        assertTrue(interfaceContent.contains("interface SampleEndpointsClient"), "Interface declaration missing")
        assertTrue(interfaceContent.contains("interface SampleEndpointsServer"), "Server aggregate missing")
        assertTrue(interfaceContent.contains("fun user("), "Generated method for endpoint missing")

        val documentation = File(outputDir, "API.md")
        assertTrue(
            documentation.exists(),
            "Generated documentation should exist, files: ${File(documentation.parent).list()?.toList()}"
        )
    }

    @Test
    fun `generate optimizes imports for merged kotlin sources by default`() {
        val compiledDir = File(SampleEndpoints::class.java.protectionDomain.codeSource.location.toURI())
        val outputDir = temporaryDir.resolve("out-2").createDirectories().toFile()
        val generatedDir = temporaryDir.resolve("generated-2").createDirectories().toFile()

        TapikCodeGenerationEngine(
            config = TapikCodeGenerationConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                endpointsSuffix = "Endpoints",
                basePackage = "dev.akif.tapik.plugin",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = emptyList(),
                generatorConfigurations =
                    mapOf(
                        "spring-restclient" to GeneratorConfiguration(),
                        "spring-webmvc" to GeneratorConfiguration(),
                        "markdown-docs" to GeneratorConfiguration()
                    )
            ),
            logger = TapikLogger.NoOp
        ).generate()

        val generatedClient = File(generatedDir, "dev/akif/tapik/plugin/fixtures/generated/SampleEndpoints.kt")
        assertTrue(generatedClient.exists(), "Expected generated client interface")
        val imports =
            generatedClient
                .readText()
                .lineSequence()
                .filter { it.startsWith("import ") }
                .toList()
        assertTrue(
            imports.contains("import dev.akif.tapik.encodeInputHeaders") &&
                imports.contains("import dev.akif.tapik.spring.toStatus"),
            "Merged Kotlin source should keep the optimized imports required by generated clients"
        )
    }

    @Test
    fun `generate writes kotlin sources into configured generated package`() {
        val compiledDir = File(SampleEndpoints::class.java.protectionDomain.codeSource.location.toURI())
        val outputDir = temporaryDir.resolve("out-3").createDirectories().toFile()
        val generatedDir = temporaryDir.resolve("generated-3").createDirectories().toFile()

        TapikCodeGenerationEngine(
            config = TapikCodeGenerationConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                generatedPackageName = "generated",
                endpointsSuffix = "Endpoints",
                basePackage = "dev.akif.tapik.plugin",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = emptyList(),
                generatorConfigurations =
                    mapOf(
                        "spring-restclient" to GeneratorConfiguration(),
                        "spring-webmvc" to GeneratorConfiguration()
                    )
            ),
            logger = TapikLogger.NoOp
        ).generate()

        val generatedClient = File(generatedDir, "dev/akif/tapik/plugin/fixtures/generated/SampleEndpoints.kt")
        assertTrue(generatedClient.exists(), "Expected generated client interface in target package")
        val content = generatedClient.readText()
        assertTrue(content.contains("package dev.akif.tapik.plugin.fixtures.generated"))
        assertTrue(content.contains("// Generated from: dev.akif.tapik.plugin.fixtures.SampleEndpoints"))
        assertTrue(content.contains("import dev.akif.tapik.plugin.fixtures.SampleEndpoints"))
        assertTrue(content.contains("SampleEndpoints.user"))
    }

    @Test
    fun `generate discovers endpoints from additional compiled class directories`() {
        val compiledDir = temporaryDir.resolve("empty-compiled").createDirectories().toFile()
        val additionalDir = temporaryDir.resolve("additional-compiled").createDirectories()
        copyFixtureClassesToDirectory(additionalDir)
        val outputDir = temporaryDir.resolve("out-4").createDirectories().toFile()
        val generatedDir = temporaryDir.resolve("generated-4").createDirectories().toFile()

        TapikCodeGenerationEngine(
            config = TapikCodeGenerationConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                endpointsSuffix = "Endpoints",
                basePackage = "dev.akif.tapik.plugin.fixtures",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = listOf(additionalDir.toFile()),
                generatorConfigurations = mapOf("markdown-docs" to GeneratorConfiguration())
            ),
            logger = TapikLogger.NoOp
        ).generate()

        val summaryLines = outputDir.resolve("tapik-endpoints.txt").readText().lineSequence().toList()
        assertTrue(summaryLines.any { "user" in it && "GET" in it }, "Summary should list endpoint from additional class directory")
    }

    @Test
    fun `generate discovers endpoints from dependency jars`() {
        val compiledDir = temporaryDir.resolve("empty-compiled-jar").createDirectories().toFile()
        val dependencyJar = temporaryDir.resolve("fixture-endpoints.jar")
        copyFixtureClassesToJar(dependencyJar)
        val outputDir = temporaryDir.resolve("out-5").createDirectories().toFile()
        val generatedDir = temporaryDir.resolve("generated-5").createDirectories().toFile()

        TapikCodeGenerationEngine(
            config = TapikCodeGenerationConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                endpointsSuffix = "Endpoints",
                basePackage = "dev.akif.tapik.plugin.fixtures",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = listOf(dependencyJar.toFile()),
                generatorConfigurations = mapOf("markdown-docs" to GeneratorConfiguration())
            ),
            logger = TapikLogger.NoOp
        ).generate()

        val summaryLines = outputDir.resolve("tapik-endpoints.txt").readText().lineSequence().toList()
        assertTrue(summaryLines.any { "user" in it && "GET" in it }, "Summary should list endpoint from dependency JAR")
    }

    @Test
    fun `generate documents fixed header values using their codecs`() {
        val compiledDir = File(SampleEndpoints::class.java.protectionDomain.codeSource.location.toURI())
        val outputDir = temporaryDir.resolve("out-6").createDirectories().toFile()
        val generatedDir = temporaryDir.resolve("generated-6").createDirectories().toFile()

        TapikCodeGenerationEngine(
            config = TapikCodeGenerationConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                endpointsSuffix = "Endpoints",
                basePackage = "dev.akif.tapik.plugin",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = emptyList(),
                generatorConfigurations = mapOf("markdown-docs" to GeneratorConfiguration())
            ),
            logger = TapikLogger.NoOp
        ).generate()

        val documentation = outputDir.resolve("API.md").readText()
        assertTrue(documentation.contains("`encoded:alpha`"))
        assertTrue(documentation.contains("`encoded:beta`"))
    }

    private fun copyFixtureClassesToDirectory(targetDirectory: Path) {
        fixtureClassFiles().forEach { source ->
            val relativePath = fixtureClassesRoot().relativize(source)
            val target = targetDirectory.resolve(relativePath.toString())
            target.parent?.createDirectories()
            source.inputStream().use { input ->
                target.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }

    private fun copyFixtureClassesToJar(targetJar: Path) {
        targetJar.outputStream().use { output ->
            JarOutputStream(output).use { jar ->
                fixtureClassFiles().forEach { source ->
                    val relativePath = fixtureClassesRoot().relativize(source).toString().replace(File.separatorChar, '/')
                    jar.putNextEntry(JarEntry(relativePath))
                    source.inputStream().use { input -> input.copyTo(jar) }
                    jar.closeEntry()
                }
            }
        }
    }

    private fun fixtureClassFiles(): List<Path> =
        fixtureClassesRoot()
            .resolve("dev/akif/tapik/plugin/fixtures")
            .toFile()
            .listFiles()
            ?.filter { it.isFile && it.name.startsWith("SampleEndpoints") && it.extension == "class" }
            ?.map { it.toPath() }
            ?.sortedBy { it.toString() }
            .orEmpty()

    private fun fixtureClassesRoot(): Path =
        File(SampleEndpoints::class.java.protectionDomain.codeSource.location.toURI()).toPath()
}
