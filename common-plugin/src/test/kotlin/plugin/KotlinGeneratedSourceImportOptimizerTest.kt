package dev.akif.tapik.plugin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KotlinGeneratedSourceImportOptimizerTest {
    @Test
    fun `optimizeContent imports and simplifies fully qualified references`() {
        val input =
            """
            package dev.akif.tapik.clients

            interface SampleClient {
                @org.springframework.web.bind.annotation.RequestMapping(
                    method = [org.springframework.web.bind.annotation.RequestMethod.HEAD]
                )
                fun user(): dev.akif.tapik.Method =
                    dev.akif.tapik.Method.GET
            }
            """.trimIndent()

        val output = KotlinGeneratedSourceImportOptimizer.optimizeContent(input)

        assertTrue(output.contains("import dev.akif.tapik.Method"))
        assertTrue(output.contains("import dev.akif.tapik.Method.GET"))
        assertTrue(output.contains("import org.springframework.web.bind.annotation.RequestMapping"))
        assertTrue(output.contains("import org.springframework.web.bind.annotation.RequestMethod.HEAD"))
        assertTrue(output.contains("@RequestMapping"))
        assertTrue(output.contains("method = [HEAD]"))
        assertTrue(output.contains("fun user(): Method ="))
        assertTrue(output.contains("GET"))
        assertTrue(!output.contains("fun user(): dev.akif.tapik.Method"))
        assertTrue(!output.contains("@org.springframework.web.bind.annotation.RequestMapping"))
    }

    @Test
    fun `optimizeContent keeps fully qualified names when simple names collide`() {
        val input =
            """
            package dev.akif.tapik.clients

            interface SampleClient {
                fun user(
                    createdAt: java.util.Date,
                    updatedAt: java.sql.Date
                ): kotlin.Unit
            }
            """.trimIndent()

        val output = KotlinGeneratedSourceImportOptimizer.optimizeContent(input)

        assertTrue(output.contains("createdAt: java.util.Date"))
        assertTrue(output.contains("updatedAt: java.sql.Date"))
        assertTrue(!output.contains("import java.util.Date"))
        assertTrue(!output.contains("import java.sql.Date"))
        assertTrue(output.contains("): Unit"))
    }

    @Test
    fun `optimizeContent keeps used existing imports`() {
        val input =
            """
            package dev.akif.tapik.clients

            import dev.akif.tapik.encodeInputHeaders

            interface SampleClient {
                fun user() {
                    val headers = encodeInputHeaders()
                    kotlin.collections.emptyMap<kotlin.String, kotlin.String>()
                }
            }
            """.trimIndent()

        val output = KotlinGeneratedSourceImportOptimizer.optimizeContent(input)

        assertTrue(output.contains("import dev.akif.tapik.encodeInputHeaders"))
        assertTrue(output.contains("encodeInputHeaders()"))
        assertTrue(output.contains("emptyMap<String, String>()"))
        assertEquals(1, output.lineSequence().count { it.trim() == "import dev.akif.tapik.encodeInputHeaders" })
    }

    @Test
    fun `optimizeContent simplifies java net URI type`() {
        val input =
            """
            package dev.akif.tapik.clients

            interface SampleClient {
                fun user(uri: java.net.URI): kotlin.Unit
            }
            """.trimIndent()

        val output = KotlinGeneratedSourceImportOptimizer.optimizeContent(input)

        assertTrue(output.contains("import java.net.URI"))
        assertTrue(output.contains("fun user(uri: URI): Unit"))
        assertTrue(!output.contains("fun user(uri: java.net.URI)"))
    }

    @Test
    fun `optimizeContent does not import symbols from current package`() {
        val input =
            """
            package dev.akif.tapik.clients

            import dev.akif.tapik.clients.LocalType

            interface SampleClient {
                fun user(localType: dev.akif.tapik.clients.LocalType): kotlin.Unit
            }
            """.trimIndent()

        val output = KotlinGeneratedSourceImportOptimizer.optimizeContent(input)

        assertTrue(!output.contains("import dev.akif.tapik.clients.LocalType"))
        assertTrue(output.contains("fun user(localType: LocalType): Unit"))
    }

    @Test
    fun `optimizeContent imports owner and preserves member access for qualified object chains`() {
        val input =
            """
            package dev.akif.tapikdemo.api.generated

            interface UsersClient {
                fun create(
                    accept: dev.akif.tapik.MediaType =
                        dev.akif.tapikdemo.api.Users.create.input.headers.item1.asHeaderValues<dev.akif.tapik.MediaType>().getFirstValueOrFail()
                ): kotlin.Unit
            }
            """.trimIndent()

        val output = KotlinGeneratedSourceImportOptimizer.optimizeContent(input)

        assertTrue(output.contains("import dev.akif.tapik.MediaType"))
        assertTrue(output.contains("import dev.akif.tapikdemo.api.Users"))
        assertTrue(output.contains("accept: MediaType ="))
        assertTrue(output.contains("Users.create.input.headers.item1.asHeaderValues<MediaType>().getFirstValueOrFail()"))
        assertTrue(!output.contains("import dev.akif.tapikdemo.api.Users.create.input.headers.item1.asHeaderValues"))
    }
}
