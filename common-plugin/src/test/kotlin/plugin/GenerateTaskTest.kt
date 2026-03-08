package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.fixtures.SampleEndpoints
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir

class GenerateTaskTest {
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

        GenerateTask(
            config = GenerateTaskConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                basePackage = "dev.akif.tapik.plugin",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = emptyList(),
                generatorConfigurations =
                    mapOf(
                        "spring-restclient" to GeneratorConfiguration(optimizeImports = true, namePrefix = null, nameSuffix = null),
                        "spring-webmvc" to GeneratorConfiguration(optimizeImports = true, namePrefix = null, nameSuffix = null),
                        "markdown-docs" to GeneratorConfiguration(optimizeImports = false, namePrefix = null, nameSuffix = null)
                    )
            ),
            log = { s, _ -> println(s) },
            logDebug = { s, _ -> println(s) },
            logWarn = { s, _ -> println(s) }
        ).generate()

        val summaryFile = File(outputDir, "tapik-endpoints.txt")
        assertTrue(
            summaryFile.exists(),
            "Expected endpoint summary file, files: ${File(summaryFile.parent).list()?.toList()}"
        )
        val summaryLines = summaryFile.readText().lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
        assertTrue(summaryLines.any { "user" in it && "GET" in it }, "Summary should list generated endpoint")

        val generatedInterface = File(generatedDir, "dev/akif/tapik/plugin/fixtures/SampleEndpointsClient.kt")
        assertTrue(
            generatedInterface.exists(),
            "Generated client interface should exist, files: ${File(generatedInterface.parent).list()?.toList()}"
        )
        val interfaceContent = generatedInterface.readText()
        assertTrue(interfaceContent.contains("interface SampleEndpointsClient"), "Interface declaration missing")
        assertTrue(interfaceContent.contains("fun user("), "Generated method for endpoint missing")

        val generatedController = File(generatedDir, "dev/akif/tapik/plugin/fixtures/SampleEndpointsController.kt")
        assertTrue(
            generatedController.exists(),
            "Generated controller interface should exist, files: ${File(generatedController.parent).list()?.toList()}"
        )
        val controllerContent = generatedController.readText()
        assertTrue(controllerContent.contains("interface SampleEndpointsController"), "Controller declaration missing")

        val documentation = File(outputDir, "API.md")
        assertTrue(
            documentation.exists(),
            "Generated documentation should exist, files: ${File(documentation.parent).list()?.toList()}"
        )
    }

    @Test
    fun `generate applies import optimization per generator`() {
        val compiledDir = File(SampleEndpoints::class.java.protectionDomain.codeSource.location.toURI())
        val outputDir = temporaryDir.resolve("out-2").createDirectories().toFile()
        val generatedDir = temporaryDir.resolve("generated-2").createDirectories().toFile()

        GenerateTask(
            config = GenerateTaskConfiguration(
                outputDirectory = outputDir,
                generatedSourcesDirectory = generatedDir,
                basePackage = "dev.akif.tapik.plugin",
                compiledClassesDirectory = compiledDir,
                additionalClasspathDirectories = emptyList(),
                generatorConfigurations =
                    mapOf(
                        "spring-restclient" to GeneratorConfiguration(optimizeImports = false, namePrefix = null, nameSuffix = null),
                        "spring-webmvc" to GeneratorConfiguration(optimizeImports = true, namePrefix = null, nameSuffix = null),
                        "markdown-docs" to GeneratorConfiguration(optimizeImports = false, namePrefix = null, nameSuffix = null)
                    )
            ),
            log = { _, _ -> },
            logDebug = { _, _ -> },
            logWarn = { _, _ -> }
        ).generate()

        val generatedClient = File(generatedDir, "dev/akif/tapik/plugin/fixtures/SampleEndpointsClient.kt")
        assertTrue(generatedClient.exists(), "Expected generated client interface")
        val imports =
            generatedClient
                .readText()
                .lineSequence()
                .filter { it.startsWith("import ") }
                .toList()
        assertEquals(
            listOf(
                "import dev.akif.tapik.spring.restclient.toStatus",
                "import dev.akif.tapik.encodeInputHeaders"
            ),
            imports,
            "RestClient generator should not be import-optimized when disabled"
        )
    }
}
