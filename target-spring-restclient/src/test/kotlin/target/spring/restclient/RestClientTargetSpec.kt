package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.*
import dev.akif.tapik.test.fixtures.library.Authors
import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.test.fixtures.library.Rentals
import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.targetConfigurationOf
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlin.cli.common.ExitCode

class RestClientTargetSpec : FunSpec({
    test("generate every shared library API") {
        val result =
            RestClientTarget.generate(
                GenerationRequest(
                    apis = listOf(Books, Authors, Rentals),
                    configuration = targetConfigurationOf("packageName" to "dev.akif.tapik.generated")
                )
            )

        result.artifacts.map { it.relativePath } shouldContainExactly
            listOf(
                "dev/akif/tapik/generated/BooksClient.kt",
                "dev/akif/tapik/generated/AuthorsClient.kt",
                "dev/akif/tapik/generated/RentalsClient.kt"
            )
        result.artifacts.forEach { artifact ->
            val compilation = compileKotlin(artifact.content)
            withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        }
    }

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

    test("generate content equality for aliased ByteArray response fields") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(BinaryDownloads)))
                .artifacts
                .single()
                .content

        source shouldContain "import dev.akif.tapik.target.spring.restclient.BinaryContent"
        source shouldContain "body: BinaryContent"
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
        source shouldContain "requireFixedResponseHeader("
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("generate uniform response body conformance checks") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(ResponseConformance)))
                .artifacts
                .single()
                .content

        source shouldContain "selectResponseBodyMediaType("
        source shouldContain "offered = emptyList()"
        source shouldContain "offered = listOf("
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("make a request body representation explicit when an input has multiple media types") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(RequestBodyAlternatives)))
                .artifacts
                .single()
                .content

        source shouldContain "public sealed interface SendRequestBody"
        source shouldContain "public data class Json("
        source shouldContain "public data class Json2("
        source shouldContain "public data class Xml("
        source shouldContain "public val body: String"
        source shouldContain "body: SendRequestBody? = null"
        source shouldContain "is SendRequestBody.Json -> RestClientRequestBody("
        source shouldContain "requestBodyAlternativesApi.send.input.bodies._1.mediaType"
        source shouldContain "requestBodyAlternativesApi.send.input.bodies._1.format.encode(requestBody.body)"
        source shouldContain "is SendRequestBody.Json2 -> RestClientRequestBody("
        source shouldContain "requestBodyAlternativesApi.send.input.bodies._2.mediaType"
        source shouldContain "is SendRequestBody.Xml -> RestClientRequestBody("
        source shouldContain "requestBodyAlternativesApi.send.input.bodies._3.mediaType"
        source shouldContain "public sealed interface SendRequiredRequestBody"
        source shouldContain "body: SendRequiredRequestBody"
        source shouldNotContain "body: SendRequiredRequestBody? = null"
        source shouldContain "when (body) {"
        source shouldContain "requestBodyAlternativesApi.sendRequired.input.bodies._1.format.encode(body.body)"
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("generate responses for every status matcher") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(RestClientStatusMatchers)))
                .artifacts
                .single()
                .content

        source shouldContain "public data class OkOrCreated("
        source shouldContain "public data class Status400To499("
        source shouldContain "public data class SuccessfulExtensionStatus("
        source shouldContain "public val status: Status"
        source shouldContain "OkOrCreated(response.status)"
        source shouldContain "Status400To499(response.status)"
        source shouldContain "SuccessfulExtensionStatus(response.status)"
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("append a remaining path as individually encoded segments") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(RestClientRemainingPaths)))
                .artifacts
                .single()
                .content

        source shouldContain "path: List<String>"
        source shouldContain ".path(\"/files/{ownerId}\")"
        source shouldContain
            ".pathSegment(*restClientRemainingPathsApi.download.uri.paths._2.format.encode(path).split('/').toTypedArray())"
        source shouldNotContain "{*path}"
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("disambiguate generated declarations and preserve endpoint property names") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(RestClientNamingCollisions)))
                .artifacts
                .single()
                .content

        source shouldContain "restClientNamingCollisionsApi.`find-book`"
        source shouldContain "public fun findBook(): FindBookResponse"
        source shouldContain "public fun findBook2(): FindBookResponse2"
        source shouldContain "public fun decodeResponseBody(): DecodeResponseBodyResponse"
        source shouldNotContain "public fun decodeResponseBody2("
        source shouldNotContain "private fun <Value : Any> decodeResponseBody("
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("disambiguate generated types and artifact paths for equal API simple names") {
        val result =
            RestClientTarget.generate(
                GenerationRequest(
                    apis = listOf(RestClientNamespace1.Catalog(), RestClientNamespace2.Catalog())
                )
            )

        result.artifacts.map { artifact -> artifact.relativePath } shouldContainExactly
            listOf(
                "dev/akif/tapik/generated/CatalogClient.kt",
                "dev/akif/tapik/generated/CatalogClient2.kt"
            )
        result.artifacts.map { artifact -> artifact.content.substringBefore(" {").substringAfterLast(' ') } shouldContainExactly
            listOf("CatalogClient", "CatalogClient2")
        result.artifacts.forEach { artifact ->
            val compilation = compileKotlin(artifact.content)
            withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
        }
    }
})

