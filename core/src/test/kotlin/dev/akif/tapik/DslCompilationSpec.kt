package dev.akif.tapik

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import java.nio.file.Files

class DslCompilationSpec : FunSpec({
    test("compile paths followed by queries") {
        compile(
            """
            import dev.akif.tapik.*

            val valid = root / "books" / path.uuid("bookId") + query.string("edition")
            """
        ) shouldBe ExitCode.OK
    }

    test("not compile a literal path after a query") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = (root + query.string("edition")) / "books"
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not compile a path variable after a query") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = (root + query.string("edition")) / path.uuid("bookId")
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not compile repetition twice") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = query.string("tag").repeated().repeated()
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not compile repetition after assigning a scalar default") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = query.string("tag").optional(default = "all").repeated()
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not compile an empty headers group") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = headersOf()
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not compile more than eight headers") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = headersOf(
                header.string("one"),
                header.string("two"),
                header.string("three"),
                header.string("four"),
                header.string("five"),
                header.string("six"),
                header.string("seven"),
                header.string("eight"),
                header.string("nine")
            )
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }
})

private fun compile(source: String): ExitCode {
    val directory = Files.createTempDirectory("tapik-compilation-").toFile().apply { deleteOnExit() }
    val sourceFile = directory.resolve("CompilationFixture.kt").apply {
        writeText(source.trimIndent())
        deleteOnExit()
    }
    val output = directory.resolve("output").apply {
        mkdirs()
        deleteOnExit()
    }
    val arguments =
        K2JVMCompilerArguments().apply {
            freeArgs = listOf(sourceFile.absolutePath)
            destination = output.absolutePath
            classpath =
                System.getProperty("surefire.test.class.path")
                    ?: System.getProperty("java.class.path")
            jvmTarget = "25"
            noStdlib = true
            noReflect = true
        }

    return K2JVMCompiler().exec(IgnoringMessages, Services.EMPTY, arguments)
}

private object IgnoringMessages : MessageCollector {
    override fun clear() = Unit

    override fun hasErrors(): Boolean = false

    override fun report(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?
    ) = Unit
}
