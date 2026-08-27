package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.*
import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.targetConfigurationOf
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContainExactly
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

    test("generate content equality for aliased ByteArray response fields") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(BinaryDownloads)))
                .artifacts
                .single()
                .content

        source shouldContain "body: dev.akif.tapik.target.spring.restclient.BinaryContent"
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

    test("generate uniform response body conformance checks") {
        val source =
            RestClientTarget.generate(GenerationRequest(apis = listOf(ResponseConformance)))
                .artifacts
                .single()
                .content

        source shouldContain "selectResponseBodyMediaType("
        source shouldContain "offered = kotlin.collections.emptyList()"
        source shouldContain "offered = kotlin.collections.listOf("
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
        source shouldContain "public fun decodeBody2(): DecodeBodyResponse"
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

public object RestClientNamingCollisions : Api() {
    public val `find-book` by get(root / "hyphen")
    public val findBook by get(root / "camel")
    public val decodeBody by get(root / "decode")
}

public class RestClientNamespace1 {
    public class Catalog : Api("RestClientCatalog1")
}

public class RestClientNamespace2 {
    public class Catalog : Api("RestClientCatalog2")
}
