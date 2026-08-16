package dev.akif.tapik.plugin.compiler

import dev.akif.tapik.ApiRegistry
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import kotlin.io.path.inputStream

class CompilerPluginSpec : FunSpec({
    test("generate a registry referencing every public API class and object in declaration order") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class Authors : Api() {
                    val list by get(root / "authors")
                }

                object Books : Api() {
                    val list by get(root / "books")
                }
                """.trimIndent()
            )

        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        URLClassLoader(
            arrayOf(compilation.outputDirectory.toUri().toURL()),
            CompilerPluginSpec::class.java.classLoader
        ).use { classLoader ->
            val registry = ServiceLoader.load(ApiRegistry::class.java, classLoader).single()
            registry.apis.map { api -> api.id } shouldContainExactly listOf("Authors", "Books")
            registry.apis shouldBeSameInstanceAs registry.apis
        }
    }

    test("do not generate a registry without concrete API types") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                abstract class ApiBase : Api()
                val helper = object : Api("Helper") {}
                object NotAnApi
                """.trimIndent()
            )

        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        URLClassLoader(
            arrayOf(compilation.outputDirectory.toUri().toURL()),
            CompilerPluginSpec::class.java.classLoader
        ).use { classLoader ->
            ServiceLoader.load(ApiRegistry::class.java, classLoader).toList().shouldBeEmpty()
        }
    }

    test("reject non-public API types") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                internal object Hidden : Api()
                """.trimIndent()
            )

        compilation.exitCode shouldBe ExitCode.COMPILATION_ERROR
        compilation.messages shouldContain "tapik API type 'example.Hidden' must be public"
    }

    test("reject API classes without a public no-argument constructor") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class Books(private val source: String) : Api()
                """.trimIndent()
            )

        compilation.exitCode shouldBe ExitCode.COMPILATION_ERROR
        compilation.messages shouldContain
            "tapik API class 'example.Books' must declare a public no-argument constructor"
    }
})

private data class Compilation(
    val exitCode: ExitCode,
    val outputDirectory: Path,
    val messages: String
)

private fun compile(sourceText: String): Compilation {
    val workspace = Files.createTempDirectory("tapik-compiler-").apply { toFile().deleteOnExit() }
    val source = workspace.resolve("Fixture.kt").apply { Files.writeString(this, sourceText) }
    val output = Files.createDirectories(workspace.resolve("classes"))
    val pluginJar = pluginJar(workspace.resolve("tapik-plugin-compiler.jar"))
    val compilerOutput = ByteArrayOutputStream()
    val exitCode =
        K2JVMCompiler().exec(
            PrintStream(compilerOutput),
            source.toString(),
            "-d",
            output.toString(),
            "-classpath",
            System.getProperty("java.class.path"),
            "-Xplugin=${pluginJar}",
            "-module-name",
            "compiler-plugin-fixture"
        )
    return Compilation(exitCode, output, compilerOutput.toString())
}

private fun pluginJar(destination: Path): Path {
    val classes = Path.of("target", "classes")
    JarOutputStream(Files.newOutputStream(destination)).use { jar ->
        Files.walk(classes).use { paths ->
            paths
                .filter(Files::isRegularFile)
                .forEach { path ->
                    val relative = classes.relativize(path).toString().replace(path.fileSystem.separator, "/")
                    jar.putNextEntry(JarEntry(relative))
                    path.inputStream().use { input -> input.copyTo(jar) }
                    jar.closeEntry()
                }
        }
    }
    return destination
}
