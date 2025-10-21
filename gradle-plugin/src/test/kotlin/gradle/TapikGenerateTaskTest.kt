package dev.akif.tapik.gradle

import dev.akif.tapik.gradle.fixtures.SampleEndpoints
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.test.Test
import kotlin.test.assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir

class TapikGenerateTaskTest {
    companion object {
        @JvmStatic
        @TempDir(cleanup = CleanupMode.ALWAYS)
        lateinit var temporaryDir: Path
    }

    @Test
    fun `generate produces endpoint summary and client sources`() {
        val project = ProjectBuilder.builder().withName("tapik-plugin-test").build()
        val task = project.tasks.register("tapikGenerateUnderTest", TapikGenerateTask::class.java).get()

        val compiledDir = File(SampleEndpoints::class.java.protectionDomain.codeSource.location.toURI())
        val outputDir = temporaryDir.resolve("out").createDirectories().toFile()
        val sourcesDir = File("gradle-plugin/src/test/kotlin").absoluteFile
        val generatedDir = temporaryDir.resolve("generated").createDirectories().toFile()

        task.endpointPackages.set(listOf("dev.akif.tapik.gradle.fixtures"))
        task.outputDirectory.set(outputDir)
        task.sourceDirectory.set(sourcesDir)
        task.compiledClassesDirectory.set(compiledDir)
        task.generatedSourcesDirectory.set(generatedDir)
        task.additionalClassDirectories.set(listOf(compiledDir.absolutePath))
        task.runtimeClasspath.from(project.files(compiledDir))

        task.generate()

        println(outputDir)

        val summaryFile = File(outputDir, "tapik-endpoints.txt")
        assertTrue(summaryFile.exists(), "Expected endpoint summary file, files: ${File(summaryFile.parent).list().toList()}")
        val summaryLines = summaryFile.readText().lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()
        assertTrue(summaryLines.any { "user" in it && "GET" in it }, "Summary should list generated endpoint")

        println(generatedDir)
        val generatedInterface =
            File(generatedDir, "dev/akif/tapik/gradle/fixtures/SampleEndpointsClient.kt")
        assertTrue(generatedInterface.exists(), "Generated client interface should exist, files: ${File(generatedInterface.parent).list().toList()}")
        val interfaceContent = generatedInterface.readText()
        assertTrue(interfaceContent.contains("interface SampleEndpointsClient"), "Interface declaration missing")
        assertTrue(interfaceContent.contains("fun user("), "Generated method for endpoint missing")
    }
}
