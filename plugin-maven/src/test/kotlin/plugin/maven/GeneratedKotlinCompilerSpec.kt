package dev.akif.tapik.plugin.maven

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.net.URLClassLoader
import java.nio.file.Files

class GeneratedKotlinCompilerSpec : FunSpec({
    test("compile only generated Kotlin sources into main output") {
        val workspace = Files.createTempDirectory("tapik-generated-compiler-").apply { toFile().deleteOnExit() }
        val source = workspace.resolve("Generated.kt")
        val output = workspace.resolve("classes")
        Files.writeString(source, "package example.generated\npublic class Generated")

        GeneratedKotlinCompiler.compile(
            sources = listOf(source),
            classpath = System.getProperty("java.class.path").split(java.io.File.pathSeparator).map(java.nio.file.Path::of),
            outputDirectory = output,
            moduleName = "generated-compiler-spec"
        )

        URLClassLoader(arrayOf(output.toUri().toURL())).use { classLoader ->
            classLoader.loadClass("example.generated.Generated").simpleName shouldBe "Generated"
        }
        Files.isRegularFile(output.resolve("META-INF/generated-compiler-spec.kotlin_module")) shouldBe true
    }
})
