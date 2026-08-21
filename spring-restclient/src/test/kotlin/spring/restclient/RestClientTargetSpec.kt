package dev.akif.tapik.spring.restclient

import dev.akif.tapik.*
import dev.akif.tapik.fixtures.library.Books
import dev.akif.tapik.plugin.core.ArtifactKind
import dev.akif.tapik.plugin.core.GenerationRequest
import dev.akif.tapik.plugin.core.targetConfigurationOf
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.cli.common.ExitCode

class RestClientTargetSpec : FunSpec({
    test("generate a complete client from the Books API") {
        val expected =
            requireNotNull(RestClientTargetSpec::class.java.getResource("/spring-restclient/BooksClient.kt"))
                .readText()
                .trimEnd()

        val result =
            RestClientTarget.generate(
                GenerationRequest(
                    apis = listOf(Books),
                    configuration = targetConfigurationOf("packageName" to "dev.akif.tapik.generated")
                )
            )

        result.artifacts.single().run {
            relativePath shouldBe "dev/akif/tapik/generated/BooksClient.kt"
            mediaType shouldBe "text/x-kotlin"
            kind shouldBe ArtifactKind.SOURCE
            content shouldBe expected
            val compilation = compileKotlin(content)
            withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        }
    }

    test("generate content equality for ByteArray response fields") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(BinaryDownloads)))
                .artifacts
                .single()
                .content

        source shouldContain "body.contentEquals(other.body)"
        source shouldContain "body.contentHashCode()"
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("validate fixed response headers without exposing response fields") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(FixedResponses)))
                .artifacts
                .single()
                .content

        source shouldContain "public data object Ok : CheckResponse"
        source shouldContain "requireFixedHeader("
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }
})

public object FixedResponses : Api() {
    private val apiVersion = header.string("X-API-Version").fixed("1")

    public val check by
        get(root / "check")
            .output(Status.Ok with noBody with headersOf(apiVersion))
}

public object BinaryDownloads : Api() {
    private val binaryFormat: ByteArrayFormat<ByteArray> =
        Format(
            codec =
                Codec(
                    decoder = Decoder { bytes -> DecodeResult.Success(bytes) },
                    encoder = Encoder { bytes -> bytes }
                ),
            schema = ScalarSchema(SchemaType.STRING, format = "binary")
        )

    public val download by
        get(root / "download")
            .output(Status.Ok with body(MediaType.OctetStream, binaryFormat))
}
