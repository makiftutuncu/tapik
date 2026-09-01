package dev.akif.tapik.plugin.compiler

import dev.akif.tapik.ApiRegistry
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlin.cli.common.ExitCode
import java.net.URLClassLoader
import java.util.ServiceLoader

class CompilerPluginSpec : FunSpec({
    test("generate a registry referencing every public API class and object") {
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
            registry.apis.map { api -> api.id }.toSet() shouldBe setOf("Authors", "Books")
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

    test("accept incremental Kotlin compilation") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                object Books : Api()
                """.trimIndent(),
                incremental = true
            )

        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("reject non-public endpoint properties") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class Books : Api() {
                    private val hidden by get(root / "hidden")
                    internal val local by get(root / "local")
                    protected val inherited by get(root / "inherited")
                }
                """.trimIndent()
            )

        compilation.exitCode shouldBe ExitCode.COMPILATION_ERROR
        compilation.messages shouldContain
            "tapik endpoint property 'example.Books.hidden' must be public for generated targets"
        compilation.messages shouldContain
            "tapik endpoint property 'example.Books.local' must be public for generated targets"
        compilation.messages shouldContain
            "tapik endpoint property 'example.Books.inherited' must be public for generated targets"
    }

    test("reject inherited non-public endpoint properties") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                abstract class LibraryApi : Api() {
                    protected val health by get(root / "health")
                }

                class Books : LibraryApi()
                """.trimIndent()
            )

        compilation.exitCode shouldBe ExitCode.COMPILATION_ERROR
        compilation.messages shouldContain
            "tapik endpoint property 'example.LibraryApi.health' must be public for generated targets"
    }

    test("let Kotlin reject inaccessible types in public endpoint signatures") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                private data class BookId(val value: String)
                private val bookId = format.string.transform(::BookId, BookId::value)

                class Books : Api() {
                    val get by get(root / path("bookId", bookId))
                }
                """.trimIndent()
            )

        compilation.exitCode shouldBe ExitCode.COMPILATION_ERROR
        compilation.messages shouldContain "exposes"
        compilation.messages shouldContain "BookId"
    }
})
