package dev.akif.tapik.plugin.compiler

import dev.akif.tapik.ApiRegistry
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.cli.common.ExitCode
import java.net.URLClassLoader
import java.util.ServiceLoader

class ApiEligibilitySpec : FunSpec({
    test("instantiate top-level and statically nested API classes") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class Authors : Api()

                abstract class LibraryApi<Model> : Api()

                class Rentals : LibraryApi<String>()

                class Library {
                    class Books private constructor(id: String) : Api(id) {
                        constructor() : this("Books")
                    }
                }
                """.trimIndent()
            )

        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        URLClassLoader(
            arrayOf(compilation.outputDirectory.toUri().toURL()),
            ApiEligibilitySpec::class.java.classLoader
        ).use { classLoader ->
            val registry = ServiceLoader.load(ApiRegistry::class.java, classLoader).single()
            registry.apis.map { api -> api.id } shouldContainExactly listOf("Authors", "Rentals", "Books")
        }
    }

    test("instantiate API classes whose public constructor parameters all have defaults") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class Books(source: String = "catalog") : Api(source)
                """.trimIndent()
            )

        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        URLClassLoader(
            arrayOf(compilation.outputDirectory.toUri().toURL()),
            ApiEligibilitySpec::class.java.classLoader
        ).use { classLoader ->
            val registry = ServiceLoader.load(ApiRegistry::class.java, classLoader).single()
            registry.apis.single().id shouldBe "catalog"
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

    test("reject inner API classes") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class Library {
                    inner class Books : Api()
                }
                """.trimIndent()
            )

        compilation.exitCode shouldBe ExitCode.COMPILATION_ERROR
        compilation.messages shouldContain "tapik API class 'example.Library.Books' must not be inner"
    }

    test("reject generic API classes") {
        val compilation =
            compile(
                """
                package example

                import dev.akif.tapik.*

                class Books<Model> : Api()
                """.trimIndent()
            )

        compilation.exitCode shouldBe ExitCode.COMPILATION_ERROR
        compilation.messages shouldContain "tapik API class 'example.Books' must not declare type parameters"
    }
})