public typealias BinaryContent = ByteArray

public object FixedResponses : Api() {
    private val apiVersion = header.string("X-API-Version").fixed("1")

    public val check by
        get(root / "check")
            .output(Status.Ok with noBody with headersOf(apiVersion))
}

public object BinaryDownloads : Api() {
    private val binaryFormat: ByteArrayFormat<BinaryContent> =
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

public object ResponseConformance : Api() {
    private val stringFormat: ByteArrayFormat<String> =
        Format(
            codec =
                Codec(
                    decoder = Decoder { bytes -> DecodeResult.Success(bytes.decodeToString()) },
                    encoder = Encoder(String::encodeToByteArray)
                ),
            schema = ScalarSchema(SchemaType.STRING)
        )
    private val json = body(MediaType.Json, stringFormat)
    private val xml = body(MediaType.Xml, stringFormat)

    public val optional by
        get(root / "optional")
            .output(Status.Ok with bodiesOf(json, xml, noBody))

    public val empty by
        get(root / "empty")
            .output(Status.NoContent with noBody)
}

public object RestClientStatusMatchers : Api() {
    public val selected by
        get(root / "selected")
            .output(statusesOf(Status.Ok, Status.Created) with noBody)

    public val clientErrors by
        get(root / "client-errors")
            .output(statusesIn(400..499) with noBody)

    public val extensionSuccess by
        get(root / "extension-success")
            .output(statusMatching("successful extension status") { it.code in 290..299 } with noBody)
}

public object RestClientRemainingPaths : Api() {
    public val download by
        get(root / "files" / path.uuid("ownerId") / path.remaining("path"))
}

public object RequestBodyAlternatives : Api() {
    private val stringFormat: ByteArrayFormat<String> =
        Format(
            codec =
                Codec(
                    decoder = Decoder { bytes -> DecodeResult.Success(bytes.decodeToString()) },
                    encoder = Encoder(String::encodeToByteArray)
                ),
            schema = ScalarSchema(SchemaType.STRING)
        )

    public val send by
        post(root / "send")
            .input(
                bodiesOf(
                    body(MediaType.Json, stringFormat),
                    body(MediaType("text/json"), stringFormat),
                    body(MediaType.Xml, stringFormat),
                    noBody
                )
            )

    public val sendRequired by
        post(root / "send-required")
            .input(
                bodiesOf(
                    body(MediaType.Json, stringFormat),
                    body(MediaType.Xml, stringFormat)
                )
            )
}

public object RestClientNamingCollisions : Api() {
    private val stringBody: ByteArrayFormat<String> =
        Format(
            codec =
                Codec(
                    decoder = Decoder { value -> DecodeResult.Success(value.decodeToString()) },
                    encoder = Encoder(String::encodeToByteArray)
                ),
            schema = ScalarSchema(SchemaType.STRING)
        )

    public val `find-book` by get(root / "hyphen")
    public val findBook by get(root / "camel")
    public val decodeResponseBody by
        get(root / "decode")
            .output(Status.Ok with body(MediaType.Json, stringBody))
}

public class RestClientNamespace1 {
    public class Catalog : Api("RestClientCatalog1")
}

public class RestClientNamespace2 {
    public class Catalog : Api("RestClientCatalog2")
}
