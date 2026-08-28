package dev.akif.tapik.target.spring.webmvc

import dev.akif.tapik.*
import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.common.plugin.ArtifactKind
import dev.akif.tapik.common.plugin.GenerationRequest
import dev.akif.tapik.common.plugin.targetConfigurationOf
import io.kotest.assertions.withClue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlin.cli.common.ExitCode

class WebMvcTargetSpec : FunSpec({
    test("separate public handlers from automatically registered Spring adapters") {
        val result =
            WebMvcTarget.generate(
                GenerationRequest(
                    apis = listOf(Books),
                    configuration = targetConfigurationOf("packageName" to "dev.akif.tapik.generated")
                )
            )

        result.artifacts.map { artifact -> artifact.relativePath to artifact.kind } shouldContainExactly
            listOf(
                "dev/akif/tapik/generated/BooksServer.kt" to ArtifactKind.SOURCE,
                "META-INF/tapik/spring/webmvc/dev.akif.tapik.generated.BooksGeneratedController.properties" to
                    ArtifactKind.RESOURCE
            )
        val source = result.artifacts.single { artifact -> artifact.kind == ArtifactKind.SOURCE }.content
        val handler =
            source
                .substringAfter("public interface BooksServer")
                .substringBefore("\n}\n\n@RestController")
        handler shouldNotContain "org.springframework"
        handler shouldNotContain "Http("
        source shouldContain "@RestController"
        source shouldContain "internal class BooksGeneratedController("
        source shouldContain "private val handler: BooksServer"
        source shouldContain "public fun list("
        source shouldNotContain "public fun listHttp("
        result.artifacts.single { artifact -> artifact.kind == ArtifactKind.RESOURCE }.content shouldBe
            "handler=dev.akif.tapik.generated.BooksServer\n" +
            "adapter=dev.akif.tapik.generated.BooksGeneratedController\n"
    }

    test("configure generated type suffixes independently") {
        val result =
            WebMvcTarget.generate(
                GenerationRequest(
                    apis = listOf(Books),
                    configuration =
                        targetConfigurationOf(
                            "serverSuffix" to "Contract",
                            "controllerSuffix" to "SpringController"
                        )
                )
            )

        result.artifacts.map { artifact -> artifact.relativePath } shouldContainExactly
            listOf(
                "dev/akif/tapik/generated/BooksContract.kt",
                "META-INF/tapik/spring/webmvc/dev.akif.tapik.generated.BooksSpringController.properties"
            )
        result.artifacts.single { artifact -> artifact.kind == ArtifactKind.SOURCE }.content.run {
            shouldContain("public interface BooksContract")
            shouldContain("internal class BooksSpringController(")
        }
    }

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

        result.artifacts.single { artifact -> artifact.kind == ArtifactKind.SOURCE }.run {
            relativePath shouldBe "dev/akif/tapik/generated/BooksServer.kt"
            mediaType shouldBe "text/x-kotlin"
            kind shouldBe ArtifactKind.SOURCE
            val compilation = compileKotlin(content)
            withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
            content shouldBe expected
            content shouldContain
                "@RequestParam queryParameters: MultiValueMap<String, String>"
            content shouldContain "val authorIdRaw = queryParameters[\"authorId\"]"
            content shouldNotContain
                "@RequestParam(name = \"authorId\""
        }
    }

    test("preserve every request and response body representation") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(BodyAlternatives)))
                .artifacts
                .single { artifact -> artifact.kind == ArtifactKind.SOURCE }
                .content

        source shouldContain "consumes = [\"application/json\", \"application/xml\"]"
        source shouldContain
            "matchesRequestMediaType(contentType, bodyAlternativesApi.echo.input.bodies._1.mediaType)"
        source shouldContain
            "matchesRequestMediaType(contentType, bodyAlternativesApi.echo.input.bodies._2.mediaType)"
        source shouldContain
            "selectResponseMediaType(accept, listOf(bodyAlternativesApi.echo.outputs._1.bodies._1.mediaType, bodyAlternativesApi.echo.outputs._1.bodies._2.mediaType))"
        source shouldContain
            "@RequestBody(required = false) bodyBytes: ByteArray? = null"
        source shouldContain "public fun optionalEcho("
        source shouldContain "body: String? = null"

        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("negotiate the body of the selected output") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(StatusBodyAlternatives)))
                .artifacts
                .single { artifact -> artifact.kind == ArtifactKind.SOURCE }
                .content

        source shouldContain
            "selectResponseMediaType(accept, listOf(statusBodyAlternativesApi.find.outputs._1.bodies._1.mediaType))"
        source shouldContain
            "selectResponseMediaType(accept, listOf(statusBodyAlternativesApi.find.outputs._2.bodies._1.mediaType))"
        source shouldContain "@GetMapping(path = [\"/optional\"])"

        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("apply endpoint defaults when response headers are omitted") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(DefaultResponseHeaders)))
                .artifacts
                .single { artifact -> artifact.kind == ArtifactKind.SOURCE }
                .content

        source shouldContain "public val retryAfter: Int? = null"
        source shouldContain
            "response.retryAfter ?: defaultResponseHeadersApi.poll.outputs._1.headers._1.presence.value"

        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("reject response headers owned by Spring WebMVC") {
        listOf(
            BodyContentTypeResponse to "Content-Type, which is derived from its selected body representation",
            ContentLengthResponse to "Content-Length, which is managed by Spring WebMVC"
        ).forEach { (api, expected) ->
            val failure = shouldThrow<IllegalArgumentException> {
                WebMvcTarget.generate(GenerationRequest(apis = listOf(api)))
            }

            requireNotNull(failure.message) shouldContain expected
        }
    }

    test("preserve Content-Type on bodyless responses") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(BodylessContentTypeResponse)))
                .artifacts
                .single { artifact -> artifact.kind == ArtifactKind.SOURCE }
                .content

        source shouldContain "put(\"Content-Type\", listOf("
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("generate content equality for aliased ByteArray response fields") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(BinaryResponses)))
                .artifacts
                .single { artifact -> artifact.kind == ArtifactKind.SOURCE }
                .content

        source shouldContain "import dev.akif.tapik.target.spring.webmvc.BinaryContent"
        source shouldContain "body: BinaryContent"
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

    test("disambiguate handlers mappings responses and endpoint property names") {
        val source =
            WebMvcTarget.generate(GenerationRequest(apis = listOf(WebMvcNamingCollisions)))
                .artifacts
                .single { artifact -> artifact.kind == ArtifactKind.SOURCE }
                .content

        source shouldContain "webMvcNamingCollisionsApi.`find-book`"
        source shouldContain "public fun findBook("
        source shouldContain "public fun findBook2(): FindBookResponse2"
        source shouldContain "public fun findBookHttp(): FindBookHttpResponse"
        source shouldContain "public fun decodeRequest(): DecodeRequestResponse"
        source shouldContain "public fun decodeRequest("
        source shouldNotContain "public fun decodeRequest2("
        source shouldNotContain "private fun <Value : Any> decodeRequest("
        source shouldNotContain "public fun findBookHttp2("
        val compilation = compileKotlin(source)
        withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
    }

    test("disambiguate generated types and artifact paths for equal API simple names") {
        val result =
            WebMvcTarget.generate(
                GenerationRequest(
                    apis = listOf(WebMvcNamespace1.Catalog(), WebMvcNamespace2.Catalog())
                )
            )

        val sources = result.artifacts.filter { artifact -> artifact.kind == ArtifactKind.SOURCE }
        sources.map { artifact -> artifact.relativePath } shouldContainExactly
            listOf(
                "dev/akif/tapik/generated/CatalogServer.kt",
                "dev/akif/tapik/generated/CatalogServer2.kt"
            )
        sources.map { artifact -> artifact.content.substringBefore(" {").substringAfterLast(' ') } shouldContainExactly
            listOf("CatalogServer", "CatalogServer2")
        sources.forEach { artifact ->
            val compilation = compileKotlin(artifact.content)
            withClue(compilation.messages) { compilation.exitCode shouldBe ExitCode.OK }
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

public object BodyContentTypeResponse : Api() {
    private val contentType = header.string("cOnTeNt-TyPe").fixed("application/octet-stream")
    private val bytes: ByteArrayFormat<ByteArray> =
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
            .output(Status.Ok with body(MediaType.OctetStream, bytes) with headersOf(contentType))
}

public object ContentLengthResponse : Api() {
    private val contentLength = header.long("content-length").fixed(0)

    public val empty by
        get(root / "empty")
            .output(Status.NoContent with noBody with headersOf(contentLength))
}

public object BodylessContentTypeResponse : Api() {
    private val contentType = header.string("Content-Type").fixed("application/json")

    public val metadata by
        head(root / "metadata")
            .output(Status.Ok with noBody with headersOf(contentType))
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

public object WebMvcNamingCollisions : Api() {
    public val `find-book` by get(root / "hyphen" + query.string("q"))
    public val findBook by get(root / "camel")
    public val findBookHttp by get(root / "http")
    public val decodeRequest by get(root / "decode")
}

public class WebMvcNamespace1 {
    public class Catalog : Api("WebMvcCatalog1")
}

public class WebMvcNamespace2 {
    public class Catalog : Api("WebMvcCatalog2")
}
