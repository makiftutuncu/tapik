package dev.akif.tapik.common.plugin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class KotlinImportsSpec : FunSpec({
    test("recognize directives and references only in lexical code regions") {
        val source = listOf(
            "@file:Suppress(",
            "    \"\"\"",
            "package fake.literal",
            "import fake.literal.Type",
            "alpha.literal.Book",
            "\"\"\"",
            ")",
            "/*",
            "package fake.comment",
            "import fake.comment.Type",
            "beta.comment.Book",
            "/* import fake.nested.Type */",
            "*/",
            "package generated",
            "",
            "import java.time.Instant",
            "import java.time.Instant",
            "",
            "/**",
            " * import fake.kdoc.Type",
            " * gamma.kdoc.Book",
            " */",
            "public fun load(value: java.time.Instant): dev.akif.tapik.DecodeResult.Success {",
            "    val quoted = \"delta.quoted.Book and import fake.quoted.Type\"",
            "    val character = 'x'",
            "    // import fake.line.Type",
            "    return dev.akif.tapik.DecodeResult.Success(value)",
            "}"
        ).joinToString("\n")

        source.optimizeKotlinImports("generated") shouldBe listOf(
            "@file:Suppress(",
            "    \"\"\"",
            "package fake.literal",
            "import fake.literal.Type",
            "alpha.literal.Book",
            "\"\"\"",
            ")",
            "/*",
            "package fake.comment",
            "import fake.comment.Type",
            "beta.comment.Book",
            "/* import fake.nested.Type */",
            "*/",
            "package generated",
            "",
            "import dev.akif.tapik.DecodeResult",
            "import java.time.Instant",
            "",
            "/**",
            " * import fake.kdoc.Type",
            " * gamma.kdoc.Book",
            " */",
            "public fun load(value: Instant): DecodeResult.Success {",
            "    val quoted = \"delta.quoted.Book and import fake.quoted.Type\"",
            "    val character = 'x'",
            "    // import fake.line.Type",
            "    return DecodeResult.Success(value)",
            "}"
        ).joinToString("\n")
    }

    test("preserve uppercase API namespaces without allocating imports for the package") {
        val source = """
            package generated.example.Books

            public interface BooksServer {
                public val api: example.Books
            }
        """.trimIndent()

        source.optimizeKotlinImports("generated.example.Books") shouldBe """
            package generated.example.Books

            import example.Books

            public interface BooksServer {
                public val api: Books
            }
        """.trimIndent()
    }

    test("optimize unambiguous imports without changing literals or comments") {
        val source =
            """
            package generated

            import dev.akif.tapik.common.spring.toSpringMediaType
            import java.time.Instant as Moment

            @org.springframework.web.bind.annotation.RestController
            public class UUID {
                public fun load(
                    id: java.util.UUID,
                    at: java.time.Instant,
                    first: alpha.model.Book,
                    second: beta.model.Book,
                    bytes: kotlin.ByteArray,
                    local: generated.LocalType
                ): dev.akif.tapik.DecodeResult.Success {
                    val literal = "java.time.Instant"
                    // org.springframework.http.MediaType
                    return dev.akif.tapik.DecodeResult.Success(first)
                }
            }
            """.trimIndent()

        source.optimizeKotlinImports("generated") shouldBe
            """
            package generated

            import dev.akif.tapik.DecodeResult
            import dev.akif.tapik.common.spring.toSpringMediaType
            import java.time.Instant as Moment
            import org.springframework.web.bind.annotation.RestController

            @RestController
            public class UUID {
                public fun load(
                    id: java.util.UUID,
                    at: Moment,
                    first: alpha.model.Book,
                    second: beta.model.Book,
                    bytes: ByteArray,
                    local: LocalType
                ): DecodeResult.Success {
                    val literal = "java.time.Instant"
                    // org.springframework.http.MediaType
                    return DecodeResult.Success(first)
                }
            }
            """.trimIndent()
    }
})
