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
import java.net.URLClassLoader
import java.util.ServiceLoader

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
