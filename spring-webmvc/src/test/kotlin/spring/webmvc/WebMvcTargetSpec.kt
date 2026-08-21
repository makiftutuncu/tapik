package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.*
import dev.akif.tapik.fixtures.library.Books
import dev.akif.tapik.plugin.core.ArtifactKind
import dev.akif.tapik.plugin.core.GenerationRequest
import dev.akif.tapik.plugin.core.targetConfigurationOf
import io.kotest.assertions.withClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.cli.common.ExitCode

class WebMvcTargetSpec : FunSpec({
    test("generate a complete server from the Books API") {
        val expected =
            requireNotNull(WebMvcTargetSpec::class.java.getResource("/spring-webmvc/BooksServer.kt"))
                .readText()
                .trimEnd()

        val result =
            WebMvcTarget.generate(
                GenerationRequest(
                    apis = listOf(Books),
                    configuration = targetConfigurationOf("packageName" to "dev.akif.tapik.generated")
                )
            )

        result.artifacts.single().run {
            relativePath shouldBe "dev/akif/tapik/generated/BooksServer.kt"
            mediaType shouldBe "text/x-kotlin"
            kind shouldBe ArtifactKind.SOURCE
            val compilation = compileKotlin(content)
            withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
            content shouldBe expected
        }
    }

    test("preserve every request and response body representation") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(BodyAlternatives)))
                .artifacts
                .single()
                .content

        source shouldContain "consumes = [\"application/json\", \"application/xml\"]"
        source shouldContain
            "mediaTypeCompatible(contentType, bodyAlternativesApi.echo.input.bodies._1.mediaType)"
        source shouldContain
            "mediaTypeCompatible(contentType, bodyAlternativesApi.echo.input.bodies._2.mediaType)"
        source shouldContain
            "selectResponseMediaType(accept, kotlin.collections.listOf(bodyAlternativesApi.echo.outputs._1.bodies._1.mediaType, bodyAlternativesApi.echo.outputs._1.bodies._2.mediaType))"
        source shouldContain
            "@org.springframework.web.bind.annotation.RequestBody(required = false) bodyBytes: kotlin.ByteArray? = null"
        source shouldContain "public fun optionalEcho("
        source shouldContain "body: kotlin.String? = null"

        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("negotiate the body of the selected output") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(StatusBodyAlternatives)))
                .artifacts
                .single()
                .content

        source shouldContain
            "selectResponseMediaType(accept, kotlin.collections.listOf(statusBodyAlternativesApi.find.outputs._1.bodies._1.mediaType))"
        source shouldContain
            "selectResponseMediaType(accept, kotlin.collections.listOf(statusBodyAlternativesApi.find.outputs._2.bodies._1.mediaType))"
        source shouldContain "@org.springframework.web.bind.annotation.GetMapping(path = [\"/optional\"])"

        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("apply endpoint defaults when response headers are omitted") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(DefaultResponseHeaders)))
                .artifacts
                .single()
                .content

        source shouldContain "public val retryAfter: kotlin.Int? = null"
        source shouldContain
            "response.retryAfter ?: defaultResponseHeadersApi.poll.outputs._1.headers._1.presence.value"

        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("generate content equality for aliased ByteArray response fields") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(BinaryResponses)))
                .artifacts
                .single()
                .content

        source shouldContain "body: dev.akif.tapik.spring.webmvc.BinaryContent"
        source shouldContain "body.contentEquals(other.body)"
        source shouldContain "body.contentHashCode()"
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("reject methods Spring WebMVC cannot map") {
        listOf(ConnectEndpoints, QueryEndpoints).forEach { api ->
            val failure = shouldThrow<IllegalArgumentException> {
                WebMvcTarget.generate(GenerationRequest(apis = listOf(api)))
            }

            requireNotNull(failure.message) shouldContain "Spring WebMVC cannot map"
        }
    }
})

public typealias BinaryContent = ByteArray

public object BodyAlternatives : Api() {
    private val bytes: ByteArrayFormat<String> =
        Format(
            codec =
                Codec(
                    decoder = Decoder { value -> DecodeResult.Success(value.decodeToString()) },
                    encoder = Encoder(String::encodeToByteArray)
                ),
            schema = ScalarSchema(SchemaType.STRING)
        )
    private val json = body(MediaType.Json, bytes)
    private val xml = body(MediaType.Xml, bytes)

    public val echo by
        post(root / "echo")
            .input(bodiesOf(json, xml))
            .output(Status.Ok with bodiesOf(json, xml))

    public val optionalEcho by
        post(root / "optional-echo")
            .input(bodiesOf(json, noBody))
            .output(Status.Ok with json)
}

public object DefaultResponseHeaders : Api() {
    private val retryAfter = header.int("Retry-After").optional(default = 30)

    public val poll by
        get(root / "poll")
            .output(Status.Ok with noBody with headersOf(retryAfter))
}

public object BinaryResponses : Api() {
    private val bytes: ByteArrayFormat<BinaryContent> =
        Format(
            codec =
                Codec(
                    decoder = Decoder { value -> DecodeResult.Success(value) },
                    encoder = Encoder { value -> value }
                ),
            schema = ScalarSchema(SchemaType.STRING, format = "binary")
        )

    public val download by
        get(root / "download")
            .output(Status.Ok with body(MediaType.OctetStream, bytes))
}

public object StatusBodyAlternatives : Api() {
    private val bytes: ByteArrayFormat<String> =
        Format(
            codec =
                Codec(
                    decoder = Decoder { value -> DecodeResult.Success(value.decodeToString()) },
                    encoder = Encoder(String::encodeToByteArray)
                ),
            schema = ScalarSchema(SchemaType.STRING)
        )
    private val json = body(MediaType.Json, bytes)
    private val xml = body(MediaType.Xml, bytes)

    public val find by
        get(root / "find")
            .output(Status.Ok with json)
            .output(Status.NotFound with xml)

    public val optional by
        get(root / "optional")
            .output(Status.Ok with bodiesOf(json, noBody))
}

public object ConnectEndpoints : Api() {
    public val tunnel by connect(root / "tunnel")
}

public object QueryEndpoints : Api() {
    public val inspect by query(root / "inspect")
}
