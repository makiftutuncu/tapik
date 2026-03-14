package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.fixtures.SampleEndpoints
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
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
                imports.contains("import dev.akif.tapik.spring.restclient.toStatus"),
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
        assertTrue(content.contains("import dev.akif.tapik.plugin.fixtures.SampleEndpoints"))
        assertTrue(content.contains("SampleEndpoints.user"))
    }
}
