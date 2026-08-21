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

    test("not expose raw non-empty tuple construction") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = Tuple1<Any, String>("value")
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not expose generic tuple appending") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = Tuple0 and "value"
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

    test("compile a delegated endpoint inside an API") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val list by get(root / "books")
            }

            val id = Books.list.id
            """
        ) shouldBe ExitCode.OK
    }

    test("not expose an id on a draft endpoint") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val draft = get(root / "books")
                val invalid = draft.id
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not accept a draft endpoint where a ready endpoint is required") {
        compile(
            """
            import dev.akif.tapik.*

            fun consume(endpoint: Endpoint<*, *, *, *, *, Ready>) = Unit

            object Books : Api("Books") {
                val invalid = consume(get(root / "books"))
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not delegate an endpoint outside an API") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val draft = get(root / "books")
            }

            val invalid by Books.draft
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not bulk initialize headers after adding a header") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val invalid by post(root / "books")
                    .header(header.string("X-One"))
                    .headers(headersOf(header.string("X-Two")))
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not bulk initialize an empty header tuple") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val invalid by get(root / "books").headers(noHeaders)
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not modify headers on a ready endpoint") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val create by post(root / "books")
                val invalid = create.header(header.string("X-Request-Id"))
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not compile more than eight endpoint headers") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val invalid by post(root / "books")
                    .header(header.string("one"))
                    .header(header.string("two"))
                    .header(header.string("three"))
                    .header(header.string("four"))
                    .header(header.string("five"))
                    .header(header.string("six"))
                    .header(header.string("seven"))
                    .header(header.string("eight"))
                    .header(header.string("nine"))
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not combine bodies with different model types") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            data class Author(val value: String)

            fun <Value : Any> valueBody(): Body<Value> = error("Only compiled")

            val invalid = bodiesOf(valueBody<Book>(), valueBody<Author>())
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not construct heterogeneous bodies through a raw tuple") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            data class Author(val value: String)

            fun bookBody(): Body<Book> = error("Only compiled")
            fun authorBody(): Body<Author> = error("Only compiled")

            val invalid: Bodies2<Body<Book>, Body<Author>> = Tuple2(bookBody(), authorBody())
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not copy a body tuple around its validation boundary") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            fun bookBody(mediaType: MediaType): Body<Book> = error("Only compiled")

            val bodies = bodiesOf(bookBody(MediaType.Json), bookBody(MediaType.Xml))
            val invalid = bodies.copy(_2 = bodies._1)
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not accept noBody before a real body") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            fun bookBody(): Body<Book> = error("Only compiled")

            val invalid = bodiesOf(noBody, bookBody())
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not accept noBody more than once") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            fun bookBody(): Body<Book> = error("Only compiled")

            val invalid = bodiesOf(bookBody(), noBody, noBody)
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not attach input twice") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            fun bookBody(): Body<Book> = error("Only compiled")

            object Books : Api("Books") {
                val invalid by post(root / "books").input(bookBody()).input(bookBody())
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not attach an empty body tuple as input") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val invalid by post(root / "books").input(noHeaders)
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not attach input to a ready endpoint") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            fun bookBody(): Body<Book> = error("Only compiled")

            object Books : Api("Books") {
                val create by post(root / "books")
                val invalid = create.input(bookBody())
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("compile outputs in status body header order") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            fun bookBody(): Body<Book> = error("Only compiled")

            object Books : Api("Books") {
                val get by get(root / "books")
                    .output(Status.Ok with bodiesOf(bookBody(), noBody) with headersOf(header.string("X-Source")))
            }
            """
        ) shouldBe ExitCode.OK
    }

    test("not attach headers before an output body") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = Status.Ok with headersOf(header.string("X-Source"))
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not accept an empty tuple as output bodies") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = Status.Ok with noHeaders
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not attach an empty output header tuple") {
        compile(
            """
            import dev.akif.tapik.*

            val invalid = Status.Ok with noBody with noHeaders
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not expose direct output construction") {
        compile(
            """
            import dev.akif.tapik.*

            val output = Status.Ok with noBody
            val invalid = Output(output.matcher, output.bodies, output.headers)
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not attach a second body after output headers") {
        compile(
            """
            import dev.akif.tapik.*

            data class Book(val value: String)
            fun bookBody(): Body<Book> = error("Only compiled")

            val invalid = Status.Ok with bookBody() with headersOf(header.string("X-Source")) with bookBody()
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not attach an output to a ready endpoint") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val list by get(root / "books")
                val invalid = list.output(Status.Ok with noBody)
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not compile more than eight outputs") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val invalid by get(root / "books")
                    .output(Status(200) with noBody)
                    .output(Status(201) with noBody)
                    .output(Status(202) with noBody)
                    .output(Status(203) with noBody)
                    .output(Status(204) with noBody)
                    .output(Status(205) with noBody)
                    .output(Status(206) with noBody)
                    .output(Status(207) with noBody)
                    .output(Status(208) with noBody)
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not document a ready endpoint") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val list by get(root / "books")
                val invalid = list.summary("List books")
            }
            """
        ) shouldBe ExitCode.COMPILATION_ERROR
    }

    test("not tag a ready endpoint") {
        compile(
            """
            import dev.akif.tapik.*

            object Books : Api("Books") {
                val list by get(root / "books")
                val invalid = list.tag("books")
            }
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
