package dev.akif.tapik.test.maven.integration

import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.test.maven.contract.Author
import dev.akif.tapik.test.maven.contract.Authors
import dev.akif.tapik.test.maven.contract.Catalog
import dev.akif.tapik.test.maven.contract.CreateAuthor
import dev.akif.tapik.test.maven.integration.generated.AuthorsClient
import dev.akif.tapik.test.maven.integration.generated.AuthorsServer
import dev.akif.tapik.test.maven.integration.generated.CatalogServer
import dev.akif.tapik.test.maven.integration.generated.samemodule.BooksClient as SameModuleBooksClient
import dev.akif.tapik.test.maven.integration.generated.samemodule.BooksServer as SameModuleBooksServer
import dev.akif.tapik.target.spring.restclient.RestClientTransport
import dev.akif.tapik.target.spring.webmvc.EnableTapikWebMvc
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.content as requestContent
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header as responseHeader
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.client.RestClient
import org.springframework.web.bind.annotation.RestController
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

class MavenUsageSpec : FunSpec({
    val webMvc = webMvcFixture()
    val plainWebMvc = webMvcFixture(PlainWebMvcTestApplication::class.java)
    afterSpec {
        webMvc.close()
        plainWebMvc.close()
    }

    test("compile API definitions and generate OpenAPI through one Tapik Maven plugin") {
        val apis = ServiceLoader.load(ApiRegistry::class.java).flatMap(ApiRegistry::apis)
        apis.map { api -> api.id }.sorted() shouldContainExactly listOf("Authors", "Books", "Catalog")
        apis.single { api -> api.id == "Authors" }.shouldBeInstanceOf<Authors>()

        Files.isRegularFile(
            Path.of(
                "target/generated-sources/tapik-restclient/dev/akif/tapik/test/maven/integration/generated/AuthorsClient.kt"
            )
        ) shouldBe true
        Class.forName("dev.akif.tapik.test.maven.integration.generated.AuthorsClient").isInterface shouldBe true
        Files.isRegularFile(
            Path.of(
                "target/generated-sources/tapik-webmvc/dev/akif/tapik/test/maven/integration/generated/AuthorsServer.kt"
            )
        ) shouldBe true
        Class.forName("dev.akif.tapik.test.maven.integration.generated.AuthorsServer").isInterface shouldBe true
        Files.isRegularFile(
            Path.of(
                "target/generated-sources/tapik-same-module-restclient/dev/akif/tapik/test/maven/integration/generated/samemodule/BooksClient.kt"
            )
        ) shouldBe true
        SameModuleBooksClient::class.java.isInterface shouldBe true
        Files.isRegularFile(
            Path.of(
                "target/generated-sources/tapik-same-module-webmvc/dev/akif/tapik/test/maven/integration/generated/samemodule/BooksServer.kt"
            )
        ) shouldBe true
        SameModuleBooksServer::class.java.isInterface shouldBe true
        Files.isRegularFile(
            Path.of(
                "target/classes/META-INF/tapik/spring/webmvc/dev.akif.tapik.test.maven.integration.generated.samemodule.BooksGeneratedController.properties"
            )
        ) shouldBe true

        generated("Authors") shouldBe expected("Authors")
        generated("Books") shouldBe expected("Books")
        generated("Catalog") shouldBe expected("Catalog")
        Files.exists(Path.of("target/generated/tapik/filtered/Catalog.yml")) shouldBe true
        Files.exists(Path.of("target/generated/tapik/filtered/Authors.yml")) shouldBe false
        Files.exists(Path.of("target/generated/tapik/filtered/Books.yml")) shouldBe false
        Files.exists(Path.of("target/generated/tapik/Books.openapi.json")) shouldBe false
    }

    test("execute a client generated from a contract dependency") {
        val builder = RestClient.builder().baseUrl("https://library.example")
        val server = MockRestServiceServer.bindTo(builder).build()
        val client =
            object : AuthorsClient {
                override val authorsApi = Authors()
                override val restClientTransport = RestClientTransport(builder.build())
            }

        server
            .expect(requestTo("https://library.example/authors?name=Ursula&page=1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("X-Request-Id", "request-1"))
            .andExpect(header("X-Client", "tapik"))
            .andRespond(
                withSuccess(
                    """[{"id":"author-1","name":"Ursula K. Le Guin"}]""",
                    MediaType.APPLICATION_JSON
                ).header("x-api-version", "1")
            )

        client.list(xRequestId = "request-1", name = listOf("Ursula")) shouldBe
            AuthorsClient.ListResponse.Ok(
                body = listOf(Author(id = "author-1", name = "Ursula K. Le Guin"))
            )
        server.verify()
    }

    test("reject missing, mismatched, or repeated fixed response headers") {
        listOf(emptyList(), listOf("2"), listOf("1", "1")).forEach { apiVersions ->
            val builder = RestClient.builder().baseUrl("https://library.example")
            val server = MockRestServiceServer.bindTo(builder).build()
            val client =
                object : AuthorsClient {
                    override val authorsApi = Authors()
                    override val restClientTransport = RestClientTransport(builder.build())
                }
            val response =
                withSuccess(
                    """[{"id":"author-1","name":"Ursula K. Le Guin"}]""",
                    MediaType.APPLICATION_JSON
                )
            if (apiVersions.isNotEmpty()) {
                response.header("X-API-Version", *apiVersions.toTypedArray())
            }
            server.expect(requestTo("https://library.example/authors?page=1")).andRespond(response)

            shouldThrow<IllegalStateException> { client.list(xRequestId = "request-1") }
                .message shouldContain "X-API-Version"
            server.verify()
        }
    }

    test("execute create response alternatives through a generated client") {
        val builder = RestClient.builder().baseUrl("https://library.example")
        val server = MockRestServiceServer.bindTo(builder).build()
        val client =
            object : AuthorsClient {
                override val authorsApi = Authors()
                override val restClientTransport = RestClientTransport(builder.build())
            }

        server
            .expect(requestTo("https://library.example/authors"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("X-Request-Id", "request-1"))
            .andExpect(requestContent().contentType(MediaType.APPLICATION_JSON))
            .andExpect(requestContent().string("""{"name":"Octavia E. Butler"}"""))
            .andRespond(
                withStatus(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Location", "/authors/author-2")
                    .body("""{"id":"author-2","name":"Octavia E. Butler"}""")
            )
        server
            .expect(requestTo("https://library.example/authors"))
            .andRespond(withStatus(HttpStatus.BAD_REQUEST))

        client.create(
            xRequestId = "request-1",
            body = CreateAuthor(name = "Octavia E. Butler")
        ) shouldBe
            AuthorsClient.CreateResponse.Created(
                body = Author(id = "author-2", name = "Octavia E. Butler"),
                location = "/authors/author-2"
            )

        client.create(
            xRequestId = "request-1",
            body = CreateAuthor(name = "invalid")
        ) shouldBe AuthorsClient.CreateResponse.BadRequest
        server.verify()
    }

    test("serve a response through a generated WebMVC interface") {
        val controller = webMvc.controller
        val mvc = webMvc.mvc

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            )
            .andExpect(status().isOk)
            .andExpect(responseHeader().string("X-API-Version", "1"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("""[{"id":"author-1","name":"Ursula K. Le Guin"}]"""))
        controller.names shouldBe listOf("Ursula")
        controller.page shouldBe 1

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula,Octavia")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            ).andExpect(status().isOk)
        controller.names shouldBe listOf("Ursula,Octavia")

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula", "Octavia")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            ).andExpect(status().isOk)
        controller.names shouldBe listOf("Ursula", "Octavia")

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
                    .accept(MediaType.APPLICATION_XML)
            ).andExpect(status().isNotAcceptable)

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
                    .header("Accept", "application/json;q=0, application/xml;q=1")
            ).andExpect(status().isNotAcceptable)

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "none")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
                    .accept(MediaType.TEXT_PLAIN)
            ).andExpect(status().isNoContent)

        mvc
            .perform(
                get("/authors")
                    .queryParam("page", "not-an-integer")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            ).andExpect(status().isBadRequest)

        mvc
            .perform(
                get("/authors")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "another-client")
            ).andExpect(status().isBadRequest)
    }

    test("execute create requests through a generated WebMVC interface") {
        val controller = webMvc.controller
        val mvc = webMvc.mvc

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Octavia E. Butler"}""")
            )
            .andExpect(status().isCreated)
            .andExpect(responseHeader().string("Location", "/authors/author-2"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("""{"id":"author-2","name":"Octavia E. Butler"}"""))
        controller.created shouldBe CreateAuthor(name = "Octavia E. Butler")

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("not-json")
            ).andExpect(status().isBadRequest)

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("Octavia E. Butler")
            ).andExpect(status().isUnsupportedMediaType)

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"invalid"}""")
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().bytes(byteArrayOf()))
    }

    test("auto-register generated adapters for one multi-API handler") {
        webMvc.adapters.map { adapter -> adapter.javaClass.simpleName }.sorted() shouldContainExactly
            listOf("AuthorsGeneratedController", "BooksGeneratedController", "CatalogGeneratedController")
        webMvc.mvc.perform(get("/books")).andExpect(status().isOk)
        webMvc.mvc.perform(get("/catalog")).andExpect(status().isOk)
    }

    test("register the same generated adapters in plain Spring") {
        plainWebMvc.adapters.map { adapter -> adapter.javaClass.simpleName }.sorted() shouldContainExactly
            listOf("AuthorsGeneratedController", "BooksGeneratedController", "CatalogGeneratedController")
        plainWebMvc.mvc.perform(get("/books")).andExpect(status().isOk)
        plainWebMvc.mvc.perform(get("/catalog")).andExpect(status().isOk)
    }
})

private class LibraryController : AuthorsServer, CatalogServer {
    override val authorsApi: Authors = Authors()
    override val catalogApi: Catalog = Catalog()
    var names: List<String>? = null
    var page: Int? = null
    var created: CreateAuthor? = null

    override fun list(
        xRequestId: String,
        name: List<String>?,
        page: Int
    ): AuthorsServer.ListResponse =
        if (name == listOf("none")) {
            AuthorsServer.ListResponse.NoContent
        } else {
            names = name
            this.page = page
            AuthorsServer.ListResponse.Ok(
                body = listOf(Author(id = "author-1", name = "Ursula K. Le Guin"))
            )
        }

    override fun create(
        xRequestId: String,
        body: CreateAuthor
    ): AuthorsServer.CreateResponse =
        if (body.name == "invalid") {
            AuthorsServer.CreateResponse.BadRequest
        } else {
            created = body
            AuthorsServer.CreateResponse.Created(
                body = Author(id = "author-2", name = body.name),
                location = "/authors/author-2"
            )
        }

    override fun list(): CatalogServer.ListResponse = CatalogServer.ListResponse.Ok
}

private class BooksHandler : SameModuleBooksServer {
    override val booksApi: Books = Books

    override fun list(): SameModuleBooksServer.ListResponse = SameModuleBooksServer.ListResponse.Ok
}

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
private class WebMvcTestApplication {
    @Bean
    fun libraryController(): LibraryController = LibraryController()

    @Bean
    fun booksHandler(): BooksHandler = BooksHandler()
}

@Configuration(proxyBeanMethods = false)
@EnableTapikWebMvc
private class PlainWebMvcTestApplication {
    @Bean
    fun libraryController(): LibraryController = LibraryController()

    @Bean
    fun booksHandler(): BooksHandler = BooksHandler()
}

private class WebMvcFixture(
    private val context: AnnotationConfigApplicationContext,
    val controller: LibraryController,
    val adapters: List<Any>,
    val mvc: org.springframework.test.web.servlet.MockMvc
) : AutoCloseable {
    override fun close() = context.close()
}

private fun webMvcFixture(
    configuration: Class<*> = WebMvcTestApplication::class.java
): WebMvcFixture {
    val context = AnnotationConfigApplicationContext(configuration)
    val adapters = context.getBeansWithAnnotation(RestController::class.java).values.toList()
    return WebMvcFixture(
        context = context,
        controller = context.getBean(LibraryController::class.java),
        adapters = adapters,
        mvc = MockMvcBuilders.standaloneSetup(*adapters.toTypedArray()).build()
    )
}

private fun generated(api: String): Any =
    Yaml().load(Files.readString(Path.of("target/generated/tapik/$api.openapi.yml")))

private fun expected(api: String): Any =
    Yaml().load(
        requireNotNull(MavenUsageSpec::class.java.getResource("/openapi/${api.lowercase()}.json"))
            .readText()
    )
